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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.projetoguiabranca.data.model.*
import com.example.projetoguiabranca.ui.components.*
import com.example.projetoguiabranca.ui.state.*
import com.example.projetoguiabranca.ui.theme.*
import com.example.projetoguiabranca.ui.viewmodel.GestaoIdeiasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestaoIdeiasScreen(
    viewModel: GestaoIdeiasViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestão de Ideias", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.carregarIdeias() }) {
                        Icon(Icons.Default.Refresh, "Atualizar", tint = AzulPrimario)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = CinzaFundo
    ) { padding ->
        when (val state = uiState) {
            is GestaoIdeiasUiState.Loading ->
                LoadingIndicator(Modifier.padding(padding))

            is GestaoIdeiasUiState.Error ->
                ErrorState(state.mensagem, { viewModel.carregarIdeias() },
                    Modifier.padding(padding))

            is GestaoIdeiasUiState.Success -> {
                val ideiasExibidas = state.ideiasParaAvaliar

                LazyColumn(
                    Modifier.padding(padding).fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Contadores
                    item {
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ContadorCard("Para Avaliar",
                                state.ideiasParaAvaliar.size, CorAlerta, Modifier.weight(1f))
                            ContadorCard("Minhas Ideias",
                                state.minhasIdeias.size, AzulPrimario, Modifier.weight(1f))
                        }
                    }

                    // Filtros
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FiltroIdeias.entries.forEach { filtro ->
                                FilterChip(
                                    selected = filtro == state.filtroAtivo,
                                    onClick = { viewModel.selecionarFiltro(filtro) },
                                    label = {
                                        Text(when (filtro) {
                                            FiltroIdeias.TODAS        -> "Todas"
                                            FiltroIdeias.EM_AVALIACAO -> "Para Avaliar"
                                            FiltroIdeias.APROVADAS    -> "Aprovadas"
                                            FiltroIdeias.REPROVADAS   -> "Reprovadas"
                                        })
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AzulClaro,
                                        selectedLabelColor = AzulPrimario
                                    )
                                )
                            }
                        }
                    }

                    // Lista
                    if (ideiasExibidas.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Inbox, null,
                                        tint = TextoSecundario, modifier = Modifier.size(48.dp))
                                    Spacer(Modifier.height(12.dp))
                                    Text("Nenhuma ideia encontrada",
                                        style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    } else {
                        items(ideiasExibidas, key = { it.id }) { ideia ->
                            Box {
                                IdeiaCard(ideia = ideia,
                                    onClick = { viewModel.abrirModalAvaliacao(ideia) })
                                val precisaAvaliar = ideia.status == IdeiaStatus.CAPTURADA || ideia.status == IdeiaStatus.EM_AVALIACAO
                                if (precisaAvaliar) {
                                    Surface(
                                        modifier = Modifier.align(Alignment.TopEnd)
                                            .padding(top = 8.dp, end = 8.dp),
                                        shape = RoundedCornerShape(20.dp),
                                        color = CorAlerta
                                    ) {
                                        Text("Avaliar",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp, vertical = 3.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Sheet de avaliação
                state.ideiaEmAvaliacao?.let { ideia ->
                    ModalBottomSheet(
                        onDismissRequest = viewModel::fecharModalAvaliacao,
                        sheetState = sheetState,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    ) {
                        Column(
                            Modifier.padding(horizontal = 20.dp).padding(bottom = 32.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Surface(Modifier.size(width = 40.dp, height = 4.dp),
                                    shape = RoundedCornerShape(2.dp),
                                    color = CinzaBorda) {}
                            }
                            Text("Avaliar Ideia",
                                style = MaterialTheme.typography.headlineMedium)
                            Card(shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = AzulClaro),
                                elevation = CardDefaults.cardElevation(0.dp)) {
                                Column(Modifier.padding(14.dp)) {
                                    Text(ideia.titulo,
                                        style = MaterialTheme.typography.titleMedium)
                                    Spacer(Modifier.height(4.dp))
                                    Text(ideia.descricao,
                                        style = MaterialTheme.typography.bodyMedium)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Autor: ${ideia.autorNome} · Impacto: ${ideia.impactoEstimado}",
                                        style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            OutlinedTextField(
                                value = state.justificativaAvaliacao,
                                onValueChange = viewModel::onJustificativaChange,
                                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 100.dp),
                                label = { Text("Justificativa (opcional)") },
                                shape = RoundedCornerShape(12.dp),
                                maxLines = 4
                            )
                            if (state.isProcessandoAvaliacao) {
                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = AzulPrimario)
                                }
                            } else {
                                Row(Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    OutlinedButton(
                                        onClick = { viewModel.avaliarIdeia(false) },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Close, null, tint = CorErro)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Reprovar", color = CorErro,
                                            fontWeight = FontWeight.SemiBold)
                                    }
                                    Button(
                                        onClick = { viewModel.avaliarIdeia(true) },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = CorSucesso)
                                    ) {
                                        Icon(Icons.Default.Check, null, tint = Color.White)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Aprovar", color = Color.White,
                                            fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                TextButton(onClick = viewModel::fecharModalAvaliacao,
                                    modifier = Modifier.fillMaxWidth()) {
                                    Text("Cancelar", color = TextoSecundario)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContadorCard(
    label: String, valor: Int, cor: Color, modifier: Modifier = Modifier
) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cor.copy(alpha = 0.10f)),
        elevation = CardDefaults.cardElevation(0.dp)) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(valor.toString(), style = MaterialTheme.typography.headlineMedium,
                color = cor, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodyMedium, color = cor)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GestaoIdeiasScreenPreview() {
    MaterialTheme {
        val ideiasExemplo = listOf(
            Ideia(
                id = "1", titulo = "App de Rastreamento",
                descricao = "Criar um app para clientes acompanharem entregas em tempo real.",
                categoria = "Tecnologia", autorId = "u1", autorNome = "Carlos M.",
                divisao = "LOGÍSTICA", status = IdeiaStatus.EM_AVALIACAO,
                dataCriacao = "2025-05-01", impactoEstimado = "ALTO",
                votos = 42, comentarios = 8
            ),
            Ideia(
                id = "2", titulo = "Checklist Digital",
                descricao = "Substituir checklist físico por formulário digital.",
                categoria = "Processos", autorId = "u1", autorNome = "Ana F.",
                divisao = "LOGÍSTICA", status = IdeiaStatus.APROVADA,
                dataCriacao = "2025-04-01", impactoEstimado = "MEDIO",
                votos = 23, comentarios = 5
            )
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gestão de Ideias") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            containerColor = CinzaFundo
        ) { padding ->
            LazyColumn(
                Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ContadorCard("Para Avaliar", 2, CorAlerta, Modifier.weight(1f))
                        ContadorCard("Minhas Ideias", 3, AzulPrimario, Modifier.weight(1f))
                    }
                }
                items(ideiasExemplo) { ideia ->
                    IdeiaCard(ideia = ideia, onClick = {})
                }
            }
        }
    }
}