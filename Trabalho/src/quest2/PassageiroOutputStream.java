package quest2;

import commun.pojo.Passageiro;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.*;

public class PassageiroOutputStream extends OutputStream {
    private Passageiro[] passageiros;      // Array de passageiros
    private int numObjetos;                // Número de objetos a transmitir
    private OutputStream destino;          // OutputStream de destino

    //Constructor vazio
    public PassageiroOutputStream() {}

    public PassageiroOutputStream(Passageiro[] passageiros, int numObjetos, OutputStream destino){
        this.passageiros = passageiros;
        this.numObjetos = numObjetos;
        this.destino = destino;
    }

    public void writeSystem(){

        PrintStream opLocal = new PrintStream(destino);
        opLocal.println("Quantidade de passageiros: " + numObjetos + "\n");

        //printa os dados de um conjunto de passageiros
        //Enquanto tiver passageiros faça
        for (Passageiro passageiro : passageiros){
            String nome = passageiro.getNome();
            String cpf = passageiro.getCpf();
            String nascimento = passageiro.getDataNascimento();

            opLocal.println("Nome do passageiro: " + nome + "\n" +
                            "Cpf: " + cpf + "\n" +
                            "Data de nascimento: " + nascimento + "\n"
            );      
        }

    }

    //Tenho que passar como argumento o endereço/nome do arquivo
    public void writeFile(String nomeArquivo) throws IOException {
		// envia os dados de um conjunto de Passageiros
        FileOutputStream file = new FileOutputStream(nomeArquivo);  //Arquivo para a gravação
        PrintStream opLocal = new PrintStream(file);

        opLocal.println("Quantidade de passageiros: " + numObjetos + "\n");


        for (Passageiro passageiro : passageiros){
            String nome = passageiro.getNome();
            String cpf = passageiro.getCpf();
            String nascimento = passageiro.getDataNascimento();

            opLocal.println("Nome do passageiro: " + nome + "\n" +
                            "Cpf: " + cpf + "\n" +
                            "Data de nascimento: " + nascimento + "\n"
            );      
        }

        opLocal.flush(); //Envio os dados para o arquivo
        opLocal.close(); //fecho o canal de print
        file.close();    //encerro o arquivo
	}


    //Como vai ser enviado dados, acredito ser um cliente que envia para um server
    //Tenho que passar como argumento o endereço do server, bemo como sua respectiva porta
    public void writeTCP(String enderecoServer, int portaServer) throws IOException {
		//Primeiro coisa que vou fazer é criar uma conexão com o server
        Socket s = null;

        //Tento estabelecer conexão
        try{
        System.out.println("Conectando ao servidor " + enderecoServer + ":" + portaServer + "...");
        s = new Socket(enderecoServer, portaServer);
        System.out.println("Conectado ao servidor!");


        //Apesar de não ser o mais viável, vou utilizar o PrintStream aqui também
        PrintStream opLocal = new PrintStream(s.getOutputStream());

        opLocal.println("Quantidade de passageiros: " + numObjetos + "\n");


        for (Passageiro passageiro : passageiros){
            String nome = passageiro.getNome();
            String cpf = passageiro.getCpf();
            String nascimento = passageiro.getDataNascimento();

            opLocal.println("Nome do passageiro: " + nome + "\n" +
                            "Cpf: " + cpf + "\n" +
                            "Data de nascimento: " + nascimento + "\n"
            );      
        }

        opLocal.flush(); //Envio os dados para o arquivo
        System.out.println("Dados enviados via TCP\n");
        } catch (IOException e) {
            System.err.println("Erro na conexão TCP: " + e.getMessage());
        } finally {
            try {
					s.close();
				} catch (IOException e) {
					System.out.println("close:" + e.getMessage());
				}
        }
	}

    @Override
	public void write(int b) throws IOException {
		// TODO Auto-generated method stub
	}

}
