@echo off
REM Script para executar o Compilador C -> LLVM

IF "%~2"=="" (
    echo Uso: compilador.bat [arquivo_entrada.c] [arquivo_saida.ll]
    exit /b 1
)

echo Compilando %1 para %2...
call mvn -q exec:java -Dexec.mainClass="Main.Main" -Dexec.args="%1 %2"

echo.
echo Processo finalizado.