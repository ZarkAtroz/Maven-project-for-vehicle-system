package br.com.veiculos.comum;

import java.io.Serializable;
import java.util.List;




public class EstadoFabrica implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int estoque;
    private final int veiculosProduzidos;
    private final int veiculosNaEsteira;
    private final Veiculo[] conteudoEsteira;
    private final List<EstadoEstacao> estacoes;
    private final List<String> ultimosLogs;

    









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
