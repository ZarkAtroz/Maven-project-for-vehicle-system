package br.com.veiculos.fabrica;

import java.util.concurrent.Semaphore;

public class Ferramenta {
    private final Semaphore semaforo;

    


    public Ferramenta() {
        this.semaforo = new Semaphore(1);
    }

    




    public void pegar() throws InterruptedException {
        semaforo.acquire();
    }

    


    public void largar() {
        semaforo.release();
    }
}
