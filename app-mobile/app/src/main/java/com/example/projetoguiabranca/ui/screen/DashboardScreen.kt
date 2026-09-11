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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.projetoguiabranca.data.model.*
import com.example.projetoguiabranca.ui.components.*
import com.example.projetoguiabranca.ui.state.DashboardUiState
import com.example.projetoguiabranca.ui.theme.*
import com.example.projetoguiabranca.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Estratégico",
                    style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.carregar() }) {
                        Icon(Icons.Default.Refresh, "Atualizar", tint = AzulPrimario)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = CinzaFundo
    ) { padding ->
        when (val state = uiState) {
            is DashboardUiState.Loading ->
                LoadingIndicator(Modifier.padding(padding))

            is DashboardUiState.Error ->
                ErrorState(state.mensagem, { viewModel.carregar() },
                    Modifier.padding(padding))

            is DashboardUiState.Success -> {
                LazyColumn(
                    Modifier.padding(padding).fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Funil de inovação
                    item {
                        Text("Funil de Inovação",
                            style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(10.dp))
                        Card(shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)) {
                            Column(Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                FunilEtapa(state.dashboard.totalIdeias,
                                    "Ideias Capturadas", 1f, AzulSecundario)
                                FunilEtapa(state.dashboard.ideiasAprovadas,
                                    "Ideias Aprovadas",
                                    state.dashboard.ideiasAprovadas.toFloat() /
                                            state.dashboard.totalIdeias.coerceAtLeast(1),
                                    AzulPrimario)
                                FunilEtapa(state.dashboard.projetosAtivos,
                                    "Projetos Ativos",
                                    state.dashboard.projetosAtivos.toFloat() /
                                            state.dashboard.totalIdeias.coerceAtLeast(1),
                                    CorSucesso)
                            }
                        }
                    }

                    // KPIs
                    item {
                        Text("KPIs Principais",
                            style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                state.dashboard.metricas.take(2).forEach { m ->
                                    MetricaCard(m.label, m.valor, m.variacao,
                                        m.positivo, Modifier.weight(1f))
                                }
                            }
                            Row(Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                state.dashboard.metricas.drop(2).forEach { m ->
                                    MetricaCard(m.label, m.valor, m.variacao,
                                        m.positivo, Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    // Portfólio
                    item {
                        Text("Portfólio de Projetos",
                            style = MaterialTheme.typography.titleMedium)
                    }
                    items(state.projetos, key = { it.id }) { projeto ->
                        ProjetoRoiCard(projeto)
                    }

                    // Pilares
                    item {
                        Text("Pilares da Inovação",
                            style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(10.dp))
                        PilaresGrid()
                    }
                }
            }
        }
    }
}

@Composable
private fun FunilEtapa(numero: Int, label: String, larguraFrac: Float, cor: Color) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(numero.toString(), style = MaterialTheme.typography.titleMedium,
                color = cor, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Box(Modifier.fillMaxWidth(larguraFrac.coerceIn(0f, 1f)).height(8.dp)) {
            Surface(Modifier.fillMaxSize(), shape = RoundedCornerShape(4.dp),
                color = cor.copy(alpha = 0.8f)) {}
        }
    }
}

@Composable
private fun ProjetoRoiCard(projeto: Projeto) {
    Card(shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(projeto.titulo, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text("Resp: ${projeto.responsavel}",
                    style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                ProjetoProgressBar(projeto.progresso)
            }
            Spacer(Modifier.width(16.dp))
            projeto.roi?.let { roi ->
                Surface(shape = RoundedCornerShape(12.dp),
                    color = CorSucesso.copy(alpha = 0.12f)) {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${roi.toInt()}%",
                            style = MaterialTheme.typography.headlineMedium,
                            color = CorSucesso, fontWeight = FontWeight.Bold)
                        Text("ROI", style = MaterialTheme.typography.bodyMedium,
                            color = CorSucesso, fontSize = 11.sp)
                    }
                }
            } ?: Surface(shape = RoundedCornerShape(12.dp), color = CinzaBorda) {
                Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.HourglassEmpty, null,
                        tint = TextoSecundario, modifier = Modifier.size(20.dp))
                    Text("Em curso", style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario, fontSize = 10.sp)
                }
            }
        }
    }
}

private data class Pilar(
    val numero: String, val titulo: String, val descricao: String,
    val icone: ImageVector, val cor: Color
)

@Composable
private fun PilaresGrid() {
    val pilares = listOf(
        Pilar("01", "Direcionamento", "Alinhamento com objetivos estratégicos.",
            Icons.Default.Flag, AzulPrimario),
        Pilar("02", "Gestão de Ideias", "Captura estruturada de dores e sugestões.",
            Icons.Default.Lightbulb, CorAlerta),
        Pilar("03", "Inovação Aberta", "Conexão com o ecossistema externo.",
            Icons.Default.Hub, AzulSecundario),
        Pilar("04", "Gestão de Projetos", "Estruturação de ideias em projetos reais.",
            Icons.Default.AccountTree, CorSucesso),
        Pilar("05", "Mensuração", "Acompanhamento de indicadores e ROI.",
            Icons.Default.BarChart, CorErro)
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            pilares.take(2).forEach { PilarCard(it, Modifier.weight(1f)) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            pilares.drop(2).take(2).forEach { PilarCard(it, Modifier.weight(1f)) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            PilarCard(pilares.last(), Modifier.fillMaxWidth(0.5f))
        }
    }
}

@Composable
private fun PilarCard(pilar: Pilar, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = pilar.cor.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(0.dp)) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(pilar.numero, style = MaterialTheme.typography.titleMedium,
                    color = pilar.cor, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Icon(pilar.icone, null, tint = pilar.cor, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(pilar.titulo, style = MaterialTheme.typography.titleMedium, color = pilar.cor)
            Spacer(Modifier.height(4.dp))
            Text(pilar.descricao, style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardScreenPreview() {
    MaterialTheme {
        val dashboard = DashboardData(
            totalIdeias = 247, ideiasAprovadas = 89, projetosAtivos = 14,
            roiMedio = "28%",
            metricas = listOf(
                DashboardMetrica("ROI Médio", "28%", "+5%", true),
                DashboardMetrica("Redução de Custos", "R$ 850K", "+12%", true),
                DashboardMetrica("Engajamento", "91%", "+3%", true),
                DashboardMetrica("Ideias/Mês", "42", "-2%", false)
            )
        )
        val projetos = listOf(
            Projeto("p1", "App Rastreamento", "Desc", "i1", "Carlos M.",
                listOf("Carlos", "Ana"), ProjetoStatus.EM_ANDAMENTO, 0.65f,
                "2025-03-01", "2025-07-31", 28.0, 150000.0, 15.0),
            Projeto("p2", "Roteirização IA", "Desc", "i2", "Ana F.",
                listOf("Ana"), ProjetoStatus.PLANEJAMENTO, 0.15f,
                "2025-06-01", "2025-12-31", null, null, null)
        )
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Dashboard Estratégico") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White))
            },
            containerColor = CinzaFundo
        ) { padding ->
            LazyColumn(
                Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Card(shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            FunilEtapa(247, "Ideias Capturadas", 1f, AzulSecundario)
                            FunilEtapa(89, "Ideias Aprovadas", 0.36f, AzulPrimario)
                            FunilEtapa(14, "Projetos Ativos", 0.06f, CorSucesso)
                        }
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            dashboard.metricas.take(2).forEach { m ->
                                MetricaCard(m.label, m.valor, m.variacao,
                                    m.positivo, Modifier.weight(1f))
                            }
                        }
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            dashboard.metricas.drop(2).forEach { m ->
                                MetricaCard(m.label, m.valor, m.variacao,
                                    m.positivo, Modifier.weight(1f))
                            }
                        }
                    }
                }
                items(projetos) { ProjetoRoiCard(it) }
                item { PilaresGrid() }
            }
        }
    }
}
