package pt.isec.amov.safetysec.ui.screens.monitor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.isec.amov.safetysec.ui.viewmodels.AuthViewModel

@Composable
fun MonitorDashboard(navController: NavController, viewModel: AuthViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Painel do MONITOR", style = MaterialTheme.typography.headlineLarge)
        Text("Bem-vindo!")

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(onClick = {
            viewModel.logout()

            // ADICIONA ISTO: Força a navegação de volta ao Login
            navController.navigate("login") {
                popUpTo(0) { inclusive = true } // Limpa o histórico para não dar para voltar atrás
            }
        }) {
            Text("Sair")
        }
    }
}