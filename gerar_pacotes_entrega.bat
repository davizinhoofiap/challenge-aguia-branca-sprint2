@echo off
title Gerador de Pacotes de Entrega - Sprint 2 FIAP
color 0A
echo ====================================================================
echo   GERADOR DE PACOTES DE ENTREGA (.ZIP) - SPRINT 2 AGUIA BRANCA
echo ====================================================================
echo.
echo [1/2] Compactando pasta backend...
powershell -Command "Compress-Archive -Path 'backend\*' -DestinationPath 'backend.zip' -Force"
echo [OK] backend.zip gerado com sucesso!
echo.
echo [2/2] Compactando pasta app-mobile...
powershell -Command "Compress-Archive -Path 'app-mobile\*' -DestinationPath 'app-mobile.zip' -Force"
echo [OK] app-mobile.zip gerado com sucesso!
echo.
echo ====================================================================
echo  Arquivos prontos para submissao no portal da FIAP:
echo    - backend.zip
echo    - app-mobile.zip
echo    - App_Inovacao_AguiaBranca.apk
echo ====================================================================
pause
