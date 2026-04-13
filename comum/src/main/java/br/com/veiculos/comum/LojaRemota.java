package br.com.veiculos.comum;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface remota para a loja de veículos.
 */
public interface LojaRemota extends Remote {
    /**
     * Vende um veículo para um cliente.
     *
     * @param idCliente identificador do cliente comprador
     * @return o veículo vendido
     * @throws RemoteException em caso de erro de comunicação RMI
     */
    Veiculo comprarVeiculo(int idCliente) throws RemoteException;

    /**
     * Obtém o estado atual da loja.
     *
     * @return estado da loja
     * @throws RemoteException em caso de erro de comunicação RMI
     */
    EstadoLoja getEstado() throws RemoteException;
}
