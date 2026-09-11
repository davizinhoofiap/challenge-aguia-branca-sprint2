package com.example.projetoguiabranca.ui.state

import com.example.projetoguiabranca.data.model.*

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val usuario: Usuario,
        val orientacoes: List<OrientacaoEstrategica>,
        val minhasIdeias: List<Ideia>,
        val dashboard: DashboardData
    ) : HomeUiState()
    data class Error(val mensagem: String) : HomeUiState()
}

data class CapturaIdeiaUiState(
    val titulo: String = "",
    val descricao: String = "",
    val categoria: String = "",
    val impactoEstimado: String = "MEDIO",
    val tituloErro: String? = null,
    val descricaoErro: String? = null,
    val categoriaErro: String? = null,
    val isSalvando: Boolean = false,
    val sucessoAoSalvar: Boolean = false,
    val erroAoSalvar: String? = null
) {
    val formularioValido: Boolean
        get() = titulo.isNotBlank() && descricao.isNotBlank() && categoria.isNotBlank()
    val categoriasDisponiveis: List<String>
        get() = listOf("Tecnologia", "Processos", "Pessoas", "Comunicação", "Segurança", "Outros")
    val impactosDisponiveis: List<String>
        get() = listOf("ALTO", "MEDIO", "BAIXO")
}

sealed class GestaoIdeiasUiState {
    object Loading : GestaoIdeiasUiState()
    data class Success(
        val ideiasParaAvaliar: List<Ideia>,
        val minhasIdeias: List<Ideia>,
        val filtroAtivo: FiltroIdeias = FiltroIdeias.TODAS,
        val ideiaEmAvaliacao: Ideia? = null,
        val justificativaAvaliacao: String = "",
        val isProcessandoAvaliacao: Boolean = false
    ) : GestaoIdeiasUiState()
    data class Error(val mensagem: String) : GestaoIdeiasUiState()
}

enum class FiltroIdeias {
    TODAS, EM_AVALIACAO, APROVADAS, REPROVADAS
}

sealed class ProjetosUiState {
    object Loading : ProjetosUiState()
    data class Success(
        val projetos: List<Projeto>,
        val projetoSelecionado: Projeto? = null
    ) : ProjetosUiState()
    data class Error(val mensagem: String) : ProjetosUiState()
}

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(
        val dashboard: DashboardData,
        val projetos: List<Projeto>
    ) : DashboardUiState()
    data class Error(val mensagem: String) : DashboardUiState()
}