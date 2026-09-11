package com.example.projetoguiabranca.data.repository

import com.example.projetoguiabranca.data.model.UserProfile
import com.example.projetoguiabranca.data.model.Usuario
import com.example.projetoguiabranca.data.network.ApiService
import com.example.projetoguiabranca.data.network.LoginRequestDto
import com.example.projetoguiabranca.data.network.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    val usuarioAtual: Flow<Usuario?>
    suspend fun login(email: String, senha: String): Result<Usuario>
    suspend fun registrar(nome: String, email: String, senha: String, cargo: String, divisao: String, perfil: String): Result<Usuario>
    suspend fun logout()
    fun estaAutenticado(): Boolean
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    private val _usuarioAtual = MutableStateFlow<Usuario?>(sessionManager.getUsuario())
    override val usuarioAtual: Flow<Usuario?> = _usuarioAtual.asStateFlow()

    override suspend fun login(email: String, senha: String): Result<Usuario> {
        return try {
            val response = apiService.login(LoginRequestDto(email, senha))

            sessionManager.salvarSessao(
                token = response.token,
                usuarioId = response.usuarioId,
                nome = response.nome,
                email = response.email,
                perfil = response.perfil,
                cargo = response.cargo,
                divisao = response.divisao
            )

            val perfilEnum = when (response.perfil.uppercase()) {
                "LIDER", "ESTRATEGICO" -> UserProfile.ESTRATEGICO
                "GESTOR", "TATICO" -> UserProfile.TATICO
                else -> UserProfile.OPERACIONAL
            }

            val usuario = Usuario(
                id = response.usuarioId,
                nome = response.nome,
                cargo = response.cargo,
                divisao = response.divisao,
                perfil = perfilEnum
            )

            _usuarioAtual.value = usuario
            Result.success(usuario)
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: ""
            if (msg.contains("Failed to connect") || msg.contains("Connection refused") || msg.contains("CLEARTEXT")) {
                Result.failure(Exception("Sem comunicacao com o servidor .NET 8 (10.0.2.2:5000). Execute iniciar_backend.bat para conectar ao MongoDB Atlas."))
            } else {
                Result.failure(Exception("Credenciais invalidas ou erro no servidor: ${e.message}"))
            }
        }
    }

    override suspend fun registrar(
        nome: String,
        email: String,
        senha: String,
        cargo: String,
        divisao: String,
        perfil: String
    ): Result<Usuario> {
        return try {
            val response = apiService.registrar(
                com.example.projetoguiabranca.data.network.RegistroRequestDto(
                    nome = nome,
                    email = email,
                    senha = senha,
                    cargo = cargo,
                    divisao = divisao,
                    perfil = perfil
                )
            )

            sessionManager.salvarSessao(
                token = response.token,
                usuarioId = response.usuarioId,
                nome = response.nome,
                email = response.email,
                perfil = response.perfil,
                cargo = response.cargo,
                divisao = response.divisao
            )

            val perfilEnum = when (response.perfil.uppercase()) {
                "LIDER", "ESTRATEGICO" -> UserProfile.ESTRATEGICO
                "GESTOR", "TATICO" -> UserProfile.TATICO
                else -> UserProfile.OPERACIONAL
            }

            val usuario = Usuario(
                id = response.usuarioId,
                nome = response.nome,
                cargo = response.cargo,
                divisao = response.divisao,
                perfil = perfilEnum
            )

            _usuarioAtual.value = usuario
            Result.success(usuario)
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: ""
            if (msg.contains("Failed to connect") || msg.contains("Connection refused")) {
                Result.failure(Exception("Nao foi possivel conectar ao backend .NET 8. Inicie o servidor via iniciar_backend.bat."))
            } else {
                Result.failure(Exception("Erro ao cadastrar usuario no MongoDB Atlas: ${e.message}"))
            }
        }
    }

    override suspend fun logout() {
        sessionManager.limparSessao()
        _usuarioAtual.value = null
    }

    override fun estaAutenticado(): Boolean = sessionManager.estaLogado()
}
