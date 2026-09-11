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
    set "PATH=%LocalAppData%\Microsoft\dotnet;%ProgramFiles%\dotnet;%ProgramFiles(x86)%\dotnet;%PATH%"
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
    echo [CONFIG] appsettings.json nao encontrado. Configurando automaticamente com MongoDB Atlas...
    (
        echo {
        echo   "Logging": {
        echo     "LogLevel": {
        echo       "Default": "Information",
        echo       "Microsoft.AspNetCore": "Warning"
        echo     }
        echo   },
        echo   "AllowedHosts": "*",
        echo   "MongoDbSettings": {
        echo     "ConnectionString": "mongodb+srv://davizinhofiap:DtNasc%%23070704@cluster0.nljwos1.mongodb.net/?retryWrites=true^&w=majority",
        echo     "DatabaseName": "InovacaoAguiaBrancaDB"
        echo   },
        echo   "JwtSettings": {
        echo     "Secret": "AguiaBrancaInovacaoSecretKey2026SuperSegura!",
        echo     "Issuer": "InovacaoAguiaBranca",
        echo     "Audience": "InovacaoAguiaBrancaApp",
        echo     "ExpirationHours": 24
        echo   },
        echo   "GeminiSettings": {
        echo     "ApiKey": "",
        echo     "Model": "gemini-1.5-flash",
        echo     "BaseUrl": "https://generativelanguage.googleapis.com/v1beta/models"
        echo   }
        echo }
    ) > "appsettings.json"
    echo [OK] appsettings.json gerado e conectado com sucesso ao Atlas na nuvem!
    echo.
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
