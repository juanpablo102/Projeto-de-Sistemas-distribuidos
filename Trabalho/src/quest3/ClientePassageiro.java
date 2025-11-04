package quest3;

import commun.pojo.Passageiro;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import quest2.*;

public class ClientePassageiro {
    public static void main(String[] args) throws IOException {
        
        // Dados de teste
        Passageiro p1 = new Passageiro("Alguem ai", "123456789", "20/10/1195");
        Passageiro p2 = new Passageiro("Testando", "0987654321", "11/11/2001");

        Passageiro[] passageiros = {p1, p2};
        
        PassageiroOutputStream out = new PassageiroOutputStream(passageiros, 2, System.out);
        
        try {
            String servidor = "localhost";
            int porta = 7896;
            
            System.out.println("Conectando ao servidor " + servidor + ":" + porta + "...");
            
            // Conectar ao servidor
            Socket socket = new Socket(servidor, porta);
            System.out.println("Conectado!\n");
            
            // Enviar dados via socket
            PrintStream ps = new PrintStream(socket.getOutputStream());
            
            // Enviar quantidade
            ps.println("Quantidade de passageiros: " + 3);
            ps.println();
            
            // Enviar cada passageiro
            for (Passageiro p : passageiros) {
                ps.println("Nome do passageiro: " + p.getNome());
                ps.println("Cpf: " + p.getCpf());
                ps.println("Data de nascimento: " + p.getDataNascimento());
                ps.println();
            }
            
            ps.flush();
            ps.close();
            socket.close();
            
            System.out.println("Dados enviados com sucesso!");
            
        } catch (IOException e) {
            System.err.println("Erro de conexão: " + e.getMessage());
        }

        out.close();
    }
}
