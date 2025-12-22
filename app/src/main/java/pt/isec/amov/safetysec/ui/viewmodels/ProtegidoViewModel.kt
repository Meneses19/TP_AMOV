package pt.isec.amov.safetysec.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import pt.isec.amov.safetysec.utils.FAuthUtil
import pt.isec.amov.safetysec.utils.FirestoreManager

class ProtegidoViewModel : ViewModel() {
    private val authUtil = FAuthUtil()
    private val firestoreManager = FirestoreManager()

    // Estados
    var isLoading = mutableStateOf(false)
    var error = mutableStateOf<String?>(null)
    var success = mutableStateOf(false)

    // Envia o código para ligar ao Monitor
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

    fun logout() {
        authUtil.logout()
    }
}