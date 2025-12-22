package pt.isec.amov.safetysec.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import pt.isec.amov.safetysec.utils.FAuthUtil
import pt.isec.amov.safetysec.utils.FirestoreManager

class MonitorViewModel : ViewModel() {
    private val authUtil = FAuthUtil()
    private val firestoreManager = FirestoreManager()

    // Estados
    var generatedCode = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)

    // Gera o código para dar ao Protegido
    fun generateCode() {
        // AQUI USAMOS O NOME INTUITIVO QUE ESCOLHESTE ANTES
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