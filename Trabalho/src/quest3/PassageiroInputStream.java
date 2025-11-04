package quest3;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import commun.pojo.Passageiro;

public class PassageiroInputStream extends InputStream {
    private InputStream destino;         
    private List<Passageiro> passageiros;  

    //Constructor padrão
    public PassageiroInputStream () {}

    //Construtor com argumentos
    public PassageiroInputStream(InputStream destino) {
        this.destino = destino;
        this.passageiros = new ArrayList<>();   //Inicializa uma list na memória
    }

    public Passageiro[] readSystem() {

        Scanner sc = new Scanner(destino);

        //Implementar lógica de continuação
        System.out.println("=== Leitura de Passageiros ===\n");
        String continua = "sim";
        while (continua.equalsIgnoreCase("sim")) {
            System.out.print("Informe o nome do passageiro: ");
            String nome = sc.nextLine();
            System.out.print("Informe o CPF do passageiro: ");
            String cpf = sc.nextLine();
            System.out.print("Informe a data de nascimento DD/MM/YYYY: ");
            String dataNascimento = sc.nextLine();

            //Cria o passageiro e adiciona na lista
            Passageiro p = new Passageiro(nome, cpf, dataNascimento);
            passageiros.add(p);

            System.out.print("\nDeseja adicionar outro passageiro? (sim/não): ");
            continua = sc.nextLine();
            System.out.println();
        }

        System.out.println(passageiros.size() + " passageiro(s) lido(s)!");
        sc.close();
        return passageiros.toArray(new Passageiro[0]);
    }

    //Vai ter um tratamento partircularmente maior
    public Passageiro[] readFile(String caminhoArquivo) {
        try (FileInputStream fis = new FileInputStream(caminhoArquivo);
         BufferedReader leitor = new BufferedReader(new InputStreamReader(fis))) {
        
        //Ler a primeira linha que contém a quantidade 
        String primeiraLinha = leitor.readLine();
        if (primeiraLinha == null || !primeiraLinha.startsWith("Quantidade de passageiros:")) {
            throw new IOException("Arquivo mal formatado! Primeira linha deve ser: 'Quantidade de passageiros: N'");
        }
        
        // Extrair o número
        String[] partes = primeiraLinha.split(":");
        int quantidade = Integer.parseInt(partes[1].trim());
        
        System.out.println("Total de passageiros no arquivo: " + quantidade + "\n");
        
        for (int i = 0; i < quantidade; i++) {
            String nome = null;
            String cpf = null;
            String dataNascimento = null;
            
            // Ler até encontrar os 3 dados
            String linha;
            while ((linha = leitor.readLine()) != null) {
                if (linha.trim().isEmpty()) {
                    continue; // Pular linhas vazias
                }
                
                if (linha.startsWith("Nome do passageiro:")) {
                    nome = linha.substring("Nome do passageiro: ".length()).trim();
                }
                else if (linha.startsWith("Cpf:")) {
                    cpf = linha.substring("Cpf: ".length()).trim();
                }
                else if (linha.startsWith("Data de nascimento:")) {
                    dataNascimento = linha.substring("Data de nascimento: ".length()).trim();
                }
                
                // Se achou os 3 dados, pode parar
                if (nome != null && cpf != null && dataNascimento != null) {
                    break;
                }
            }
            
            // Criar e adicionar o passageiro
            if (nome != null && cpf != null && dataNascimento != null) {
                Passageiro p = new Passageiro(nome, cpf, dataNascimento);
                passageiros.add(p);
            } else {
                System.err.println("Erro ao ler passageiro " + (i + 1) + " do arquivo!");
            }
        }
        
        System.out.println("\n" + passageiros.size() + " passageiro(s) lido(s) com sucesso!");
        
    } catch (IOException e) {
        System.err.println("Erro ao ler arquivo: " + e.getMessage());
    }
    
    return passageiros.toArray(new Passageiro[0]);
}

//Acredito ser um server
//vou aceitar apenas 1 cliente por vez
//Aguarda conexão de um cliente que envia dados
public Passageiro[] readTcp(int porta) throws IOException {
     System.out.println("=== Servidor TCP de Leitura de Passageiros ===");
     System.out.println("Aguardando conexão na porta " + porta + "...\n");

     try (java.net.ServerSocket servidor = new java.net.ServerSocket(porta)) {

        // Aceitar conexão do cliente
        java.net.Socket cliente = servidor.accept();
        System.out.println("Cliente conectado: " + cliente.getInetAddress().getHostAddress() + "\n");

        // Ler dados do cliente
        try (BufferedReader leitor = new BufferedReader(
                new InputStreamReader(cliente.getInputStream()))) {
            
            // Ler primeira linha: quantidade
            String primeiraLinha = leitor.readLine();
            if (primeiraLinha == null || !primeiraLinha.startsWith("Quantidade de passageiros:")) {
                throw new IOException("Formato inválido!");
            }
            
            int quantidade = Integer.parseInt(primeiraLinha.split(":")[1].trim());
            // Ler cada passageiro
            for (int i = 0; i < quantidade; i++) {
                String nome = null;
                String cpf = null;
                String dataNascimento = null;
                
                String linha;
                while ((linha = leitor.readLine()) != null) {
                    if (linha.trim().isEmpty()) continue;
                    
                    if (linha.startsWith("Nome do passageiro:")) {
                        nome = linha.substring("Nome do passageiro: ".length()).trim();
                    }
                    else if (linha.startsWith("Cpf:")) {
                        cpf = linha.substring("Cpf: ".length()).trim();
                    }
                    else if (linha.startsWith("Data de nascimento:")) {
                        dataNascimento = linha.substring("Data de nascimento: ".length()).trim();
                    }
                    
                    if (nome != null && cpf != null && dataNascimento != null) {
                        break;
                    }
                }
                
                if (nome != null && cpf != null && dataNascimento != null) {
                    Passageiro p = new Passageiro(nome, cpf, dataNascimento);
                    passageiros.add(p);
                }
            }
            
            System.out.println("\n" + passageiros.size() + " passageiro(s) recebido(s) via TCP!");
        }
        
        cliente.close();
        
    } catch (IOException e) {
        System.err.println("Erro no servidor TCP: " + e.getMessage());
        throw e;
    }
    
    return passageiros.toArray(new Passageiro[0]);
}

    @Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}
}
