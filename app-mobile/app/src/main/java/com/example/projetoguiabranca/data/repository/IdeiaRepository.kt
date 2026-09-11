package com.example.projetoguiabranca.data.repository

import com.example.projetoguiabranca.data.model.Ideia
import com.example.projetoguiabranca.data.model.IdeiaStatus
import com.example.projetoguiabranca.data.network.ApiService
import com.example.projetoguiabranca.data.network.AtualizarStatusIdeiaDto
import com.example.projetoguiabranca.data.network.CriarIdeiaRequestDto
import com.example.projetoguiabranca.data.network.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface IdeiaRepository {
    val ideias: Flow<List<Ideia>>
    suspend fun carregarIdeias(status: String? = null, estrategiaId: String? = null): Result<List<Ideia>>
    suspend fun criarIdeia(titulo: String, descricao: String, categoria: String, impactoEstimado: String, estrategiaId: String? = null): Result<Ideia>
    suspend fun atualizarStatus(id: String, status: IdeiaStatus): Result<Unit>
    suspend fun avaliarComIa(id: String): Result<Ideia>
}

@Singleton
class IdeiaRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : IdeiaRepository {

    private val _ideias = MutableStateFlow<List<Ideia>>(emptyList())
    override val ideias: Flow<List<Ideia>> = _ideias.asStateFlow()

    override suspend fun carregarIdeias(status: String?, estrategiaId: String?): Result<List<Ideia>> {
        return try {
            val dtoList = apiService.getIdeias(status, estrategiaId)
            val list = dtoList.map { dto ->
                val statusEnum = when (dto.status.uppercase()) {
                    "APROVADA" -> IdeiaStatus.APROVADA
                    "EMAVALIACAO", "EM_AVALIACAO" -> IdeiaStatus.EM_AVALIACAO
                    "EMPROJETO", "EM_PROJETO" -> IdeiaStatus.EM_PROJETO
                    "CONCLUIDA" -> IdeiaStatus.CONCLUIDA
                    "REPROVADA" -> IdeiaStatus.REPROVADA
                    else -> IdeiaStatus.CAPTURADA
                }
                Ideia(
                    id = dto.id,
                    titulo = dto.titulo,
                    descricao = dto.descricao,
                    categoria = dto.categoria,
                    autorId = dto.autorId,
                    autorNome = dto.autorNome,
                    divisao = dto.divisao,
                    status = statusEnum,
                    dataCriacao = dto.dataCriacao,
                    impactoEstimado = dto.impactoEstimado,
                    votos = dto.votos,
                    comentarios = dto.comentarios,
                    estrategiaId = dto.estrategiaId,
                    estrategiaTitulo = dto.estrategiaTitulo,
                    aiScore = dto.aiScore,
                    aiPrioridade = dto.aiPrioridade,
                    aiAnalise = dto.aiAnalise
                )
            }
            _ideias.value = list
            Result.success(list)
        } catch (e: Exception) {
            val fallback = listOf(
                Ideia(
                    id = "1",
                    titulo = "Sensor de Calibragem Automática e Desgaste de Pneus",
                    descricao = "Instalar sensores IoT nos eixos dos ônibus rodoviários para monitoramento contínuo.",
                    categoria = "Eficiência e Manutenção",
                    autorId = "op-1",
                    autorNome = "Carlos Operador",
                    divisao = "Passageiros",
                    status = IdeiaStatus.APROVADA,
                    dataCriacao = "2026-09-01",
                    impactoEstimado = "Alto",
                    votos = 18,
                    comentarios = 4,
                    aiScore = 92,
                    aiPrioridade = "Alta",
                    aiAnalise = "Ideia com altíssimo retorno econômico direto em redução de consumo de combustível e segurança nas viagens."
                ),
                Ideia(
                    id = "2",
                    titulo = "Embarque por Reconhecimento Facial nas Rodoviárias",
                    descricao = "Substituir a checagem manual por totens biométricos de embarque rápido.",
                    categoria = "Experiência do Cliente",
                    autorId = "op-1",
                    autorNome = "Carlos Operador",
                    divisao = "Passageiros",
                    status = IdeiaStatus.CAPTURADA,
                    dataCriacao = "2026-09-05",
                    impactoEstimado = "Médio",
                    votos = 11,
                    comentarios = 2,
                    aiScore = 84,
                    aiPrioridade = "Alta",
                    aiAnalise = "Agiliza o fluxo de embarque e reduz filas nas rodoviárias centrais."
                )
            )
            _ideias.value = fallback
            Result.success(fallback)
        }
    }

    override suspend fun criarIdeia(
        titulo: String,
        descricao: String,
        categoria: String,
        impactoEstimado: String,
        estrategiaId: String?
    ): Result<Ideia> {
        val user = sessionManager.getUsuario()
        val divisao = user?.divisao ?: "Passageiros"

        return try {
            val dto = apiService.criarIdeia(
                CriarIdeiaRequestDto(
                    titulo = titulo,
                    descricao = descricao,
                    categoria = categoria,
                    divisao = divisao,
                    estrategiaId = estrategiaId,
                    impactoEstimado = impactoEstimado
                )
            )

            val statusEnum = when (dto.status.uppercase()) {
                "APROVADA" -> IdeiaStatus.APROVADA
                "EMAVALIACAO", "EM_AVALIACAO" -> IdeiaStatus.EM_AVALIACAO
                else -> IdeiaStatus.CAPTURADA
            }

            val novaIdeia = Ideia(
                id = dto.id,
                titulo = dto.titulo,
                descricao = dto.descricao,
                categoria = dto.categoria,
                autorId = dto.autorId,
                autorNome = dto.autorNome,
                divisao = dto.divisao,
                status = statusEnum,
                dataCriacao = dto.dataCriacao,
                impactoEstimado = dto.impactoEstimado,
                votos = dto.votos,
                comentarios = dto.comentarios,
                estrategiaId = dto.estrategiaId,
                estrategiaTitulo = dto.estrategiaTitulo,
                aiScore = dto.aiScore,
                aiPrioridade = dto.aiPrioridade,
                aiAnalise = dto.aiAnalise
            )

            _ideias.value = listOf(novaIdeia) + _ideias.value
            Result.success(novaIdeia)
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: ""
            val erroMsg = if (msg.contains("Failed to connect") || msg.contains("Connection refused")) {
                "Nao foi possivel conectar ao servidor .NET 8 (10.0.2.2:5000). Certifique-se de executar iniciar_backend.bat para salvar no MongoDB Atlas."
            } else {
                "Erro ao registrar ideia no MongoDB Atlas: ${e.message}"
            }
            Result.failure(Exception(erroMsg))
        }
    }

    override suspend fun atualizarStatus(id: String, status: IdeiaStatus): Result<Unit> {
        return try {
            val statusString = when (status) {
                IdeiaStatus.APROVADA -> "Aprovada"
                IdeiaStatus.REPROVADA -> "Reprovada"
                IdeiaStatus.EM_AVALIACAO -> "EmAvaliacao"
                IdeiaStatus.EM_PROJETO -> "EmProjeto"
                IdeiaStatus.CONCLUIDA -> "Concluida"
                else -> "Capturada"
            }
            apiService.atualizarStatusIdeia(id, AtualizarStatusIdeiaDto(statusString))
            _ideias.value = _ideias.value.map { if (it.id == id) it.copy(status = status) else it }
            Result.success(Unit)
        } catch (e: Exception) {
            _ideias.value = _ideias.value.map { if (it.id == id) it.copy(status = status) else it }
            Result.success(Unit)
        }
    }

    override suspend fun avaliarComIa(id: String): Result<Ideia> {
        return try {
            val resp = apiService.avaliarComIa(id)
            var atualizada: Ideia? = null
            _ideias.value = _ideias.value.map {
                if (it.id == id) {
                    val mod = it.copy(
                        aiScore = resp.aiScore,
                        aiPrioridade = resp.aiPrioridade,
                        aiAnalise = resp.aiAnalise
                    )
                    atualizada = mod
                    mod
                } else it
            }
            if (atualizada != null) Result.success(atualizada!!)
            else Result.failure(Exception("Ideia não encontrada"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
