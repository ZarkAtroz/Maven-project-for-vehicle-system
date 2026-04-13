package br.com.veiculos.comum;

import java.io.Serializable;
import java.util.List;

/**
 * Estado completo da fábrica de veículos.
 */
public class EstadoFabrica implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int estoque;
    private final int veiculosProduzidos;
    private final int veiculosNaEsteira;
    private final Veiculo[] conteudoEsteira;
    private final List<EstadoEstacao> estacoes;
    private final List<String> ultimosLogs;

    /**
     * Construtor completo.
     *
     * @param estoque             quantidade disponível no estoque global
     * @param veiculosProduzidos  total de veículos produzidos
     * @param veiculosNaEsteira   quantidade de veículos na esteira
     * @param conteudoEsteira     snapshot da esteira circular
     * @param estacoes            lista com estados de cada estação
     * @param ultimosLogs         últimas mensagens de log
     */
    public EstadoFabrica(int estoque, int veiculosProduzidos, int veiculosNaEsteira,
                         Veiculo[] conteudoEsteira, List<EstadoEstacao> estacoes,
                         List<String> ultimosLogs) {
        this.estoque = estoque;
        this.veiculosProduzidos = veiculosProduzidos;
        this.veiculosNaEsteira = veiculosNaEsteira;
        this.conteudoEsteira = conteudoEsteira;
        this.estacoes = estacoes;
        this.ultimosLogs = ultimosLogs;
    }

    public int getEstoque() {
        return estoque;
    }

    public int getVeiculosProduzidos() {
        return veiculosProduzidos;
    }

    public int getVeiculosNaEsteira() {
        return veiculosNaEsteira;
    }

    public Veiculo[] getConteudoEsteira() {
        return conteudoEsteira;
    }

    public List<EstadoEstacao> getEstacoes() {
        return estacoes;
    }

    public List<String> getUltimosLogs() {
        return ultimosLogs;
    }
}
