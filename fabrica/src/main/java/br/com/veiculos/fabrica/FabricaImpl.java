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

/**
 * Implementação da fábrica de veículos usando RMI.
 */
public class FabricaImpl extends UnicastRemoteObject implements FabricaRemota {
    private static final long serialVersionUID = 1L;

    private final Semaphore estoqueGlobal;
    private final EsteiraCircular<Veiculo> esteiraSaida;
    private final LogService log;
    private final List<Estacao> estacoes;
    private final AtomicInteger totalProduzidos;

    /**
     * Construtor da fábrica.
     *
     * @throws RemoteException em caso de erro RMI
     */
    public FabricaImpl() throws RemoteException {
        super();

        this.estoqueGlobal = new Semaphore(500);
        this.esteiraSaida = new EsteiraCircular<>(40);
        this.log = new LogService("fabrica.log");
        this.estacoes = new ArrayList<>();
        this.totalProduzidos = new AtomicInteger(0);

        // Cria 4 estações (ids 1 a 4)
        for (int i = 1; i <= 4; i++) {
            estacoes.add(new Estacao(i, estoqueGlobal, esteiraSaida));
        }
    }

    /**
     * Solicita um veículo da fábrica para uma loja.
     * Bloqueia se não houver veículos disponíveis na esteira.
     *
     * @param idLoja identificador da loja solicitante
     * @return o veículo produzido
     * @throws RemoteException em caso de erro de comunicação RMI
     */
    @Override
    public Veiculo solicitarVeiculo(int idLoja) throws RemoteException {
        try {
            // 1. Retira veículo da esteira (bloqueia se vazia)
            Veiculo v = esteiraSaida.retirar();

            // 2. Seta o ID da loja
            v.setIdLoja(idLoja);

            // 3. Registra no log
            log.vendaParaLoja(v);

            // 4. Retorna o veículo
            return v;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RemoteException("Interrompido ao solicitar veículo", e);
        }
    }

    /**
     * Obtém o estado atual da fábrica.
     *
     * @return estado da fábrica
     * @throws RemoteException em caso de erro de comunicação RMI
     */
    @Override
    public EstadoFabrica getEstado() throws RemoteException {
        // Coleta estados das estações (placeholder)
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
                estoqueGlobal.availablePermits(),      // estoque disponível
                totalProduzidos.get(),                  // total produzidos
                esteiraSaida.tamanhoAtual(),            // veículos na esteira
                esteiraSaida.snapshot(),                // snapshot da esteira
                estadosEstacoes,                        // estados das estações
                log.getLogs()                           // últimos logs
        );
    }

    /**
     * Inicia a fábrica: ativa as estações e registra no RMI registry.
     *
     * @throws RemoteException em caso de erro RMI
     */
    public void iniciar() throws RemoteException {
        // Inicia as 4 estações
        for (Estacao estacao : estacoes) {
            estacao.iniciar();
        }

        // Registra no RMI registry
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("Fabrica", this);
            System.out.println("✓ Fábrica online — aguardando conexões...");
        } catch (Exception e) {
            throw new RemoteException("Falha ao registrar fábrica no RMI", e);
        }
    }
}
