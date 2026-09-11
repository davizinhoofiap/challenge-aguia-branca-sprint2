package com.example.projetoguiabranca.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.projetoguiabranca.data.model.*
import com.example.projetoguiabranca.ui.components.*
import com.example.projetoguiabranca.ui.state.HomeUiState
import com.example.projetoguiabranca.ui.theme.*
import com.example.projetoguiabranca.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCaptura: () -> Unit = {},
    onNavigateToGestao: () -> Unit = {},
    onNavigateToProjetos: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
    onIdeiaClick: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var ideiaSelecionada by remember { mutableStateOf<Ideia?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.carregarDados()
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    "Sair da Plataforma",
                    fontWeight = FontWeight.Bold,
                    color = TextoPrimario
                )
            },
            text = {
                Text(
                    "Deseja encerrar a sessão atual para entrar com outro perfil de usuário?",
                    color = TextoSecundario
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CorErro)
                ) {
                    Text("Sair / Trocar de Conta", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }) {
                    Text("Continuar Conectado", color = TextoSecundario)
                }
            },
            containerColor = CardBranco,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Inovação GAB",
                            style = MaterialTheme.typography.titleLarge,
                            color = AzulPrimario,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Grupo Águia Branca",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextoSecundario
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.carregarDados() }) {
                        Icon(Icons.Default.Refresh, "Atualizar dados", tint = AzulPrimario)
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.ExitToApp, "Sair / Trocar de Perfil", tint = CorErro)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = CinzaFundo
    ) { padding ->
        when (val state = uiState) {
            is HomeUiState.Loading ->
                LoadingIndicator(Modifier.padding(padding))

            is HomeUiState.Error ->
                ErrorState(state.mensagem, { viewModel.carregarDados() }, Modifier.padding(padding))

            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.padding(padding).fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header usuário
                    item {
                        Card(shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)) {
                            HeaderUsuario(
                                usuario = state.usuario,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    // Ações rápidas
                    item {
                        Text("Ações Rápidas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextoPrimario)
                        Spacer(Modifier.height(10.dp))
                        AcoesRapidasRow(
                            perfil = state.usuario.perfil,
                            onCapturar = onNavigateToCaptura,
                            onGerenciar = onNavigateToGestao,
                            onProjetos = onNavigateToProjetos,
                            onDashboard = onNavigateToDashboard
                        )
                    }

                    // Métricas
                    item {
                        Text("Visão Geral", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextoPrimario)
                        Spacer(Modifier.height(10.dp))
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricaCard("Total de Ideias",
                                "${state.dashboard.totalIdeias}", "+8%", true,
                                Modifier.weight(1f))
                            MetricaCard("Projetos Ativos",
                                "${state.dashboard.projetosAtivos}", "+2", true,
                                Modifier.weight(1f))
                        }
                    }

                    // Orientações estratégicas
                    item {
                        Text("Orientações Estratégicas",
                            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextoPrimario)
                        Spacer(Modifier.height(10.dp))
                        Row(
                            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            state.orientacoes.forEach { OrientacaoCard(it) }
                        }
                    }

                    // Minhas ideias
                    if (state.minhasIdeias.isNotEmpty()) {
                        item {
                            Row(Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {
                                Text("Minhas Ideias (com Score IA)",
                                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextoPrimario)
                                TextButton(onClick = onNavigateToGestao) {
                                    Text("Ver todas", color = AzulPrimario, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        items(state.minhasIdeias) { ideia ->
                            IdeiaCard(
                                ideia = ideia,
                                onClick = { 
                                    ideiaSelecionada = ideia
                                    onIdeiaClick(ideia.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        ideiaSelecionada?.let { ideia ->
            ModalBottomSheet(
                onDismissRequest = { ideiaSelecionada = null },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                containerColor = CardBranco
            ) {
                Column(
                    Modifier
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Surface(
                            Modifier.size(width = 40.dp, height = 4.dp),
                            shape = RoundedCornerShape(2.dp),
                            color = CinzaBorda
                        ) {}
                    }

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusChip(ideia.status)
                        ImpactoChip(ideia.impactoEstimado)
                    }

                    Text(
                        ideia.titulo,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrimario
                    )

                    Text(
                        ideia.descricao,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario
                    )

                    if (ideia.aiScore > 0) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = IaScoreFundo),
                            border = androidx.compose.foundation.BorderStroke(1.dp, IaScoreDestaque.copy(alpha = 0.25f))
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, null, tint = IaScoreDestaque, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        "Score IA: ${ideia.aiScore}/100 (${ideia.aiPrioridade})",
                                        color = IaScoreDestaque,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                                if (ideia.aiAnalise.isNotBlank()) {
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        ideia.aiAnalise,
                                        color = TextoPrimario,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Autor: ${ideia.autorNome}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Divisão: ${ideia.divisao}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario
                        )
                    }

                    Button(
                        onClick = { ideiaSelecionada = null },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
                    ) {
                        Text("Fechar Detalhes", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AcoesRapidasRow(
    perfil: UserProfile,
    onCapturar: () -> Unit,
    onGerenciar: () -> Unit,
    onProjetos: () -> Unit,
    onDashboard: () -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        if (perfil == UserProfile.OPERACIONAL || perfil == UserProfile.TATICO) {
            AcaoRapidaItem(Icons.Default.Add, "Nova Ideia",
                AzulPrimario, onCapturar, Modifier.weight(1f))
        }
        if (perfil == UserProfile.TATICO || perfil == UserProfile.ESTRATEGICO) {
            AcaoRapidaItem(Icons.Default.Checklist, "Avaliar",
                CorAlerta, onGerenciar, Modifier.weight(1f))
        }
        AcaoRapidaItem(Icons.Default.FolderOpen, "Projetos",
            CorSucesso, onProjetos, Modifier.weight(1f))
        if (perfil == UserProfile.ESTRATEGICO) {
            AcaoRapidaItem(Icons.Default.BarChart, "Dashboard",
                AzulSecundario, onDashboard, Modifier.weight(1f))
        }
    }
}

@Composable
private fun AcaoRapidaItem(
    icone: ImageVector, label: String, cor: Color,
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Card(onClick = onClick, modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cor.copy(alpha = 0.10f)),
        elevation = CardDefaults.cardElevation(0.dp)) {
        Column(Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icone, null, tint = cor, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(6.dp))
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = cor, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            viewModel = hiltViewModel(),
            onNavigateToCaptura = {},
            onNavigateToGestao = {},
            onNavigateToProjetos = {},
            onNavigateToDashboard = {}
        )
    }
}
