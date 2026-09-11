package com.example.projetoguiabranca.data.network

import android.content.Context
import android.content.SharedPreferences
import com.example.projetoguiabranca.data.model.UserProfile
import com.example.projetoguiabranca.data.model.Usuario
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("gab_inovacao_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NOME = "user_nome"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PERFIL = "user_perfil"
        private const val KEY_USER_CARGO = "user_cargo"
        private const val KEY_USER_DIVISAO = "user_divisao"
        private const val KEY_BASE_URL = "custom_base_url"
    }

    fun salvarSessao(
        token: String,
        usuarioId: String,
        nome: String,
        email: String,
        perfil: String,
        cargo: String,
        divisao: String
    ) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_ID, usuarioId)
            .putString(KEY_USER_NOME, nome)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_PERFIL, perfil)
            .putString(KEY_USER_CARGO, cargo)
            .putString(KEY_USER_DIVISAO, divisao)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUsuario(): Usuario? {
        val id = prefs.getString(KEY_USER_ID, null) ?: return null
        val nome = prefs.getString(KEY_USER_NOME, "") ?: ""
        val email = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        val cargo = prefs.getString(KEY_USER_CARGO, "") ?: ""
        val divisao = prefs.getString(KEY_USER_DIVISAO, "") ?: ""
        val perfilStr = prefs.getString(KEY_USER_PERFIL, "Operador") ?: "Operador"

        val userProfile = when (perfilStr.uppercase()) {
            "LIDER", "ESTRATEGICO" -> UserProfile.ESTRATEGICO
            "GESTOR", "TATICO" -> UserProfile.TATICO
            else -> UserProfile.OPERACIONAL
        }

        return Usuario(
            id = id,
            nome = nome,
            cargo = cargo,
            divisao = divisao,
            perfil = userProfile
        )
    }

    fun estaLogado(): Boolean = !getToken().isNullOrBlank()

    fun limparSessao() {
        prefs.edit().clear().apply()
    }

    fun getBaseUrl(): String = prefs.getString(KEY_BASE_URL, ApiConfig.BASE_URL) ?: ApiConfig.BASE_URL

    fun setBaseUrl(url: String) {
        prefs.edit().putString(KEY_BASE_URL, url).apply()
    }
}
