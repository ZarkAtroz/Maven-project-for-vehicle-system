package br.com.veiculos.comum;

import java.io.Serializable;

/**
 * Estado de uma loja de veículos.
 */
public class EstadoLoja implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final int veiculosNaEsteira;
    private final int capacidadeEsteira;
    private final int totalRecebidos;
    private final int totalVendidos;

    /**
     * Construtor completo.
     *
     * @param id                  identificador da loja
     * @param veiculosNaEsteira   quantidade atual de veículos na esteira
     * @param capacidadeEsteira   capacidade máxima da esteira
     * @param totalRecebidos      total de veículos recebidos da fábrica
     * @param totalVendidos       total de veículos vendidos
     */
    public EstadoLoja(int id, int veiculosNaEsteira, int capacidadeEsteira,
                      int totalRecebidos, int totalVendidos) {
        this.id = id;
        this.veiculosNaEsteira = veiculosNaEsteira;
        this.capacidadeEsteira = capacidadeEsteira;
        this.totalRecebidos = totalRecebidos;
        this.totalVendidos = totalVendidos;
    }

    public int getId() {
        return id;
    }

    public int getVeiculosNaEsteira() {
        return veiculosNaEsteira;
    }

    public int getCapacidadeEsteira() {
        return capacidadeEsteira;
    }

    public int getTotalRecebidos() {
        return totalRecebidos;
    }

    public int getTotalVendidos() {
        return totalVendidos;
    }
}
