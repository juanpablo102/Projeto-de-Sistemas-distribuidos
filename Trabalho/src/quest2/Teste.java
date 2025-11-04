package quest2;

import commun.pojo.Passageiro;
import java.io.IOException;

public class Teste {
    public static void main(String[] args) throws IOException {

    //Primeiro eu defino o passageiro, em seguida coloco em um vetor
    Passageiro primeiro = new Passageiro("Alguem ai", "123456789", "20/10/1995");
    Passageiro[] passageiros = {primeiro};
    
    // //Arquivo
    // PassageiroOutputStream file = new PassageiroOutputStream(passageiros, 1, System.out);
    // //Tente
    // try{
    //     file.writeFile("Passageiros.txt");
    //     System.out.println("Dados gravados no arquivo.");
    // }catch (IOException msg) {
    //     System.err.println("Erro: " + msg.getMessage());
    // }

    // file.close();

    // //Terminal

    // PassageiroOutputStream pos = new PassageiroOutputStream(passageiros, 1, System.out);
    // pos.writeSystem();
    // pos.close();

    //Conexão TCP
    PassageiroOutputStream tcp = new PassageiroOutputStream(passageiros, 1, System.out);

    try{
        tcp.writeTCP("localhost", 7897);
    } catch (IOException e){
        System.err.println("✗ Erro: " + e.getMessage());
    }

    tcp.close();
    
    }
}
