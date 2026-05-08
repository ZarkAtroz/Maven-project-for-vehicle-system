package br.com.veiculos.comum;

import java.io.Serializable;




public class EstadoFuncionario implements Serializable {
    private static final long serialVersionUID = 1L;

    


    public enum Status {
        PRODUZINDO,
        AGUARDANDO,
        BLOQUEADO
    }

    private final int id;
    private final Status status;
    private final int veiculosProduzidos;

    






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
