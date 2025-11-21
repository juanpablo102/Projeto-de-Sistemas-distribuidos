package com.example;
import com.example.pojo.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;             

import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ServicoPassagensProxy {
    
    private IServicoPassagens servicoRemoto;
    private Gson gson = new Gson();
    private AtomicInteger requestCounter = new AtomicInteger(0); // Para IDs únicos

    public ServicoPassagensProxy() {
        try {
            servicoRemoto = (IServicoPassagens) Naming.lookup("rmi://localhost/ServicoPassagens");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String comprarPassagem(Passagem p) {
    try {
        String argsJson = gson.toJson(p);

        RequestMessage req = new RequestMessage();
        req.requestID = requestCounter.incrementAndGet();
        req.objectReference = "ServicoPassagens";
        req.methodID = "comprarPassagem";
        req.arguments = argsJson;

        String jsonRequest = gson.toJson(req);
        String jsonReply = servicoRemoto.comprarPassagem(jsonRequest);

        ReplyMessage rep = gson.fromJson(jsonReply, ReplyMessage.class);

        //Faz o parse de uma String
        String idComprado = gson.fromJson(rep.result, String.class);
        return idComprado; // Retorna o ID (ou null se a compra falhou)

    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
public List<Passagem> pesquisarPassagem(String cpf) {
    try {
        String argsJson = gson.toJson(cpf);

        RequestMessage req = new RequestMessage();
        req.requestID = requestCounter.incrementAndGet();
        req.objectReference = "ServicoPassagens";
        req.methodID = "pesquisarPassagem";
        req.arguments = argsJson;

        String jsonRequest = gson.toJson(req);
        String jsonReply = servicoRemoto.pesquisarPassagem(jsonRequest);

        ReplyMessage rep = gson.fromJson(jsonReply, ReplyMessage.class);

        Type tipoListaPassagens = new TypeToken<ArrayList<Passagem>>(){}.getType();
        List<Passagem> resultado = gson.fromJson(rep.result, tipoListaPassagens);
        
        return resultado;

    } catch (Exception e) {
        e.printStackTrace();
        return new ArrayList<Passagem>(); // Retorna lista vazia em caso de erro
    }
}

public boolean cancelarPassagem(String idPassagem) {
    try {
        String argsJson = gson.toJson(idPassagem);

        RequestMessage req = new RequestMessage();
        req.requestID = requestCounter.incrementAndGet();
        req.objectReference = "ServicoPassagens";
        req.methodID = "cancelarPassagem";
        req.arguments = argsJson;

        String jsonRequest = gson.toJson(req);
        String jsonReply = servicoRemoto.cancelarPassagem(jsonRequest);

        ReplyMessage rep = gson.fromJson(jsonReply, ReplyMessage.class);

        boolean sucesso = gson.fromJson(rep.result, Boolean.class);
        return sucesso;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public List<Viagem> listarViagens() {
    try {
        String argsJson = gson.toJson(null);

        RequestMessage req = new RequestMessage();
        req.requestID = requestCounter.incrementAndGet();
        req.objectReference = "ServicoPassagens";
        req.methodID = "listarViagens";
        req.arguments = argsJson;

        String jsonRequest = gson.toJson(req);
        String jsonReply = servicoRemoto.listarViagens(jsonRequest);

        ReplyMessage rep = gson.fromJson(jsonReply, ReplyMessage.class);

        Type tipoListaViagens = new TypeToken<ArrayList<Viagem>>(){}.getType();
        List<Viagem> resultado = gson.fromJson(rep.result, tipoListaViagens);
        
        return resultado;

    } catch (Exception e) {
        e.printStackTrace();
        return new ArrayList<Viagem>();
    }
}
    
}