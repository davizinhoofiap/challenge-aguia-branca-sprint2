package com.aguiabranca.inovacao.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aguiabranca.inovacao.data.firebase.AuthService
import com.aguiabranca.inovacao.data.model.UserProfile
import com.aguiabranca.inovacao.data.repository.InovacaoRepository
import com.aguiabranca.inovacao.ui.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * LoginViewModel — gerencia autenticação via Firebase Auth.
 *
 * Observa o Flow de autenticação do AuthService para
 * redirecionar automaticamente se o usuário já estiver logado
 * (ex: app reaberto após sessão ativa).
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService,
    private val repository: InovacaoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Flow derivado do AuthService — a NavGraph observa este Flow
     * para navegar para Home quando o login for confirmado pelo Firebase.
     */
    val estaLogado: StateFlow<Boolean> = authService.usuarioAtualFlow
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), authService.estaLogado)

    // ── Handlers de formulário (State Hoisting) ───────────────────────

    fun onEmailChange(valor: String) {
        _uiState.update { it.copy(email = valor, emailErro = null, erroLogin = null) }
    }

    fun onSenhaChange(valor: String) {
        _uiState.update { it.copy(senha = valor, senhaErro = null, erroLogin = null) }
    }

    fun toggleSenhaVisivel() {
        _uiState.update { it.copy(senhaVisivel = !it.senhaVisivel) }
    }

    // ── Login com e-mail e senha ──────────────────────────────────────

    fun loginComEmail() {
        if (!validar()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isCarregando = true, erroLogin = null) }

            authService.loginComEmail(_uiState.value.email, _uiState.value.senha)
                .onSuccess { user ->
                    // Garante que o documento do usuário existe no Firestore
                    repository.garantirUsuarioCriado(
                        uid    = user.uid,
                        nome   = user.displayName ?: user.email ?: "Usuário",
                        email  = user.email ?: "",
                        perfil = UserProfile.OPERACIONAL
                    )
                    // O Flow estaLogado emitirá true automaticamente
                    _uiState.update { it.copy(isCarregando = false) }
                }
                .onFailure { erro ->
                    _uiState.update {
                        it.copy(
                            isCarregando = false,
                            erroLogin = traduzirErroFirebase(erro.message)
                        )
                    }
                }
        }
    }

    // ── Login com Google ──────────────────────────────────────────────
    // idToken vem do resultado do GoogleSignIn na Activity/Screen

    fun loginComGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCarregando = true, erroLogin = null) }

            authService.loginComGoogle(idToken)
                .onSuccess { user ->
                    repository.garantirUsuarioCriado(
                        uid   = user.uid,
                        nome  = user.displayName ?: "Usuário Google",
                        email = user.email ?: ""
                    )
                    _uiState.update { it.copy(isCarregando = false) }
                }
                .onFailure { erro ->
                    _uiState.update {
                        it.copy(isCarregando = false, erroLogin = erro.message)
                    }
                }
        }
    }

    fun logout() = authService.logout()

    // ── Validação local antes de chamar Firebase ──────────────────────

    private fun validar(): Boolean {
        val state = _uiState.value
        var valido = true

        if (state.email.isBlank() || !state.email.contains("@")) {
            _uiState.update { it.copy(emailErro = "Informe um e-mail válido") }
            valido = false
        }
        if (state.senha.length < 6) {
            _uiState.update { it.copy(senhaErro = "A senha deve ter ao menos 6 caracteres") }
            valido = false
        }
        return valido
    }

    // ── Tradução das mensagens de erro do Firebase ────────────────────

    private fun traduzirErroFirebase(mensagem: String?): String {
        return when {
            mensagem == null                          -> "Erro desconhecido"
            mensagem.contains("no user record")      -> "E-mail não cadastrado"
            mensagem.contains("password is invalid") -> "Senha incorreta"
            mensagem.contains("badly formatted")     -> "E-mail inválido"
            mensagem.contains("network error")       -> "Sem conexão com a internet"
            mensagem.contains("too many requests")   -> "Muitas tentativas. Aguarde e tente novamente"
            else                                     -> "Erro ao fazer login. Tente novamente"
        }
    }
}
