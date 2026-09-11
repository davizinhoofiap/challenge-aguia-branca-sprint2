package com.example.projetoguiabranca.ui.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projetoguiabranca.ui.screen.*
import com.example.projetoguiabranca.ui.viewmodel.LoginViewModel

sealed class Rota(val caminho: String) {
    object Login        : Rota("login")
    object Home         : Rota("home")
    object CapturaIdeia : Rota("captura_ideia")
    object GestaoIdeias : Rota("gestao_ideias")
    object Projetos     : Rota("projetos")
    object Dashboard    : Rota("dashboard")
}

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val estaLogado by loginViewModel.estaLogado.collectAsStateWithLifecycle()

    val startDestination = if (estaLogado) Rota.Home.caminho else Rota.Login.caminho

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Rota.Login.caminho) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSucesso = {
                    navController.navigate(Rota.Home.caminho) {
                        popUpTo(Rota.Login.caminho) { inclusive = true }
                    }
                },
                webClientId = "SEU_WEB_CLIENT_ID_AQUI"
            )
        }

        composable(Rota.Home.caminho) {
            HomeScreen(
                onNavigateToCaptura   = { navController.navigate(Rota.CapturaIdeia.caminho) },
                onNavigateToGestao    = { navController.navigate(Rota.GestaoIdeias.caminho) },
                onNavigateToProjetos  = { navController.navigate(Rota.Projetos.caminho) },
                onNavigateToDashboard = { navController.navigate(Rota.Dashboard.caminho) },
                onIdeiaClick          = {},
                onLogout              = {
                    navController.navigate(Rota.Login.caminho) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Rota.CapturaIdeia.caminho) {
            CapturaIdeiaScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Rota.GestaoIdeias.caminho) {
            GestaoIdeiasScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Rota.Projetos.caminho) {
            ProjetosScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Rota.Dashboard.caminho) {
            DashboardScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}