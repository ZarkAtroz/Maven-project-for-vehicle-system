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

/**
 * Serviço de log thread-safe usando apenas Semaphore.
 * Escreve logs em arquivo e mantém histórico das últimas 50 linhas em memória.
 */
public class LogService {
    private static final int MAX_HISTORICO = 50;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private final PrintWriter writer;
    private final Semaphore mutex;
    private final LinkedList<String> historico;

    /**
     * Construtor que abre o arquivo de log em modo append.
     *
     * @param nomeArquivo caminho do arquivo de log
     * @throws RuntimeException se não conseguir abrir o arquivo
     */
    public LogService(String nomeArquivo) {
        this.mutex = new Semaphore(1);
        this.historico = new LinkedList<>();

        try {
            this.writer = new PrintWriter(new FileWriter(nomeArquivo, true), true);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao abrir arquivo de log: " + nomeArquivo, e);
        }
    }

    /**
     * Registra a produção de um veículo.
     *
     * @param v veículo produzido
     */
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

    /**
     * Registra a venda de um veículo para uma loja.
     *
     * @param v veículo vendido
     */
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

    /**
     * Registra o recebimento de um veículo em uma loja.
     *
     * @param v      veículo recebido
     * @param idLoja identificador da loja
     */
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

    /**
     * Registra a venda de um veículo para um cliente.
     *
     * @param v         veículo vendido
     * @param idCliente identificador do cliente
     */
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

    /**
     * Retorna uma cópia do histórico de logs para o monitor.
     *
     * @return lista com as últimas linhas de log
     */
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

    /**
     * Fecha o arquivo de log.
     */
    public void fechar() {
        try {
            mutex.acquire();
            writer.close();
            mutex.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Adiciona linha ao histórico, removendo a mais antiga se ultrapassar o limite.
     *
     * @param linha linha a ser adicionada
     */
    private void adicionarAoHistorico(String linha) {
        historico.add(linha);
        if (historico.size() > MAX_HISTORICO) {
            historico.removeFirst();
        }
    }

    /**
     * Retorna o timestamp atual no formato [HH:mm:ss.SSS].
     *
     * @return timestamp formatado
     */
    private String timestamp() {
        return "[" + LocalTime.now().format(TIME_FORMATTER) + "]";
    }
}
