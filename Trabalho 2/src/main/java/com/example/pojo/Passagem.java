package com.example.pojo;

public class Passagem {
    private String id; // ID único da passagem (ex: TICKET-001)
    private Passageiro passageiro; 
    private Viagem viagem;         

    // Construtor
    public Passagem(Passageiro passageiro, Viagem viagem) {
        this.passageiro = passageiro;
        this.viagem = viagem;
    }

    public Passageiro getPassageiro() {
        return this.passageiro;
    }
    
    // Outros getters e setters
    public Viagem getViagem() { return viagem; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}