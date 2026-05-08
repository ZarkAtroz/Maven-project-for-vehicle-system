@echo off
title LOJA 3 - Sistema de Veiculos
:: ============================================================
:: CONFIGURACAO — edite os IPs antes de executar
SET MEU_IP=192.168.1.13
SET IP_FABRICA=192.168.1.10
SET PORTA_FABRICA=1099
SET PORTA_LOJA=1102
SET CAPACIDADE=20
:: ============================================================
echo ========================================
echo INICIANDO LOJA 3
echo ========================================
echo IP desta maquina : %MEU_IP%
echo Porta desta loja : %PORTA_LOJA%
echo Fabrica em       : %IP_FABRICA%:%PORTA_FABRICA%
echo.

java -Djava.rmi.server.hostname=%MEU_IP% ^
     -cp comum\target\classes;loja\target\classes ^
     br.com.veiculos.loja.LojaMain 3 %IP_FABRICA% %CAPACIDADE% %PORTA_FABRICA% %PORTA_LOJA%

pause
