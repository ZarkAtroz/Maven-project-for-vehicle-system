@echo off
title FABRICA - Sistema de Veiculos
:: ============================================================
:: CONFIGURACAO — edite MEU_IP com o IP desta maquina (PC1)
SET MEU_IP=192.168.1.10
SET PORTA_FABRICA=1099
:: ============================================================
echo ========================================
echo INICIANDO FABRICA
echo ========================================
echo IP desta maquina : %MEU_IP%
echo Porta RMI        : %PORTA_FABRICA%
echo.

java -Djava.rmi.server.hostname=%MEU_IP% ^
     -cp comum\target\classes;fabrica\target\classes ^
     br.com.veiculos.fabrica.FabricaMain %PORTA_FABRICA%

pause
