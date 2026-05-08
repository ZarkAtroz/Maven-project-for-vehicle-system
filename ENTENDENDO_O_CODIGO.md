# Entendendo o Código: Sistema de Veículos Distribuído

## Introdução Geral do Sistema

Este é um sistema distribuído de produção e venda de veículos que utiliza **programação concorrente** e **RMI (Remote Method Invocation)** do Java. O sistema simula uma cadeia produtiva completa:

1. **Fábrica**: Produz veículos usando 4 estações de trabalho, cada uma com 5 funcionários
2. **Lojas** (3 unidades): Compram veículos da fábrica e mantêm estoque local
3. **Clientes** (20 threads): Compram veículos das lojas e armazenam em suas garagens

O sistema resolve problemas clássicos de **sincronização** (como o Jantar dos Filósofos) e **comunicação distribuída** (através de RMI). Os principais mecanismos de controle de concorrência são **Semáforos** e **Esteiras Circulares** (buffers circulares limitados).

---

## Módulo Comum

Este módulo contém as classes compartilhadas por todos os outros módulos: a classe `Veiculo`, a estrutura de dados `EsteiraCircular`, as interfaces RMI, as classes de estado para o monitor, e o serviço de log.

### Classe: `Veiculo`

**Papel no Sistema:**
Representa um veículo sendo produzido e comercializado. O veículo é o objeto central que flui através de toda a cadeia: é criado na fábrica, enviado para uma loja, e comprado por um cliente. Como a classe implementa `Serializable`, os objetos podem ser transmitidos via RMI entre processos diferentes.

**Campos Importantes:**

- **Contadores estáticos** (`contadorId`, `contadorCor`, `contadorTipo`): São do tipo `AtomicInteger` porque **múltiplas threads** (funcionários) criam veículos simultaneamente. `AtomicInteger` garante que os incrementos sejam **thread-safe** sem precisar de `synchronized`.

- **`id`**: Identificador único do veículo, gerado automaticamente e sequencialmente.

- **`cor` e `tipo`**: Gerados ciclicamente usando os arrays `CORES` e `TIPOS`. A cor alterna entre R, G, B e o tipo entre SUV e SEDAN.

- **`idEstacao` e `idFuncionario`**: Registram quem produziu o veículo, útil para rastreamento e logs.

- **`posEsteiraFabrica`, `idLoja`, `posEsteiraLoja`**: Campos mutáveis que rastreiam a localização do veículo ao longo da cadeia.

**Métodos Importantes:**

- **Construtor `Veiculo(int idEstacao, int idFuncionario)`**:
  - Gera automaticamente o ID, cor e tipo usando os contadores estáticos
  - Usa `getAndIncrement()` para obter o valor atual e incrementar atomicamente
  - Usa `getAndUpdate(i -> (i + 1) % CORES.length)` para alternar ciclicamente
  - **Por que assim?** Simplifica a criação de veículos pelos funcionários e garante que cada veículo tenha propriedades únicas

- **Setters (`setPosEsteiraFabrica`, `setIdLoja`, `setPosEsteiraLoja`)**:
  - Permitem atualizar a posição do veículo conforme ele se move pela cadeia
  - **Por que apenas esses setters?** Os outros campos são imutáveis após criação, garantindo integridade dos dados

**Conexões com outras classes:**
- Criado por: `Funcionario`
- Armazenado em: `EsteiraCircular` (na fábrica e nas lojas)
- Transmitido via RMI: `FabricaRemota.solicitarVeiculo()` e `LojaRemota.comprarVeiculo()`

---

### Classe: `EsteiraCircular<T>`

**Papel no Sistema:**
É uma **fila FIFO (First In, First Out) circular e limitada**, implementando o padrão **Produtor-Consumidor**. Ela sincroniza threads usando apenas semáforos, sem locks ou synchronized. É usada em três lugares: esteira de saída da fábrica, esteiras das lojas, e garagens dos clientes.

**Campos Importantes:**

- **`array[]`**: Buffer circular que armazena os itens (genérico, tipo `T`)

- **`capacidade`**: Tamanho máximo da esteira

- **`cabeca` e `cauda`**: Ponteiros que indicam onde inserir (cabeça) e de onde retirar (cauda)

- **Três semáforos** (único mecanismo de sincronização):
  - **`mutex`**: Garante **exclusão mútua** no acesso ao array e aos ponteiros (inicializado com 1)
  - **`vazios`**: Conta quantas posições estão livres (inicializado com `capacidade`)
  - **`cheios`**: Conta quantos itens estão disponíveis (inicializado com 0)

**Métodos Importantes:**

- **`inserir(T item)`**:
  1. `vazios.acquire()` - Bloqueia se estiver cheia, aguardando espaço
  2. `mutex.acquire()` - Garante acesso exclusivo
  3. Insere o item na posição `cabeca` e avança o ponteiro: `cabeca = (cabeca + 1) % capacidade`
  4. `mutex.release()` - Libera acesso
  5. `cheios.release()` - Sinaliza que há um item a mais disponível

  **Por que assim?** O padrão de semáforos resolve o problema produtor-consumidor de forma elegante: vazios/cheios controlam a capacidade, mutex protege a região crítica. A operação `% capacidade` faz o buffer ser circular.

- **`retirar()`**:
  1. `cheios.acquire()` - Bloqueia se estiver vazia, aguardando item
  2. `mutex.acquire()` - Garante acesso exclusivo
  3. Retira o item da posição `cauda` e avança o ponteiro
  4. `mutex.release()` - Libera acesso
  5. `vazios.release()` - Sinaliza que há um espaço a mais disponível

  **Por que assim?** Ordem simétrica à inserção, garantindo FIFO

- **`tamanhoAtual()`**:
  - Retorna `cheios.availablePermits()` - número de itens disponíveis
  - **Importante**: Não bloqueia, é apenas uma leitura do estado atual

- **`snapshot()`**:
  - Retorna uma cópia do array para o monitor visualizar
  - **Por que sem mutex?** Prioriza performance sobre precisão absoluta, pois é só para monitoramento

**Conceitos de Concorrência Envolvidos:**
- **Problema Produtor-Consumidor**: Clássico problema de sincronização
- **Semáforos**: Primitiva de sincronização mais fundamental
- **Buffer Circular**: Estrutura de dados eficiente que reutiliza posições
- **Exclusão Mútua**: Garante que apenas uma thread acesse o array por vez

**Conexões com outras classes:**
- Usada por: `FabricaImpl` (esteira de saída), `LojaImpl` (esteira local), `Cliente` (garagem)
- Armazena: Objetos `Veiculo`

---

### Interface: `FabricaRemota`

**Papel no Sistema:**
Define o **contrato RMI** para a fábrica, permitindo que lojas remotas solicitem veículos. Estende `Remote` para ser usada com RMI.

**Métodos:**

- **`solicitarVeiculo(int idLoja)`**:
  - Chamado remotamente pelas lojas
  - Retorna um `Veiculo` quando disponível (pode bloquear se não houver estoque)
  - Lança `RemoteException` em caso de erro de rede

  **Conceito**: **RMI (Remote Method Invocation)** - permite chamar métodos em objetos que estão em outra JVM, possivelmente em outra máquina

- **`getEstado()`**:
  - Usado pelo monitor para obter informações sobre a fábrica
  - Retorna `EstadoFabrica` com snapshot completo

**Conexões:**
- Implementada por: `FabricaImpl`
- Usada por: `LojaImpl` (cliente RMI)

---

### Interface: `LojaRemota`

**Papel no Sistema:**
Define o **contrato RMI** para as lojas, permitindo que clientes remotos comprem veículos.

**Métodos:**

- **`comprarVeiculo(int idCliente)`**:
  - Chamado remotamente pelos clientes
  - Retorna um `Veiculo` quando disponível na esteira da loja
  - Pode bloquear se a loja estiver sem estoque

- **`getEstado()`**:
  - Usado pelo monitor para visualizar o estado da loja

**Conexões:**
- Implementada por: `LojaImpl`
- Usada por: `Cliente` (cliente RMI)

---

### Interface: `ClientesRemoto`

**Papel no Sistema:**
Define o **contrato RMI** para o sistema de clientes, permitindo que o monitor consulte o estado de todos os clientes.

**Métodos:**

- **`getEstados()`**:
  - Retorna uma lista com o estado de todos os 20 clientes
  - Usado pelo monitor para visualização

**Conexões:**
- Implementada por: `ClientesMain`
- Usada por: Monitor (não implementado neste projeto)

---

### Classes de Estado

Estas classes são **DTOs (Data Transfer Objects)** usadas para transferir informações de estado via RMI para o monitor. Todas implementam `Serializable`.

#### `EstadoFuncionario`

**Campos:**
- `id`: Identificador do funcionário
- `status`: Enum com valores `PRODUZINDO`, `AGUARDANDO`, `BLOQUEADO`
- `veiculosProduzidos`: Contador de produção

**Por que existe?** Permite que o monitor visualize o que cada funcionário está fazendo em tempo real.

#### `EstadoEstacao`

**Campos:**
- `id`: Identificador da estação
- `funcionarios`: Lista de estados dos 5 funcionários
- `ferramentasLivres`: Array booleano indicando quais ferramentas estão disponíveis

**Por que existe?** Agrupa informações de uma estação completa.

#### `EstadoFabrica`

**Campos:**
- `estoque`: Quantidade disponível no estoque global (permissões do semáforo)
- `veiculosProduzidos`: Total produzido desde o início
- `veiculosNaEsteira`: Quantidade atual na esteira de saída
- `conteudoEsteira`: Snapshot do array da esteira
- `estacoes`: Lista com estados das 4 estações
- `ultimosLogs`: Últimas 50 linhas de log

**Por que existe?** Fornece visão completa da fábrica para o monitor.

#### `EstadoLoja`

**Campos:**
- `id`: Identificador da loja
- `veiculosNaEsteira`: Quantidade atual
- `capacidadeEsteira`: Tamanho máximo
- `totalRecebidos`: Total recebido da fábrica
- `totalVendidos`: Total vendido para clientes

**Por que existe?** Permite monitorar o desempenho de cada loja.

#### `EstadoCliente`

**Campos:**
- `id`: Identificador do cliente
- `veiculosNaGaragem`: Quantidade atual
- `capacidadeGaragem`: Tamanho máximo
- `totalComprado`: Total de compras realizadas

**Por que existe?** Permite visualizar o estado de cada cliente.

---

### Classe: `LogService`

**Papel no Sistema:**
Serviço centralizado de logging **thread-safe** que registra eventos importantes em arquivo e mantém histórico em memória. Usa apenas `Semaphore` para sincronização (sem `synchronized`).

**Campos Importantes:**

- **`writer`**: `PrintWriter` para escrever no arquivo
- **`mutex`**: Semáforo binário para exclusão mútua
- **`historico`**: `LinkedList` com as últimas 50 linhas (para o monitor)

**Métodos Importantes:**

- **`producao(Veiculo v)`**:
  - Registra quando um veículo é produzido
  - Usa `mutex.acquire()` e `release()` para proteger escrita no arquivo e no histórico

  **Por que assim?** Múltiplos funcionários produzem simultaneamente, então o log precisa ser thread-safe

- **`vendaParaLoja(Veiculo v)`**:
  - Registra quando um veículo é vendido para uma loja
  - Inclui informações de loja e posição na esteira

- **`recebimentoNaLoja(Veiculo v, int idLoja)`**:
  - Registra quando uma loja recebe um veículo

- **`vendaParaCliente(Veiculo v, int idCliente)`**:
  - Registra quando um cliente compra um veículo

- **`getLogs()`**:
  - Retorna cópia do histórico para o monitor
  - Usa mutex para garantir consistência

  **Por que assim?** O monitor pode chamar esse método enquanto logs estão sendo escritos

**Conceitos de Concorrência:**
- **Thread-Safety**: Múltiplas threads podem chamar os métodos simultaneamente sem corromper dados
- **Semáforo Binário como Mutex**: Alternativa ao `synchronized`
- **Tratamento de `InterruptedException`**: Restaura o flag de interrupção com `Thread.currentThread().interrupt()`

**Conexões:**
- Usada por: `FabricaImpl`, `LojaImpl`, `ClientesMain`

---

## Módulo Fábrica

Este módulo implementa a produção de veículos, resolvendo o problema do **Jantar dos Filósofos** (funcionários compartilham ferramentas).

### Classe: `Ferramenta`

**Papel no Sistema:**
Representa uma ferramenta compartilhada entre funcionários. É o recurso pelo qual os funcionários competem, análogo aos garfos no problema do Jantar dos Filósofos.

**Campos:**

- **`semaforo`**: Semáforo binário (inicializado com 1) que controla o acesso exclusivo à ferramenta

**Métodos:**

- **`pegar()`**:
  - Chama `semaforo.acquire()` para adquirir a ferramenta
  - Bloqueia se a ferramenta estiver em uso por outro funcionário

  **Por que assim?** Semáforo binário é a forma mais simples de exclusão mútua

- **`largar()`**:
  - Chama `semaforo.release()` para liberar a ferramenta
  - Permite que outro funcionário a pegue

**Conceito de Concorrência:**
- **Recurso Compartilhado**: Múltiplas threads competem pelo mesmo recurso
- **Semáforo Binário**: Funciona como um mutex (0 = ocupado, 1 = livre)

**Conexões:**
- Criada por: `Estacao`
- Usada por: `Funcionario`

---

### Classe: `Funcionario`

**Papel no Sistema:**
Representa um funcionário que produz veículos. Implementa `Runnable` para executar em uma thread separada. Cada funcionário compete por duas ferramentas (esquerda e direita) com seus vizinhos, resolvendo o problema clássico do **Jantar dos Filósofos**.

**Campos Importantes:**

- **`id`**: Identificador do funcionário (0 a 4)
- **`idEstacao`**: Estação à qual pertence
- **`ferramentaEsquerda` e `ferramentaDireita`**: Ferramentas compartilhadas com vizinhos
- **`estoqueGlobal`**: Semáforo compartilhado que limita a produção total da fábrica em 500 veículos
- **`esteiraSaida`**: Esteira circular onde o veículo produzido é inserido
- **`random`**: Para gerar tempo aleatório de produção

**Método `run()` - Ciclo de Vida:**

1. **`estoqueGlobal.acquire()`**: Aguarda até ter "permissão" do estoque global
   - **Por que?** Limita a produção total da fábrica a 500 veículos

2. **Aquisição de Ferramentas (ESTRATÉGIA ANTI-DEADLOCK)**:
   ```java
   if (id == 4) {
       ferramentaDireita.pegar();  // Funcionário 4: DIREITA → ESQUERDA
       ferramentaEsquerda.pegar();
   } else {
       ferramentaEsquerda.pegar();  // Funcionários 0-3: ESQUERDA → DIREITA
       ferramentaDireita.pegar();
   }
   ```

   **Por que assim?** Esta é a **solução assimétrica** para o Jantar dos Filósofos:
   - Se todos pegassem na mesma ordem (esquerda → direita), poderia ocorrer **deadlock**: todos pegam a ferramenta da esquerda e ficam esperando a da direita eternamente
   - Ao fazer o último funcionário (id=4) pegar na ordem inversa, **quebramos o ciclo de dependências**
   - Garante que pelo menos um funcionário conseguirá pegar ambas as ferramentas

3. **Produção**: `Thread.sleep(tempoProducao)` simula o tempo de fabricação (500ms a 1500ms)

4. **Criação do Veículo**: `new Veiculo(idEstacao, id)`

5. **Liberação de Ferramentas**: Ordem inversa da aquisição (boa prática)

6. **Inserção na Esteira**: `esteiraSaida.inserir(veiculo)` - pode bloquear se esteira cheia

7. **Atualização da Posição**: `veiculo.setPosEsteiraFabrica(pos)`

8. **Log no Console**: Confirmação visual

**Conceitos de Concorrência:**
- **Jantar dos Filósofos**: Problema clássico de deadlock
- **Solução Assimétrica**: Quebra o ciclo de dependências
- **Semáforo de Recursos**: `estoqueGlobal` limita recursos disponíveis
- **Produtor-Consumidor**: Produz veículos para a esteira

**Conexões:**
- Criado por: `Estacao`
- Usa: `Ferramenta` (2 instâncias), `Semaphore` (estoqueGlobal), `EsteiraCircular`
- Cria: `Veiculo`

---

### Classe: `Estacao`

**Papel no Sistema:**
Agrupa 5 funcionários e 5 ferramentas, organizando uma estação completa de produção. Gerencia a criação e inicialização das threads dos funcionários.

**Campos:**

- **`id`**: Identificador da estação (1 a 4)
- **`ferramentas[5]`**: Array circular de ferramentas
- **`funcionarios[5]`**: Array de funcionários
- **`threads[5]`**: Array de threads para controle

**Construtor:**

1. **Cria 5 ferramentas** dispostas em círculo
2. **Cria 5 funcionários**, cada um com:
   - Ferramenta esquerda: `ferramentas[i]`
   - Ferramenta direita: `ferramentas[(i+1) % 5]`

   **Por que `% 5`?** Para fechar o círculo: o funcionário 4 pega a ferramenta 0 como direita

**Método `iniciar()`:**

- Cria e inicia as 5 threads
- Nome das threads: `"Estacao-{id}-Funcionario-{i}"` (bom para debug)
- **Não bloqueia**: Retorna imediatamente após iniciar

**Método `getEstado()`:**

- Placeholder para retornar informações para o monitor
- Atualmente retorna apenas os IDs dos funcionários

**Conceitos:**
- **Topologia Circular**: Funcionários e ferramentas formam um anel
- **Thread Pool Simples**: Gerencia múltiplas threads de trabalho

**Conexões:**
- Criada por: `FabricaImpl`
- Cria: `Funcionario` e `Ferramenta`

---

### Classe: `FabricaImpl`

**Papel no Sistema:**
Implementação RMI da fábrica. É o servidor RMI que lojas conectam para solicitar veículos. Gerencia o estoque global, a esteira de saída, as 4 estações de produção e o serviço de log.

**Campos Importantes:**

- **`estoqueGlobal`**: Semáforo inicializado com 500 (limite de produção)
  - **Por que 500?** Define capacidade máxima da fábrica
  - Cada `acquire()` reduz o estoque, limitando produção

- **`esteiraSaida`**: `EsteiraCircular` com capacidade 40
  - Armazena veículos prontos aguardando as lojas
  - **Buffer entre produção e distribuição**

- **`log`**: `LogService` escrevendo em "fabrica.log"

- **`estacoes`**: Lista com 4 `Estacao` (ids 1 a 4)

- **`totalProduzidos`**: `AtomicInteger` para contar total (usado pelo monitor)

**Métodos Importantes:**

- **`solicitarVeiculo(int idLoja)` (chamado remotamente pelas lojas)**:
  1. `esteiraSaida.retirar()` - **Bloqueia se não houver veículos**
  2. `v.setIdLoja(idLoja)` - Marca para qual loja vai
  3. `log.vendaParaLoja(v)` - Registra a transação
  4. Retorna o veículo

  **Por que bloqueia?** Se a produção está lenta, a loja aguarda até ter estoque. Isso é **bloqueio implícito** proporcionado pela `EsteiraCircular`

- **`getEstado()` (chamado pelo monitor)**:
  - Coleta estado de todas as estações
  - Retorna `EstadoFabrica` completo com snapshot

- **`iniciar()`**:
  1. Inicia as 4 estações (20 threads de funcionários no total)
  2. Cria RMI registry na porta 1099
  3. Registra-se com o nome "Fabrica"

  **Por que criar o registry?** `createRegistry()` cria um novo servidor RMI. Outras JVMs podem localizar a fábrica através de `LocateRegistry.getRegistry()`

**Conceitos de Concorrência e Distribuição:**
- **RMI (Remote Method Invocation)**: Permite chamadas de método entre JVMs
- **Servidor RMI**: Exporta objeto `UnicastRemoteObject` e registra no registry
- **Bloqueio Distribuído**: Lojas bloqueiam aguardando estoque remoto
- **Semáforo de Recursos Globais**: `estoqueGlobal` limita produção total

**Conexões:**
- Estende: `UnicastRemoteObject`
- Implementa: `FabricaRemota`
- Cria: `Estacao`, `EsteiraCircular`, `LogService`
- Usada por: `LojaImpl` (via RMI)

---

### Classe: `FabricaMain`

**Papel no Sistema:**
Ponto de entrada da aplicação da fábrica. Simplesmente instancia e inicia a fábrica.

**Método `main()`:**

1. Cria `FabricaImpl`
2. Chama `fabrica.iniciar()`
3. `Thread.currentThread().join()` - **Mantém processo vivo**
   - **Por que?** Sem isso, o processo terminaria imediatamente
   - O `join()` faz a main thread aguardar indefinidamente (até ser interrompida)

**Conexões:**
- Cria: `FabricaImpl`

---

## Módulo Loja

Este módulo implementa as lojas que compram da fábrica e vendem para clientes.

### Classe: `LojaImpl`

**Papel no Sistema:**
Implementação RMI de uma loja. Atua como **cliente RMI da fábrica** (solicita veículos) e **servidor RMI para clientes** (vende veículos). Mantém uma esteira local que funciona como buffer entre aquisição e venda.

**Campos Importantes:**

- **`id`**: Identificador da loja (1, 2 ou 3)
- **`hostFabrica`**: Endereço IP/hostname onde a fábrica está rodando
- **`capacidadeEsteira`**: Tamanho da esteira local (padrão 20)
- **`esteiraLocal`**: `EsteiraCircular` de veículos disponíveis para venda
- **`fabrica`**: Referência remota para `FabricaRemota`
- **`log`**: `LogService` escrevendo em "loja_{id}.log"
- **`totalRecebidos` e `totalVendidos`**: Contadores `AtomicInteger`

**Métodos Importantes:**

- **`comprarVeiculo(int idCliente)` (chamado remotamente pelos clientes)**:
  1. `esteiraLocal.retirar()` - **Bloqueia se loja sem estoque**
  2. `totalVendidos.incrementAndGet()`
  3. `log.vendaParaCliente(v, idCliente)`
  4. Retorna o veículo

  **Por que bloqueia?** Se o cliente chama quando não há estoque, ele aguarda até a thread de abastecimento trazer mais veículos

- **`getEstado()`**:
  - Retorna `EstadoLoja` para o monitor

- **`iniciar()`**:
  1. **Conecta à fábrica via RMI**:
     ```java
     Registry r = LocateRegistry.getRegistry(hostFabrica, 1099);
     fabrica = (FabricaRemota) r.lookup("Fabrica");
     ```
     - **`getRegistry(host, port)`**: Localiza o registry RMI remoto
     - **`lookup("Fabrica")`**: Obtém a referência ao objeto remoto

  2. **Inicia thread de abastecimento** que executa infinitamente:
     ```java
     while (true) {
         Veiculo v = fabrica.solicitarVeiculo(id);  // Chamada RMI
         int pos = esteiraLocal.inserir(v);
         v.setPosEsteiraLoja(pos);
         totalRecebidos.incrementAndGet();
         log.recebimentoNaLoja(v, id);
     }
     ```

     **Por que thread separada?** Permite que a loja solicite veículos da fábrica continuamente, sem bloquear o servidor RMI que atende os clientes

  3. **Registra-se como servidor RMI**:
     - Porta: `1099 + id` (Loja 1 → 1100, Loja 2 → 1101, Loja 3 → 1102)
     - Nome: `"Loja{id}"`

**Conceitos de Concorrência e Distribuição:**
- **Cliente e Servidor RMI Simultaneamente**: Chama métodos remotos na fábrica E exporta métodos para os clientes
- **Thread de Abastecimento**: Padrão produtor onde a thread "produz" veículos solicitando da fábrica
- **Buffer Local**: A `esteiraLocal` desacopla a velocidade de aquisição da fábrica da velocidade de venda para clientes
- **Múltiplos Registries RMI**: Cada loja cria seu próprio registry em porta diferente

**Conexões:**
- Estende: `UnicastRemoteObject`
- Implementa: `LojaRemota`
- Cliente RMI de: `FabricaRemota`
- Servidor RMI para: `Cliente`
- Usa: `EsteiraCircular`, `LogService`

---

### Classe: `LojaMain`

**Papel no Sistema:**
Ponto de entrada da aplicação de uma loja. Recebe argumentos de linha de comando para configurar ID, host da fábrica e capacidade.

**Método `main()`:**

1. **Valida argumentos**:
   - Mínimo: `<idLoja> <hostFabrica>`
   - Opcional: `[capacidadeEsteira]` (padrão 20)

2. **Parse**: Converte strings para int

3. **Cria e inicia loja**:
   ```java
   LojaImpl loja = new LojaImpl(id, hostFabrica, capacidadeEsteira);
   loja.iniciar();
   Thread.currentThread().join();
   ```

**Exemplo de uso:**
```bash
java LojaMain 1 localhost 20
```

**Conceitos:**
- **Configuração via CLI**: Permite executar múltiplas lojas em máquinas diferentes
- **Tratamento de exceções**: Valida entrada e reporta erros

**Conexões:**
- Cria: `LojaImpl`

---

## Módulo Clientes

Este módulo implementa o sistema de clientes que compram veículos das lojas.

### Classe: `Cliente`

**Papel no Sistema:**
Representa um cliente que compra veículos das lojas via RMI e armazena em sua garagem. Implementa `Runnable` para executar em uma thread separada. Cada cliente escolhe aleatoriamente de qual das 3 lojas comprar.

**Campos Importantes:**

- **`id`**: Identificador do cliente (1 a 20)
- **`hostLojas[]`**: Array com 3 hosts das lojas
- **`capacidadeGaragem`**: Tamanho máximo da garagem
- **`garagem`**: `EsteiraCircular` que armazena veículos comprados
- **`lojas[]`**: Array com 3 referências remotas para `LojaRemota`
- **`log`**: Referência ao `LogService` compartilhado
- **`random`**: Para escolher loja aleatoriamente e tempo de espera
- **`totalComprado`**: Contador `AtomicInteger`

**Método `run()` - Ciclo de Vida:**

1. **Conecta às 3 lojas via RMI** (executado uma vez):
   ```java
   for (int i = 0; i < 3; i++) {
       Registry r = LocateRegistry.getRegistry(hostLojas[i], 1100 + i);
       lojas[i] = (LojaRemota) r.lookup("Loja" + (i + 1));
   }
   ```

   **Portas**: Loja 1 → 1100, Loja 2 → 1101, Loja 3 → 1102

2. **Loop infinito de compras**:
   a. `int idx = random.nextInt(3)` - Escolhe loja aleatoriamente
   b. `Veiculo v = lojas[idx].comprarVeiculo(id)` - **Chamada RMI que pode bloquear**
   c. `garagem.inserir(v)` - **Pode bloquear se garagem cheia**
   d. `totalComprado.incrementAndGet()`
   e. `log.vendaParaCliente(v, id)`
   f. Imprime confirmação
   g. `Thread.sleep(200 a 800ms)` - Pausa entre compras

   **Por que pausa?** Simula ritmo realista de compra

**Método `getEstado()`:**

- Retorna `EstadoCliente` com informações atuais
- Chamado por `ClientesMain` para fornecer ao monitor

**Conceitos de Concorrência e Distribuição:**
- **Cliente RMI Multi-Servidor**: Conecta-se a 3 servidores RMI diferentes
- **Balanceamento de Carga Aleatório**: Distribui requisições entre as 3 lojas
- **Buffer do Consumidor**: A garagem armazena compras, limitando taxa de consumo

**Conexões:**
- Implementa: `Runnable`
- Cliente RMI de: `LojaRemota` (3 instâncias)
- Usa: `EsteiraCircular`, `LogService`

---

### Classe: `ClientesMain`

**Papel no Sistema:**
Ponto de entrada do sistema de clientes. Gerencia 20 threads de clientes, fornece serviço de log compartilhado e exporta interface RMI para o monitor.

**Campos:**

- **`clientes`**: Lista com 20 instâncias de `Cliente`
- **`log`**: `LogService` compartilhado por todos os clientes

**Métodos:**

- **`getEstados()` (chamado remotamente pelo monitor)**:
  - Coleta e retorna estados de todos os 20 clientes

- **`adicionarCliente(Cliente cliente)`**:
  - Adiciona cliente à lista interna

**Método `main()`:**

1. **Valida argumentos**:
   - Mínimo: `<hostLoja1> <hostLoja2> <hostLoja3>`
   - Opcional: `[capacidadeGaragem]` (padrão 10)

2. **Cria sistema**:
   ```java
   ClientesMain sistema = new ClientesMain();
   ```

3. **Cria e inicia 20 clientes**:
   ```java
   for (int i = 1; i <= 20; i++) {
       Cliente cliente = new Cliente(i, hostLojas, capacidadeGaragem, sistema.log);
       sistema.adicionarCliente(cliente);
       Thread thread = new Thread(cliente, "Cliente-" + i);
       thread.start();
   }
   ```

   **Importante**: Todos compartilham o mesmo `LogService`

4. **Registra como servidor RMI**:
   - Porta: 1103
   - Nome: "Clientes"

   **Por que?** Para o monitor poder consultar estados dos clientes

5. **Mantém vivo**: `Thread.currentThread().join()`

**Conceitos:**
- **Pool de Threads**: Gerencia 20 threads de clientes
- **Serviço Compartilhado**: Um único `LogService` thread-safe para todos
- **Agregador de Estados**: Coleta informações de todas as threads filhas

**Exemplo de uso:**
```bash
java ClientesMain localhost localhost localhost 10
```

**Conexões:**
- Estende: `UnicastRemoteObject`
- Implementa: `ClientesRemoto`
- Cria: `Cliente` (20 instâncias), `LogService`

---

## Fluxo Completo de um Veículo

Vamos acompanhar a jornada de um único veículo, do momento que é criado até chegar na garagem de um cliente:

### 1. Produção na Fábrica

**Thread**: `Funcionario` da Estação 2, ID 3

1. Funcionário aguarda liberação do `estoqueGlobal` (semáforo com 500 permissões)
2. Adquire `ferramentaEsquerda` (semáforo da ferramenta 3)
3. Adquire `ferramentaDireita` (semáforo da ferramenta 4)
4. Dorme 800ms (simulando produção)
5. Cria: `new Veiculo(2, 3)` → **Veículo ID=42, Cor=R, Tipo=SUV**
6. Libera as ferramentas
7. Insere na `esteiraSaida` da fábrica (posição 15)
8. Define `veiculo.setPosEsteiraFabrica(15)`
9. Log no console: `[PROD] [VEICULO] ID=42 | Cor=R | Tipo=SUV | Estação=2 | Func=3 | PosEstFab=15`

**Estado atual**: Veículo na esteira de saída da fábrica, aguardando uma loja solicitar

---

### 2. Transporte para a Loja

**Thread**: `Loja2-Abastecimento` (thread de abastecimento da Loja 2)

1. Thread executa: `fabrica.solicitarVeiculo(2)` (chamada RMI)
2. Na fábrica, `FabricaImpl.solicitarVeiculo()` executa:
   - `esteiraSaida.retirar()` → retira o veículo da posição 15
   - `v.setIdLoja(2)` → marca que vai para Loja 2
   - `log.vendaParaLoja(v)` → registra em "fabrica.log"
   - Retorna o veículo (serializado via RMI)
3. Na loja, a thread recebe o veículo (deserializado)
4. Insere na `esteiraLocal` da Loja 2 (posição 7)
5. Define `veiculo.setPosEsteiraLoja(7)`
6. `totalRecebidos.incrementAndGet()` → contador da loja
7. `log.recebimentoNaLoja(v, 2)` → registra em "loja_2.log"

**Estado atual**: Veículo na esteira da Loja 2, disponível para clientes

---

### 3. Compra pelo Cliente

**Thread**: `Cliente-15`

1. Cliente escolhe aleatoriamente: `idx = 1` (Loja 2)
2. Executa: `lojas[1].comprarVeiculo(15)` (chamada RMI)
3. Na loja, `LojaImpl.comprarVeiculo()` executa:
   - `esteiraLocal.retirar()` → retira o veículo da posição 7
   - `totalVendidos.incrementAndGet()`
   - `log.vendaParaCliente(v, 15)` → registra em "loja_2.log"
   - Retorna o veículo (serializado via RMI)
4. No cliente, thread recebe o veículo (deserializado)
5. Insere na `garagem` (EsteiraCircular do cliente)
6. `totalComprado.incrementAndGet()`
7. `log.vendaParaCliente(v, 15)` → registra em "clientes.log"
8. Imprime: `[CLIENTE-15] comprou: [VEICULO] ID=42 | Cor=R | Tipo=SUV | Estação=2 | Func=3 | PosEstFab=15`

**Estado final**: Veículo na garagem do Cliente 15

---