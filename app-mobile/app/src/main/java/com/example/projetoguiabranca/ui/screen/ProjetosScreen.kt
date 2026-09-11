package com.example.projetoguiabranca.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.projetoguiabranca.data.model.*
import com.example.projetoguiabranca.ui.components.*
import com.example.projetoguiabranca.ui.state.ProjetosUiState
import com.example.projetoguiabranca.ui.theme.*
import com.example.projetoguiabranca.ui.viewmodel.ProjetosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjetosScreen(
    viewModel: ProjetosViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Projetos", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.carregarProjetos() }) {
                        Icon(Icons.Default.Refresh, "Atualizar", tint = AzulPrimario)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = CinzaFundo
    ) { padding ->
        when (val state = uiState) {
            is ProjetosUiState.Loading ->
                LoadingIndicator(Modifier.padding(padding))

            is ProjetosUiState.Error ->
                ErrorState(state.mensagem, { viewModel.carregarProjetos() },
                    Modifier.padding(padding))

            is ProjetosUiState.Success -> {
                LazyColumn(
                    Modifier.padding(padding).fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Resumo status
                    item {
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatusCount("Em Andamento",
                                state.projetos.count { it.status == ProjetoStatus.EM_ANDAMENTO },
                                AzulPrimario, Modifier.weight(1f))
                            StatusCount("Planejamento",
                                state.projetos.count { it.status == ProjetoStatus.PLANEJAMENTO },
                                CorAlerta, Modifier.weight(1f))
                            StatusCount("Concluídos",
                                state.projetos.count { it.status == ProjetoStatus.CONCLUIDO },
                                CorSucesso, Modifier.weight(1f))
                        }
                    }

                    item {
                        Text("Todos os Projetos (${state.projetos.size})",
                            style = MaterialTheme.typography.titleMedium)
                    }

                    items(state.projetos, key = { it.id }) { projeto ->
                        ProjetoCard(projeto = projeto,
                            onClick = { viewModel.selecionarProjeto(projeto) })
                    }
                }

                // Bottom Sheet detalhes
                state.projetoSelecionado?.let { projeto ->
                    ModalBottomSheet(
                        onDismissRequest = { viewModel.selecionarProjeto(null) },
                        sheetState = sheetState,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        Column(
                            Modifier.padding(horizontal = 20.dp).padding(bottom = 32.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Surface(Modifier.size(width = 40.dp, height = 4.dp),
                                    shape = RoundedCornerShape(2.dp), color = CinzaBorda) {}
                            }
                            Text(projeto.titulo, style = MaterialTheme.typography.headlineMedium)
                            Text(projeto.descricao, style = MaterialTheme.typography.bodyLarge,
                                color = TextoSecundario)
                            ProjetoProgressBar(progresso = projeto.progresso)
                            HorizontalDivider(color = CinzaBorda)

                            // Equipe
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Equipe", style = MaterialTheme.typography.titleMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    projeto.equipe.forEach { membro ->
                                        Surface(shape = RoundedCornerShape(20.dp),
                                            color = AzulClaro) {
                                            Text(membro,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = AzulPrimario,
                                                modifier = Modifier.padding(
                                                    horizontal = 10.dp, vertical = 4.dp))
                                        }
                                    }
                                }
                            }

                            // Datas
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Cronograma", style = MaterialTheme.typography.titleMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                    Column {
                                        Text("Início", style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 11.sp)
                                        Text(projeto.dataInicio,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold)
                                    }
                                    Column {
                                        Text("Previsão",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 11.sp)
                                        Text(projeto.dataPrevisao,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            // ROI
                            projeto.roi?.let { roi ->
                                HorizontalDivider(color = CinzaBorda)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Resultados",
                                        style = MaterialTheme.typography.titleMedium)
                                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        ResultadoChip("ROI", "${roi.toInt()}%",
                                            CorSucesso, Modifier.weight(1f))
                                        projeto.reducaoCustos?.let { rc ->
                                            ResultadoChip("Redução",
                                                "R$ ${String.format("%,.0f", rc)}",
                                                AzulPrimario, Modifier.weight(1f))
                                        }
                                    }
                                }
                            }

                            Button(
                                onClick = { viewModel.selecionarProjeto(null) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
                            ) {
                                Text("Fechar", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusCount(label: String, count: Int, cor: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cor.copy(alpha = 0.10f)),
        elevation = CardDefaults.cardElevation(0.dp)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(count.toString(), style = MaterialTheme.typography.headlineMedium,
                color = cor, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodyMedium, color = cor)
        }
    }
}

@Composable
private fun ResultadoChip(label: String, valor: String, cor: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cor.copy(alpha = 0.10f)),
        elevation = CardDefaults.cardElevation(0.dp)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(valor, style = MaterialTheme.typography.titleMedium,
                color = cor, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodyMedium, color = cor)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProjetosScreenPreview() {
    MaterialTheme {
        val projetosExemplo = listOf(
            Projeto(
                id = "p1", titulo = "App Rastreamento v2.0",
                descricao = "Desenvolvimento do app de rastreamento.",
                ideiaOrigemId = "i1", responsavel = "Carlos M.",
                equipe = listOf("Carlos", "Ana", "Pedro"),
                status = ProjetoStatus.EM_ANDAMENTO, progresso = 0.65f,
                dataInicio = "2025-03-01", dataPrevisao = "2025-07-31",
                roi = 28.0, reducaoCustos = 150000.0, ganhoProdutividade = 15.0
            ),
            Projeto(
                id = "p2", titulo = "Roteirização com IA",
                descricao = "Algoritmo de IA para otimização de rotas.",
                ideiaOrigemId = "i2", responsavel = "Ana F.",
                equipe = listOf("Ana", "Tech Lab"),
                status = ProjetoStatus.PLANEJAMENTO, progresso = 0.15f,
                dataInicio = "2025-06-01", dataPrevisao = "2025-12-31",
                roi = null, reducaoCustos = null, ganhoProdutividade = null
            )
        )
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Projetos") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White))
            },
            containerColor = CinzaFundo
        ) { padding ->
            LazyColumn(
                Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatusCount("Em Andamento", 1, AzulPrimario, Modifier.weight(1f))
                        StatusCount("Planejamento", 1, CorAlerta, Modifier.weight(1f))
                        StatusCount("Concluídos", 0, CorSucesso, Modifier.weight(1f))
                    }
                }
                items(projetosExemplo) { projeto ->
                    ProjetoCard(projeto = projeto, onClick = {})
                }
            }
        }
    }
}
