package br.com.veiculos.comum;

import java.rmi.Remote;
import java.rmi.RemoteException;




public interface LojaRemota extends Remote {
    






    Veiculo comprarVeiculo(int idCliente) throws RemoteException;

    





    EstadoLoja getEstado() throws RemoteException;
}
