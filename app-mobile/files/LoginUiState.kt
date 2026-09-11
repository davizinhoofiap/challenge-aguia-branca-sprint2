package com.aguiabranca.inovacao.ui.state

// ─────────────────────────────────────────────────────────────
// UiState da tela de Login
// ─────────────────────────────────────────────────────────────

data class LoginUiState(
    val email: String = "",
    val senha: String = "",
    val emailErro: String? = null,
    val senhaErro: String? = null,
    val isCarregando: Boolean = false,
    val erroLogin: String? = null,
    val loginSucesso: Boolean = false,
    val senhaVisivel: Boolean = false
) {
    val formularioValido: Boolean
        get() = email.isNotBlank() && senha.length >= 6
}
