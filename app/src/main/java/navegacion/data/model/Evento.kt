package navegacion.data.model

import java.time.LocalDateTime

data class Evento(
    val id: Int,
    val partidoId: Int,
    val jugadorId: Int,
    val jugadorNombre: String,
    val equipoNombre: String,
    val tipoEvento: TipoEvento,
    val minuto: Int,
    val asistenciaJugadorId: Int? = null,
    val asistenciaJugadorNombre: String? = null,
    val comentarios: String? = null,
    val fechaHora: LocalDateTime = LocalDateTime.now()
)

enum class TipoEvento {
    GOL,
    TARJETA_AMARILLA,
    TARJETA_ROJA,
    CAMBIO
}
