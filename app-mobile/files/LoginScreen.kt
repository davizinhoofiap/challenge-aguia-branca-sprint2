package com.aguiabranca.inovacao.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aguiabranca.inovacao.ui.state.LoginUiState
import com.aguiabranca.inovacao.ui.theme.AguiaBrancaColors
import com.aguiabranca.inovacao.ui.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

/**
 * LoginScreen — ponto de entrada do app para usuários não autenticados.
 *
 * Observa o StateFlow estaLogado do ViewModel:
 * quando true, chama onLoginSucesso que dispara a navegação no NavGraph.
 * Isso mantém a View burra (sem lógica de navegação condicional).
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSucesso: () -> Unit,
    webClientId: String  // vem do google-services.json (Web client ID)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val estaLogado by viewModel.estaLogado.collectAsStateWithLifecycle()

    // Navega quando Firebase confirma o login via Flow
    LaunchedEffect(estaLogado) {
        if (estaLogado) onLoginSucesso()
    }

    // Launcher para o fluxo do Google Sign-In
    val context = LocalContext.current
    val googleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                .getResult(ApiException::class.java)
            account.idToken?.let { viewModel.loginComGoogle(it) }
        } catch (e: ApiException) {
            // Usuário cancelou ou erro do Google
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AguiaBrancaColors.CinzaFundo
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Logo / Header ──────────────────────────────────────
            LogoHeader()

            Spacer(Modifier.height(40.dp))

            // ── Card do formulário ────────────────────────────────
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Entrar",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // E-mail
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        label = { Text("E-mail corporativo") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, null,
                                tint = AguiaBrancaColors.AzulPrimario)
                        },
                        isError = uiState.emailErro != null,
                        supportingText = uiState.emailErro?.let { { Text(it) } },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = campoColors(),
                        singleLine = true
                    )

                    // Senha
                    OutlinedTextField(
                        value = uiState.senha,
                        onValueChange = viewModel::onSenhaChange,
                        label = { Text("Senha") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, null,
                                tint = AguiaBrancaColors.AzulPrimario)
                        },
                        trailingIcon = {
                            IconButton(onClick = viewModel::toggleSenhaVisivel) {
                                Icon(
                                    if (uiState.senhaVisivel) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    "Mostrar/ocultar senha",
                                    tint = AguiaBrancaColors.TextoSecundario
                                )
                            }
                        },
                        visualTransformation = if (uiState.senhaVisivel)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        isError = uiState.senhaErro != null,
                        supportingText = uiState.senhaErro?.let { { Text(it) } },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = campoColors(),
                        singleLine = true
                    )

                    // Erro geral de login
                    AnimatedVisibility(visible = uiState.erroLogin != null) {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = AguiaBrancaColors.Erro.copy(alpha = 0.10f)
                            )
                        ) {
                            Row(
                                Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ErrorOutline, null,
                                    tint = AguiaBrancaColors.Erro,
                                    modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    uiState.erroLogin ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AguiaBrancaColors.Erro
                                )
                            }
                        }
                    }

                    // Botão de login principal
                    BotaoLogin(uiState = uiState, onClick = viewModel::loginComEmail)

                    // Divisor
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(Modifier.weight(1f),
                            color = AguiaBrancaColors.CinzaBorda)
                        Text(
                            "  ou  ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AguiaBrancaColors.TextoSecundario
                        )
                        HorizontalDivider(Modifier.weight(1f),
                            color = AguiaBrancaColors.CinzaBorda)
                    }

                    // Botão Google Sign-In
                    OutlinedButton(
                        onClick = {
                            val gso = GoogleSignInOptions.Builder(
                                GoogleSignInOptions.DEFAULT_SIGN_IN
                            )
                                .requestIdToken(webClientId)
                                .requestEmail()
                                .build()
                            val client = GoogleSignIn.getClient(context, gso)
                            googleLauncher.launch(client.signInIntent)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !uiState.isCarregando,
                        border = ButtonDefaults.outlinedButtonBorder.copy()
                    ) {
                        // Sem ícone SVG do Google aqui para evitar dependência extra;
                        // adicione com a lib 'com.google.android.gms:play-services-auth'
                        Icon(Icons.Default.AccountCircle, null,
                            tint = AguiaBrancaColors.AzulPrimario)
                        Spacer(Modifier.width(8.dp))
                        Text("Entrar com Google",
                            color = AguiaBrancaColors.TextoPrimario,
                            fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Acesso restrito a colaboradores do Grupo Águia Branca",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = AguiaBrancaColors.TextoSecundario
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Logo / Header da tela de login
// ─────────────────────────────────────────────────────────────

@Composable
private fun LogoHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Placeholder do logo — substitua por Image(painterResource(...))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AguiaBrancaColors.AzulPrimario,
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    "GAB",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Inovação GAB",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = AguiaBrancaColors.AzulPrimario
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Gestão Integrada de Inovação",
            style = MaterialTheme.typography.bodyLarge,
            color = AguiaBrancaColors.TextoSecundario
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Botão principal com estado de loading
// ─────────────────────────────────────────────────────────────

@Composable
private fun BotaoLogin(uiState: LoginUiState, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        enabled = uiState.formularioValido && !uiState.isCarregando,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AguiaBrancaColors.AzulPrimario)
    ) {
        if (uiState.isCarregando) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
            Spacer(Modifier.width(10.dp))
            Text("Entrando...", color = Color.White, fontWeight = FontWeight.SemiBold)
        } else {
            Text("Entrar", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun campoColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = AguiaBrancaColors.AzulPrimario,
    focusedLabelColor    = AguiaBrancaColors.AzulPrimario,
    cursorColor          = AguiaBrancaColors.AzulPrimario,
    unfocusedBorderColor = AguiaBrancaColors.CinzaBorda,
    unfocusedContainerColor = Color.White,
    focusedContainerColor   = Color.White
)
