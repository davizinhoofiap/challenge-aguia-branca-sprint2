package com.example.projetoguiabranca.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetoguiabranca.data.repository.DashboardRepository
import com.example.projetoguiabranca.data.repository.ProjetoRepository
import com.example.projetoguiabranca.ui.state.DashboardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val projetoRepository: ProjetoRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        dashboardRepository.dashboardData,
        projetoRepository.projetos
    ) { dados, projetos ->
        if (dados == null) {
            DashboardUiState.Loading
        } else {
            DashboardUiState.Success(dados, projetos)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState.Loading)

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            dashboardRepository.carregarDashboard()
            projetoRepository.carregarProjetos()
        }
    }
}
