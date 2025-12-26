package pt.isec.amov.safetysec.ui.screens.protegido

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pt.isec.amov.safetysec.ui.viewmodels.ProtegidoViewModel

@Composable
fun ProtegidoDashboard(
    navController: NavController,
    viewModel: ProtegidoViewModel = viewModel() // Cria o ViewModel automaticamente
) {
    var codeInput by remember { mutableStateOf("") }
    val isLoading = viewModel.isLoading.value
    val error = viewModel.error.value
    val success = viewModel.success.value
    val context = LocalContext.current

    // Mostra Toast quando a associação funciona
    LaunchedEffect(success) {
        if (success) {
            Toast.makeText(context, "Conectado ao Monitor com sucesso!", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Modo PROTEGIDO", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(32.dp))

        // CAIXA PARA INSERIR CÓDIGO
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Vincular a um Monitor", style = MaterialTheme.typography.titleMedium)
                Text("Insira o código que o Monitor lhe deu:", style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { codeInput = it },
                    label = { Text("Código (ex: 12345)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                if (error != null) {
                    Text(error, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.associateWithMonitor(codeInput) },
                    enabled = !isLoading && codeInput.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    else Text("Vincular")
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Botão SOS
        Button(
            onClick = {
                viewModel.sendSOS()
                Toast.makeText(context, "ALERTA ENVIADO!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.size(150.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("SOS", style = MaterialTheme.typography.headlineLarge)
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