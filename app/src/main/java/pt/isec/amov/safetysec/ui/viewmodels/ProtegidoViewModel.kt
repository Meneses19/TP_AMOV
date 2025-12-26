package pt.isec.amov.safetysec.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import pt.isec.amov.safetysec.utils.FAuthUtil
import pt.isec.amov.safetysec.utils.FirestoreManager

class ProtegidoViewModel : ViewModel() {
    private val authUtil = FAuthUtil()
    private val firestoreManager = FirestoreManager()

    var isLoading = mutableStateOf(false)
    var error = mutableStateOf<String?>(null)
    var success = mutableStateOf(false)

    fun associateWithMonitor(code: String) {
        val idUtilizador = authUtil.currentUser?.uid

        if (idUtilizador != null) {
            isLoading.value = true
            error.value = null
            success.value = false

            firestoreManager.associateProtegido(code, idUtilizador) { res, msg ->
                isLoading.value = false
                if (res) {
                    success.value = true
                } else {
                    error.value = msg
                }
            }
        }
    }
    fun sendSOS() {
        val idUtilizador = authUtil.currentUser?.uid
        if (idUtilizador != null) {
            isLoading.value = true

            firestoreManager.createSOSAlert(idUtilizador) { success ->
                isLoading.value = false
                if (success) {
                    android.util.Log.d("ProtegidoViewModel", "SOS enviado!")
                } else {
                    error.value = "Falha ao enviar SOS."
                }
            }
        }
    }
    fun logout() {
        authUtil.logout()
    }
}