package com.example.projetoguiabranca.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetoguiabranca.data.repository.AuthRepository
import com.example.projetoguiabranca.ui.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val estaLogado: StateFlow<Boolean> = authRepository.usuarioAtual
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), authRepository.estaAutenticado())

    fun onEmailChange(valor: String) {
        _uiState.update { it.copy(email = valor, emailErro = null, erroLogin = null) }
    }

    fun onSenhaChange(valor: String) {
        _uiState.update { it.copy(senha = valor, senhaErro = null, erroLogin = null) }
    }

    fun toggleSenhaVisivel() {
        _uiState.update { it.copy(senhaVisivel = !it.senhaVisivel) }
    }

    fun loginComEmail() {
        if (!validar()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isCarregando = true, erroLogin = null) }
            authRepository.login(_uiState.value.email, _uiState.value.senha)
                .onSuccess {
                    _uiState.update { it.copy(isCarregando = false) }
                }
                .onFailure { erro ->
                    _uiState.update {
                        it.copy(
                            isCarregando = false,
                            erroLogin = erro.localizedMessage ?: "Erro ao fazer login. Verifique as credenciais."
                        )
                    }
                }
        }
    }

    fun loginComGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCarregando = true, erroLogin = null) }
            authRepository.login("operador@aguiabranca.com.br", "123456")
                .onSuccess {
                    _uiState.update { it.copy(isCarregando = false) }
                }
                .onFailure { erro ->
                    _uiState.update {
                        it.copy(isCarregando = false, erroLogin = erro.message)
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun alternarModo() {
        _uiState.update { 
            it.copy(
                isModoRegistro = !it.isModoRegistro,
                erroLogin = null,
                emailErro = null,
                senhaErro = null,
                nomeErro = null,
                cargoErro = null
            ) 
        }
    }

    fun onNomeChange(valor: String) {
        _uiState.update { it.copy(nome = valor, nomeErro = null, erroLogin = null) }
    }

    fun onCargoChange(valor: String) {
        _uiState.update { it.copy(cargo = valor, cargoErro = null, erroLogin = null) }
    }

    fun onDivisaoChange(valor: String) {
        _uiState.update { it.copy(divisao = valor) }
    }

    fun onPerfilChange(valor: String) {
        _uiState.update { it.copy(perfil = valor) }
    }

    fun preencherCredenciais(email: String, senha: String) {
        _uiState.update { 
            it.copy(
                email = email,
                senha = senha,
                isModoRegistro = false,
                emailErro = null,
                senhaErro = null,
                erroLogin = null
            ) 
        }
    }

    fun registrar() {
        if (!validarRegistro()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isCarregando = true, erroLogin = null) }
            val state = _uiState.value
            authRepository.registrar(
                nome = state.nome.trim(),
                email = state.email.trim(),
                senha = state.senha,
                cargo = state.cargo.trim(),
                divisao = state.divisao,
                perfil = state.perfil
            ).onSuccess {
                _uiState.update { it.copy(isCarregando = false) }
            }.onFailure { erro ->
                _uiState.update { 
                    it.copy(
                        isCarregando = false,
                        erroLogin = erro.localizedMessage ?: "Erro ao registrar usuário."
                    )
                }
            }
        }
    }

    private fun validarRegistro(): Boolean {
        val state = _uiState.value
        var valido = true
        if (state.nome.isBlank()) {
            _uiState.update { it.copy(nomeErro = "Informe seu nome completo") }
            valido = false
        }
        if (state.email.isBlank() || !state.email.contains("@")) {
            _uiState.update { it.copy(emailErro = "Informe um e-mail corporativo válido") }
            valido = false
        }
        if (state.senha.length < 6) {
            _uiState.update { it.copy(senhaErro = "A senha deve ter ao menos 6 caracteres") }
            valido = false
        }
        if (state.cargo.isBlank()) {
            _uiState.update { it.copy(cargoErro = "Informe seu cargo") }
            valido = false
        }
        return valido
    }

    private fun validar(): Boolean {
        val state = _uiState.value
        var valido = true
        if (state.email.isBlank() || !state.email.contains("@")) {
            _uiState.update { it.copy(emailErro = "Informe um e-mail corporativo válido") }
            valido = false
        }
        if (state.senha.length < 6) {
            _uiState.update { it.copy(senhaErro = "A senha deve ter ao menos 6 caracteres") }
            valido = false
        }
        return valido
    }
}