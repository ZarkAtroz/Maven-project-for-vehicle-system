package br.com.veiculos.fabrica;

import br.com.veiculos.comum.EsteiraCircular;
import br.com.veiculos.comum.Veiculo;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Funcionario implements Runnable {
    private final int id;
    private final int idEstacao;
    private final Ferramenta ferramentaEsquerda;
    private final Ferramenta ferramentaDireita;
    private final Semaphore estoqueGlobal;
    private final EsteiraCircular<Veiculo> esteiraSaida;
    private final Random random;

    /**
     * Construtor do funcionário.
     *
     * @param id                  identificador do funcionário (0 a 4)
     * @param idEstacao           identificador da estação
     * @param ferramentaEsquerda  ferramenta compartilhada com vizinho esquerdo
     * @param ferramentaDireita   ferramenta compartilhada com vizinho direito
     * @param estoqueGlobal       semáforo que controla estoque global
     * @param esteiraSaida        esteira circular para inserir veículos produzidos
     */
    public Funcionario(int id, int idEstacao,
                       Ferramenta ferramentaEsquerda, Ferramenta ferramentaDireita,
                       Semaphore estoqueGlobal, EsteiraCircular<Veiculo> esteiraSaida) {
        this.id = id;
        this.idEstacao = idEstacao;
        this.ferramentaEsquerda = ferramentaEsquerda;
        this.ferramentaDireita = ferramentaDireita;
        this.estoqueGlobal = estoqueGlobal;
        this.esteiraSaida = esteiraSaida;
        this.random = new Random();
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 1. Aguarda estoque global disponível
                estoqueGlobal.acquire();

                // 2. Adquire ferramentas SEM DEADLOCK
                // Funcionários 0-3: esquerda → direita
                // Funcionário 4 (assimétrico): direita → esquerda
                if (id == 4) {
                    ferramentaDireita.pegar();
                    ferramentaEsquerda.pegar();
                } else {
                    ferramentaEsquerda.pegar();
                    ferramentaDireita.pegar();
                }

                // 3. Simula produção do veículo (500ms a 1500ms)
                int tempoProducao = 500 + random.nextInt(1001); // 500 + [0, 1000]
                Thread.sleep(tempoProducao);

                // 4. Cria novo veículo
                Veiculo veiculo = new Veiculo(idEstacao, id);

                // 5. Larga ferramentas (ordem inversa da aquisição)
                if (id == 4) {
                    ferramentaEsquerda.largar();
                    ferramentaDireita.largar();
                } else {
                    ferramentaDireita.largar();
                    ferramentaEsquerda.largar();
                }

                // 6. Insere veículo na esteira de saída
                int pos = esteiraSaida.inserir(veiculo);

                // 7. Atualiza posição do veículo na esteira
                veiculo.setPosEsteiraFabrica(pos);

                // 8. Imprime confirmação
                System.out.println("[PROD] " + veiculo.toString());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
