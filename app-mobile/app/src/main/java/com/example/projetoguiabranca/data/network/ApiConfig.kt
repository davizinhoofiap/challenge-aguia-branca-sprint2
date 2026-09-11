package com.example.projetoguiabranca.data.network

/**
 * CONFIGURACAO CENTRALIZADA DA URL DA API (.NET 8):
 *
 * 1. Emulador do Android Studio (Padrao):
 *    -> "http://10.0.2.2:5000/api/" (o IP 10.0.2.2 e o alias do emulador para o localhost da sua maquina).
 *
 * 2. Celular Fisico via Cabo USB (Recomendado para apresentacao):
 *    -> Conecte o cabo USB com depuracao ativada.
 *    -> Execute no terminal do PC: adb reverse tcp:5000 tcp:5000
 *    -> Pode manter "http://10.0.2.2:5000/api/" (o AuthInterceptor do app tenta 10.0.2.2 e faz fallback automatico para 127.0.0.1).
 *
 * 3. Celular Fisico via Wi-Fi (Sem cabo):
 *    -> O celular e o computador precisam estar conectados na mesma rede Wi-Fi.
 *    -> Descubra o IP local do seu computador no terminal: ipconfig (no Windows) -> IPv4 (ex: 192.168.1.15).
 *    -> Altere a constante BASE_URL abaixo para: "http://192.168.1.15:5000/api/"
 */
object ApiConfig {
    const val BASE_URL: String = "http://10.0.2.2:5000/api/"
}
