# Como Executar o Sistema de Veículos em Rede

## Visão geral

O sistema é composto por **5 processos Java independentes** que se comunicam via RMI.
Cada processo pode rodar em um PC diferente na mesma rede local.

```
PC1 (Fábrica) ←── PC2 (Loja 1)
                ←── PC3 (Loja 2)  ←── PC5 (Clientes)
                ←── PC4 (Loja 3)
```

---

## Exemplo com 5 PCs

| PC  | Processo    | IP exemplo    | Porta RMI |
|-----|-------------|---------------|-----------|
| PC1 | Fábrica     | 192.168.1.10  | 1099      |
| PC2 | Loja 1      | 192.168.1.11  | 1100      |
| PC3 | Loja 2      | 192.168.1.12  | 1101      |
| PC4 | Loja 3      | 192.168.1.13  | 1102      |
| PC5 | Clientes    | 192.168.1.14  | 1103      |

---

## Pré-requisitos

- Java 17+ instalado em todos os PCs
- O projeto compilado (`compilar.bat`) em todos os PCs
- As portas 1099–1103 liberadas no firewall de cada PC
- Todos os PCs na mesma rede local (ou com roteamento configurado)

### Compilar (rodar uma vez em cada PC)

```bat
compilar.bat
```

---

## Ordem de inicialização (IMPORTANTE)

> As lojas precisam que a fábrica já esteja no ar.
> Os clientes precisam que as 3 lojas já estejam no ar.

```
1. PC1 → Fábrica
2. PC2 → Loja 1    (aguardar a fábrica subir)
3. PC3 → Loja 2    (aguardar a fábrica subir)
4. PC4 → Loja 3    (aguardar a fábrica subir)
5. PC5 → Clientes  (aguardar as 3 lojas subirem)
```

---

## Comandos por PC

### PC1 — Fábrica (192.168.1.10)

Edite `1-iniciar-fabrica.bat` e ajuste:
```bat
SET MEU_IP=192.168.1.10
SET PORTA_FABRICA=1099
```
Depois execute:
```bat
1-iniciar-fabrica.bat
```

Ou diretamente no terminal:
```bat
java -Djava.rmi.server.hostname=192.168.1.10 ^
     -cp comum\target\classes;fabrica\target\classes ^
     br.com.veiculos.fabrica.FabricaMain 1099
```

---

### PC2 — Loja 1 (192.168.1.11)

Edite `2-iniciar-loja1.bat` e ajuste:
```bat
SET MEU_IP=192.168.1.11
SET IP_FABRICA=192.168.1.10
SET PORTA_FABRICA=1099
SET PORTA_LOJA=1100
```
Depois execute:
```bat
2-iniciar-loja1.bat
```

Ou diretamente:
```bat
java -Djava.rmi.server.hostname=192.168.1.11 ^
     -cp comum\target\classes;loja\target\classes ^
     br.com.veiculos.loja.LojaMain 1 192.168.1.10 20 1099 1100
```

---

### PC3 — Loja 2 (192.168.1.12)

Edite `3-iniciar-loja2.bat` e ajuste:
```bat
SET MEU_IP=192.168.1.12
SET IP_FABRICA=192.168.1.10
```
Depois execute:
```bat
3-iniciar-loja2.bat
```

Ou diretamente:
```bat
java -Djava.rmi.server.hostname=192.168.1.12 ^
     -cp comum\target\classes;loja\target\classes ^
     br.com.veiculos.loja.LojaMain 2 192.168.1.10 20 1099 1101
```

---

### PC4 — Loja 3 (192.168.1.13)

Edite `4-iniciar-loja3.bat` e ajuste:
```bat
SET MEU_IP=192.168.1.13
SET IP_FABRICA=192.168.1.10
```
Depois execute:
```bat
4-iniciar-loja3.bat
```

Ou diretamente:
```bat
java -Djava.rmi.server.hostname=192.168.1.13 ^
     -cp comum\target\classes;loja\target\classes ^
     br.com.veiculos.loja.LojaMain 3 192.168.1.10 20 1099 1102
```

---

### PC5 — Clientes (192.168.1.14)

Edite `5-iniciar-clientes.bat` e ajuste:
```bat
SET MEU_IP=192.168.1.14
SET IP_LOJA1=192.168.1.11
SET IP_LOJA2=192.168.1.12
SET IP_LOJA3=192.168.1.13
```
Depois execute:
```bat
5-iniciar-clientes.bat
```

Ou diretamente:
```bat
java -Djava.rmi.server.hostname=192.168.1.14 ^
     -cp comum\target\classes;clientes\target\classes ^
     br.com.veiculos.clientes.ClientesMain ^
     192.168.1.11 192.168.1.12 192.168.1.13 ^
     10 1100 1101 1102 1103
```

---

## Rodando tudo no mesmo PC (modo local)

Se quiser testar com um único PC, use `localhost` em todos os scripts e mantenha
as portas padrão. Os `.bat` originais já fazem isso — basta mudar `MEU_IP`
para `127.0.0.1` ou `localhost`:

```bat
:: Em todos os bats, troque:
SET MEU_IP=localhost
SET IP_FABRICA=localhost
SET IP_LOJA1=localhost
SET IP_LOJA2=localhost
SET IP_LOJA3=localhost
```

---

## Por que o `-Djava.rmi.server.hostname` é obrigatório em rede

Sem essa propriedade, o Java RMI anuncia o IP interno da máquina nos stubs
enviados aos clientes. Em redes com múltiplas interfaces (Wi-Fi + cabo, VPN, etc.)
o IP detectado automaticamente pode ser o errado, fazendo as chamadas remotas
falharem com `Connection refused`.

A propriedade força o RMI a usar o IP que você especificou — que é o acessível
pelos outros PCs na rede.

---

## Portas usadas (liberar no firewall)

| Porta | Processo   |
|-------|------------|
| 1099  | Fábrica    |
| 1100  | Loja 1     |
| 1101  | Loja 2     |
| 1102  | Loja 3     |
| 1103  | Clientes   |

No Windows, para liberar uma porta via PowerShell (executar como Administrador):
```powershell
New-NetFirewallRule -DisplayName "RMI Fabrica" -Direction Inbound -Protocol TCP -LocalPort 1099 -Action Allow
New-NetFirewallRule -DisplayName "RMI Loja1"   -Direction Inbound -Protocol TCP -LocalPort 1100 -Action Allow
New-NetFirewallRule -DisplayName "RMI Loja2"   -Direction Inbound -Protocol TCP -LocalPort 1101 -Action Allow
New-NetFirewallRule -DisplayName "RMI Loja3"   -Direction Inbound -Protocol TCP -LocalPort 1102 -Action Allow
New-NetFirewallRule -DisplayName "RMI Clientes" -Direction Inbound -Protocol TCP -LocalPort 1103 -Action Allow
```

---

## Referência rápida dos argumentos

### FabricaMain
```
java FabricaMain [portaRegistry]
                  └─ padrão: 1099
```

### LojaMain
```
java LojaMain <idLoja> <hostFabrica> [capacidade] [portaFabrica] [portaLoja]
               1..3     IP do PC1      padrão:20    padrão:1099   padrão:1099+id
```

### ClientesMain
```
java ClientesMain <hostLoja1> <hostLoja2> <hostLoja3>
                  [capacidade] [portaLoja1] [portaLoja2] [portaLoja3] [portaClientes]
                   padrão:10    padrão:1100  padrão:1101  padrão:1102  padrão:1103
```
