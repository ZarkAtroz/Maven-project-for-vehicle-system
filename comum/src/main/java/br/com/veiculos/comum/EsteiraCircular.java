package br.com.veiculos.comum;

import java.util.concurrent.Semaphore;

public class EsteiraCircular<T> {
    private final T[] array;
    private final int capacidade;
    private int cabeca;
    private int cauda;

    // Três semáforos - únicos mecanismos de sincronização
    private final Semaphore mutex;   // Acesso exclusivo ao array e ponteiros
    private final Semaphore vazios;  // Conta posições livres
    private final Semaphore cheios;  // Conta itens disponíveis

    /**
     * Construtor que cria uma esteira circular com capacidade configurável.
     *
     * @param capacidade tamanho máximo da esteira
     */
    @SuppressWarnings("unchecked")
    public EsteiraCircular(int capacidade) {
        this.capacidade = capacidade;
        this.array = (T[]) new Object[capacidade];
        this.cabeca = 0;
        this.cauda = 0;

        this.mutex = new Semaphore(1);
        this.vazios = new Semaphore(capacidade);
        this.cheios = new Semaphore(0);
    }

    /**
     * Insere um item na esteira circular.
     * Bloqueia se a esteira estiver cheia.
     *
     * @param item o item a ser inserido
     * @return a posição onde o item foi inserido
     * @throws InterruptedException se a thread for interrompida enquanto aguarda
     */
    public int inserir(T item) throws InterruptedException {
        vazios.acquire();      // Aguarda até ter espaço disponível
        mutex.acquire();       // Garante acesso exclusivo

        int posicao = cabeca;
        array[cabeca] = item;
        cabeca = (cabeca + 1) % capacidade;

        mutex.release();       // Libera acesso exclusivo
        cheios.release();      // Sinaliza que há um item disponível

        return posicao;
    }

    /**
     * Retira um item da esteira circular.
     * Bloqueia se a esteira estiver vazia.
     *
     * @return o item retirado
     * @throws InterruptedException se a thread for interrompida enquanto aguarda
     */
    public T retirar() throws InterruptedException {
        cheios.acquire();      // Aguarda até ter item disponível
        mutex.acquire();       // Garante acesso exclusivo

        T item = array[cauda];
        array[cauda] = null;   // Limpa a referência
        cauda = (cauda + 1) % capacidade;

        mutex.release();       // Libera acesso exclusivo
        vazios.release();      // Sinaliza que há espaço disponível

        return item;
    }

    /**
     * Retorna o tamanho atual da esteira (quantidade de itens).
     *
     * @return número de itens atualmente na esteira
     */
    public int tamanhoAtual() {
        return cheios.availablePermits();
    }

    /**
     * Retorna uma cópia do array interno para leitura pelo monitor.
     * Não bloqueia - retorna o estado atual.
     *
     * @return cópia do array interno
     */
    @SuppressWarnings("unchecked")
    public T[] snapshot() {
        T[] copia = (T[]) new Object[capacidade];
        System.arraycopy(array, 0, copia, 0, capacidade);
        return copia;
    }

    /**
     * Retorna a capacidade máxima da esteira.
     *
     * @return capacidade máxima
     */
    public int getCapacidade() {
        return capacidade;
    }
}
