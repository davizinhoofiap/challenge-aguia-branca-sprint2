package com.example.projetoguiabranca.data.model

enum class UserProfile {
    OPERACIONAL,
    TATICO,
    ESTRATEGICO
}

enum class IdeiaStatus {
    CAPTURADA,
    EM_AVALIACAO,
    APROVADA,
    EM_PROJETO,
    CONCLUIDA,
    REPROVADA
}

enum class ProjetoStatus {
    PLANEJAMENTO,
    EM_ANDAMENTO,
    CONCLUIDO,
    PAUSADO
}

data class Usuario(
    val id: String,
    val nome: String,
    val cargo: String,
    val divisao: String,
    val perfil: UserProfile,
    val avatarUrl: String? = null
)

data class OrientacaoEstrategica(
    val id: String,
    val titulo: String,
    val descricao: String,
    val pilar: String,
    val dataCriacao: String,
    val prioridade: Int
)

data class Ideia(
    val id: String,
    val titulo: String,
    val descricao: String,
    val categoria: String,
    val autorId: String,
    val autorNome: String,
    val divisao: String,
    val status: IdeiaStatus,
    val dataCriacao: String,
    val impactoEstimado: String,
    val votos: Int = 0,
    val comentarios: Int = 0,
    val estrategiaId: String? = null,
    val estrategiaTitulo: String? = null,
    val aiScore: Int = 0,
    val aiPrioridade: String = "Não Avaliado",
    val aiAnalise: String = ""
)

data class Projeto(
    val id: String,
    val titulo: String,
    val descricao: String,
    val ideiaOrigemId: String,
    val responsavel: String,
    val equipe: List<String>,
    val status: ProjetoStatus,
    val progresso: Float,
    val dataInicio: String,
    val dataPrevisao: String,
    val roi: Double?,
    val reducaoCustos: Double?,
    val ganhoProdutividade: Double?
)

data class DashboardMetrica(
    val label: String,
    val valor: String,
    val variacao: String,
    val positivo: Boolean
)

data class DashboardData(
    val totalIdeias: Int,
    val ideiasAprovadas: Int,
    val projetosAtivos: Int,
    val roiMedio: String,
    val metricas: List<DashboardMetrica>
)