package com.example.projetoguiabranca.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetoguiabranca.data.model.OrientacaoEstrategica
import com.example.projetoguiabranca.data.repository.EstrategiaRepository
import com.example.projetoguiabranca.data.repository.IdeiaRepository
import com.example.projetoguiabranca.ui.state.CapturaIdeiaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CapturaIdeiaViewModel @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val estrategiaRepository: EstrategiaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CapturaIdeiaUiState())
    val uiState: StateFlow<CapturaIdeiaUiState> = _uiState.asStateFlow()

    val orientacoes: StateFlow<List<OrientacaoEstrategica>> = estrategiaRepository.orientacoes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            estrategiaRepository.carregarOrientacoes()
        }
    }

    fun onTituloChange(valor: String) {
        _uiState.update { it.copy(titulo = valor, tituloErro = null) }
    }

    fun onDescricaoChange(valor: String) {
        _uiState.update { it.copy(descricao = valor, descricaoErro = null) }
    }

    fun onCategoriaChange(valor: String) {
        _uiState.update { it.copy(categoria = valor, categoriaErro = null) }
    }

    fun onImpactoChange(valor: String) {
        _uiState.update { it.copy(impactoEstimado = valor) }
    }

    fun submeter() {
        if (!validar()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSalvando = true, erroAoSalvar = null) }
            val primeiraEstrategia = orientacoes.value.firstOrNull()?.id
            ideiaRepository.criarIdeia(
                titulo = _uiState.value.titulo,
                descricao = _uiState.value.descricao,
                categoria = _uiState.value.categoria,
                impactoEstimado = _uiState.value.impactoEstimado,
                estrategiaId = primeiraEstrategia
            ).onSuccess {
                _uiState.update { it.copy(isSalvando = false, sucessoAoSalvar = true) }
            }.onFailure { erro ->
                _uiState.update { it.copy(isSalvando = false, erroAoSalvar = erro.message) }
            }
        }
    }

    fun resetarSucesso() {
        _uiState.update { CapturaIdeiaUiState() }
    }

    private fun validar(): Boolean {
        val state = _uiState.value
        var valido = true
        if (state.titulo.isBlank()) {
            _uiState.update { it.copy(tituloErro = "Informe um título claro para a ideia") }
            valido = false
        }
        if (state.descricao.isBlank()) {
            _uiState.update { it.copy(descricaoErro = "Descreva a ideia e como ela resolve o problema") }
            valido = false
        }
        if (state.categoria.isBlank()) {
            _uiState.update { it.copy(categoriaErro = "Selecione uma categoria") }
            valido = false
        }
        return valido
    }
}
