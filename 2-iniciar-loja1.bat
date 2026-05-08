@echo off
title LOJA 1 - Sistema de Veiculos

:: =====================================================
:: PC1 — roda a LOJA 1 (mesmo PC da fabrica)
:: Edite MEU_IP com o IP desta maquina (ipconfig)
:: IP_FABRICA = mesmo IP pois esta no mesmo PC
:: =====================================================
SET MEU_IP=192.168.1.10
SET IP_FABRICA=192.168.1.10

echo ========================================
echo INICIANDO LOJA 1
echo ========================================
echo Meu IP   : %MEU_IP%
echo Porta    : 1100
echo Fabrica  : %IP_FABRICA%:1099
echo.

java -Djava.rmi.server.hostname=%MEU_IP% ^
     -cp comum\target\classes;loja\target\classes ^
     br.com.veiculos.loja.LojaMain 1 %IP_FABRICA% 20

pause
