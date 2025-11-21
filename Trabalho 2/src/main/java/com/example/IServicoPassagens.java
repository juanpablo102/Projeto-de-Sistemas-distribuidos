package com.example;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IServicoPassagens extends Remote {

    // Os métodos recebem e retornam a mensagem empacotada (JSON)
    String comprarPassagem(String jsonRequest) throws RemoteException;
    
    String pesquisarPassagem(String jsonRequest) throws RemoteException;
    
    String listarViagens(String jsonRequest) throws RemoteException;
    
    String cancelarPassagem(String jsonRequest) throws RemoteException;
}
