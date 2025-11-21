package com.example.pojo;

public class Rota {
    private String cidadeOrigem;     
    private String cidadeDestino;    
    private double distanciaKm;      
    private int duracaoMinutos;      
    
    // Construtor
    public Rota(String cidadeOrigem, String cidadeDestino, 
                double distanciaKm, int duracaoMinutos) {
        this.cidadeOrigem = cidadeOrigem;
        this.cidadeDestino = cidadeDestino;
        this.distanciaKm = distanciaKm;
        this.duracaoMinutos = duracaoMinutos;
    }
    
    
    // Getters
    public String getOrigem() {
        return this.cidadeOrigem;
    }

    public String getDestino() {
        return this.cidadeDestino;
    }
    public String getCidadeOrigem() { return cidadeOrigem; }
    public String getCidadeDestino() { return cidadeDestino; }
    public double getDistanciaKm() { return distanciaKm; }
    public int getDuracaoMinutos() { return duracaoMinutos; }
    
    // Setters
    public void setDistanciaKm(double distanciaKm) { this.distanciaKm = distanciaKm; }
    public void setDuracaoMinutos(int duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }
}
