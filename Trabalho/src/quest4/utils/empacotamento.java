package quest4.utils; // Você pode mudar o pacote

import commun.pojo.Passageiro;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class empacotamento extends OutputStream {
    private DataOutputStream dos;

    public empacotamento(OutputStream destino) throws IOException {
        // Envelopa o stream de destino com um DataOutputStream
        this.dos = new DataOutputStream(destino);
    }

    public void writePassageiros(Passageiro[] passageiros) throws IOException {
        //Escreve a quantidade de passageiros 
        dos.writeInt(passageiros.length);

        //Itera e escreve os campos de cada um, na ordem
        for (Passageiro p : passageiros) {
            dos.writeUTF(p.getNome());
            dos.writeUTF(p.getCpf());
            dos.writeUTF(p.getDataNascimento());
        }
        dos.flush(); // Garante o envio
    }

    /*@Override
    public void write(int b) throws IOException {
        dos.write(b);
    }

    @Override
    public void close() throws IOException {
        dos.close();
    }*/
}