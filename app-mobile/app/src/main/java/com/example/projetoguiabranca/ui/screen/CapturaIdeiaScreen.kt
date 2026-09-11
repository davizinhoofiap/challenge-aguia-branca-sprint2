package com.example.projetoguiabranca.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.projetoguiabranca.ui.components.*
import com.example.projetoguiabranca.ui.state.CapturaIdeiaUiState
import com.example.projetoguiabranca.ui.theme.*
import com.example.projetoguiabranca.ui.viewmodel.CapturaIdeiaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CapturaIdeiaScreen(
    viewModel: CapturaIdeiaViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.sucessoAoSalvar) {
        if (uiState.sucessoAoSalvar) {
            kotlinx.coroutines.delay(1500)
            viewModel.resetarSucesso()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Ideia", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = CinzaFundo
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Banner do processo
                Card(shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AzulClaro),
                    elevation = CardDefaults.cardElevation(0.dp)) {
                    Row(Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly) {
                        EtapaItem("01", "Capturar", true)
                        Icon(Icons.Default.ChevronRight, null, tint = TextoSecundario)
                        EtapaItem("02", "Estruturar", false)
                        Icon(Icons.Default.ChevronRight, null, tint = TextoSecundario)
                        EtapaItem("03", "Acompanhar", false)
                    }
                }

                // Título
                CampoLabel("Título da Ideia *")
                OutlinedTextField(
                    value = uiState.titulo,
                    onValueChange = viewModel::onTituloChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ex: Checklist digital de veículos") },
                    isError = uiState.tituloErro != null,
                    supportingText = uiState.tituloErro?.let { { Text(it) } },
                    leadingIcon = {
                        Icon(Icons.Default.Lightbulb, null, tint = AzulPrimario)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = campoColors()
                )

                // Descrição
                CampoLabel("Descreva a Ideia *")
                OutlinedTextField(
                    value = uiState.descricao,
                    onValueChange = viewModel::onDescricaoChange,
                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 120.dp),
                    placeholder = { Text("Qual o problema? Qual a solução? Qual o impacto?") },
                    isError = uiState.descricaoErro != null,
                    supportingText = uiState.descricaoErro?.let { { Text(it) } },
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp),
                    colors = campoColors()
                )

                // Categoria
                CampoLabel("Categoria *")
                CategoriaDropdown(
                    categoriaSelecionada = uiState.categoria,
                    categorias = uiState.categoriasDisponiveis,
                    erro = uiState.categoriaErro,
                    onCategoriaChange = viewModel::onCategoriaChange
                )

                // Impacto
                CampoLabel("Impacto Estimado")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.impactosDisponiveis.forEach { impacto ->
                        val selecionado = impacto == uiState.impactoEstimado
                        val cor = when (impacto) {
                            "ALTO"  -> ImpactoAlto
                            "MEDIO" -> ImpactoMedio
                            else    -> ImpactoBaixo
                        }
                        FilterChip(
                            selected = selecionado,
                            onClick = { viewModel.onImpactoChange(impacto) },
                            label = {
                                Text(impacto.lowercase().replaceFirstChar { it.uppercase() })
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = cor.copy(alpha = 0.15f),
                                selectedLabelColor = cor
                            )
                        )
                    }
                }

                Spacer(Modifier.height(80.dp))
            }

            // Botão fixo no rodapé
            Button(
                onClick = { viewModel.submeter() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = uiState.formularioValido && !uiState.isSalvando,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
            ) {
                if (uiState.isSalvando) {
                    CircularProgressIndicator(color = Color.White,
                        modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                    Text("Enviando...", color = Color.White, fontWeight = FontWeight.SemiBold)
                } else {
                    Icon(Icons.Default.Send, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Enviar Ideia", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            // Feedback de sucesso
            AnimatedVisibility(
                visible = uiState.sucessoAoSalvar,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp),
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Card(shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CorSucesso)) {
                    Row(Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Ideia salva no MongoDB Atlas com sucesso!", color = Color.White,
                            fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Feedback de erro (ex: servidor desconectado)
            AnimatedVisibility(
                visible = uiState.erroAoSalvar != null,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp),
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Card(shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CorErro)) {
                    Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ErrorOutline, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            uiState.erroAoSalvar ?: "",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EtapaItem(numero: String, label: String, ativo: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(numero, style = MaterialTheme.typography.titleMedium,
            color = if (ativo) AzulPrimario else TextoSecundario,
            fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodyMedium,
            color = if (ativo) AzulPrimario else TextoSecundario)
    }
}

@Composable
private fun CampoLabel(label: String) {
    Text(
        label,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextoPrimario
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoriaDropdown(
    categoriaSelecionada: String,
    categorias: List<String>,
    erro: String?,
    onCategoriaChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = categoriaSelecionada.ifBlank { "Selecione..." },
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            leadingIcon = { Icon(Icons.Default.Category, null, tint = AzulPrimario) },
            isError = erro != null,
            supportingText = erro?.let { { Text(it, color = CorErro) } },
            shape = RoundedCornerShape(12.dp),
            colors = campoColors()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categorias.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat, color = TextoPrimario, fontWeight = FontWeight.Medium) },
                    onClick = {
                        onCategoriaChange(cat)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun campoColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextoPrimario,
    unfocusedTextColor = TextoPrimario,
    focusedBorderColor = AzulPrimario,
    unfocusedBorderColor = CinzaBorda,
    focusedLabelColor = AzulPrimario,
    unfocusedLabelColor = TextoSecundario,
    cursorColor = AzulPrimario,
    focusedContainerColor = CardBranco,
    unfocusedContainerColor = CardBranco
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CapturaIdeiaScreenPreview() {
    MaterialTheme {
        CapturaIdeiaScreen()
    }
}