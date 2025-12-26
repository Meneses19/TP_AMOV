package pt.isec.amov.safetysec.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import pt.isec.amov.safetysec.utils.FAuthUtil
import pt.isec.amov.safetysec.utils.FirestoreManager
import pt.isec.amov.safetysec.model.Alert

class MonitorViewModel : ViewModel() {
    private val authUtil = FAuthUtil()
    private val firestoreManager = FirestoreManager()

    var generatedCode = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)
    var activeAlerts = mutableStateOf<List<Alert>>(emptyList())

    init {
        startListening()
    }
    private fun startListening() {
        val monitorId = authUtil.currentUser?.uid
        if (monitorId != null) {
            firestoreManager.getMonitorAssociatedUsers(monitorId) { usersList ->
                if (usersList.isNotEmpty()) {
                    firestoreManager.listenForAlerts(usersList) { alerts ->
                        activeAlerts.value = alerts
                    }
                }
            }
        }
    }
    fun generateCode() {
        val idUtilizador = authUtil.currentUser?.uid

        if (idUtilizador != null) {
            isLoading.value = true
            firestoreManager.generateAssociationCode(idUtilizador) { code ->
                isLoading.value = false
                generatedCode.value = code
            }
        }
    }

    fun logout() {
        authUtil.logout()
    }
}