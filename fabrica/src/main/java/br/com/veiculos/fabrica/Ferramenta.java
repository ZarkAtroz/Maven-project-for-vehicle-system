package br.com.veiculos.fabrica;

import java.util.concurrent.Semaphore;

public class Ferramenta {
    private final Semaphore semaforo;

    /**
     * Construtor que cria uma ferramenta com semáforo binário.
     */
    public Ferramenta() {
        this.semaforo = new Semaphore(1);
    }

    /**
     * Adquire a ferramenta, bloqueando se estiver em uso.
     *
     * @throws InterruptedException se a thread for interrompida enquanto aguarda
     */
    public void pegar() throws InterruptedException {
        semaforo.acquire();
    }

    /**
     * Libera a ferramenta para outros funcionários.
     */
    public void largar() {
        semaforo.release();
    }
}
