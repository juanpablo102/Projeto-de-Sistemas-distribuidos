package com.example;


import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.gson.Gson; 
import com.google.gson.JsonSyntaxException;

public class servidor {
    // Classes pojo
    public static class MensagemCliente {
        String tipoComando; String payload;
        public MensagemCliente() {}
    }
    public static class MensagemServidor {
        boolean sucesso; String mensagem;
        public MensagemServidor(boolean sucesso, String mensagem) {
            this.sucesso = sucesso; this.mensagem = mensagem;
        }
    }
    public static class MensagemInformativa {
        String tipo; String conteudo;
        public MensagemInformativa(String tipo, String conteudo) {
            this.tipo = tipo; this.conteudo = conteudo;
        }
    }

    private static final int SERVIDOR_PORTA_TCP = 5000;
    private static final String MULTICAST_IP = "230.0.0.0";
    private static final int MULTICAST_PORTA = 6000;

    private static final Gson gson = new Gson();

    private static List<String> candidatos = new CopyOnWriteArrayList<>();
    private static Map<String, AtomicInteger> votos = new ConcurrentHashMap<>(); 
    private static Map<String, String[]> usuarios = new HashMap<>();
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private static boolean votacaoAberta = false;

    public static void main(String[] args) {
        adicionarCandidato("Lula");
        adicionarCandidato("Bolsonaro");
        adicionarCandidato("Ciro");
        try (ServerSocket servidor = new ServerSocket(SERVIDOR_PORTA_TCP)) {
            System.out.println("Servidor aguardando conexões..." + SERVIDOR_PORTA_TCP);

            // carregar logins antes de aceitar conexões
            arqreader(usuarios);

            while (true) {
                Socket cliente = servidor.accept();
                new Thread(new ClienteHandler(cliente, gson)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClienteHandler implements Runnable {
        private Socket cliente;
        private Gson gson; 
        private boolean autenticado = false;
        private String tipoUsuario = "";

        public ClienteHandler(Socket cliente, Gson gson) {
            this.cliente = cliente;
            this.gson = gson;
        }

        private void enviarResposta(PrintWriter out, boolean sucesso, String mensagem) {
            MensagemServidor resposta = new MensagemServidor(sucesso, mensagem);
            String jsonResposta = gson.toJson(resposta);
            out.println(jsonResposta);
        }

public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                PrintWriter out = new PrintWriter(cliente.getOutputStream(), true)
            ) {
                String msgJson;
                while ((msgJson = in.readLine()) != null) {
                    try {
                        MensagemCliente msg = gson.fromJson(msgJson, MensagemCliente.class);

                        if (!autenticado) {
                            if (msg.tipoComando != null && msg.tipoComando.equalsIgnoreCase("login")) {
                                processarLogin(out, msg.payload);
                            } else {
                                enviarResposta(out, false, "Ação não permitida. Faça o login primeiro.");
                            }
                        } else {
                            if (tipoUsuario.equalsIgnoreCase("admin")) {
                                processarAdmin(out, msg);
                            } else {
                                processarEleitor(out, msg);
                            }
                        }
                    } catch (JsonSyntaxException e) {
                        enviarResposta(out, false, "Erro: JSON mal formatado.");
                    }
                }
            } catch (IOException e) {
                System.out.println("Cliente " + cliente.getInetAddress() + " desconectado.");
            }
        }

    private void processarLogin(PrintWriter out, String payload) {
            if (payload == null || !payload.contains(",")) {
                enviarResposta(out, false, "Login inválido. Formato esperado: 'login usuario,senha'");
                return;
            }
            
            String[] partes = payload.split(",", 2);
            String login = partes[0];
            String senha = partes[1];

            if (verificaSenha(login, senha, usuarios)) {
                this.autenticado = true;
                this.tipoUsuario = usuarios.get(login)[1];
                enviarResposta(out, true, "Login bem-sucedido! Tipo: " + this.tipoUsuario);

                if (this.tipoUsuario.equalsIgnoreCase("admin")) {
                    enviarResposta(out, true, "Comandos: add <nome>, remove <nome>, iniciar, anunciar <msg>, exit");
                } else {
                    enviarResposta(out, true, "Candidatos: " + String.join(", ", candidatos) + ". Comando: votar <nome>");
                }
            } else {
                enviarResposta(out, false, "Login ou senha inválidos.");
            }
        }

    private void processarAdmin(PrintWriter out, MensagemCliente msg) {
            switch (msg.tipoComando.toLowerCase()) {
                case "add":
                    adicionarCandidato(msg.payload);
                    enviarResposta(out, true, "Candidato adicionado: " + msg.payload);
                    break;
                case "remove":
                    removerCandidato(msg.payload);
                    enviarResposta(out, true, "Candidato removido: " + msg.payload);
                    break;
                case "iniciar":
                    iniciarVotacao(votos); 
                    enviarResposta(out, true, "Votação iniciada!");
                    break;
                case "anunciar":
                    if (msg.payload == null || msg.payload.trim().isEmpty()) {
                        enviarResposta(out, false, "Uso: anunciar <mensagem>");
                        break;
                    }
                    MensagemInformativa nota = new MensagemInformativa("ANUNCIO_ADMIN", msg.payload);
                    broadcastMulticast(gson.toJson(nota));
                    enviarResposta(out, true, "Anúncio enviado para todos os eleitores.");
                    break;
                case "exit":
                    enviarResposta(out, true, "Deslogando...");
                    try { cliente.close(); } catch (IOException e) {}
                    break;
                default:
                    enviarResposta(out, false, "Comando admin desconhecido.");
            }
        }
        private void processarEleitor(PrintWriter out, MensagemCliente msg) {
            if (msg.tipoComando.equalsIgnoreCase("votar")) {
                String voto = msg.payload;
                if (votacaoAberta && votos.containsKey(voto)) {
                    votos.get(voto).incrementAndGet();
                    enviarResposta(out, true, "Voto registrado com sucesso para " + voto + "!");
                } else {
                    enviarResposta(out, false, "Erro: votação encerrada ou candidato inválido.");
                }
            } else {
                enviarResposta(out, false, "Comando eleitor desconhecido.");
            }
        }
    }

    private static void broadcastMulticast(String mensagemJson) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress grupo = InetAddress.getByName(MULTICAST_IP);
            byte[] buffer = mensagemJson.getBytes();

            DatagramPacket pacote = new DatagramPacket(buffer, buffer.length, grupo, MULTICAST_PORTA);
            socket.send(pacote);
            System.out.println("[Multicast] Enviado: " + mensagemJson);
        } catch (IOException e) {
            System.err.println("Erro ao enviar multicast: " + e.getMessage());
        }
    }

    public static void iniciarVotacao(Map<String, AtomicInteger> mapaDeVotos) {
        votos = mapaDeVotos;
        votacaoAberta = true;
        System.out.println(LocalTime.now() + " Votação iniciada!");
        
        MensagemInformativa notaInicio = new MensagemInformativa("INICIO_VOTACAO", "A votação começou! Candidatos: " + String.join(", ", candidatos));
        broadcastMulticast(gson.toJson(notaInicio));

        Runnable aviso = () -> {
            String msg = "A votação ainda está aberta... (" + LocalTime.now() + ")";
            System.out.println(msg);
            MensagemInformativa notaStatus = new MensagemInformativa("STATUS", msg);
            broadcastMulticast(gson.toJson(notaStatus));
        };
        ScheduledFuture<?> tarefaAviso = executor.scheduleAtFixedRate(aviso, 0, 10, TimeUnit.SECONDS);

        executor.schedule(() -> {
            votacaoAberta = false;
            tarefaAviso.cancel(false);
            System.out.println("\n=== Votação encerrada! ===");
            MensagemInformativa notaFim = new MensagemInformativa("FIM_VOTACAO", "A votação foi encerrada.");
            broadcastMulticast(gson.toJson(notaFim));
            
            contabilizarResultados(); 
        }, 30, TimeUnit.SECONDS); 
    }

private static void contabilizarResultados() {
        System.out.println("\nResultados da votação:");
        int total = votos.values().stream().mapToInt(AtomicInteger::get).sum();
        
        String ganhador = "Nenhum voto registrado";
        int maxVotos = 0; 
        boolean empate = false;

        Map<String, String> resultadosMap = new HashMap<>();

        for (Map.Entry<String, AtomicInteger> entry : votos.entrySet()) {
            String candidato = entry.getKey();
            int qtdAtual = entry.getValue().get();

            // Calcula o percentual e atualiza o mapa de resultados
            double perc = (total == 0) ? 0 : (qtdAtual * 100.0 / total);
            String res = String.format("%d votos (%.2f%%)", qtdAtual, perc);
            System.out.printf("%s: %s%n", candidato, res);
            resultadosMap.put(candidato, res);

            if (qtdAtual > maxVotos) {
                maxVotos = qtdAtual;
                ganhador = candidato;
                empate = false;
            } else if (qtdAtual == maxVotos && maxVotos > 0) {
                empate = true;
            }
        }

        // Define a string final do ganhador
        if (empate) {
            ganhador = "Empate técnico";
        } else if (maxVotos == 0) {
            ganhador = "Nenhum voto registrado";
        }
        
        System.out.println("\n==================================");
        System.out.println("GANHADOR: " + ganhador);
        System.out.println("==================================");

        // Adiciona o total e o ganhador ao JSON final
        resultadosMap.put("TOTAL", total + " votos");
        resultadosMap.put("GANHADOR", ganhador);

        // Envia resultados finais via Multicast
        MensagemInformativa notaResultado = new MensagemInformativa("RESULTADO_FINAL", gson.toJson(resultadosMap));
        broadcastMulticast(gson.toJson(notaResultado));
    }

    private static void arqreader(Map<String, String[]> usuarios) {
        String csvFile = "C:/Users/sampa/OneDrive/Documentos/Sistemas distribuidos/sistema_de_votacao/output.csv"; // CUIDADO: Hardcoded path
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String linha;
            boolean primeira = true;
            while ((linha = reader.readLine()) != null) {
                if (primeira) { primeira = false; continue; }
                String[] partes = linha.split(",");
                if (partes.length < 3) continue;
                String username = partes[0].trim();
                String password = partes[1].trim();
                String role = partes[2].trim();
                usuarios.put(username, new String[]{password, role});
            }
            System.out.println("Logins carregados: " + usuarios.keySet());
        } catch (Exception e) {
            System.out.println("Erro ao ler o arquivo de logins: " + e.getMessage());
        }
    }

    private static boolean verificaSenha(String user, String password, Map<String, String[]> usuarios) {
        if (usuarios.containsKey(user)) {
            String[] dados = usuarios.get(user);
            String senhaCorreta = dados[0];
            if (password.equals(senhaCorreta)) {
                System.out.println("Login bem-sucedido para " + user);
                return true;
            }
        }
        System.out.println("Falha no login para " + user);
        return false;
    }
    private static void adicionarCandidato(String nome) {
        if (candidatos.contains(nome)) {
            System.out.println("Esse candidato já existe!");
        } else {
            candidatos.add(nome);
            votos.put(nome, new AtomicInteger(0));
            System.out.println("Candidato adicionado: " + nome);
        }
    }
    private static void removerCandidato(String nome) {
        if (candidatos.remove(nome)) {
            votos.remove(nome);
            System.out.println("Candidato removido: " + nome);
        } else {
            System.out.println("Candidato não encontrado!");
        }
    }
}
