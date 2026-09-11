package com.example.projetoguiabranca.data.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = sessionManager.getToken()

        val builder = originalRequest.newBuilder()
            .header("Content-Type", "application/json; charset=utf-8")
        if (!token.isNullOrBlank()) {
            builder.header("Authorization", "Bearer $token")
        }

        val request = builder.build()

        return try {
            chain.proceed(request)
        } catch (e: Exception) {
            val url = request.url
            // Se falhou ao conectar em 10.0.2.2 (emulador), tenta automaticamente 127.0.0.1 (celular USB via adb reverse)
            if (url.host == "10.0.2.2") {
                val fallbackUrl = url.newBuilder().host("127.0.0.1").build()
                val fallbackRequest = request.newBuilder().url(fallbackUrl).build()
                try {
                    chain.proceed(fallbackRequest)
                } catch (_: Exception) {
                    throw e
                }
            } else if (url.host == "127.0.0.1" || url.host == "localhost") {
                val fallbackUrl = url.newBuilder().host("10.0.2.2").build()
                val fallbackRequest = request.newBuilder().url(fallbackUrl).build()
                try {
                    chain.proceed(fallbackRequest)
                } catch (_: Exception) {
                    throw e
                }
            } else {
                throw e
            }
        }
    }
}
