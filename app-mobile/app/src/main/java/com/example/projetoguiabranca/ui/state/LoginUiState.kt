package com.example.projetoguiabranca.ui.state

data class LoginUiState(
    val email: String = "",
    val senha: String = "",
    val nome: String = "",
    val cargo: String = "",
    val divisao: String = "Passageiros",
    val perfil: String = "Operador",
    val emailErro: String? = null,
    val senhaErro: String? = null,
    val nomeErro: String? = null,
    val cargoErro: String? = null,
    val isModoRegistro: Boolean = false,
    val isCarregando: Boolean = false,
    val erroLogin: String? = null,
    val loginSucesso: Boolean = false,
    val senhaVisivel: Boolean = false
) {
    val formularioValido: Boolean
        get() = if (isModoRegistro) {
            nome.isNotBlank() && email.contains("@") && senha.length >= 6 && cargo.isNotBlank()
        } else {
            email.isNotBlank() && senha.length >= 6
        }
}