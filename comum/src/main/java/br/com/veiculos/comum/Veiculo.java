package br.com.veiculos.comum;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Veiculo implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final AtomicInteger contadorId = new AtomicInteger(0);
    private static final AtomicInteger contadorCor = new AtomicInteger(0);
    private static final AtomicInteger contadorTipo = new AtomicInteger(0);

    private static final String[] CORES = {"R", "G", "B"};

    private static final String[] TIPOS = {"SUV", "SEDAN"};

    private final int id;
    private final String cor;
    private final String tipo;
    private final int idEstacao;
    private final int idFuncionario;
    private int posEsteiraFabrica;
    private int idLoja;
    private int posEsteiraLoja;

    public Veiculo(int idEstacao, int idFuncionario) {
        this.id = contadorId.getAndIncrement();
        this.cor = CORES[contadorCor.getAndUpdate(i -> (i + 1) % CORES.length)];
        this.tipo = TIPOS[contadorTipo.getAndUpdate(i -> (i + 1) % TIPOS.length)];
        this.idEstacao = idEstacao;
        this.idFuncionario = idFuncionario;
        this.posEsteiraFabrica = -1;
        this.idLoja = -1;
        this.posEsteiraLoja = -1;
    }

    public int getId() {
        return id;
    }

    public String getCor() {
        return cor;
    }

    public String getTipo() {
        return tipo;
    }

    public int getIdEstacao() {
        return idEstacao;
    }

    public int getIdFuncionario() {
        return idFuncionario;
    }

    public int getPosEsteiraFabrica() {
        return posEsteiraFabrica;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public int getPosEsteiraLoja() {
        return posEsteiraLoja;
    }

    public void setPosEsteiraFabrica(int posEsteiraFabrica) {
        this.posEsteiraFabrica = posEsteiraFabrica;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public void setPosEsteiraLoja(int posEsteiraLoja) {
        this.posEsteiraLoja = posEsteiraLoja;
    }

    @Override
    public String toString() {
        return String.format("[VEICULO] ID=%d | Cor=%s | Tipo=%s | Estação=%d | Func=%d | PosEstFab=%d",
                id, cor, tipo, idEstacao, idFuncionario, posEsteiraFabrica);
    }
}
