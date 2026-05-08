package br.com.veiculos.comum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;





public class LogService {
    private static final int MAX_HISTORICO = 50;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private final PrintWriter writer;
    private final Semaphore mutex;
    private final LinkedList<String> historico;

    





    public LogService(String nomeArquivo) {
        this.mutex = new Semaphore(1);
        this.historico = new LinkedList<>();

        try {
            this.writer = new PrintWriter(new FileWriter(nomeArquivo, true), true);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao abrir arquivo de log: " + nomeArquivo, e);
        }
    }

    




    public void producao(Veiculo v) {
        try {
            mutex.acquire();

            String linha = "[PRODUCAO] " + timestamp() + " " + v.toString();

            writer.println(linha);
            writer.flush();

            adicionarAoHistorico(linha);

            mutex.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    




    public void vendaParaLoja(Veiculo v) {
        try {
            mutex.acquire();

            String linha = "[VENDA_LOJA] " + timestamp() + " " + v.toString()
                    + " | Loja=" + v.getIdLoja() + " | PosLoja=" + v.getPosEsteiraLoja();

            writer.println(linha);
            writer.flush();

            adicionarAoHistorico(linha);

            mutex.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    





    public void recebimentoNaLoja(Veiculo v, int idLoja) {
        try {
            mutex.acquire();

            String linha = "[RECEBIMENTO] " + timestamp() + " " + v.toString()
                    + " | Loja=" + idLoja;

            writer.println(linha);
            writer.flush();

            adicionarAoHistorico(linha);

            mutex.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    





    public void vendaParaCliente(Veiculo v, int idCliente) {
        try {
            mutex.acquire();

            String linha = "[VENDA_CLIENTE] " + timestamp() + " " + v.toString()
                    + " | Cliente=" + idCliente;

            writer.println(linha);
            writer.flush();

            adicionarAoHistorico(linha);

            mutex.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    




    public List<String> getLogs() {
        try {
            mutex.acquire();
            List<String> copia = new ArrayList<>(historico);
            mutex.release();
            return copia;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }
    }

    


    public void fechar() {
        try {
            mutex.acquire();
            writer.close();
            mutex.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    




    private void adicionarAoHistorico(String linha) {
        historico.add(linha);
        if (historico.size() > MAX_HISTORICO) {
            historico.removeFirst();
        }
    }

    




    private String timestamp() {
        return "[" + LocalTime.now().format(TIME_FORMATTER) + "]";
    }
}
