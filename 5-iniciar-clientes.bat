@echo off
title CLIENTES - Sistema de Veiculos
:: ============================================================
:: CONFIGURACAO — edite os IPs antes de executar
SET MEU_IP=192.168.1.14
SET IP_LOJA1=192.168.1.11
SET IP_LOJA2=192.168.1.12
SET IP_LOJA3=192.168.1.13
SET PORTA_LOJA1=1100
SET PORTA_LOJA2=1101
SET PORTA_LOJA3=1102
SET PORTA_CLIENTES=1103
SET CAPACIDADE_GARAGEM=10
:: ============================================================
echo ========================================
echo INICIANDO CLIENTES (20 threads)
echo ========================================
echo IP desta maquina : %MEU_IP%
echo Porta clientes   : %PORTA_CLIENTES%
echo Loja 1 em        : %IP_LOJA1%:%PORTA_LOJA1%
echo Loja 2 em        : %IP_LOJA2%:%PORTA_LOJA2%
echo Loja 3 em        : %IP_LOJA3%:%PORTA_LOJA3%
echo.

java -Djava.rmi.server.hostname=%MEU_IP% ^
     -cp comum\target\classes;clientes\target\classes ^
     br.com.veiculos.clientes.ClientesMain ^
     %IP_LOJA1% %IP_LOJA2% %IP_LOJA3% ^
     %CAPACIDADE_GARAGEM% ^
     %PORTA_LOJA1% %PORTA_LOJA2% %PORTA_LOJA3% %PORTA_CLIENTES%

pause
