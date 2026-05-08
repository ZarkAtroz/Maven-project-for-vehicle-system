package br.com.veiculos.comum;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;




public interface ClientesRemoto extends Remote {
    





    List<EstadoCliente> getEstados() throws RemoteException;
}
