package br.com.veiculos.clientes;

import br.com.veiculos.comum.ClientesRemoto;
import br.com.veiculos.comum.EstadoCliente;
import br.com.veiculos.comum.LogService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;





public class ClientesMain extends UnicastRemoteObject implements ClientesRemoto {
    private static final long serialVersionUID = 1L;

    private final List<Cliente> clientes;
    private final LogService log;

    




    public ClientesMain() throws RemoteException {
        super();
        this.clientes = new ArrayList<>();
        this.log = new LogService("clientes.log");
    }

    





    @Override
    public List<EstadoCliente> getEstados() throws RemoteException {
        List<EstadoCliente> estados = new ArrayList<>();
        for (Cliente cliente : clientes) {
            estados.add(cliente.getEstado());
        }
        return estados;
    }

    




    public void adicionarCliente(Cliente cliente) {
        clientes.add(cliente);
    }

    public static void main(String[] args) {
        try {
            
            if (args.length < 3) {
                System.err.println("Uso: java ClientesMain <hostLoja1> <hostLoja2> <hostLoja3> [capacidadeGaragem]");
                System.err.println("Exemplo: java ClientesMain localhost localhost localhost 10");
                System.exit(1);
            }

            
            String[] hostLojas = {args[0], args[1], args[2]};
            int capacidadeGaragem = (args.length >= 4) ? Integer.parseInt(args[3]) : 10;

            
            ClientesMain sistema = new ClientesMain();

            
            for (int i = 1; i <= 20; i++) {
                Cliente cliente = new Cliente(i, hostLojas, capacidadeGaragem, sistema.log);
                sistema.adicionarCliente(cliente);

                Thread thread = new Thread(cliente, "Cliente-" + i);
                thread.start();
            }

            
            Registry registry = LocateRegistry.createRegistry(1103);
            registry.bind("Clientes", sistema);

            
            System.out.println("✓ Clientes online — 20 threads ativas");

            
            Thread.currentThread().join();

        } catch (NumberFormatException e) {
            System.err.println("Erro: Capacidade da garagem deve ser um número inteiro");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Erro ao iniciar sistema de clientes: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
