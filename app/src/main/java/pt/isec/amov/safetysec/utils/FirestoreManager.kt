package pt.isec.amov.safetysec.utils

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class FirestoreManager {
    private val db = FirebaseFirestore.getInstance()

    // --- FUNÇÕES DO MONITOR ---

    // Gera um código aleatório (ex: 12345) e guarda na coleção "Associations"
    fun generateAssociationCode(monitorId: String, onResult: (String?) -> Unit) {
        val code = Random.nextInt(10000, 99999).toString()

        val associationData = hashMapOf(
            "code" to code,
            "monitorId" to monitorId,
            "status" to "PENDING"
        )

        // Guarda o código na Firestore
        db.collection("Associations").document(code).set(associationData)
            .addOnSuccessListener {
                onResult(code)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    // --- FUNÇÕES DO PROTEGIDO ---

    // Recebe o código (ex: "12345") e tenta ligar ao Monitor
    fun associateProtegido(code: String, idProtegido: String, onResult: (Boolean, String?) -> Unit) {
        // 1. Procura o código na tabela de Associações
        db.collection("Associations").document(code).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // 2. Encontrámos! Quem é o monitor?
                    val idMonitor = document.getString("monitorId")

                    if (idMonitor != null) {
                        // 3. Atualizar o perfil do Protegido com o ID do Monitor
                        addAssociationToUser(idProtegido, idMonitor, onResult)

                        // Opcional: Também podíamos adicionar o Protegido à lista do Monitor aqui
                        addAssociationToUser(idMonitor, idProtegido) { _, _ -> }
                    } else {
                        onResult(false, "Código inválido (sem monitor associado).")
                    }
                } else {
                    onResult(false, "Código não encontrado.")
                }
            }
            .addOnFailureListener {
                onResult(false, "Erro ao validar código.")
            }
    }

    // Função auxiliar genérica: Adiciona um ID à lista "associatedUsers" de qualquer utilizador
    private fun addAssociationToUser(userId: String, otherUserId: String, onResult: (Boolean, String?) -> Unit) {
        db.collection("Users").document(userId)
            .update("associatedUsers", FieldValue.arrayUnion(otherUserId))
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, "Erro ao guardar associação: ${e.message}")
            }
    }
}