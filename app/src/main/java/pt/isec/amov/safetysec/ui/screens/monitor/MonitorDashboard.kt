package pt.isec.amov.safetysec.ui.screens.monitor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pt.isec.amov.safetysec.ui.viewmodels.MonitorViewModel

@Composable
fun MonitorDashboard(
    navController: NavController,
    viewModel: MonitorViewModel = viewModel() // Cria o ViewModel automaticamente
) {
    val code = viewModel.generatedCode.value
    val isLoading = viewModel.isLoading.value

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Painel do MONITOR", style = MaterialTheme.typography.headlineLarge)
        Text("Adicione um protegido para começar.")

        Spacer(modifier = Modifier.height(32.dp))

        // CARTÃO COM O CÓDIGO
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Código de Associação", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                } else if (code != null) {
                    Text(
                        text = code,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Dê este código ao Protegido", fontSize = 12.sp)
                } else {
                    Text("----", fontSize = 32.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { viewModel.generateCode() }) {
                    Text("Gerar Novo Código")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(onClick = {
            viewModel.logout()
            navController.navigate("login") { popUpTo(0) }
        }) {
            Text("Sair")
        }
    }
}