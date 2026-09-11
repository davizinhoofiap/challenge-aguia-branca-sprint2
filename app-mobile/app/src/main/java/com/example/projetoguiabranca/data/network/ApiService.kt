package com.example.projetoguiabranca.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.*

// Request & Response DTOs
data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("senha") val senha: String
)

data class RegistroRequestDto(
    @SerializedName("nome") val nome: String,
    @SerializedName("email") val email: String,
    @SerializedName("senha") val senha: String,
    @SerializedName("cargo") val cargo: String,
    @SerializedName("divisao") val divisao: String,
    @SerializedName("perfil") val perfil: String
)

data class AuthResponseDto(
    @SerializedName("token") val token: String,
    @SerializedName("usuarioId") val usuarioId: String,
    @SerializedName("nome") val nome: String,
    @SerializedName("email") val email: String,
    @SerializedName("perfil") val perfil: String,
    @SerializedName("cargo") val cargo: String,
    @SerializedName("divisao") val divisao: String,
    @SerializedName("expiracao") val expiracao: String
)

data class EstrategiaApiDto(
    @SerializedName("id") val id: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("campanha") val campanha: String,
    @SerializedName("pilar") val pilar: String,
    @SerializedName("prioridade") val prioridade: Int,
    @SerializedName("ativa") val ativa: Boolean,
    @SerializedName("dataCriacao") val dataCriacao: String
)

data class CriarEstrategiaRequestDto(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("campanha") val campanha: String,
    @SerializedName("pilar") val pilar: String,
    @SerializedName("prioridade") val prioridade: Int
)

data class IdeiaApiDto(
    @SerializedName("id") val id: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("autorId") val autorId: String,
    @SerializedName("autorNome") val autorNome: String,
    @SerializedName("divisao") val divisao: String,
    @SerializedName("status") val status: String,
    @SerializedName("estrategiaId") val estrategiaId: String?,
    @SerializedName("estrategiaTitulo") val estrategiaTitulo: String?,
    @SerializedName("impactoEstimado") val impactoEstimado: String,
    @SerializedName("votos") val votos: Int,
    @SerializedName("comentarios") val comentarios: Int,
    @SerializedName("dataCriacao") val dataCriacao: String,
    @SerializedName("aiScore") val aiScore: Int,
    @SerializedName("aiPrioridade") val aiPrioridade: String,
    @SerializedName("aiAnalise") val aiAnalise: String
)

data class CriarIdeiaRequestDto(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("divisao") val divisao: String,
    @SerializedName("estrategiaId") val estrategiaId: String?,
    @SerializedName("impactoEstimado") val impactoEstimado: String
)

data class AtualizarStatusIdeiaDto(
    @SerializedName("status") val status: String
)

data class AvaliarIaResponseDto(
    @SerializedName("ideiaId") val ideiaId: String,
    @SerializedName("aiScore") val aiScore: Int,
    @SerializedName("aiPrioridade") val aiPrioridade: String,
    @SerializedName("aiAnalise") val aiAnalise: String
)

data class ProjetoApiDto(
    @SerializedName("id") val id: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("ideiaOrigemId") val ideiaOrigemId: String?,
    @SerializedName("estrategiaId") val estrategiaId: String?,
    @SerializedName("estrategiaTitulo") val estrategiaTitulo: String?,
    @SerializedName("responsavel") val responsavel: String,
    @SerializedName("equipe") val equipe: List<String>,
    @SerializedName("status") val status: String,
    @SerializedName("etapa") val etapa: String,
    @SerializedName("progresso") val progresso: Float,
    @SerializedName("investimento") val investimento: Double,
    @SerializedName("roi") val roi: Double?,
    @SerializedName("lucroObtido") val lucroObtido: Double?,
    @SerializedName("reducaoCustos") val reducaoCustos: Double?,
    @SerializedName("ganhoProdutividade") val ganhoProdutividade: Double?,
    @SerializedName("dataInicio") val dataInicio: String,
    @SerializedName("dataPrevisao") val dataPrevisao: String
)

data class CriarProjetoRequestDto(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("ideiaOrigemId") val ideiaOrigemId: String?,
    @SerializedName("estrategiaId") val estrategiaId: String?,
    @SerializedName("responsavel") val responsavel: String,
    @SerializedName("equipe") val equipe: List<String>,
    @SerializedName("etapa") val etapa: String,
    @SerializedName("investimento") val investimento: Double
)

data class AtualizarProjetoRequestDto(
    @SerializedName("titulo") val titulo: String? = null,
    @SerializedName("descricao") val descricao: String? = null,
    @SerializedName("responsavel") val responsavel: String? = null,
    @SerializedName("equipe") val equipe: List<String>? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("etapa") val etapa: String? = null,
    @SerializedName("progresso") val progresso: Double? = null,
    @SerializedName("investimento") val investimento: Double? = null,
    @SerializedName("roi") val roi: Double? = null,
    @SerializedName("lucroObtido") val lucroObtido: Double? = null,
    @SerializedName("reducaoCustos") val reducaoCustos: Double? = null,
    @SerializedName("ganhoProdutividade") val ganhoProdutividade: Double? = null
)

data class MetricaItemApiDto(
    @SerializedName("label") val label: String,
    @SerializedName("valor") val valor: String,
    @SerializedName("variacao") val variacao: String,
    @SerializedName("positivo") val positivo: Boolean
)

data class RetornoEstrategiaApiDto(
    @SerializedName("estrategiaId") val estrategiaId: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("quantidadeProjetos") val quantidadeProjetos: Int,
    @SerializedName("totalInvestimento") val totalInvestimento: Double,
    @SerializedName("totalEconomia") val totalEconomia: Double,
    @SerializedName("roiMedio") val roiMedio: Double
)

data class DashboardResumoApiDto(
    @SerializedName("totalIdeias") val totalIdeias: Int,
    @SerializedName("ideiasAprovadas") val ideiasAprovadas: Int,
    @SerializedName("totalProjetos") val totalProjetos: Int,
    @SerializedName("projetosAtivos") val projetosAtivos: Int,
    @SerializedName("projetosConcluidos") val projetosConcluidos: Int,
    @SerializedName("totalInvestido") val totalInvestido: Double,
    @SerializedName("totalEconomiaGerada") val totalEconomiaGerada: Double,
    @SerializedName("totalLucroObtido") val totalLucroObtido: Double,
    @SerializedName("roiMedioPercentual") val roiMedioPercentual: Double,
    @SerializedName("ganhoProdutividadeMedio") val ganhoProdutividadeMedio: Double,
    @SerializedName("metricas") val metricas: List<MetricaItemApiDto>,
    @SerializedName("retornoPorEstrategia") val retornoPorEstrategia: List<RetornoEstrategiaApiDto>
)

interface ApiService {
    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @POST("auth/register")
    suspend fun registrar(@Body request: RegistroRequestDto): AuthResponseDto

    // Estrategias
    @GET("estrategias")
    suspend fun getEstrategias(@Query("apenasAtivas") apenasAtivas: Boolean = false): List<EstrategiaApiDto>

    @POST("estrategias")
    suspend fun criarEstrategia(@Body request: CriarEstrategiaRequestDto): EstrategiaApiDto

    // Ideias (Operador & Gestor)
    @GET("ideias")
    suspend fun getIdeias(
        @Query("status") status: String? = null,
        @Query("estrategiaId") estrategiaId: String? = null
    ): List<IdeiaApiDto>

    @POST("ideias")
    suspend fun criarIdeia(@Body request: CriarIdeiaRequestDto): IdeiaApiDto

    @PATCH("ideias/{id}/status")
    suspend fun atualizarStatusIdeia(
        @Path("id") id: String,
        @Body request: AtualizarStatusIdeiaDto
    ): IdeiaApiDto

    @POST("ideias/{id}/avaliar-ia")
    suspend fun avaliarComIa(@Path("id") id: String): AvaliarIaResponseDto

    // Projetos (Gestor & Lider)
    @GET("projetos")
    suspend fun getProjetos(
        @Query("status") status: String? = null,
        @Query("estrategiaId") estrategiaId: String? = null
    ): List<ProjetoApiDto>

    @POST("projetos")
    suspend fun criarProjeto(@Body request: CriarProjetoRequestDto): ProjetoApiDto

    @PUT("projetos/{id}")
    suspend fun atualizarProjeto(
        @Path("id") id: String,
        @Body request: AtualizarProjetoRequestDto
    ): ProjetoApiDto

    // Dashboard Executivo (Lider)
    @GET("dashboard/resumo")
    suspend fun getDashboardResumo(): DashboardResumoApiDto
}
