package com.example.projetoguiabranca.data.repository

import com.example.projetoguiabranca.data.model.OrientacaoEstrategica
import com.example.projetoguiabranca.data.network.ApiService
import com.example.projetoguiabranca.data.network.CriarEstrategiaRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface EstrategiaRepository {
    val orientacoes: Flow<List<OrientacaoEstrategica>>
    suspend fun carregarOrientacoes(): Result<List<OrientacaoEstrategica>>
    suspend fun criarOrientacao(titulo: String, descricao: String, pilar: String, prioridade: Int, categoria: String = "Inovação"): Result<OrientacaoEstrategica>
}

@Singleton
class EstrategiaRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : EstrategiaRepository {

    private val _orientacoes = MutableStateFlow<List<OrientacaoEstrategica>>(emptyList())
    override val orientacoes: Flow<List<OrientacaoEstrategica>> = _orientacoes.asStateFlow()

    override suspend fun carregarOrientacoes(): Result<List<OrientacaoEstrategica>> {
        return try {
            val dtoList = apiService.getEstrategias()
            val list = dtoList.map { dto ->
                OrientacaoEstrategica(
                    id = dto.id,
                    titulo = dto.titulo,
                    descricao = dto.descricao,
                    pilar = dto.pilar,
                    dataCriacao = dto.dataCriacao,
                    prioridade = dto.prioridade
                )
            }
            _orientacoes.value = list
            Result.success(list)
        } catch (e: Exception) {
            val fallback = listOf(
                OrientacaoEstrategica("1", "Eficiência Energética e Descarbonização da Frota", "Otimizar o consumo de combustível e transição para energias limpas.", "Operação Sustentável", "2026-08-01", 1),
                OrientacaoEstrategica("2", "Experiência Digital do Passageiro", "Aprimorar a jornada do cliente nos serviços de passageiros.", "Excelência ao Cliente", "2026-08-15", 2),
                OrientacaoEstrategica("3", "Segurança Operacional e Zero Acidentes", "Implementação de tecnologias de telemetria e suporte ao motorista.", "Respeito às Pessoas", "2026-09-01", 1)
            )
            _orientacoes.value = fallback
            Result.success(fallback)
        }
    }

    override suspend fun criarOrientacao(
        titulo: String,
        descricao: String,
        pilar: String,
        prioridade: Int,
        categoria: String
    ): Result<OrientacaoEstrategica> {
        return try {
            val dto = apiService.criarEstrategia(
                CriarEstrategiaRequestDto(
                    titulo = titulo,
                    descricao = descricao,
                    pilar = pilar,
                    prioridade = prioridade,
                    categoria = categoria,
                    campanha = "Inova GAB 2026"
                )
            )
            val nova = OrientacaoEstrategica(
                id = dto.id,
                titulo = dto.titulo,
                descricao = dto.descricao,
                pilar = dto.pilar,
                dataCriacao = dto.dataCriacao,
                prioridade = dto.prioridade
            )
            _orientacoes.value = listOf(nova) + _orientacoes.value
            Result.success(nova)
        } catch (e: Exception) {
            val local = OrientacaoEstrategica(
                id = System.currentTimeMillis().toString(),
                titulo = titulo,
                descricao = descricao,
                pilar = pilar,
                dataCriacao = "Hoje",
                prioridade = prioridade
            )
            _orientacoes.value = listOf(local) + _orientacoes.value
            Result.success(local)
        }
    }
}
