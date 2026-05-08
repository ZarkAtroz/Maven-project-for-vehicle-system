package br.com.veiculos.loja;




public class LojaMain {
    public static void main(String[] args) {
        try {
            
            if (args.length < 2) {
                System.err.println("Uso: java LojaMain <idLoja> <hostFabrica> [capacidadeEsteira]");
                System.err.println("Exemplo: java LojaMain 1 localhost 20");
                System.exit(1);
            }

            
            int id = Integer.parseInt(args[0]);
            String hostFabrica = args[1];
            int capacidadeEsteira = (args.length >= 3) ? Integer.parseInt(args[2]) : 20;

            
            LojaImpl loja = new LojaImpl(id, hostFabrica, capacidadeEsteira);

            
            loja.iniciar();

            
            Thread.currentThread().join();

        } catch (NumberFormatException e) {
            System.err.println("Erro: ID da loja e capacidade devem ser números inteiros");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Erro ao iniciar loja: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
