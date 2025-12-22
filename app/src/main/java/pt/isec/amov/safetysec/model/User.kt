package pt.isec.amov.safetysec.model

enum class UserType {
    MONITOR, PROTECTED, NONE
}

data class User(
    val id: String = "",           // ID único do Firebase Authentication
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val type: UserType = UserType.NONE, // Define se é Monitor ou Protegido
    val associatedUsers: List<String> = emptyList() // IDs dos utilizadores ligados a este
)