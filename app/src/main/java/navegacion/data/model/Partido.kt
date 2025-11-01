package navegacion.data.model

import java.time.LocalDateTime

data class Partido(
    val id: Int,
    val torneoId: Int,
    val torneoNombre: String,
    val equipoLocal: Equipo,
    val equipoVisitante: Equipo,
    val golesLocal: Int = 0,
    val golesVisitante: Int = 0,
    val fechaHora: LocalDateTime,
    val cancha: String,
    val jornada: Int,
    val estatus: EstatusPartido,
    val minuto: Int = 0
)

enum class EstatusPartido {
    PROGRAMADO,
    EN_VIVO,
    FINALIZADO,
    CANCELADO
}

data class ResultadoJugador(
    val goles: Int = 0,
    val asistencias: Int = 0,
    val tarjetasAmarillas: Int = 0,
    val tarjetasRojas: Int = 0
)
