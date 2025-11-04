package quest3;

import commun.pojo.Passageiro;
import java.io.IOException;
import quest2.PassageiroOutputStream;

public class TesteInputPassageiro {
    
    public static void main(String[] args) throws IOException{
        Passageiro[] pessoas = new Passageiro[2];

        //Terminal
        // PassageiroInputStream pis = new PassageiroInputStream(System.in);
        // try {
        //     pessoas = pis.readSystem();
        // } catch (Exception e){
        //     System.err.println("Erro: " + e.getMessage());
        // }
        // pis.close();

        //Arquivo

        // PassageiroInputStream pis = new PassageiroInputStream(System.in);
        // try{
        //     pessoas = pis.readFile("Passageiros.txt");
        // }catch (Exception e){
        //     System.err.println("Erro: " + e.getMessage());
        // }
        // pis.close();

        //TCP
        PassageiroInputStream pis = new PassageiroInputStream(System.in);        
        try {
            pessoas = pis.readTcp(7896);
            
            // Exibir resultados
            System.out.println("\n=== PASSAGEIROS RECEBIDOS VIA TCP ===");
            for (int i = 0; i < pessoas.length; i++) {
                Passageiro p = pessoas[i];
                System.out.println("\n" + (i + 1) + ". " + p.getNome());
                System.out.println("   CPF: " + p.getCpf());
                System.out.println("   Data: " + p.getDataNascimento());
            }
            
            System.out.println("\nTeste TCP concluído com sucesso!");
            
        } catch (IOException e) {
            System.err.println("Erro: " + e.getMessage());
        }

        pis.close();


        // PassageiroOutputStream pos = new PassageiroOutputStream(pessoas, 2, System.out);
        // pos.writeSystem();
        // pos.close();

}

}
