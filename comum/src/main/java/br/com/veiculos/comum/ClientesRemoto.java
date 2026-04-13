package br.com.veiculos.comum;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interface remota para o sistema de clientes.
 */
public interface ClientesRemoto extends Remote {
    /**
     * Obtém os estados de todos os clientes.
     *
     * @return lista com os estados de cada cliente
     * @throws RemoteException em caso de erro de comunicação RMI
     */
    List<EstadoCliente> getEstados() throws RemoteException;
}
