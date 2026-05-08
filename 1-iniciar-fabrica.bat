@echo off
title FABRICA - Sistema de Veiculos

:: =====================================================
:: PC1 — roda a FABRICA
:: Edite MEU_IP com o IP desta maquina (ipconfig)
:: =====================================================
SET MEU_IP=localhost

echo ========================================
echo INICIANDO FABRICA
echo ========================================
echo Meu IP  : %MEU_IP%
echo Porta   : 1099
echo.

java -Djava.rmi.server.hostname=%MEU_IP% ^
     -cp comum\target\classes;fabrica\target\classes ^
     br.com.veiculos.fabrica.FabricaMain

pause
