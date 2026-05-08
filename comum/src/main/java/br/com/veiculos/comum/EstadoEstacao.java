package br.com.veiculos.comum;

import java.io.Serializable;
import java.util.List;




public class EstadoEstacao implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final List<EstadoFuncionario> funcionarios;
    private final boolean[] ferramentasLivres;

    






    public EstadoEstacao(int id, List<EstadoFuncionario> funcionarios, boolean[] ferramentasLivres) {
        this.id = id;
        this.funcionarios = funcionarios;
        this.ferramentasLivres = ferramentasLivres;
    }

    public int getId() {
        return id;
    }

    public List<EstadoFuncionario> getFuncionarios() {
        return funcionarios;
    }

    public boolean[] getFerramentasLivres() {
        return ferramentasLivres;
    }
}
