package com.example.projetoguiabranca.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.projetoguiabranca.ui.theme.*
import com.example.projetoguiabranca.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSucesso: () -> Unit,
    webClientId: String = ""
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val estaLogado by viewModel.estaLogado.collectAsStateWithLifecycle()

    LaunchedEffect(estaLogado) {
        if (estaLogado) onLoginSucesso()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CinzaFundo
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = AzulPrimario,
                modifier = Modifier.size(88.dp),
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "GAB",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Inovação GAB",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = AzulPrimario
            )
            Text(
                "Gestão Integrada de Inovação Corporativa",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = TextoSecundario
            )

            Spacer(Modifier.height(32.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBranco),
                elevation = CardDefaults.cardElevation(4.dp),
                border = BorderStroke(1.dp, CinzaBorda),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Acessar Plataforma",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrimario
                    )
                    Text(
                        "Entre com seu e-mail corporativo e senha",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario
                    )

                    // E-mail Input
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        label = { Text("E-mail corporativo", color = TextoSecundario) },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = AzulPrimario) },
                        isError = uiState.emailErro != null,
                        supportingText = uiState.emailErro?.let { { Text(it, color = CorErro) } },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextoPrimario,
                            unfocusedTextColor = TextoPrimario,
                            focusedBorderColor = AzulPrimario,
                            unfocusedBorderColor = CinzaBorda,
                            focusedContainerColor = CardBranco,
                            unfocusedContainerColor = CardBranco,
                            cursorColor = AzulPrimario
                        )
                    )

                    // Senha Input
                    OutlinedTextField(
                        value = uiState.senha,
                        onValueChange = viewModel::onSenhaChange,
                        label = { Text("Senha", color = TextoSecundario) },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = AzulPrimario) },
                        trailingIcon = {
                            IconButton(onClick = viewModel::toggleSenhaVisivel) {
                                Icon(
                                    if (uiState.senhaVisivel) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    "Mostrar/ocultar senha",
                                    tint = TextoSecundario
                                )
                            }
                        },
                        visualTransformation = if (uiState.senhaVisivel)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        isError = uiState.senhaErro != null,
                        supportingText = uiState.senhaErro?.let { { Text(it, color = CorErro) } },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextoPrimario,
                            unfocusedTextColor = TextoPrimario,
                            focusedBorderColor = AzulPrimario,
                            unfocusedBorderColor = CinzaBorda,
                            focusedContainerColor = CardBranco,
                            unfocusedContainerColor = CardBranco,
                            cursorColor = AzulPrimario
                        )
                    )

                    // Mensagem de Erro
                    AnimatedVisibility(visible = uiState.erroLogin != null) {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = CorErroFundo),
                            border = BorderStroke(1.dp, CorErro.copy(alpha = 0.3f))
                        ) {
                            Row(
                                Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.ErrorOutline,
                                    null,
                                    tint = CorErro,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    uiState.erroLogin ?: "",
                                    color = CorErro,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Botão Principal
                    Button(
                        onClick = viewModel::loginComEmail,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = uiState.formularioValido && !uiState.isCarregando,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AzulPrimario,
                            disabledContainerColor = AzulPrimario.copy(alpha = 0.4f)
                        )
                    ) {
                        if (uiState.isCarregando) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "Conectando ao MongoDB Atlas...",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                "Entrar no Sistema",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "Backend .NET 8 na porta 5000 · MongoDB Atlas Cloud\nGrupo Águia Branca · FIAP 2026",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = TextoSecundario,
                fontWeight = FontWeight.Medium
            )
        }
    }
}