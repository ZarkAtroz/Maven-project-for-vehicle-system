package br.com.veiculos.loja;

import br.com.veiculos.comum.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementação da loja de veículos usando RMI.
 */
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

    /**
     * Construtor da loja.
     *
     * @param id                 identificador da loja (1, 2 ou 3)
     * @param hostFabrica        endereço do host da fábrica
     * @param capacidadeEsteira  capacidade da esteira local
     * @throws RemoteException em caso de erro RMI
     */
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

    /**
     * Vende um veículo para um cliente.
     * Bloqueia se não houver veículos disponíveis na esteira local.
     *
     * @param idCliente identificador do cliente comprador
     * @return o veículo vendido
     * @throws RemoteException em caso de erro de comunicação RMI
     */
    @Override
    public Veiculo comprarVeiculo(int idCliente) throws RemoteException {
        try {
            // 1. Retira veículo da esteira local (bloqueia se vazia)
            Veiculo v = esteiraLocal.retirar();

            // 2. Incrementa contador de vendidos
            totalVendidos.incrementAndGet();

            // 3. Registra no log
            log.vendaParaCliente(v, idCliente);

            // 4. Retorna o veículo
            return v;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RemoteException("Interrompido ao comprar veículo", e);
        }
    }

    /**
     * Obtém o estado atual da loja.
     *
     * @return estado da loja
     * @throws RemoteException em caso de erro de comunicação RMI
     */
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

    /**
     * Inicia a loja: conecta à fábrica, inicia abastecimento e registra no RMI.
     *
     * @throws RemoteException em caso de erro RMI
     */
    public void iniciar() throws RemoteException {
        try {
            // 1. Conecta à fábrica
            Registry r = LocateRegistry.getRegistry(hostFabrica, 1099);
            fabrica = (FabricaRemota) r.lookup("Fabrica");

            // 2. Inicia thread de abastecimento
            Thread threadAbastecimento = new Thread(() -> {
                while (true) {
                    try {
                        // Solicita veículo da fábrica (bloqueia se não houver)
                        Veiculo v = fabrica.solicitarVeiculo(id);

                        // Insere na esteira local (bloqueia se estiver cheia)
                        int pos = esteiraLocal.inserir(v);

                        // Atualiza posição na esteira da loja
                        v.setPosEsteiraLoja(pos);

                        // Incrementa contador de recebidos
                        totalRecebidos.incrementAndGet();

                        // Registra no log
                        log.recebimentoNaLoja(v, id);

                    } catch (RemoteException e) {
                        System.err.println("Erro ao solicitar veículo da fábrica: " + e.getMessage());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }, "Loja-" + id + "-Abastecimento");

            threadAbastecimento.start();

            // 3. Registra a si mesmo no RMI registry
            Registry registry = LocateRegistry.createRegistry(1099 + id);
            registry.bind("Loja" + id, this);

            // 4. Imprime confirmação
            System.out.println("✓ Loja " + id + " online — porta " + (1099 + id));

        } catch (Exception e) {
            throw new RemoteException("Falha ao iniciar loja " + id, e);
        }
    }
}
