package br.com.veiculos.comum;

import java.io.Serializable;

/**
 * Estado de um funcionário da fábrica.
 */
public class EstadoFuncionario implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Status possíveis de um funcionário.
     */
    public enum Status {
        PRODUZINDO,
        AGUARDANDO,
        BLOQUEADO
    }

    private final int id;
    private final Status status;
    private final int veiculosProduzidos;

    /**
     * Construtor completo.
     *
     * @param id                  identificador do funcionário
     * @param status              status atual do funcionário
     * @param veiculosProduzidos  total de veículos produzidos
     */
    public EstadoFuncionario(int id, Status status, int veiculosProduzidos) {
        this.id = id;
        this.status = status;
        this.veiculosProduzidos = veiculosProduzidos;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public int getVeiculosProduzidos() {
        return veiculosProduzidos;
    }
}
