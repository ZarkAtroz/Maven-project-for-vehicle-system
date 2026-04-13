package br.com.veiculos.clientes;

import br.com.veiculos.comum.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Cliente que compra veículos de lojas via RMI.
 */
public class Cliente implements Runnable {
    private final int id;
    private final String[] hostLojas;
    private final int capacidadeGaragem;
    private final LogService log;

    private final EsteiraCircular<Veiculo> garagem;
    private final LojaRemota[] lojas;
    private final Random random;
    private final AtomicInteger totalComprado;

    /**
     * Construtor do cliente.
     *
     * @param id                identificador do cliente (1 a 20)
     * @param hostLojas         array com 3 hosts das lojas
     * @param capacidadeGaragem capacidade máxima da garagem
     * @param log               serviço de log compartilhado
     */
    public Cliente(int id, String[] hostLojas, int capacidadeGaragem, LogService log) {
        this.id = id;
        this.hostLojas = hostLojas;
        this.capacidadeGaragem = capacidadeGaragem;
        this.log = log;

        this.garagem = new EsteiraCircular<>(capacidadeGaragem);
        this.lojas = new LojaRemota[3];
        this.random = new Random();
        this.totalComprado = new AtomicInteger(0);
    }

    @Override
    public void run() {
        try {
            // 1. Conecta nas 3 lojas via RMI
            for (int i = 0; i < 3; i++) {
                Registry r = LocateRegistry.getRegistry(hostLojas[i], 1100 + i);
                lojas[i] = (LojaRemota) r.lookup("Loja" + (i + 1));
            }

            // 2. Loop infinito de compras
            while (true) {
                // a. Escolhe loja aleatória
                int idx = random.nextInt(3);

                // b. Compra veículo da loja (bloqueia se loja vazia)
                Veiculo v = lojas[idx].comprarVeiculo(id);

                // c. Insere na garagem (bloqueia se garagem cheia)
                garagem.inserir(v);

                // d. Incrementa contador
                totalComprado.incrementAndGet();

                // e. Registra no log
                log.vendaParaCliente(v, id);

                // f. Imprime confirmação
                System.out.println("[CLIENTE-" + id + "] comprou: " + v.toString());

                // g. Aguarda entre 200ms e 800ms
                int tempoEspera = 200 + random.nextInt(601); // 200 + [0, 600]
                Thread.sleep(tempoEspera);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("[CLIENTE-" + id + "] Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retorna o estado atual do cliente.
     *
     * @return estado do cliente
     */
    public EstadoCliente getEstado() {
        return new EstadoCliente(
                id,
                garagem.tamanhoAtual(),
                capacidadeGaragem,
                totalComprado.get()
        );
    }
}
