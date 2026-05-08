@echo off
title CLIENTES - Sistema de Veiculos

:: =====================================================
:: PC3 — roda os CLIENTES
:: Edite MEU_IP com o IP desta maquina (ipconfig)
:: Edite IP_LOJA1 com o IP do PC1 (Loja 1)
:: Edite IP_LOJA2 com o IP do PC2 (Loja 2)
:: Edite IP_LOJA3 com o IP do PC2 (Loja 3)
:: =====================================================
SET MEU_IP=localhost
SET IP_LOJA1=localhost
SET IP_LOJA2=localhost
SET IP_LOJA3=localhost

echo ========================================
echo INICIANDO CLIENTES (20 threads)
echo ========================================
echo Meu IP   : %MEU_IP%
echo Porta    : 1103
echo Loja 1   : %IP_LOJA1%:1100
echo Loja 2   : %IP_LOJA2%:1101
echo Loja 3   : %IP_LOJA3%:1102
echo.

java -Djava.rmi.server.hostname=%MEU_IP% ^
     -cp comum\target\classes;clientes\target\classes ^
     br.com.veiculos.clientes.ClientesMain %IP_LOJA1% %IP_LOJA2% %IP_LOJA3% 10

pause
