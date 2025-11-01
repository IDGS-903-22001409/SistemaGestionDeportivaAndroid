package navegacion.data.model

import java.time.LocalDate

data class Jugador(
    val id: Int,
    val usuarioId: Int,
    val equipoId: Int,
    val numeroJugador: Int,
    val posicion: Posicion,
    val fotoUrl: String? = null,
    val fechaNacimiento: LocalDate,
    val estatus: EstatusJugador = EstatusJugador.ACTIVO
)

enum class Posicion {
    PORTERO,
    DEFENSA,
    MEDIOCAMPISTA,
    DELANTERO
}

enum class EstatusJugador {
    ACTIVO,
    SUSPENDIDO,
    LESIONADO,
    INACTIVO
}

data class Estadistica(
    val jugadorId: Int,
    val torneoId: Int,
    val goles: Int = 0,
    val asistencias: Int = 0,
    val partidosJugados: Int = 0,
    val tarjetasAmarillas: Int = 0,
    val tarjetasRojas: Int = 0,
    val minutos: Int = 0
) {
    val promedioGolesPorPartido: Double
        get() = if (partidosJugados > 0) goles.toDouble() / partidosJugados else 0.0
}

data class EstadisticaDetallada(
    val estadistica: Estadistica,
    val efectividadTiro: Double,
    val pasesCompletados: Double,
    val posicionRanking: Int,
    val totalJugadores: Int
)
