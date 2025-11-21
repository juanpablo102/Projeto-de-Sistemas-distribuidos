package com.example.pojo; 

public abstract class Pessoa {
    protected String nome;
    protected String cpf;
    protected String dataNascimento;

    // Construtor
    public Pessoa(String nome, String cpf, String dataNascimento) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
    }

    // Getters
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getDataNascimento() { return dataNascimento; }
    
    // Setters 
    public void setNome(String nome) { this.nome = nome; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }
}
