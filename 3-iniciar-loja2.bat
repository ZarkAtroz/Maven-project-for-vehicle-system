@echo off
title LOJA 2 - Sistema de Veiculos

:: =====================================================
:: PC2 — roda a LOJA 2
:: Edite MEU_IP com o IP desta maquina (ipconfig)
:: Edite IP_FABRICA com o IP do PC1
:: =====================================================
SET MEU_IP=localhost
SET IP_FABRICA=localhost

echo ========================================
echo INICIANDO LOJA 2
echo ========================================
echo Meu IP   : %MEU_IP%
echo Porta    : 1101
echo Fabrica  : %IP_FABRICA%:1099
echo.

java -Djava.rmi.server.hostname=%MEU_IP% ^
     -cp comum\target\classes;loja\target\classes ^
     br.com.veiculos.loja.LojaMain 2 %IP_FABRICA% 20

pause
