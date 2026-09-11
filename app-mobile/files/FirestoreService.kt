package com.aguiabranca.inovacao.data.firebase

import com.aguiabranca.inovacao.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Camada de acesso ao Firestore.
 *
 * Estrutura das coleções:
 *   /usuarios/{uid}
 *   /ideias/{ideiaId}
 *   /projetos/{projetoId}
 *   /orientacoes/{orientacaoId}
 *
 * Funções que precisam de atualização em tempo real retornam Flow<T>.
 * Funções pontuais (criar, atualizar) são suspend fun que retornam Result<T>.
 */
@Singleton
class FirestoreService @Inject constructor(
    private val db: FirebaseFirestore
) {

    // ════════════════════════════════════════════════════════════
    // USUÁRIOS
    // ════════════════════════════════════════════════════════════

    suspend fun getUsuario(uid: String): Result<Usuario> {
        return try {
            val snap = db.collection("usuarios").document(uid).get().await()
            val usuario = snap.toUsuario()
                ?: return Result.failure(Exception("Usuário não encontrado"))
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun salvarUsuario(usuario: Usuario): Result<Unit> {
        return try {
            db.collection("usuarios")
                .document(usuario.id)
                .set(usuario.toMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ════════════════════════════════════════════════════════════
    // ORIENTAÇÕES ESTRATÉGICAS
    // ════════════════════════════════════════════════════════════

    /**
     * Retorna Flow — a UI recompõe automaticamente quando
     * uma orientação é adicionada/editada no Firestore.
     */
    fun getOrientacoesFlow(): Flow<List<OrientacaoEstrategica>> {
        return db.collection("orientacoes")
            .orderBy("prioridade", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toOrientacao() }
            }
    }

    // ════════════════════════════════════════════════════════════
    // IDEIAS
    // ════════════════════════════════════════════════════════════

    /** Flow das ideias de um autor específico, ordenadas por data */
    fun getIdeiasDoAutorFlow(autorId: String): Flow<List<Ideia>> {
        return db.collection("ideias")
            .whereEqualTo("autorId", autorId)
            .orderBy("dataCriacao", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.documents.mapNotNull { doc -> doc.toIdeia() } }
    }

    /** Flow de ideias com status EM_AVALIACAO (para gestores) */
    fun getIdeiasParaAvaliarFlow(): Flow<List<Ideia>> {
        return db.collection("ideias")
            .whereEqualTo("status", IdeiaStatus.EM_AVALIACAO.name)
            .orderBy("dataCriacao", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.documents.mapNotNull { doc -> doc.toIdeia() } }
    }

    /** Busca pontual de todas as ideias (para dashboard) */
    suspend fun getTodasIdeias(): Result<List<Ideia>> {
        return try {
            val snap = db.collection("ideias").get().await()
            Result.success(snap.documents.mapNotNull { it.toIdeia() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun salvarIdeia(ideia: Ideia): Result<Unit> {
        return try {
            db.collection("ideias")
                .document(ideia.id)
                .set(ideia.toMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Atualiza apenas o campo status + justificativa da avaliação.
     * Uso de update() em vez de set() preserva os outros campos.
     */
    suspend fun atualizarStatusIdeia(
        ideiaId: String,
        novoStatus: IdeiaStatus,
        justificativa: String
    ): Result<Unit> {
        return try {
            db.collection("ideias").document(ideiaId).update(
                mapOf(
                    "status" to novoStatus.name,
                    "justificativaAvaliacao" to justificativa,
                    "dataAvaliacao" to com.google.firebase.Timestamp.now()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun incrementarVoto(ideiaId: String): Result<Unit> {
        return try {
            db.collection("ideias").document(ideiaId)
                .update("votos", com.google.firebase.firestore.FieldValue.increment(1))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ════════════════════════════════════════════════════════════
    // PROJETOS
    // ════════════════════════════════════════════════════════════

    fun getProjetosFlow(): Flow<List<Projeto>> {
        return db.collection("projetos")
            .orderBy("dataInicio", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.documents.mapNotNull { doc -> doc.toProjeto() } }
    }

    suspend fun salvarProjeto(projeto: Projeto): Result<Unit> {
        return try {
            db.collection("projetos")
                .document(projeto.id)
                .set(projeto.toMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun atualizarProgressoProjeto(
        projetoId: String,
        progresso: Float,
        roi: Double? = null
    ): Result<Unit> {
        return try {
            val campos = mutableMapOf<String, Any>("progresso" to progresso)
            roi?.let { campos["roi"] = it }
            db.collection("projetos").document(projetoId).update(campos).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// ════════════════════════════════════════════════════════════
// Extension functions: DocumentSnapshot → Domain Model
// ════════════════════════════════════════════════════════════

private fun com.google.firebase.firestore.DocumentSnapshot.toUsuario(): Usuario? {
    return try {
        Usuario(
            id      = id,
            nome    = getString("nome") ?: return null,
            cargo   = getString("cargo") ?: "",
            divisao = getString("divisao") ?: "",
            perfil  = UserProfile.valueOf(getString("perfil") ?: UserProfile.OPERACIONAL.name)
        )
    } catch (e: Exception) { null }
}

private fun com.google.firebase.firestore.DocumentSnapshot.toOrientacao(): OrientacaoEstrategica? {
    return try {
        OrientacaoEstrategica(
            id           = id,
            titulo       = getString("titulo") ?: return null,
            descricao    = getString("descricao") ?: "",
            pilar        = getString("pilar") ?: "",
            dataCriacao  = getString("dataCriacao") ?: "",
            prioridade   = getLong("prioridade")?.toInt() ?: 1
        )
    } catch (e: Exception) { null }
}

private fun com.google.firebase.firestore.DocumentSnapshot.toIdeia(): Ideia? {
    return try {
        Ideia(
            id              = id,
            titulo          = getString("titulo") ?: return null,
            descricao       = getString("descricao") ?: "",
            categoria       = getString("categoria") ?: "",
            autorId         = getString("autorId") ?: "",
            autorNome       = getString("autorNome") ?: "",
            divisao         = getString("divisao") ?: "",
            status          = IdeiaStatus.valueOf(getString("status") ?: IdeiaStatus.CAPTURADA.name),
            dataCriacao     = getString("dataCriacao") ?: "",
            impactoEstimado = getString("impactoEstimado") ?: "MEDIO",
            votos           = getLong("votos")?.toInt() ?: 0,
            comentarios     = getLong("comentarios")?.toInt() ?: 0
        )
    } catch (e: Exception) { null }
}

private fun com.google.firebase.firestore.DocumentSnapshot.toProjeto(): Projeto? {
    return try {
        @Suppress("UNCHECKED_CAST")
        Projeto(
            id                  = id,
            titulo              = getString("titulo") ?: return null,
            descricao           = getString("descricao") ?: "",
            ideiaOrigemId       = getString("ideiaOrigemId") ?: "",
            responsavel         = getString("responsavel") ?: "",
            equipe              = get("equipe") as? List<String> ?: emptyList(),
            status              = ProjetoStatus.valueOf(getString("status") ?: ProjetoStatus.PLANEJAMENTO.name),
            progresso           = getDouble("progresso")?.toFloat() ?: 0f,
            dataInicio          = getString("dataInicio") ?: "",
            dataPrevisao        = getString("dataPrevisao") ?: "",
            roi                 = getDouble("roi"),
            reducaoCustos       = getDouble("reducaoCustos"),
            ganhoProdutividade  = getDouble("ganhoProdutividade")
        )
    } catch (e: Exception) { null }
}

// ════════════════════════════════════════════════════════════
// Extension functions: Domain Model → Map (para salvar no Firestore)
// ════════════════════════════════════════════════════════════

private fun Usuario.toMap() = mapOf(
    "nome"    to nome,
    "cargo"   to cargo,
    "divisao" to divisao,
    "perfil"  to perfil.name
)

private fun Ideia.toMap() = mapOf(
    "titulo"          to titulo,
    "descricao"       to descricao,
    "categoria"       to categoria,
    "autorId"         to autorId,
    "autorNome"       to autorNome,
    "divisao"         to divisao,
    "status"          to status.name,
    "dataCriacao"     to dataCriacao,
    "impactoEstimado" to impactoEstimado,
    "votos"           to votos,
    "comentarios"     to comentarios
)

private fun Projeto.toMap() = mapOf(
    "titulo"              to titulo,
    "descricao"           to descricao,
    "ideiaOrigemId"       to ideiaOrigemId,
    "responsavel"         to responsavel,
    "equipe"              to equipe,
    "status"              to status.name,
    "progresso"           to progresso,
    "dataInicio"          to dataInicio,
    "dataPrevisao"        to dataPrevisao,
    "roi"                 to roi,
    "reducaoCustos"       to reducaoCustos,
    "ganhoProdutividade"  to ganhoProdutividade
)
