package br.com.veiculos.fabrica;

/**
 * Classe principal para iniciar a fábrica de veículos.
 */
public class FabricaMain {
    public static void main(String[] args) {
        try {
            // Instancia a fábrica
            FabricaImpl fabrica = new FabricaImpl();

            // Inicia as estações e registra no RMI
            fabrica.iniciar();

            // Mantém o processo vivo
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("Erro ao iniciar fábrica: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
