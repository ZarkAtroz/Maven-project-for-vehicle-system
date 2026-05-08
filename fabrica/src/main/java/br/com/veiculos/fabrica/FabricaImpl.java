package br.com.veiculos.fabrica;

import br.com.veiculos.comum.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;




public class FabricaImpl extends UnicastRemoteObject implements FabricaRemota {
    private static final long serialVersionUID = 1L;

    private final Semaphore estoqueGlobal;
    private final EsteiraCircular<Veiculo> esteiraSaida;
    private final LogService log;
    private final List<Estacao> estacoes;
    private final AtomicInteger totalProduzidos;

    




    public FabricaImpl() throws RemoteException {
        super();

        this.estoqueGlobal = new Semaphore(500);
        this.esteiraSaida = new EsteiraCircular<>(40);
        this.log = new LogService("fabrica.log");
        this.estacoes = new ArrayList<>();
        this.totalProduzidos = new AtomicInteger(0);

        
        for (int i = 1; i <= 4; i++) {
            estacoes.add(new Estacao(i, estoqueGlobal, esteiraSaida));
        }
    }

    







    @Override
    public Veiculo solicitarVeiculo(int idLoja) throws RemoteException {
        try {
            
            Veiculo v = esteiraSaida.retirar();

            
            v.setIdLoja(idLoja);

            
            log.vendaParaLoja(v);

            
            return v;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RemoteException("Interrompido ao solicitar veículo", e);
        }
    }

    





    @Override
    public EstadoFabrica getEstado() throws RemoteException {
        
        List<EstadoEstacao> estadosEstacoes = new ArrayList<>();
        for (int i = 0; i < estacoes.size(); i++) {
            int idEstacao = i + 1;
            EstadoEstacao estado = new EstadoEstacao(
                    idEstacao,
                    List.of(),
                    new boolean[5]
            );
            estadosEstacoes.add(estado);
        }

        return new EstadoFabrica(
                estoqueGlobal.availablePermits(),      
                totalProduzidos.get(),                  
                esteiraSaida.tamanhoAtual(),            
                esteiraSaida.snapshot(),                
                estadosEstacoes,                        
                log.getLogs()                           
        );
    }

    




    public void iniciar() throws RemoteException {
        
        for (Estacao estacao : estacoes) {
            estacao.iniciar();
        }

        
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("Fabrica", this);
            System.out.println("✓ Fábrica online — aguardando conexões...");
        } catch (Exception e) {
            throw new RemoteException("Falha ao registrar fábrica no RMI", e);
        }
    }
}
