package br.com.veiculos.comum;

import java.io.Serializable;

/**
 * Estado de um cliente.
 */
public class EstadoCliente implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final int veiculosNaGaragem;
    private final int capacidadeGaragem;
    private final int totalComprado;

    /**
     * Construtor completo.
     *
     * @param id                   identificador do cliente
     * @param veiculosNaGaragem    quantidade atual de veículos na garagem
     * @param capacidadeGaragem    capacidade máxima da garagem
     * @param totalComprado        total de veículos comprados
     */
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
