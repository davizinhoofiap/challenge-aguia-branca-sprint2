package com.aguiabranca.inovacao.ui.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aguiabranca.inovacao.ui.screen.*
import com.aguiabranca.inovacao.ui.viewmodel.*

// ── Rotas ─────────────────────────────────────────────────────

sealed class Rota(val caminho: String) {
    object Login         : Rota("login")
    object Home          : Rota("home")
    object CapturaIdeia  : Rota("captura_ideia")
    object GestaoIdeias  : Rota("gestao_ideias")
    object Projetos      : Rota("projetos")
    object Dashboard     : Rota("dashboard")
}

// ── NavGraph com Auth Guard ────────────────────────────────────

/**
 * AppNavGraph com guard de autenticação.
 *
 * O LoginViewModel (via Hilt) mantém um StateFlow do estado
 * de autenticação Firebase. O NavGraph observa esse estado e
 * define o startDestination dinamicamente.
 *
 * Isso evita "flash" da tela de login quando o usuário já
 * tem sessão ativa.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    // LoginViewModel é compartilhado para observar o estado de auth
    val loginViewModel: LoginViewModel = hiltViewModel()
    val estaLogado by loginViewModel.estaLogado.collectAsStateWithLifecycle()

    // startDestination reativo ao estado de autenticação
    val startDestination = if (estaLogado) Rota.Home.caminho else Rota.Login.caminho

    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {

        // ── Login ──────────────────────────────────────────────
        composable(Rota.Login.caminho) {
            LoginScreen(
                viewModel      = loginViewModel,
                onLoginSucesso = {
                    // Remove o login do back stack: o usuário não pode
                    // voltar para a tela de login com o botão "voltar"
                    navController.navigate(Rota.Home.caminho) {
                        popUpTo(Rota.Login.caminho) { inclusive = true }
                    }
                },
                // Substitua pelo seu Web Client ID do google-services.json
                webClientId = "SEU_WEB_CLIENT_ID_AQUI"
            )
        }

        // ── Home ───────────────────────────────────────────────
        composable(Rota.Home.caminho) {
            val vm: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel         = vm,
                onNavigateToCaptura   = { navController.navigate(Rota.CapturaIdeia.caminho) },
                onNavigateToGestao    = { navController.navigate(Rota.GestaoIdeias.caminho) },
                onNavigateToProjetos  = { navController.navigate(Rota.Projetos.caminho) },
                onNavigateToDashboard = { navController.navigate(Rota.Dashboard.caminho) },
                onIdeiaClick          = { /* TODO: detalhe */ }
            )
        }

        // ── Captura ────────────────────────────────────────────
        composable(Rota.CapturaIdeia.caminho) {
            val vm: CapturaIdeiaViewModel = hiltViewModel()
            CapturaIdeiaScreen(
                viewModel      = vm,
                autorId        = "",   // ViewModel pega do AuthService via repositório
                autorNome      = "",
                divisao        = "",
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Gestão de Ideias ───────────────────────────────────
        composable(Rota.GestaoIdeias.caminho) {
            val vm: GestaoIdeiasViewModel = hiltViewModel()
            GestaoIdeiasScreen(
                viewModel      = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Projetos ───────────────────────────────────────────
        composable(Rota.Projetos.caminho) {
            val vm: ProjetosViewModel = hiltViewModel()
            ProjetosScreen(
                viewModel      = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Dashboard ──────────────────────────────────────────
        composable(Rota.Dashboard.caminho) {
            val vm: DashboardViewModel = hiltViewModel()
            DashboardScreen(
                viewModel      = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
