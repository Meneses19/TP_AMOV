package pt.isec.amov.safetysec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pt.isec.amov.safetysec.model.UserType
import pt.isec.amov.safetysec.ui.screens.auth.LoginScreen
import pt.isec.amov.safetysec.ui.screens.auth.RegisterScreen
import pt.isec.amov.safetysec.ui.screens.monitor.MonitorDashboard
import pt.isec.amov.safetysec.ui.screens.protected.ProtegidoDashboard
import pt.isec.amov.safetysec.ui.theme.SafetySecTheme
import pt.isec.amov.safetysec.ui.viewmodels.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o ViewModel (Gestor de Autenticação)
        val authViewModel: AuthViewModel by viewModels()

        setContent {
            SafetySecTheme {
                // Controlador de Navegação
                val navController = rememberNavController()

                // Verifica estado atual para saber onde começar
                val isLoggedIn = authViewModel.isUserLoggedIn()
                val userType = authViewModel.userType.value

                // Define o destino inicial
                val startDestination = if (isLoggedIn && userType != UserType.NONE) {
                    if (userType == UserType.MONITOR) "monitorHome" else "protectedHome"
                } else {
                    "login"
                }

                // --- O NAVHOST (O Mapa de Estradas) ---
                NavHost(navController = navController, startDestination = startDestination) {

                    // Rota 1: Login
                    composable("login") {
                        LoginScreen(navController, authViewModel)
                    }

                    // Rota 2: Registo
                    composable("register") {
                        RegisterScreen(navController, authViewModel)
                    }

                    // Rota 3: Dashboard do Monitor
                    composable("monitorHome") {
                        MonitorDashboard(navController, authViewModel)
                    }

                    // Rota 4: Dashboard do Protegido
                    composable("protectedHome") {
                        ProtegidoDashboard(navController, authViewModel)
                    }
                }

                // --- REDIRECIONAMENTO AUTOMÁTICO (Efeito Secundário) ---
                // Se o utilizador fizer login com sucesso, muda de ecrã automaticamente
                if (isLoggedIn && userType != UserType.NONE) {
                    val targetRoute = if (userType == UserType.MONITOR) "monitorHome" else "protectedHome"

                    // Só navega se não estivermos já lá
                    if (navController.currentDestination?.route != targetRoute) {
                        navController.navigate(targetRoute) {
                            // Limpa o histórico para não voltar ao login com o botão "Voltar"
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}