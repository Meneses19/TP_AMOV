package pt.isec.amov.safetysec.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pt.isec.amov.safetysec.model.User
import pt.isec.amov.safetysec.model.UserType

class FAuthUtil {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Devolve o utilizador atual (ou null se não estiver logado)
    val currentUser get() = auth.currentUser

    // REGISTO
    fun register(
        email: String,
        pass: String,
        name: String,
        phone: String,
        type: UserType,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                val userId = result.user?.uid
                if (userId != null) {
                    val newUser = User(id = userId, email = email, name = name, phone = phone, type = type)
                    saveUserToFirestore(newUser, onResult)
                } else {
                    onResult(false, "Erro ao obter ID.")
                }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message ?: "Erro desconhecido.")
            }
    }

    // LOGIN
    fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    // LOGOUT
    fun logout() {
        auth.signOut()
    }

    // Auxiliar: Guarda dados extra na Firestore (coleção "Users")
    private fun saveUserToFirestore(user: User, onResult: (Boolean, String?) -> Unit) {
        db.collection("Users").document(user.id).set(user)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, "Erro DB: ${e.message}") }
    }

    fun getUserData(idUtilizador: String, onResult: (User?) -> Unit) {
        db.collection("Users").document(idUtilizador).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    onResult(user)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}