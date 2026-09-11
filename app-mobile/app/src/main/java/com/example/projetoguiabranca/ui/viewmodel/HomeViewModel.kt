package com.example.projetoguiabranca.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetoguiabranca.data.model.*
import com.example.projetoguiabranca.data.repository.*
import com.example.projetoguiabranca.ui.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val estrategiaRepository: EstrategiaRepository,
    private val ideiaRepository: IdeiaRepository,
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        carregarDados()
    }

    fun carregarDados() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val usuario = authRepository.usuarioAtual.firstOrNull() ?: Usuario(
                id = "op-1",
                nome = "Carlos Operador",
                cargo = "Motorista Instrutor",
                divisao = "Passageiros",
                perfil = UserProfile.OPERACIONAL
            )

            estrategiaRepository.carregarOrientacoes()
            ideiaRepository.carregarIdeias()
            dashboardRepository.carregarDashboard()

            val dashboard = dashboardRepository.dashboardData.firstOrNull() ?: DashboardData(
                totalIdeias = 3,
                ideiasAprovadas = 1,
                projetosAtivos = 2,
                roiMedio = "215%",
                metricas = emptyList()
            )

            combine(
                estrategiaRepository.orientacoes,
                ideiaRepository.ideias
            ) { orientacoes, ideias ->
                val ordenadas = ideias.reversed() // A nova ideia inserida no topo ou com ID mais recente fica primeiro
                val minhasIdeias = if (usuario.perfil == UserProfile.OPERACIONAL) {
                    val filtradas = ordenadas.filter {
                        it.autorId == usuario.id ||
                        it.autorNome.equals(usuario.nome, ignoreCase = true) ||
                        it.autorNome.contains(usuario.nome.split(" ").first(), ignoreCase = true)
                    }
                    if (filtradas.isNotEmpty()) filtradas else ordenadas
                } else {
                    ordenadas
                }

                HomeUiState.Success(
                    usuario = usuario,
                    orientacoes = orientacoes,
                    minhasIdeias = minhasIdeias,
                    dashboard = dashboard
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
