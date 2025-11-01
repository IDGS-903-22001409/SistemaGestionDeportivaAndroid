package navegacion.data.model

data class Usuario(
    val id: Int,
    val email: String,
    val nombre: String,
    val apellidos: String,
    val telefono: String,
    val fotoUrl: String? = null,
    val rol: Rol,
    val activo: Boolean = true
) {
    val nombreCompleto: String
        get() = "$nombre $apellidos"
}

enum class Rol {
    JUGADOR,
    ARBITRO,
    ADMIN,
    CAPITAN
}
