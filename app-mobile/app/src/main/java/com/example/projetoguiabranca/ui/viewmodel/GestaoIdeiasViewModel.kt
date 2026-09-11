package com.example.projetoguiabranca.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetoguiabranca.data.model.Ideia
import com.example.projetoguiabranca.data.model.IdeiaStatus
import com.example.projetoguiabranca.data.network.SessionManager
import com.example.projetoguiabranca.data.repository.IdeiaRepository
import com.example.projetoguiabranca.ui.state.FiltroIdeias
import com.example.projetoguiabranca.ui.state.GestaoIdeiasUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestaoIdeiasViewModel @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _filtroAtivo = MutableStateFlow(FiltroIdeias.TODAS)
    private val _ideiaEmAvaliacao = MutableStateFlow<Ideia?>(null)
    private val _justificativa = MutableStateFlow("")
    private val _isProcessando = MutableStateFlow(false)

    val uiState: StateFlow<GestaoIdeiasUiState> = combine(
        ideiaRepository.ideias,
        _filtroAtivo,
        _ideiaEmAvaliacao,
        _justificativa,
        _isProcessando
    ) { ideias: List<Ideia>, filtro: FiltroIdeias, ideiaModal: Ideia?, justificativa: String, processando: Boolean ->
        val usuario = sessionManager.getUsuario()
        val usuarioId = usuario?.id ?: ""
        val minhasIdeias = ideias.filter { it.autorId == usuarioId || it.autorNome == usuario?.nome }

        val filtradas = when (filtro) {
            FiltroIdeias.TODAS -> ideias
            FiltroIdeias.EM_AVALIACAO -> ideias.filter { it.status == IdeiaStatus.CAPTURADA || it.status == IdeiaStatus.EM_AVALIACAO }
            FiltroIdeias.APROVADAS -> ideias.filter { it.status == IdeiaStatus.APROVADA }
            FiltroIdeias.REPROVADAS -> ideias.filter { it.status == IdeiaStatus.REPROVADA }
        }

        val ordenadas = filtradas.sortedByDescending { it.aiScore }

        GestaoIdeiasUiState.Success(
            ideiasParaAvaliar = ordenadas,
            minhasIdeias = minhasIdeias,
            filtroAtivo = filtro,
            ideiaEmAvaliacao = ideiaModal,
            justificativaAvaliacao = justificativa,
            isProcessandoAvaliacao = processando
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GestaoIdeiasUiState.Loading)

    init {
        carregarIdeias()
    }

    fun carregarIdeias() {
        viewModelScope.launch {
            ideiaRepository.carregarIdeias()
        }
    }

    fun selecionarFiltro(novoFiltro: FiltroIdeias) {
        _filtroAtivo.value = novoFiltro
    }

    fun onFiltroChange(novoFiltro: FiltroIdeias) {
        _filtroAtivo.value = novoFiltro
    }

    fun abrirModalAvaliacao(ideia: Ideia) {
        _ideiaEmAvaliacao.value = ideia
        _justificativa.value = ""
    }

    fun fecharModalAvaliacao() {
        _ideiaEmAvaliacao.value = null
        _justificativa.value = ""
    }

    fun onJustificativaChange(valor: String) {
        _justificativa.value = valor
    }

    fun avaliarIdeia(aprovada: Boolean) {
        val ideia = _ideiaEmAvaliacao.value ?: return
        viewModelScope.launch {
            _isProcessando.value = true
            val novoStatus = if (aprovada) IdeiaStatus.APROVADA else IdeiaStatus.REPROVADA
            ideiaRepository.atualizarStatus(ideia.id, novoStatus)
            _isProcessando.value = false
            fecharModalAvaliacao()
        }
    }
}
