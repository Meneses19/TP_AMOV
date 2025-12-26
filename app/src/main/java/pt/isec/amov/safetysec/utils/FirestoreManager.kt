package pt.isec.amov.safetysec.utils

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class FirestoreManager {
    private val db = FirebaseFirestore.getInstance()

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

    // Recebe o código (ex: "12345") e tenta ligar ao Monitor
    fun associateProtegido(code: String, idProtegido: String, onResult: (Boolean, String?) -> Unit) {
        // Procura o código na tabela de Associações
        db.collection("Associations").document(code).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val idMonitor = document.getString("monitorId")

                    if (idMonitor != null) {
                        //Atualizar o perfil do Protegido com o ID do Monitor
                        addAssociationToUser(idProtegido, idMonitor, onResult)

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
    fun createSOSAlert(protegidoId: String, onResult: (Boolean) -> Unit) {
        val alertData = hashMapOf(
            "protegidoId" to protegidoId,
            "type" to "SOS",
            "timestamp" to com.google.firebase.Timestamp.now(),
            "location" to null,
            "solved" to false
        )

        db.collection("Alerts").add(alertData)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }
    fun getMonitorAssociatedUsers(monitorId: String, onResult: (List<String>) -> Unit) {
        db.collection("Users").document(monitorId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val list = document.get("associatedUsers") as? List<String> ?: emptyList()
                    onResult(list)
                } else {
                    onResult(emptyList())
                }
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun listenForAlerts(protegidosIds: List<String>, onAlertsReceived: (List<pt.isec.amov.safetysec.model.Alert>) -> Unit) {
        if (protegidosIds.isEmpty()) {
            onAlertsReceived(emptyList())
            return
        }
        db.collection("Alerts")
            .whereIn("protegidoId", protegidosIds)
            .whereEqualTo("solved", false)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) {
                    return@addSnapshotListener
                }

                val alerts = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(pt.isec.amov.safetysec.model.Alert::class.java)?.copy(id = doc.id)
                }
                onAlertsReceived(alerts)
            }
    }
}