package com.example.pojo;

import java.util.ArrayList;
import java.util.List;

public class Viagem {
    private String id;
    private Rota rota;
    private String dataHora;
    private int capacidade;
    
    private transient List<Passagem> passagens; 

    // Construtor
    public Viagem(String id, Rota rota, String dataHora, int capacidade) {
        this.id = id;
        this.rota = rota;
        this.dataHora = dataHora;
        this.capacidade = capacidade;
        this.passagens = new ArrayList<>(); 
    }
    
    // Getters 
    public String getId() { return id; }
    public Rota getRota() { return rota; }
    public int getCapacidade() { return capacidade; }
    public List<Passagem> getPassagens() { return passagens; }
    
    // Método de negócio 
    public boolean estaLotada() {
        return passagens.size() >= capacidade;
    }
    
    public void adicionarPassagem(Passagem p) {
        this.passagens.add(p);
    }
}