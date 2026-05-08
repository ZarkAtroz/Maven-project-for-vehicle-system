package br.com.veiculos.loja;

import br.com.veiculos.comum.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicInteger;




public class LojaImpl extends UnicastRemoteObject implements LojaRemota {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String hostFabrica;
    private final int capacidadeEsteira;

    private final EsteiraCircular<Veiculo> esteiraLocal;
    private FabricaRemota fabrica;
    private final LogService log;
    private final AtomicInteger totalRecebidos;
    private final AtomicInteger totalVendidos;

    







    public LojaImpl(int id, String hostFabrica, int capacidadeEsteira) throws RemoteException {
        super();

        this.id = id;
        this.hostFabrica = hostFabrica;
        this.capacidadeEsteira = capacidadeEsteira;

        this.esteiraLocal = new EsteiraCircular<>(capacidadeEsteira);
        this.log = new LogService("loja_" + id + ".log");
        this.totalRecebidos = new AtomicInteger(0);
        this.totalVendidos = new AtomicInteger(0);
    }

    







    @Override
    public Veiculo comprarVeiculo(int idCliente) throws RemoteException {
        try {
            
            Veiculo v = esteiraLocal.retirar();

            
            int vendidos = totalVendidos.incrementAndGet();

            
            log.vendaParaCliente(v, idCliente);

            
            System.out.println("[LOJA-" + id + "] vendeu para Cliente-" + idCliente + ": " + v);

            
            if (vendidos % 5 == 0) {
                System.out.println("[LOJA-" + id + "] resumo: recebidos=" + totalRecebidos.get()
                        + " | vendidos=" + vendidos
                        + " | em estoque=" + esteiraLocal.tamanhoAtual());
            }

            
            return v;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RemoteException("Interrompido ao comprar veículo", e);
        }
    }

    





    @Override
    public EstadoLoja getEstado() throws RemoteException {
        return new EstadoLoja(
                id,
                esteiraLocal.tamanhoAtual(),
                capacidadeEsteira,
                totalRecebidos.get(),
                totalVendidos.get()
        );
    }

    




    public void iniciar() throws RemoteException {
        try {
            
            Registry r = LocateRegistry.getRegistry(hostFabrica, 1099);
            fabrica = (FabricaRemota) r.lookup("Fabrica");

            
            Thread threadAbastecimento = new Thread(() -> {
                while (true) {
                    try {
                        
                        Veiculo v = fabrica.solicitarVeiculo(id);

                        
                        int pos = esteiraLocal.inserir(v);

                        
                        v.setPosEsteiraLoja(pos);

                        
                        totalRecebidos.incrementAndGet();

                        
                        log.recebimentoNaLoja(v, id);

                        
                        System.out.println("[LOJA-" + id + "] recebeu: " + v + " | PosLoja=" + pos);

                    } catch (RemoteException e) {
                        System.err.println("Erro ao solicitar veículo da fábrica: " + e.getMessage());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }, "Loja-" + id + "-Abastecimento");

            threadAbastecimento.start();

            
            Registry registry = LocateRegistry.createRegistry(1099 + id);
            registry.bind("Loja" + id, this);

            
            System.out.println("✓ Loja " + id + " online — porta " + (1099 + id));

        } catch (Exception e) {
            throw new RemoteException("Falha ao iniciar loja " + id, e);
        }
    }
}
