package quest4.serialization; 

import commun.pojo.Passageiro;
import quest4.utils.desempacotamento;

import java.io.*;
import java.net.*;

public class Cliente {
    public static void main(String[] args) {
        String host = "localhost";
        int porta = 5004;

        try (Socket socket = new Socket(host, porta);
             
             /* cria a varivel que sera responsavel por empacota o request 
                com um simples comando de texto como não estamos utilizando ObjectOutputStream
                 garantimos a interoperabilidade por que DataStream é aberto e pode ser implementado
                 por varias outras linguagens*/
             DataOutputStream requestWriter = new DataOutputStream(socket.getOutputStream());
             
            
             /*o desempacotamento(reply) é feito manualmente no formato binario */ 
             desempacotamento replyReader = 
                     new desempacotamento(socket.getInputStream())
             ) {
            
            System.out.println("Conectado ao Servidor");

            //Enviar o request empacotado
            System.out.println("Enviando comando");
            requestWriter.writeUTF("GET_ALL_PASSAGEIROS");
            requestWriter.flush(); // Garante o envio
            
            //Recebe e Desempacota a resposta binária
            System.out.println("Aguardando resposta do servidor");
            Passageiro[] resposta = replyReader.readPassageiros();

            System.out.println("=== Resposta Recebida ===");
            if (resposta.length == 0) {
                System.out.println("O servidor não retornou passageiros.");
            }
            
            for (Passageiro p : resposta) {
                 System.out.println(p.getNome() + " | " + p.getCpf() + " | " + p.getDataNascimento());
            }
            System.out.println("==================================================");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}