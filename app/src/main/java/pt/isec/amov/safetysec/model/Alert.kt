package pt.isec.amov.safetysec.model

import com.google.firebase.Timestamp

data class Alert(
    val id: String = "",
    val protegidoId: String = "",
    val type: String = "",
    val timestamp: Timestamp? = null,
    val location: String? = null,
    val solved: Boolean = false
)