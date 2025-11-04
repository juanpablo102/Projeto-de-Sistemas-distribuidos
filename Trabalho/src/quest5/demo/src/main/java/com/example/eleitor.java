package com.example;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import com.example.eleitor.MensagemServidor;
import com.google.gson.Gson; 
import com.google.gson.JsonSyntaxException; 

public class eleitor {

    public static class MensagemCliente {
        String tipoComando; String payload;
        public MensagemCliente(String tipoComando, String payload) {
            this.tipoComando = tipoComando; this.payload = payload;
        }
    }
    public static class MensagemServidor {
        boolean sucesso; String mensagem;
        public MensagemServidor() {}
    }
    public static class MensagemInformativa {
        String tipo; String conteudo;
        public MensagemInformativa() {}
    }

    private static final String SERVIDOR_IP = "127.0.0.1";
    private static final int SERVIDOR_PORTA = 5000;
    private static final String MULTICAST_IP = "230.0.0.0"; // grupo multicast
    private static final int MULTICAST_PORTA = 6000;

    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVIDOR_IP, SERVIDOR_PORTA);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner sc = new Scanner(System.in)
        ) {
            System.out.println("Conectado ao servidor!");

            
        // 🔹 Inicia uma thread separada para mensagens do servidor 
            new Thread(() -> {
                try {
                    String msgJson;
                    while ((msgJson = in.readLine()) != null) {
                        try {
                            MensagemServidor msg = gson.fromJson(msgJson, MensagemServidor.class);
                            System.out.println("[SERVIDOR]: " + msg.mensagem);

                        } catch (JsonSyntaxException e) { 
                            System.out.println("[SERVIDOR-RAW]: " + msgJson);
                        }
                    }
                } catch (IOException e) { 
                    System.out.println("Conexão encerrada.");
                }
            }).start();

            // 🔹 Inicia thread para receber mensagens multicast (UDP)
            new Thread(() -> {
                try (MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORTA)) {
                    InetAddress grupo = InetAddress.getByName(MULTICAST_IP);
                    multicastSocket.joinGroup(grupo);
                    byte[] buffer = new byte[1024];
                    System.out.println("[Multicast] Aguardando notas informativas...");

                    while (true) {
                        DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);
                        multicastSocket.receive(pacote);
                        String mensagem = new String(pacote.getData(), 0, pacote.getLength());
                        
                        try {
                            MensagemInformativa nota = gson.fromJson(mensagem, MensagemInformativa.class);
                            System.out.println("\n[NOTA INFORMATIVA - " + nota.tipo + "]: " + nota.conteudo);
                        } catch (JsonSyntaxException e) {
                            System.out.println("\n[NOTA-RAW]: " + mensagem); 
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Erro no multicast: " + e.getMessage());
                }
            }).start();

            // 🔹 Envia mensagens do teclado (login, comandos, voto)
            while (true) {
                String msg = sc.nextLine();
                if (msg.equalsIgnoreCase("sair")) {
                    socket.close();
                    break;
                }
                
                String[] partes = msg.split(" ", 2); 
                String comando = partes[0];
                String payload = (partes.length > 1) ? partes[1] : ""; 

                MensagemCliente msgCliente = new MensagemCliente(comando, payload);

                String jsonParaEnviar = gson.toJson(msgCliente);
                out.println(jsonParaEnviar);
            }

        } catch (IOException e) {
            System.out.println("Erro de conexão: " + e.getMessage());
        }
    }
}
