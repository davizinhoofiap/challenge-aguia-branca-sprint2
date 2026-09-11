@echo off
title Backend Inovacao Aguia Branca - .NET 8
color 0B
echo ====================================================================
echo        GRUPO AGUIA BRANCA - PLATAFORMA DE INOVACAO CORPORATIVA
echo                    BACKEND .NET 8 COM MONGODB ATLAS
echo ====================================================================
echo.
echo [1/3] Verificando ambiente .NET SDK 8...
where dotnet >nul 2>nul
if %errorlevel% neq 0 (
    set "PATH=%LocalAppData%\Microsoft\dotnet;%PATH%"
)

where dotnet >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERRO] .NET 8 SDK nao encontrado no sistema.
    echo Por favor, instale o .NET 8 SDK ou execute manualmente pelo Visual Studio / VS Code.
    pause
    exit /b 1
)

echo [OK] .NET 8 SDK detectado.
echo.
echo [2/3] Acessando diretorio do projeto...
cd /d "%~dp0backend\src"

if not exist "appsettings.json" (
    if exist "appsettings.Example.json" (
        echo [AVISO] appsettings.json nao encontrado. Criando copia a partir de appsettings.Example.json...
        copy "appsettings.Example.json" "appsettings.json" >nul
        echo [OK] appsettings.json gerado. Insira a string de conexao do MongoDB Atlas se ainda nao inseriu.
        echo.
    )
)

echo [3/3] Iniciando servidor ASP.NET Core e sincronizando com MongoDB Atlas...
echo.
echo ====================================================================
echo  API REST Ativa em:    http://localhost:5000
echo  Documentacao Swagger: http://localhost:5000/swagger
echo  Acesso Emulador:      http://10.0.2.2:5000
echo  Banco de Dados Cloud: MongoDB Atlas (InovacaoAguiaBrancaDB)
echo ====================================================================
echo Pressione Ctrl+C para encerrar o servidor.
echo.

dotnet run --urls "http://0.0.0.0:5000"

if %errorlevel% neq 0 (
    echo.
    echo [AVISO] O servidor foi finalizado ou ocorreu um erro.
    pause
)
