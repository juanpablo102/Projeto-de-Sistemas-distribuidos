package com.example.pojo; 

public class Passageiro extends Pessoa {

    // Os campos 'nome', 'cpf' e 'dataNascimento' sao herdados
    // e nao precisam ser declarados novamente

    public Passageiro(String nome, String cpf, String dataNascimento) {
        // 'super' chama o construtor de Pessoa
        super(nome, cpf, dataNascimento);
    }
}