package com.example;
import com.example.pojo.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken; 
import java.lang.reflect.Type;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Iterator;



public class ServicoPassagensImpl extends UnicastRemoteObject implements IServicoPassagens {
    private Gson gson = new Gson();
    private List<Viagem> viagensDisponiveis = new ArrayList<>();
    private List<Passagem> passagensVendidas = new ArrayList<>();
    

public ServicoPassagensImpl() throws RemoteException {
        super(); // Necessário para UnicastRemoteObject
        
        System.out.println("[SERVIDOR] Iniciando dados de teste");

        Rota rotaQuixadaFortaleza = new Rota("Quixadá", "Fortaleza", 180.0, 210);
        Rota rotaQuixadaIguatu = new Rota("Quixadá", "Iguatu", 120.0, 150);

        Viagem v1 = new Viagem("QXD_FOR_001", rotaQuixadaFortaleza, "20/12/2025 08:00", 42);
        Viagem v2 = new Viagem("QXD_IGU_002", rotaQuixadaIguatu, "21/12/2025 10:00", 2); 
        
        // Adicionar viagens à lista do servidor
        this.viagensDisponiveis.add(v1);
        this.viagensDisponiveis.add(v2);
        
        System.out.println("[SERVIDOR] Dados de teste carregados. Viagens disponíveis: " + this.viagensDisponiveis.size());
    }
@Override
public String comprarPassagem(String jsonRequest) throws RemoteException {
    RequestMessage req = gson.fromJson(jsonRequest, RequestMessage.class);
    
    Passagem passagemInfoCliente = gson.fromJson(req.arguments, Passagem.class);

    // Chama a lógica de negócio
    String idPassagemComprada = logicaDeCompra(passagemInfoCliente);

    // Empacota a Resposta
    ReplyMessage rep = new ReplyMessage();
    rep.requestID = req.requestID;
    rep.result = gson.toJson(idPassagemComprada); // Retorna o ID (ou null)

    return gson.toJson(rep);
}

    @Override
public String pesquisarPassagem(String jsonRequest) throws RemoteException {
    RequestMessage req = gson.fromJson(jsonRequest, RequestMessage.class);
    ReplyMessage rep = new ReplyMessage();
    rep.requestID = req.requestID;

    try {
        //Desempacotar Argumentos 
        String cpf = gson.fromJson(req.arguments, String.class);
        
        //Executa busca por passagens 
        List<Passagem> encontradas = new ArrayList<>();
        for (Passagem p : passagensVendidas) {
            if (p.getPassageiro().getCpf().equals(cpf)) {
                encontradas.add(p);
            }
        }

        //Empacotar Resultado (a lista de passagens)
        rep.result = gson.toJson(encontradas);

    } catch (Exception e) {
        e.printStackTrace();
        rep.result = gson.toJson(new ArrayList<Passagem>()); 
    }

    //Retornar JSON da Resposta
    return gson.toJson(rep);
}

@Override
public String cancelarPassagem(String jsonRequest) throws RemoteException {
    RequestMessage req = gson.fromJson(jsonRequest, RequestMessage.class);
    ReplyMessage rep = new ReplyMessage();
    rep.requestID = req.requestID;
    boolean sucesso = false;

    try {
        //Desempacotar Argumentos (ID da Passagem)
        String idPassagem = gson.fromJson(req.arguments, String.class);

        //Executar o Cancelamento da Passagem
        Iterator<Passagem> iterator = passagensVendidas.iterator();
        while (iterator.hasNext()) {
            Passagem p = iterator.next();
            if (p.getId().equals(idPassagem)) {
                iterator.remove(); // Remove a passagem da lista          
                sucesso = true;
                break;
            }
        }

        //Empacotar Resultado (boolean)
        rep.result = gson.toJson(sucesso);

    } catch (Exception e) {
        e.printStackTrace();
        rep.result = gson.toJson(false);
    }


    return gson.toJson(rep);
}

@Override
public String listarViagens(String jsonRequest) throws RemoteException {
    RequestMessage req = gson.fromJson(jsonRequest, RequestMessage.class);
    ReplyMessage rep = new ReplyMessage();
    rep.requestID = req.requestID;
    
    //Apenas serializar a lista de viagens
    rep.result = gson.toJson(viagensDisponiveis);
    
    return gson.toJson(rep);
}
private synchronized String logicaDeCompra(Passagem passagemParaComprar) {
    System.err.println("\n[SERVIDOR] Recebida solicitação de compra...");
    
    if (passagemParaComprar == null || passagemParaComprar.getViagem() == null) {
        System.err.println("[SERVIDOR] ERRO: Objeto de passagem ou viagem nulo.");
        return null; 
    }
    
    String idViagemCliente = passagemParaComprar.getViagem().getId();
    System.err.println("[SERVIDOR] Procurando viagem com ID: " + idViagemCliente);

    Viagem viagemReal = null;
    for (Viagem v : viagensDisponiveis) {
        if (v.getId().equals(idViagemCliente)) {
            viagemReal = v;
            break;
        }
    }

    if (viagemReal == null) {
        System.err.println("[SERVIDOR] FALHA: Viagem com ID '" + idViagemCliente + "' não encontrada.");
        return null; 
    }

    System.err.println("[SERVIDOR] Viagem encontrada: " + viagemReal.getId());
    System.err.println("[SERVIDOR] Capacidade: " + viagemReal.getCapacidade());
    System.err.println("[SERVIDOR] Vagas ocupadas: " + viagemReal.getPassagens().size());
    
    if (viagemReal.estaLotada()) {
        System.err.println("[SERVIDOR] FALHA: Viagem está lotada!");
        return null; 
    }

    Passageiro passageiroInfo = passagemParaComprar.getPassageiro();
    String idUnico = "TICKET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    
    Passagem novaPassagemServidor = new Passagem(passageiroInfo, viagemReal);
    novaPassagemServidor.setId(idUnico);

    passagensVendidas.add(novaPassagemServidor);
    viagemReal.adicionarPassagem(novaPassagemServidor); 

    System.err.println("[SERVIDOR] SUCESSO: Compra aprovada. ID: " + idUnico);
    return idUnico; 
}

    public static void main(String[] args) {
            try {
                java.rmi.registry.LocateRegistry.createRegistry(1099);
                ServicoPassagensImpl servico = new ServicoPassagensImpl(); 
                
                Naming.rebind("rmi://localhost/ServicoPassagens", servico);
                System.out.println("Servidor de Passagens pronto.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}