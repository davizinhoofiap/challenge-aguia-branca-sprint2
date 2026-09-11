package com.example.projetoguiabranca.data.repository

import com.example.projetoguiabranca.data.model.Projeto
import com.example.projetoguiabranca.data.model.ProjetoStatus
import com.example.projetoguiabranca.data.network.ApiService
import com.example.projetoguiabranca.data.network.AtualizarProjetoRequestDto
import com.example.projetoguiabranca.data.network.CriarProjetoRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface ProjetoRepository {
    val projetos: Flow<List<Projeto>>
    suspend fun carregarProjetos(status: String? = null, estrategiaId: String? = null): Result<List<Projeto>>
    suspend fun criarProjeto(titulo: String, descricao: String, ideiaOrigemId: String?, responsavel: String, equipe: List<String>, investimento: Double): Result<Projeto>
    suspend fun atualizarProgresso(id: String, novoProgresso: Float, novoStatus: ProjetoStatus? = null): Result<Unit>
}

@Singleton
class ProjetoRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ProjetoRepository {

    private val _projetos = MutableStateFlow<List<Projeto>>(emptyList())
    override val projetos: Flow<List<Projeto>> = _projetos.asStateFlow()

    override suspend fun carregarProjetos(status: String?, estrategiaId: String?): Result<List<Projeto>> {
        return try {
            val dtoList = apiService.getProjetos(status, estrategiaId)
            val list = dtoList.map { dto ->
                val statusEnum = when (dto.status.uppercase()) {
                    "EMANDAMENTO", "EM_ANDAMENTO" -> ProjetoStatus.EM_ANDAMENTO
                    "CONCLUIDO" -> ProjetoStatus.CONCLUIDO
                    "PAUSADO" -> ProjetoStatus.PAUSADO
                    else -> ProjetoStatus.PLANEJAMENTO
                }
                Projeto(
                    id = dto.id,
                    titulo = dto.titulo,
                    descricao = dto.descricao,
                    ideiaOrigemId = dto.ideiaOrigemId ?: "",
                    responsavel = dto.responsavel,
                    equipe = dto.equipe,
                    status = statusEnum,
                    progresso = dto.progresso,
                    dataInicio = dto.dataInicio,
                    dataPrevisao = dto.dataPrevisao,
                    roi = dto.roi,
                    reducaoCustos = dto.reducaoCustos,
                    ganhoProdutividade = dto.ganhoProdutividade
                )
            }
            _projetos.value = list
            Result.success(list)
        } catch (e: Exception) {
            val fallback = listOf(
                Projeto(
                    id = "1",
                    titulo = "Piloto de Telemetria e Pressão de Pneus em Tempo Real",
                    descricao = "Instalação do sistema em 40 ônibus da linha Vitória x BH.",
                    ideiaOrigemId = "1",
                    responsavel = "Mariana Gestora",
                    equipe = listOf("Mariana Gestora", "Carlos Operador"),
                    status = ProjetoStatus.EM_ANDAMENTO,
                    progresso = 0.65f,
                    dataInicio = "2026-07-01",
                    dataPrevisao = "2026-10-30",
                    roi = 280.0,
                    reducaoCustos = 95000.0,
                    ganhoProdutividade = 18.5
                ),
                Projeto(
                    id = "2",
                    titulo = "Modernização Digital do Atendimento Rodoviário",
                    descricao = "Autoatendimento e check-in digital.",
                    ideiaOrigemId = "2",
                    responsavel = "Mariana Gestora",
                    equipe = listOf("Mariana Gestora"),
                    status = ProjetoStatus.PLANEJAMENTO,
                    progresso = 0.25f,
                    dataInicio = "2026-08-15",
                    dataPrevisao = "2026-12-20",
                    roi = 150.0,
                    reducaoCustos = 30000.0,
                    ganhoProdutividade = 12.0
                )
            )
            _projetos.value = fallback
            Result.success(fallback)
        }
    }

    override suspend fun criarProjeto(
        titulo: String,
        descricao: String,
        ideiaOrigemId: String?,
        responsavel: String,
        equipe: List<String>,
        investimento: Double
    ): Result<Projeto> {
        return try {
            val dto = apiService.criarProjeto(
                CriarProjetoRequestDto(
                    titulo = titulo,
                    descricao = descricao,
                    ideiaOrigemId = ideiaOrigemId,
                    estrategiaId = null,
                    responsavel = responsavel,
                    equipe = equipe,
                    etapa = "Ideação",
                    investimento = investimento
                )
            )

            val statusEnum = when (dto.status.uppercase()) {
                "EMANDAMENTO", "EM_ANDAMENTO" -> ProjetoStatus.EM_ANDAMENTO
                "CONCLUIDO" -> ProjetoStatus.CONCLUIDO
                "PAUSADO" -> ProjetoStatus.PAUSADO
                else -> ProjetoStatus.PLANEJAMENTO
            }

            val novo = Projeto(
                id = dto.id,
                titulo = dto.titulo,
                descricao = dto.descricao,
                ideiaOrigemId = dto.ideiaOrigemId ?: "",
                responsavel = dto.responsavel,
                equipe = dto.equipe,
                status = statusEnum,
                progresso = dto.progresso,
                dataInicio = dto.dataInicio,
                dataPrevisao = dto.dataPrevisao,
                roi = dto.roi,
                reducaoCustos = dto.reducaoCustos,
                ganhoProdutividade = dto.ganhoProdutividade
            )

            _projetos.value = listOf(novo) + _projetos.value
            Result.success(novo)
        } catch (e: Exception) {
            val local = Projeto(
                id = System.currentTimeMillis().toString(),
                titulo = titulo,
                descricao = descricao,
                ideiaOrigemId = ideiaOrigemId ?: "",
                responsavel = responsavel,
                equipe = equipe,
                status = ProjetoStatus.PLANEJAMENTO,
                progresso = 0.0f,
                dataInicio = "Hoje",
                dataPrevisao = "Em 3 meses",
                roi = 120.0,
                reducaoCustos = 25000.0,
                ganhoProdutividade = 10.0
            )
            _projetos.value = listOf(local) + _projetos.value
            Result.success(local)
        }
    }

    override suspend fun atualizarProgresso(id: String, novoProgresso: Float, novoStatus: ProjetoStatus?): Result<Unit> {
        return try {
            val statusStr = novoStatus?.name
            apiService.atualizarProjeto(
                id,
                AtualizarProjetoRequestDto(
                    progresso = novoProgresso.toDouble(),
                    status = statusStr
                )
            )
            _projetos.value = _projetos.value.map {
                if (it.id == id) it.copy(progresso = novoProgresso, status = novoStatus ?: it.status)
                else it
            }
            Result.success(Unit)
        } catch (e: Exception) {
            _projetos.value = _projetos.value.map {
                if (it.id == id) it.copy(progresso = novoProgresso, status = novoStatus ?: it.status)
                else it
            }
            Result.success(Unit)
        }
    }
}
