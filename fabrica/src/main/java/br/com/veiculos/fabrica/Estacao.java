package br.com.veiculos.fabrica;

import br.com.veiculos.comum.EsteiraCircular;
import br.com.veiculos.comum.Veiculo;

import java.util.concurrent.Semaphore;

public class Estacao {
    private final int id;
    private final Semaphore estoqueGlobal;
    private final EsteiraCircular<Veiculo> esteiraSaidaFabrica;

    
    private final Ferramenta[] ferramentas;
    private final Funcionario[] funcionarios;
    private final Thread[] threads;

    






    public Estacao(int id, Semaphore estoqueGlobal, EsteiraCircular<Veiculo> esteiraSaidaFabrica) {
        this.id = id;
        this.estoqueGlobal = estoqueGlobal;
        this.esteiraSaidaFabrica = esteiraSaidaFabrica;

        
        this.ferramentas = new Ferramenta[5];
        for (int i = 0; i < 5; i++) {
            ferramentas[i] = new Ferramenta();
        }

        
        
        this.funcionarios = new Funcionario[5];
        for (int i = 0; i < 5; i++) {
            Ferramenta ferramentaEsquerda = ferramentas[i];
            Ferramenta ferramentaDireita = ferramentas[(i + 1) % 5];

            funcionarios[i] = new Funcionario(
                    i,                          
                    id,                         
                    ferramentaEsquerda,
                    ferramentaDireita,
                    estoqueGlobal,
                    esteiraSaidaFabrica
            );
        }

        
        this.threads = new Thread[5];
    }

    



    public void iniciar() {
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(funcionarios[i], "Estacao-" + id + "-Funcionario-" + i);
            threads[i].start();
        }
    }

    





    public int[] getEstado() {
        int[] estado = new int[5];
        for (int i = 0; i < 5; i++) {
            estado[i] = i; 
        }
        return estado;
    }
}
