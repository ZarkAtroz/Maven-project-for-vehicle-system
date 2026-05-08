@echo off
echo ========================================
echo LIMPANDO PROCESSOS JAVA
echo ========================================
echo.

echo Matando todos os processos java.exe...
taskkill /F /IM java.exe 2>nul

if %errorlevel% equ 0 (
    echo Processos Java finalizados com sucesso!
) else (
    echo Nenhum processo Java encontrado.
)

echo.
echo Aguardando 2 segundos...
timeout /t 2 /nobreak >nul

echo.
echo ========================================
echo PORTAS LIBERADAS - Pronto para executar!
echo ========================================
echo.
pause
