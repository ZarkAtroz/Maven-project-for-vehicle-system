package br.com.veiculos.fabrica;

import br.com.veiculos.comum.EsteiraCircular;
import br.com.veiculos.comum.Veiculo;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Funcionario implements Runnable {
    private final int id;
    private final int idEstacao;
    private final Ferramenta ferramentaEsquerda;
    private final Ferramenta ferramentaDireita;
    private final Semaphore estoqueGlobal;
    private final EsteiraCircular<Veiculo> esteiraSaida;
    private final Random random;

    









    public Funcionario(int id, int idEstacao,
                       Ferramenta ferramentaEsquerda, Ferramenta ferramentaDireita,
                       Semaphore estoqueGlobal, EsteiraCircular<Veiculo> esteiraSaida) {
        this.id = id;
        this.idEstacao = idEstacao;
        this.ferramentaEsquerda = ferramentaEsquerda;
        this.ferramentaDireita = ferramentaDireita;
        this.estoqueGlobal = estoqueGlobal;
        this.esteiraSaida = esteiraSaida;
        this.random = new Random();
    }

    @Override
    public void run() {
        while (true) {
            try {
                
                estoqueGlobal.acquire();

                
                
                
                if (id == 4) {
                    ferramentaDireita.pegar();
                    ferramentaEsquerda.pegar();
                } else {
                    ferramentaEsquerda.pegar();
                    ferramentaDireita.pegar();
                }

                
                int tempoProducao = 500 + random.nextInt(1001); 
                Thread.sleep(tempoProducao);

                
                Veiculo veiculo = new Veiculo(idEstacao, id);

                
                if (id == 4) {
                    ferramentaEsquerda.largar();
                    ferramentaDireita.largar();
                } else {
                    ferramentaDireita.largar();
                    ferramentaEsquerda.largar();
                }

                
                int pos = esteiraSaida.inserir(veiculo);

                
                veiculo.setPosEsteiraFabrica(pos);

                
                System.out.println("[PROD] " + veiculo.toString());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
