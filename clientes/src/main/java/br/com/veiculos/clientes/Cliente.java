package br.com.veiculos.clientes;

import br.com.veiculos.comum.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;




public class Cliente implements Runnable {
    private final int id;
    private final String[] hostLojas;
    private final int capacidadeGaragem;
    private final LogService log;

    private final EsteiraCircular<Veiculo> garagem;
    private final LojaRemota[] lojas;
    private final Random random;
    private final AtomicInteger totalComprado;

    







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
            
            for (int i = 0; i < 3; i++) {
                Registry r = LocateRegistry.getRegistry(hostLojas[i], 1100 + i);
                lojas[i] = (LojaRemota) r.lookup("Loja" + (i + 1));
            }

            
            while (true) {
                
                int idx = random.nextInt(3);

                
                Veiculo v = lojas[idx].comprarVeiculo(id);

                
                garagem.inserir(v);

                
                totalComprado.incrementAndGet();

                
                log.vendaParaCliente(v, id);

                
                System.out.println("[CLIENTE-" + id + "] comprou: " + v.toString());

                
                int tempoEspera = 200 + random.nextInt(601); 
                Thread.sleep(tempoEspera);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("[CLIENTE-" + id + "] Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    




    public EstadoCliente getEstado() {
        return new EstadoCliente(
                id,
                garagem.tamanhoAtual(),
                capacidadeGaragem,
                totalComprado.get()
        );
    }
}
