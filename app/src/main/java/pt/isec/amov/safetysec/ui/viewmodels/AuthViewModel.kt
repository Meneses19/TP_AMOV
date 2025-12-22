package pt.isec.amov.safetysec.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import pt.isec.amov.safetysec.model.UserType
import pt.isec.amov.safetysec.utils.FAuthUtil

class AuthViewModel : ViewModel() {
    private val authUtil = FAuthUtil()

    // Estados observáveis pela UI
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    var userType = mutableStateOf(UserType.NONE)

    fun isUserLoggedIn(): Boolean = authUtil.currentUser != null

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        isLoading.value = true
        errorMessage.value = null
        authUtil.login(email, pass) { success, error ->
            isLoading.value = false
            if (success) onSuccess() else errorMessage.value = error
        }
    }

    fun register(email: String, pass: String, name: String, phone: String, type: UserType, onSuccess: () -> Unit) {
        isLoading.value = true
        errorMessage.value = null

        // Log para veres no Logcat do Android Studio
        android.util.Log.d("AuthViewModel", "A iniciar registo para: $email")

        authUtil.register(email, pass, name, phone, type) { success, error ->
            // IMPORTANTE: Desligar o loading SEMPRE, aconteça o que acontecer
            isLoading.value = false

            if (success) {
                android.util.Log.d("AuthViewModel", "Registo com sucesso! Tipo: $type")
                userType.value = type
                onSuccess()
            } else {
                android.util.Log.e("AuthViewModel", "Erro no registo: $error")
                errorMessage.value = error ?: "Erro desconhecido"
            }
        }
    }

    fun logout() {
        authUtil.logout()
    }
}