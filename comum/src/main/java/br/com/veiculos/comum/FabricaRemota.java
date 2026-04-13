package br.com.veiculos.comum;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface remota para a fábrica de veículos.
 */
public interface FabricaRemota extends Remote {
    /**
     * Solicita um veículo da fábrica.
     *
     * @param idLoja identificador da loja solicitante
     * @return o veículo produzido
     * @throws RemoteException em caso de erro de comunicação RMI
     */
    Veiculo solicitarVeiculo(int idLoja) throws RemoteException;

    /**
     * Obtém o estado atual da fábrica.
     *
     * @return estado da fábrica
     * @throws RemoteException em caso de erro de comunicação RMI
     */
    EstadoFabrica getEstado() throws RemoteException;
}
