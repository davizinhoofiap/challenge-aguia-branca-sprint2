package com.aguiabranca.inovacao.data.repository

import com.aguiabranca.inovacao.data.firebase.AuthService
import com.aguiabranca.inovacao.data.firebase.FirestoreService
import com.aguiabranca.inovacao.data.model.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositório atualizado — agora usa Firebase como fonte de dados.
 *
 * A separação Repository / FirestoreService garante que:
 *  - A lógica de negócio (ex: gerar ID, montar objeto) fica aqui
 *  - O acesso ao Firestore fica isolado no FirestoreService
 *  - ViewModels não conhecem Firebase diretamente
 */
@Singleton
class InovacaoRepository @Inject constructor(
    private val authService: AuthService,
    private val firestoreService: FirestoreService
) {

    // ── Usuário ───────────────────────────────────────────────────────

    suspend fun getUsuarioLogado(): Result<Usuario> {
        val uid = authService.usuarioAtual?.uid
            ?: return Result.failure(Exception("Nenhum usuário logado"))
        return firestoreService.getUsuario(uid)
    }

    /**
     * Chamado após o primeiro login para criar o documento do usuário
     * no Firestore caso ainda não exista.
     */
    suspend fun garantirUsuarioCriado(
        uid: String,
        nome: String,
        email: String,
        perfil: UserProfile = UserProfile.OPERACIONAL
    ): Result<Unit> {
        val existente = firestoreService.getUsuario(uid)
        if (existente.isSuccess) return Result.success(Unit) // já existe

        val novoUsuario = Usuario(
            id      = uid,
            nome    = nome,
            cargo   = "",
            divisao = "",
            perfil  = perfil
        )
        return firestoreService.salvarUsuario(novoUsuario)
    }

    // ── Orientações ───────────────────────────────────────────────────

    /** Flow em tempo real — recomposição automática quando dados mudam */
    fun getOrientacoesFlow(): Flow<List<OrientacaoEstrategica>> =
        firestoreService.getOrientacoesFlow()

    // ── Ideias ────────────────────────────────────────────────────────

    fun getIdeiasDoAutorFlow(autorId: String): Flow<List<Ideia>> =
        firestoreService.getIdeiasDoAutorFlow(autorId)

    fun getIdeiasParaAvaliarFlow(): Flow<List<Ideia>> =
        firestoreService.getIdeiasParaAvaliarFlow()

    suspend fun submeterIdeia(
        titulo: String,
        descricao: String,
        categoria: String,
        impacto: String
    ): Result<Unit> {
        val usuario = getUsuarioLogado().getOrElse {
            return Result.failure(it)
        }

        val novaIdeia = Ideia(
            id              = UUID.randomUUID().toString(),
            titulo          = titulo,
            descricao       = descricao,
            categoria       = categoria,
            autorId         = usuario.id,
            autorNome       = usuario.nome,
            divisao         = usuario.divisao,
            status          = IdeiaStatus.CAPTURADA,
            dataCriacao     = LocalDate.now().toString(),
            impactoEstimado = impacto
        )
        return firestoreService.salvarIdeia(novaIdeia)
    }

    suspend fun avaliarIdeia(
        ideiaId: String,
        aprovada: Boolean,
        justificativa: String
    ): Result<Unit> {
        val novoStatus = if (aprovada) IdeiaStatus.APROVADA else IdeiaStatus.REPROVADA
        return firestoreService.atualizarStatusIdeia(ideiaId, novoStatus, justificativa)
    }

    suspend fun votarIdeia(ideiaId: String): Result<Unit> =
        firestoreService.incrementarVoto(ideiaId)

    // ── Projetos ──────────────────────────────────────────────────────

    fun getProjetosFlow(): Flow<List<Projeto>> =
        firestoreService.getProjetosFlow()

    suspend fun salvarProjeto(projeto: Projeto): Result<Unit> =
        firestoreService.salvarProjeto(projeto)

    suspend fun atualizarProgresso(
        projetoId: String,
        progresso: Float,
        roi: Double? = null
    ): Result<Unit> = firestoreService.atualizarProgressoProjeto(projetoId, progresso, roi)

    // ── Dashboard (calculado a partir do Firestore) ───────────────────

    suspend fun getDashboard(): Result<DashboardData> {
        return try {
            val ideias   = firestoreService.getTodasIdeias().getOrElse { emptyList() }
            val projetos = firestoreService.getProjetosFlow()
            // Cálculo das métricas a partir dos dados reais
            val total      = ideias.size
            val aprovadas  = ideias.count { it.status == IdeiaStatus.APROVADA }
            val emProjeto  = ideias.count { it.status == IdeiaStatus.EM_PROJETO }

            Result.success(
                DashboardData(
                    totalIdeias      = total,
                    ideiasAprovadas  = aprovadas,
                    projetosAtivos   = emProjeto,
                    roiMedio         = "—",  // calculado do portfólio
                    metricas         = listOf(
                        DashboardMetrica("Total de Ideias", total.toString(), "", true),
                        DashboardMetrica("Aprovadas", aprovadas.toString(), "", true),
                        DashboardMetrica("Em Projeto", emProjeto.toString(), "", true),
                        DashboardMetrica("Taxa Aprovação",
                            if (total > 0) "${(aprovadas * 100 / total)}%" else "0%", "", true)
                    )
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
