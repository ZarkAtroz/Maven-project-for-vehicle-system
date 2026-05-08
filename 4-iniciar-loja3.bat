@echo off
title LOJA 3 - Sistema de Veiculos

:: =====================================================
:: PC2 — roda a LOJA 3 (mesmo PC da Loja 2)
:: Edite MEU_IP com o IP desta maquina (ipconfig)
:: Edite IP_FABRICA com o IP do PC1
:: =====================================================
SET MEU_IP=localhost
SET IP_FABRICA=localhost

echo ========================================
echo INICIANDO LOJA 3
echo ========================================
echo Meu IP   : %MEU_IP%
echo Porta    : 1102
echo Fabrica  : %IP_FABRICA%:1099
echo.

java -Djava.rmi.server.hostname=%MEU_IP% ^
     -cp comum\target\classes;loja\target\classes ^
     br.com.veiculos.loja.LojaMain 3 %IP_FABRICA% 20

pause
