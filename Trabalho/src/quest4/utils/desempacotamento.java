package quest4.utils; 

import commun.pojo.Passageiro;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class desempacotamento extends InputStream {
    private DataInputStream dis;

    public desempacotamento(InputStream origem) throws IOException {
        // Envelopa o stream de origem com um DataInputStream
        this.dis = new DataInputStream(origem);
    }

    /**
     * Define o protocolo de leitura manual (ordem inversa da escrita).
     */
    public Passageiro[] readPassageiros() throws IOException {
        //Lê a quantidade de passageiros
        int quantidade = dis.readInt();
        Passageiro[] passageiros = new Passageiro[quantidade];

        //Itera e lê os campos de cada um na ordem exata da escrita
        for (int i = 0; i < quantidade; i++) {
            String nome = dis.readUTF();
            String cpf = dis.readUTF();
            String dataNascimento = dis.readUTF();

            //Recria o objeto 
            passageiros[i] = new Passageiro(nome, cpf, dataNascimento);
        }
        return passageiros;
    }

    // Métodos obrigatórios da herança
    @Override
    public int read() throws IOException {
        return dis.read();
    }

    @Override
    public void close() throws IOException {
        dis.close();
    }
}