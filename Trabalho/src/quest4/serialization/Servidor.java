package quest4.serialization; 

import commun.pojo.Passageiro;
import quest4.utils.empacotamento;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Servidor {
    
    private static final String NOME_ARQUIVO = "C:\\Users\\sampa\\Downloads\\Trabalho\\Trabalho\\bin\\Passageiros.txt";

    public static void main(String[] args) {
        int porta = 5004;
        System.out.println("Servidor aguardando na porta " + porta + "...");

        try (ServerSocket servidor = new ServerSocket(porta);
             Socket cliente = servidor.accept()) {
            
            System.out.println("Cliente conectado: " + cliente.getInetAddress());

            //desempacotar (Request) 
            // O request será um comando simples em string 
            // Usamos DataInputStream para ler o comando
            DataInputStream requestReader = new DataInputStream(cliente.getInputStream());
            String comando = requestReader.readUTF();
            
            System.out.println("Comando do cliente recebido: " + comando);

            //chama o metodo para fazer o parse dos dados de Passageiros.txt para poder ser enviado pela stream e empacota para enviar
            if (comando.equals("GET_ALL_PASSAGEIROS")) {
                
                ArrayList<Passageiro> passageirosDoTxt = parseArquivoPassageiros();
                System.out.println("Dados lidos do " + NOME_ARQUIVO + 
                                   " e convertidos para " + passageirosDoTxt.size() + " objetos.");

                // empacotar (Reply) 
                // Agora, enviamos a resposta usando o formato binário manual
                empacotamento replyWriter = 
                        new empacotamento(cliente.getOutputStream());
                
                // Converter ArrayList para Array para o nosso writer
                Passageiro[] passageirosArray = passageirosDoTxt.toArray(new Passageiro[0]);
                
                replyWriter.writePassageiros(passageirosArray);
                
                System.out.println("Resposta binária enviada ao cliente.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Passageiro> parseArquivoPassageiros() throws IOException {
        ArrayList<Passageiro> lista = new ArrayList<>();

        try (BufferedReader fileReader = new BufferedReader(new FileReader(NOME_ARQUIVO))) {
            String linhaQtde = fileReader.readLine();
            int quantidade = Integer.parseInt(linhaQtde.split(": ")[1]);

            fileReader.readLine(); // Pular linha em branco

            for (int i = 0; i < quantidade; i++) {
                String nome = fileReader.readLine().split(": ")[1]; 
                String cpf = fileReader.readLine().split(": ")[1];  
                String data = fileReader.readLine().split(": ")[1]; 
                
                lista.add(new Passageiro(nome, cpf, data));
                
                fileReader.readLine(); // Pular linha em branco entre passageiros
            }
        } catch (FileNotFoundException e) {
            System.err.println("ERRO: O 'Banco de Dados' " + NOME_ARQUIVO + " não foi encontrado!");
        }
        
        return lista;
    }
}