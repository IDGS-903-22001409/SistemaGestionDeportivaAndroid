package navegacion.data.model

data class Equipo(
    val id: Int,
    val nombre: String,
    val logoUrl: String? = null,
    val colorPrimario: String,
    val colorSecundario: String,
    val capitanId: Int,
    val activo: Boolean = true
)
