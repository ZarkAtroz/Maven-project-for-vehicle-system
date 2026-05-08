package br.com.veiculos.fabrica;




public class FabricaMain {
    public static void main(String[] args) {
        try {
            
            FabricaImpl fabrica = new FabricaImpl();

            
            fabrica.iniciar();

            
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("Erro ao iniciar fábrica: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
