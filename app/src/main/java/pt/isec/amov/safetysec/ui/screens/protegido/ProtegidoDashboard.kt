package pt.isec.amov.safetysec.ui.screens.protected

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.isec.amov.safetysec.ui.viewmodels.AuthViewModel

@Composable
fun ProtegidoDashboard(navController: NavController, viewModel: AuthViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Modo PROTEGIDO", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { /* TODO: Alerta */ },
            modifier = Modifier.size(150.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("SOS", style = MaterialTheme.typography.headlineMedium)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = {
            viewModel.logout()

            // ADICIONA ISTO
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }) {
            Text("Sair")
        }
    }
}