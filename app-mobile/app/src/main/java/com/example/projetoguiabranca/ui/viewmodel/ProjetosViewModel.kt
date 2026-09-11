package com.example.projetoguiabranca.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetoguiabranca.data.model.Projeto
import com.example.projetoguiabranca.data.repository.ProjetoRepository
import com.example.projetoguiabranca.ui.state.ProjetosUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjetosViewModel @Inject constructor(
    private val projetoRepository: ProjetoRepository
) : ViewModel() {

    private val _projetoSelecionado = MutableStateFlow<Projeto?>(null)

    val uiState: StateFlow<ProjetosUiState> = combine(
        projetoRepository.projetos,
        _projetoSelecionado
    ) { lista, selecionado ->
        ProjetosUiState.Success(
            projetos = lista,
            projetoSelecionado = selecionado
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProjetosUiState.Loading)

    init {
        carregarProjetos()
    }

    fun carregarProjetos() {
        viewModelScope.launch {
            projetoRepository.carregarProjetos()
        }
    }

    fun selecionarProjeto(projeto: Projeto?) {
        _projetoSelecionado.value = projeto
    }
}
