package com.example.pojo; 

public class Administrador extends Pessoa {

    // Campo específico do Administrador
    private String nivelAcesso;

    public Administrador(String nome, String cpf, String dataNascimento, String nivelAcesso) {
        // Chama o construtor da classe pai (Pessoa)
        super(nome, cpf, dataNascimento);
        this.nivelAcesso = nivelAcesso;
    }

    public String getNivelAcesso() {
        return nivelAcesso;
    }
}