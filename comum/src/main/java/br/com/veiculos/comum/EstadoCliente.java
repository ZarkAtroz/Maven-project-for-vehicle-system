package br.com.veiculos.comum;

import java.io.Serializable;




public class EstadoCliente implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final int veiculosNaGaragem;
    private final int capacidadeGaragem;
    private final int totalComprado;

    







    public EstadoCliente(int id, int veiculosNaGaragem, int capacidadeGaragem, int totalComprado) {
        this.id = id;
        this.veiculosNaGaragem = veiculosNaGaragem;
        this.capacidadeGaragem = capacidadeGaragem;
        this.totalComprado = totalComprado;
    }

    public int getId() {
        return id;
    }

    public int getVeiculosNaGaragem() {
        return veiculosNaGaragem;
    }

    public int getCapacidadeGaragem() {
        return capacidadeGaragem;
    }

    public int getTotalComprado() {
        return totalComprado;
    }
}
