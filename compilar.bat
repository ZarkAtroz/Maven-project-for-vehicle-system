@echo off
echo ========================================
echo Compilando Sistema de Veiculos
echo ========================================

echo.
echo [1/4] Compilando modulo COMUM...
javac -d comum\target\classes comum\src\main\java\br\com\veiculos\comum\*.java
if %errorlevel% neq 0 (
    echo ERRO ao compilar modulo comum!
    pause
    exit /b 1
)

echo [2/4] Compilando modulo FABRICA...
javac -cp comum\target\classes -d fabrica\target\classes fabrica\src\main\java\br\com\veiculos\fabrica\*.java
if %errorlevel% neq 0 (
    echo ERRO ao compilar modulo fabrica!
    pause
    exit /b 1
)

echo [3/4] Compilando modulo LOJA...
javac -cp comum\target\classes -d loja\target\classes loja\src\main\java\br\com\veiculos\loja\*.java
if %errorlevel% neq 0 (
    echo ERRO ao compilar modulo loja!
    pause
    exit /b 1
)

echo [4/4] Compilando modulo CLIENTES...
javac -cp comum\target\classes -d clientes\target\classes clientes\src\main\java\br\com\veiculos\clientes\*.java
if %errorlevel% neq 0 (
    echo ERRO ao compilar modulo clientes!
    pause
    exit /b 1
)

echo.
echo ========================================
echo Compilacao concluida com sucesso!
echo ========================================
echo.
pause
