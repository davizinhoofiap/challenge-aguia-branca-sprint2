package com.example.projetoguiabranca.data.repository

import com.example.projetoguiabranca.data.model.DashboardData
import com.example.projetoguiabranca.data.model.DashboardMetrica
import com.example.projetoguiabranca.data.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface DashboardRepository {
    val dashboardData: Flow<DashboardData?>
    suspend fun carregarDashboard(): Result<DashboardData>
}

@Singleton
class DashboardRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : DashboardRepository {

    private val _dashboardData = MutableStateFlow<DashboardData?>(null)
    override val dashboardData: Flow<DashboardData?> = _dashboardData.asStateFlow()

    override suspend fun carregarDashboard(): Result<DashboardData> {
        return try {
            val dto = apiService.getDashboardResumo()
            val metricas = dto.metricas.map {
                DashboardMetrica(
                    label = it.label,
                    valor = it.valor,
                    variacao = it.variacao,
                    positivo = it.positivo
                )
            }
            val data = DashboardData(
                totalIdeias = dto.totalIdeias,
                ideiasAprovadas = dto.ideiasAprovadas,
                projetosAtivos = dto.projetosAtivos,
                roiMedio = "${dto.roiMedioPercentual}%",
                metricas = metricas
            )
            _dashboardData.value = data
            Result.success(data)
        } catch (e: Exception) {
            val fallback = DashboardData(
                totalIdeias = 3,
                ideiasAprovadas = 1,
                projetosAtivos = 2,
                roiMedio = "215%",
                metricas = listOf(
                    DashboardMetrica("ROI Médio", "215%", "+18% vs trimestre anterior", true),
                    DashboardMetrica("Economia Operacional", "R$ 125.000", "+24% redução de custos", true),
                    DashboardMetrica("Lucro Gerado", "R$ 140.000", "+32% novas receitas", true),
                    DashboardMetrica("Ganho de Produtividade", "15.2%", "+5.4% no ano", true)
                )
            )
            _dashboardData.value = fallback
            Result.success(fallback)
        }
    }
}
