package br.com.veiculos.fabrica;

import br.com.veiculos.comum.EsteiraCircular;
import br.com.veiculos.comum.Veiculo;

import java.util.concurrent.Semaphore;

public class Estacao {
    private final int id;
    private final Semaphore estoqueGlobal;
    private final EsteiraCircular<Veiculo> esteiraSaidaFabrica;

    // Arrays internos
    private final Ferramenta[] ferramentas;
    private final Funcionario[] funcionarios;
    private final Thread[] threads;

    /**
     * Construtor da estação de produção.
     *
     * @param id                    identificador da estação (1 a 4)
     * @param estoqueGlobal         semáforo que controla o estoque global
     * @param esteiraSaidaFabrica   esteira circular de saída da fábrica
     */
    public Estacao(int id, Semaphore estoqueGlobal, EsteiraCircular<Veiculo> esteiraSaidaFabrica) {
        this.id = id;
        this.estoqueGlobal = estoqueGlobal;
        this.esteiraSaidaFabrica = esteiraSaidaFabrica;

        // Cria 5 ferramentas (dispostas em círculo)
        this.ferramentas = new Ferramenta[5];
        for (int i = 0; i < 5; i++) {
            ferramentas[i] = new Ferramenta();
        }

        // Cria 5 funcionários
        // Cada funcionário i usa ferramenta[i] como esquerda e ferramenta[(i+1)%5] como direita
        this.funcionarios = new Funcionario[5];
        for (int i = 0; i < 5; i++) {
            Ferramenta ferramentaEsquerda = ferramentas[i];
            Ferramenta ferramentaDireita = ferramentas[(i + 1) % 5];

            funcionarios[i] = new Funcionario(
                    i,                          // id do funcionário (0 a 4)
                    id,                         // id da estação
                    ferramentaEsquerda,
                    ferramentaDireita,
                    estoqueGlobal,
                    esteiraSaidaFabrica
            );
        }

        // Array de threads para gerenciamento
        this.threads = new Thread[5];
    }

    /**
     * Inicia as 5 threads dos funcionários.
     * Não bloqueia - retorna imediatamente após iniciar as threads.
     */
    public void iniciar() {
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(funcionarios[i], "Estacao-" + id + "-Funcionario-" + i);
            threads[i].start();
        }
    }

    /**
     * Retorna o estado atual da estação.
     * Placeholder para o monitor usar futuramente.
     *
     * @return array com os IDs dos 5 funcionários
     */
    public int[] getEstado() {
        int[] estado = new int[5];
        for (int i = 0; i < 5; i++) {
            estado[i] = i; // ID de cada funcionário
        }
        return estado;
    }
}
