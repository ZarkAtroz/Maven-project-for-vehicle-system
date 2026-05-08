package br.com.veiculos.comum;

import java.rmi.Remote;
import java.rmi.RemoteException;




public interface FabricaRemota extends Remote {
    






    Veiculo solicitarVeiculo(int idLoja) throws RemoteException;

    





    EstadoFabrica getEstado() throws RemoteException;
}
