package navegacion.data.repository

import com.example.torneomanager.data.model.*
import java.time.LocalDate
import java.time.LocalDateTime

object MockDataRepository {

    // Equipos mock
    private val equipos = listOf(
        Equipo(1, "Los Tigres FC", null, "#667EEA", "#764BA2", 1, true),
        Equipo(2, "Águilas United", null, "#F56565", "#ED8936", 2, true),
        Equipo(3, "Leones FC", null, "#48BB78", "#38A169", 3, true),
        Equipo(4, "Halcones SC", null, "#4299E1", "#3182CE", 4, true),
        Equipo(5, "Estrellas FC", null, "#ECC94B", "#D69E2E", 5, true),
        Equipo(6, "Rayos FC", null, "#9F7AEA", "#805AD5", 6, true),
        Equipo(7, "Pumas FC", null, "#ED8936", "#DD6B20", 7, true),
        Equipo(8, "Lobos SC", null, "#718096", "#4A5568", 8, true)
    )

    // Usuarios mock
    private val usuarios = listOf(
        Usuario(1, "juan.perez@correo.com", "Juan", "Pérez", "+52 123 456 7890", null, Rol.JUGADOR, true),
        Usuario(2, "roberto.martinez@correo.com", "Roberto", "Martínez", "+52 987 654 3210", null, Rol.ARBITRO, true)
    )

    // Jugadores mock
    private val jugadores = listOf(
        Jugador(1, 1, 1, 10, Posicion.DELANTERO, null, LocalDate.of(1995, 3, 15), EstatusJugador.ACTIVO)
    )

    // Partidos mock
    private val partidos = listOf(
        // Partido finalizado
        Partido(
            id = 1,
            torneoId = 1,
            torneoNombre = "Torneo Apertura 2025",
            equipoLocal = equipos[0],
            equipoVisitante = equipos[1],
            golesLocal = 3,
            golesVisitante = 2,
            fechaHora = LocalDateTime.now().minusDays(1),
            cancha = "Cancha Municipal Norte",
            jornada = 5,
            estatus = EstatusPartido.FINALIZADO
        ),
        // Partido en vivo
        Partido(
            id = 2,
            torneoId = 1,
            torneoNombre = "Torneo Apertura 2025",
            equipoLocal = equipos[2],
            equipoVisitante = equipos[3],
            golesLocal = 1,
            golesVisitante = 1,
            fechaHora = LocalDateTime.now().minusHours(1),
            cancha = "Estadio Central",
            jornada = 6,
            estatus = EstatusPartido.EN_VIVO,
            minuto = 67
        ),
        // Partido programado
        Partido(
            id = 3,
            torneoId = 1,
            torneoNombre = "Torneo Apertura 2025",
            equipoLocal = equipos[0],
            equipoVisitante = equipos[4],
            golesLocal = 0,
            golesVisitante = 0,
            fechaHora = LocalDateTime.now().plusDays(7),
            cancha = "Cancha Sur",
            jornada = 7,
            estatus = EstatusPartido.PROGRAMADO
        ),
        // Más partidos programados
        Partido(
            id = 4,
            torneoId = 1,
            torneoNombre = "Torneo Apertura 2025",
            equipoLocal = equipos[5],
            equipoVisitante = equipos[0],
            golesLocal = 0,
            golesVisitante = 0,
            fechaHora = LocalDateTime.now().plusDays(11),
            cancha = "Estadio Central",
            jornada = 8,
            estatus = EstatusPartido.PROGRAMADO
        ),
        // Partidos anteriores
        Partido(
            id = 5,
            torneoId = 1,
            torneoNombre = "Torneo Apertura 2025",
            equipoLocal = equipos[2],
            equipoVisitante = equipos[0],
            golesLocal = 2,
            golesVisitante = 2,
            fechaHora = LocalDateTime.now().minusDays(5),
            cancha = "Estadio Sur",
            jornada = 4,
            estatus = EstatusPartido.FINALIZADO
        ),
        Partido(
            id = 6,
            torneoId = 1,
            torneoNombre = "Torneo Apertura 2025",
            equipoLocal = equipos[0],
            equipoVisitante = equipos[7],
            golesLocal = 4,
            golesVisitante = 1,
            fechaHora = LocalDateTime.now().minusDays(9),
            cancha = "Campo Deportivo",
            jornada = 3,
            estatus = EstatusPartido.FINALIZADO
        ),
        Partido(
            id = 7,
            torneoId = 1,
            torneoNombre = "Torneo Apertura 2025",
            equipoLocal = equipos[6],
            equipoVisitante = equipos[0],
            golesLocal = 3,
            golesVisitante = 1,
            fechaHora = LocalDateTime.now().minusDays(13),
            cancha = "Cancha Principal",
            jornada = 2,
            estatus = EstatusPartido.FINALIZADO
        )
    )

    // Estadísticas mock
    private val estadisticas = Estadistica(
        jugadorId = 1,
        torneoId = 1,
        goles = 8,
        asistencias = 5,
        partidosJugados = 12,
        tarjetasAmarillas = 2,
        tarjetasRojas = 0,
        minutos = 1035
    )

    private val estadisticaDetallada = EstadisticaDetallada(
        estadistica = estadisticas,
        efectividadTiro = 62.0,
        pasesCompletados = 85.0,
        posicionRanking = 3,
        totalJugadores = 120
    )

    // Eventos mock
    private val eventos = listOf(
        Evento(1, 1, 1, "Torres", "Leones FC", TipoEvento.GOL, 45, 2, "Martínez", null),
        Evento(2, 1, 2, "Jiménez", "Halcones SC", TipoEvento.TARJETA_AMARILLA, 38, null, null, "Falta táctica"),
        Evento(3, 1, 3, "Rodríguez", "Halcones SC", TipoEvento.GOL, 23, null, null, null),
        Evento(4, 1, 4, "Ramírez", "Leones FC", TipoEvento.TARJETA_AMARILLA, 15, null, null, "Juego brusco")
    )

    // Funciones de acceso
    fun getUsuario(email: String): Usuario? = usuarios.find { it.email == email }

    fun getUsuarioById(id: Int): Usuario? = usuarios.find { it.id == id }

    fun getJugadorByUsuarioId(usuarioId: Int): Jugador? = jugadores.find { it.usuarioId == usuarioId }

    fun getEquipoById(equipoId: Int): Equipo? = equipos.find { it.id == equipoId }

    fun getPartidosByEquipo(equipoId: Int): List<Partido> = partidos.filter {
        it.equipoLocal.id == equipoId || it.equipoVisitante.id == equipoId
    }

    fun getPartidosFinalizadosByEquipo(equipoId: Int): List<Partido> =
        getPartidosByEquipo(equipoId).filter { it.estatus == EstatusPartido.FINALIZADO }

    fun getProximosPartidosByEquipo(equipoId: Int): List<Partido> =
        getPartidosByEquipo(equipoId).filter { it.estatus == EstatusPartido.PROGRAMADO }

    fun getEstadisticasByJugador(jugadorId: Int): Estadistica = estadisticas

    fun getEstadisticaDetalladaByJugador(jugadorId: Int): EstadisticaDetallada = estadisticaDetallada

    fun getResultadoJugadorEnPartido(jugadorId: Int, partidoId: Int): ResultadoJugador {
        // Datos mock para el partido específico
        return when (partidoId) {
            1 -> ResultadoJugador(goles = 2, asistencias = 1)
            5 -> ResultadoJugador(goles = 1, asistencias = 1)
            6 -> ResultadoJugador(goles = 1, asistencias = 2)
            7 -> ResultadoJugador(goles = 1, tarjetasAmarillas = 1)
            else -> ResultadoJugador()
        }
    }

    // Para árbitros
    fun getPartidosAsignadosArbitro(arbitroId: Int): List<Partido> {
        // Retorna partidos programados y en vivo
        return listOf(
            partidos[1], // En vivo
            partidos[2], // Programado hoy
            partidos[3]  // Programado futuro
        )
    }

    fun getEventosPartido(partidoId: Int): List<Evento> = eventos.filter { it.partidoId == partidoId }

    fun getGoleadores(): List<Triple<Int, String, Int>> {
        // Retorna: (posición, nombre, goles)
        return listOf(
            Triple(1, "Carlos Martínez", 12),
            Triple(2, "Roberto López", 10),
            Triple(3, "Juan Pérez (Tú)", 8),
            Triple(4, "Miguel Sánchez", 7),
            Triple(5, "Pedro González", 7)
        )
    }
}
