package pt.isec.amov.safetysec.ui.screens.monitor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pt.isec.amov.safetysec.ui.viewmodels.MonitorViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorDashboard(
    navController: NavController,
    viewModel: MonitorViewModel = viewModel()
) {
    val code = viewModel.generatedCode.value
    val isLoading = viewModel.isLoading.value
    val alerts = viewModel.activeAlerts.value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Painel do Monitor", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        navController.navigate("login") { popUpTo(0) }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Sair"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (alerts.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ATENÇÃO: ALERTAS ATIVOS!", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f) // Ocupa o espaço disponível
                ) {
                    items(alerts) { alert ->
                        AlertItem(alert)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50), // Verde bonito
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Tudo calmo.",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF2E7D32)
                    )
                    Text("Nenhum pedido de socorro ativo.", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CARTÃO GERAR CÓDIGO (Mais profissional)
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Adicionar Novo Protegido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading) {
                        CircularProgressIndicator()
                    } else if (code != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                            border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = code,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace, // Fonte tipo máquina de escrever
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                                letterSpacing = 4.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Partilhe este código com o Protegido.", fontSize = 12.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.generateCode() }) {
                            Text("Gerar Outro")
                        }
                    } else {
                        Text("Gere um código para associar um dispositivo.", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.generateCode() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Gerar Código Agora")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlertItem(alert: pt.isec.amov.safetysec.model.Alert) {
    val date = alert.timestamp?.toDate()
    val formatStr = if (date != null) SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(date) else "--:--"

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f) // Vermelho mais suave no fundo
        ),
        border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.error), // Borda vermelha forte
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = "Alert",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "PEDIDO DE SOCORRO!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Hora do alerta: ", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text(formatStr, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}