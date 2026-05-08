# Sistema Distribuído de Veículos

Sistema de produção e venda de veículos desenvolvido em Java com **RMI** e **programação concorrente**.

## 🚀 Como Começar

### ⚠️ PRIMEIRO: Limpe processos anteriores (se já executou antes)
```powershell
.\0-limpar-processos.ps1
```

### Opção 1: Início Super Rápido (Recomendado)
1. Abra **5 terminais** (CMD ou PowerShell)
2. Execute na ordem:
   - `1-iniciar-fabrica.bat`
   - `2-iniciar-loja1.bat`
   - `3-iniciar-loja2.bat`
   - `4-iniciar-loja3.bat`
   - `5-iniciar-clientes.bat`

✅ **O projeto já está compilado e pronto para executar!**

📖 **Leia:** [INICIO-RAPIDO.md](INICIO-RAPIDO.md) para instruções detalhadas

### Opção 2: Compilar Novamente
```cmd
compilar.bat
```

---

## 📚 Documentação

### Para Executar
- **[INICIO-RAPIDO.md](INICIO-RAPIDO.md)** ⭐ Comece aqui! Guia visual de 5 minutos
- **[COMO_EXECUTAR.md](COMO_EXECUTAR.md)** - Guia completo com todos os parâmetros
- **[LEIA-ME-PRIMEIRO.txt](LEIA-ME-PRIMEIRO.txt)** - Resumo em texto puro

### Para Entender o Código
- **[ENTENDENDO_O_CODIGO.md](ENTENDENDO_O_CODIGO.md)** ⭐ Explicação completa de cada classe
  - Papel de cada classe no sistema
  - Explicação de campos e métodos
  - Conceitos de concorrência envolvidos
  - 20+ perguntas e respostas para a defesa
  - Diagramas e fluxos
  - Análise de desempenho

---

## 🏗️ Arquitetura

```
Sistema Multi-Processo com RMI

┌─────────────────────────────────────────────┐
│              FÁBRICA (porta 1099)           │
│  • 4 Estações (20 funcionários total)      │
│  • Jantar dos Filósofos (2 ferramentas)    │
│  • Estoque global: 500 veículos            │
│  • Esteira circular: capacidade 40         │
└────────────────┬────────────────────────────┘
                 │ RMI
       ┌─────────┼─────────┬─────────┐
       ▼         ▼         ▼         │
┌──────────┐ ┌──────────┐ ┌──────────┐
│  LOJA 1  │ │  LOJA 2  │ │  LOJA 3  │
│ (1100)   │ │ (1101)   │ │ (1102)   │
│ Est: 20  │ │ Est: 20  │ │ Est: 20  │
└────┬─────┘ └────┬─────┘ └────┬─────┘
     └────────────┼────────────┘
                  │ RMI
          ┌───────▼────────┐
          │   CLIENTES     │
          │   (porta 1103) │
          │ • 20 threads   │
          │ • Garagem: 10  │
          └────────────────┘
```

---

## ✨ Conceitos Demonstrados

### Programação Concorrente
- ✅ **Jantar dos Filósofos** - Solução assimétrica para evitar deadlock
- ✅ **Produtor-Consumidor** - Esteiras circulares com semáforos
- ✅ **Semáforos** - Sincronização sem `synchronized`
- ✅ **AtomicInteger** - Contadores thread-safe
- ✅ **Thread-Safety** - LogService com exclusão mútua

### Sistemas Distribuídos
- ✅ **RMI** - Comunicação entre processos Java
- ✅ **Serialização** - Transmissão de objetos pela rede
- ✅ **Cliente-Servidor** - Múltiplos servidores RMI
- ✅ **Arquitetura em Pipeline** - Fábrica → Lojas → Clientes

### Estruturas de Dados
- ✅ **Buffer Circular** - Esteiras FIFO limitadas
- ✅ **Genéricos** - `EsteiraCircular<T>` reutilizável

---

## 📁 Estrutura do Projeto

```
sistema-veiculos/
├── comum/                    # Classes compartilhadas
│   └── src/main/java/br/com/veiculos/comum/
│       ├── Veiculo.java
│       ├── EsteiraCircular.java
│       ├── FabricaRemota.java
│       ├── LojaRemota.java
│       ├── ClientesRemoto.java
│       ├── Estado*.java      # Classes de estado
│       └── LogService.java
│
├── fabrica/                  # Módulo de produção
│   └── src/main/java/br/com/veiculos/fabrica/
│       ├── Ferramenta.java
│       ├── Funcionario.java
│       ├── Estacao.java
│       ├── FabricaImpl.java
│       └── FabricaMain.java
│
├── loja/                     # Módulo de lojas
│   └── src/main/java/br/com/veiculos/loja/
│       ├── LojaImpl.java
│       └── LojaMain.java
│
├── clientes/                 # Módulo de clientes
│   └── src/main/java/br/com/veiculos/clientes/
│       ├── Cliente.java
│       └── ClientesMain.java
│
├── compilar.bat              # Script de compilação
├── 1-iniciar-fabrica.bat     # Iniciar fábrica
├── 2-iniciar-loja1.bat       # Iniciar loja 1
├── 3-iniciar-loja2.bat       # Iniciar loja 2
├── 4-iniciar-loja3.bat       # Iniciar loja 3
└── 5-iniciar-clientes.bat    # Iniciar clientes
```

---

## 📊 Logs Gerados

Durante a execução, o sistema gera:
- `fabrica.log` - Produções e vendas para lojas
- `loja_1.log` - Recebimentos e vendas da Loja 1
- `loja_2.log` - Recebimentos e vendas da Loja 2
- `loja_3.log` - Recebimentos e vendas da Loja 3
- `clientes.log` - Compras dos 20 clientes

---

## 🎯 Características Técnicas

| Componente | Quantidade | Configuração |
|------------|------------|--------------|
| Estações | 4 | IDs 1-4 |
| Funcionários por estação | 5 | IDs 0-4 |
| Ferramentas por estação | 5 | Compartilhadas em círculo |
| Estoque global | 500 | Semaphore(500) |
| Esteira da fábrica | 40 | Capacidade máxima |
| Lojas | 3 | IDs 1-3 |
| Esteira de cada loja | 20 | Capacidade padrão |
| Clientes | 20 | IDs 1-20 |
| Garagem de cada cliente | 10 | Capacidade padrão |

**Total de Threads:** ~71 (20 funcionários + lojas + clientes + RMI internas)

---

## 🔧 Requisitos

- **Java:** 17 ou superior (projeto usa Java 24)
- **Sistema Operacional:** Windows (scripts .bat) ou Linux/Mac (adaptar scripts)
- **Portas:** 1099, 1100, 1101, 1102, 1103 livres

---

## 🎓 Para a Defesa

Prepare-se estudando:

1. **[ENTENDENDO_O_CODIGO.md](ENTENDENDO_O_CODIGO.md)** - Seção completa com 20+ perguntas
2. Execute o sistema e observe o comportamento
3. Leia os logs gerados
4. Entenda o fluxo: Funcionário → Esteira Fábrica → Loja → Cliente

**Perguntas mais prováveis:**
- Como evita deadlock no Jantar dos Filósofos?
- Como funciona a `EsteiraCircular`?
- Por que usar semáforos em vez de `synchronized`?
- Como funciona a comunicação RMI?
- O que acontece se a fábrica ficar sem estoque?

---

## 🐛 Troubleshooting

### Erro: "Address already in use"
```cmd
# Mate todos os processos Java
taskkill /F /IM java.exe
```

### Erro: "Connection refused"
- Inicie a fábrica primeiro
- Aguarde 5 segundos entre cada componente
- Verifique se o firewall não está bloqueando

### Recompilar
```cmd
compilar.bat
```

---

## 👨‍💻 Autor

Sistema desenvolvido para demonstrar conceitos de:
- Programação Concorrente
- Sistemas Distribuídos
- RMI (Remote Method Invocation)
- Sincronização com Semáforos

---

## 📖 Referências

- "Operating System Concepts" - Silberschatz
- "Java Concurrency in Practice" - Brian Goetz
- [Tutorial RMI Oracle](https://docs.oracle.com/javase/tutorial/rmi/)
- [Semaphores em Java](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Semaphore.html)

---

## ⚡ Quick Start

```cmd
1-iniciar-fabrica.bat
2-iniciar-loja1.bat
3-iniciar-loja2.bat
4-iniciar-loja3.bat
5-iniciar-clientes.bat
```

**Pronto! Sistema executando! 🚀**
