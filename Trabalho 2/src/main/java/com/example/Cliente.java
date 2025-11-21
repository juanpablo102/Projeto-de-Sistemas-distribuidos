package com.example;
import com.example.pojo.*;
import java.util.List;


public class Cliente {
    
    public static void main(String[] args) {
        try {
            ServicoPassagensProxy servico = new ServicoPassagensProxy();
            System.out.println("[CLIENTE] Conectado ao servidor.");

            System.out.println("\n--- 1. Listando Viagens Disponíveis ---");
            List<Viagem> viagens = servico.listarViagens();
            if (viagens.isEmpty()) {
                System.out.println("[CLIENTE] Nenhuma viagem disponível. Encerrando teste.");
                return;
            }
            for (Viagem v : viagens) {
                System.out.println("  ID: " + v.getId() + ", Rota: " + v.getRota().getOrigem() + " -> " + v.getRota().getDestino());
            }
            
            System.out.println("\n--- 2. Comprando passagens ---");
            Viagem viagemEscolhida = viagens.get(0); // Pega a primeira viagem
            Passageiro p1 = new Passageiro("Fulano Silva", "123.456.789-00", "01/01/1990");
            
            // Compra a primeira passagem
            Passagem passagemParaComprar1 = new Passagem(p1, viagemEscolhida);
            String idPassagem1 = servico.comprarPassagem(passagemParaComprar1);
            
            if (idPassagem1 != null) {
                System.out.println("[CLIENTE] SUCESSO! Passagem 1 comprada, ID: " + idPassagem1);
            } else {
                System.out.println("[CLIENTE] FALHA ao comprar passagem 1.");
            }

            // Compra a segunda passagem (mesmo passageiro, mesma viagem)
            Passagem passagemParaComprar2 = new Passagem(p1, viagemEscolhida);
            String idPassagem2 = servico.comprarPassagem(passagemParaComprar2);
            
            if (idPassagem2 != null) {
                System.out.println("[CLIENTE] SUCESSO! Passagem 2 comprada, ID: " + idPassagem2);
            } else {
                System.out.println("[CLIENTE] FALHA ao comprar passagem 2.");
            }

            System.out.println("\n--- 3. Pesquisando passagens para '123.456.789-00' ---");
            List<Passagem> minhasPassagens = servico.pesquisarPassagem("123.456.789-00");
            System.out.println("[CLIENTE] Encontradas " + minhasPassagens.size() + " passagens:");
            for (Passagem p : minhasPassagens) {
                System.out.println("  - ID: " + p.getId());
            }

            System.out.println("\n--- 4. Cancelando Passagem 1 (" + idPassagem1 + ") ---");
            boolean sucessoCancelamento = servico.cancelarPassagem(idPassagem1);
            if (sucessoCancelamento) {
                System.out.println("[CLIENTE] SUCESSO! Passagem 1 cancelada.");
            } else {
                System.out.println("[CLIENTE] FALHA ao cancelar passagem 1.");
            }

            System.out.println("\n--- 5. Pesquisando novamente para '123.456.789-00' ---");
            minhasPassagens = servico.pesquisarPassagem("123.456.789-00");
            System.out.println("[CLIENTE] Encontradas " + minhasPassagens.size() + " passagens:");
            for (Passagem p : minhasPassagens) {
                System.out.println("  - ID: " + p.getId() + " (Deverá ser a Passagem 2)");
            }

            System.out.println("\n--- 6. Tentando cancelar Passagem 1 novamente (" + idPassagem1 + ") ---");
            sucessoCancelamento = servico.cancelarPassagem(idPassagem1);
            if (!sucessoCancelamento) {
                System.out.println("[CLIENTE] Falha ao cancelar passagem");
            }

            System.out.println("\n--- 7. Tentando cancelar Passagem 2  (" + idPassagem1 + ") ---");
            sucessoCancelamento = servico.cancelarPassagem(idPassagem2);
            if (sucessoCancelamento) {
                System.out.println("[CLIENTE] SUCESSO! Passagem 2 cancelada.");
            } else {
                System.out.println("[CLIENTE] FALHA ao cancelar passagem 2.");
            }


            System.out.println("\n--- 8. Pesquisando novamente para '123.456.789-00' ---");
            minhasPassagens = servico.pesquisarPassagem("123.456.789-00");
            System.out.println("[CLIENTE] Encontradas " + minhasPassagens.size() + " passagens:");
            for (Passagem p : minhasPassagens) {
                System.out.println("  - ID: " + p.getId() + " (Deverá ser nenhuma)");
            }

        } catch (Exception e) {
            System.err.println("[CLIENTE] ERRO DE EXECUÇÃO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}