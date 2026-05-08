package br.com.veiculos.comum;

import java.util.concurrent.Semaphore;

public class EsteiraCircular<T> {
    private final T[] array;
    private final int capacidade;
    private int cabeca;
    private int cauda;

    
    private final Semaphore mutex;   
    private final Semaphore vazios;  
    private final Semaphore cheios;  

    




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

    







    public int inserir(T item) throws InterruptedException {
        vazios.acquire();      
        mutex.acquire();       

        int posicao = cabeca;
        array[cabeca] = item;
        cabeca = (cabeca + 1) % capacidade;

        mutex.release();       
        cheios.release();      

        return posicao;
    }

    






    public T retirar() throws InterruptedException {
        cheios.acquire();      
        mutex.acquire();       

        T item = array[cauda];
        array[cauda] = null;   
        cauda = (cauda + 1) % capacidade;

        mutex.release();       
        vazios.release();      

        return item;
    }

    




    public int tamanhoAtual() {
        return cheios.availablePermits();
    }

    





    @SuppressWarnings("unchecked")
    public T[] snapshot() {
        T[] copia = (T[]) new Object[capacidade];
        System.arraycopy(array, 0, copia, 0, capacidade);
        return copia;
    }

    




    public int getCapacidade() {
        return capacidade;
    }
}
