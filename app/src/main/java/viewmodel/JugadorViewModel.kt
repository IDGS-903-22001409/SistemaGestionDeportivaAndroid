package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import navegacion.data.model.Equipo
import navegacion.data.model.Estadistica
import navegacion.data.model.EstadisticaDetallada
import navegacion.data.model.EstatusPartido
import navegacion.data.model.Jugador
import navegacion.data.model.Partido
import navegacion.data.model.ResultadoJugador
import navegacion.data.model.Usuario
import navegacion.data.repository.MockDataRepository

data class JugadorUiState(
    val usuario: Usuario? = null,
    val jugador: Jugador? = null,
    val equipo: Equipo? = null,
    val estadisticas: Estadistica? = null,
    val estadisticaDetallada: EstadisticaDetallada? = null,
    val ultimosResultados: List<Partido> = emptyList(),
    val proximosPartidos: List<Partido> = emptyList(),
    val todosLosPartidos: List<Partido> = emptyList(),
    val resultadosJugador: Map<Int, ResultadoJugador> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class JugadorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(JugadorUiState())
    val uiState: StateFlow<JugadorUiState> = _uiState.asStateFlow()

    fun cargarDatosJugador(usuarioId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val usuario = MockDataRepository.getUsuarioById(usuarioId)
                val jugador = MockDataRepository.getJugadorByUsuarioId(usuarioId)
                val equipo = jugador?.let { MockDataRepository.getEquipoById(it.equipoId) }

                if (jugador != null && equipo != null) {
                    val estadisticas = MockDataRepository.getEstadisticasByJugador(jugador.id)
                    val estadisticaDetallada = MockDataRepository.getEstadisticaDetalladaByJugador(jugador.id)

                    val todosPartidos = MockDataRepository.getPartidosByEquipo(equipo.id)
                    val finalizados = todosPartidos
                        .filter { it.estatus == EstatusPartido.FINALIZADO }
                        .sortedByDescending { it.fechaHora }
                    val proximos = todosPartidos
                        .filter { it.estatus == EstatusPartido.PROGRAMADO || it.estatus == EstatusPartido.EN_VIVO }
                        .sortedBy { it.fechaHora }

                    // Cargar resultados del jugador en cada partido
                    val resultados = mutableMapOf<Int, ResultadoJugador>()
                    finalizados.forEach { partido ->
                        resultados[partido.id] = MockDataRepository.getResultadoJugadorEnPartido(jugador.id, partido.id)
                    }

                    _uiState.value = _uiState.value.copy(
                        usuario = usuario,
                        jugador = jugador,
                        equipo = equipo,
                        estadisticas = estadisticas,
                        estadisticaDetallada = estadisticaDetallada,
                        ultimosResultados = finalizados.take(3),
                        proximosPartidos = proximos,
                        todosLosPartidos = todosPartidos,
                        resultadosJugador = resultados,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No se encontró información del jugador"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun getPartidosFiltrados(filtro: FiltroPartido): List<Partido> {
        val todosPartidos = _uiState.value.todosLosPartidos

        return when (filtro) {
            FiltroPartido.TODOS -> todosPartidos.sortedByDescending { it.fechaHora }
            FiltroPartido.PROXIMOS -> todosPartidos
                .filter { it.estatus == EstatusPartido.PROGRAMADO }
                .sortedBy { it.fechaHora }
            FiltroPartido.JUGADOS -> todosPartidos
                .filter { it.estatus == EstatusPartido.FINALIZADO }
                .sortedByDescending { it.fechaHora }
            FiltroPartido.GANADOS -> {
                val equipoId = _uiState.value.equipo?.id ?: return emptyList()
                todosPartidos
                    .filter {
                        it.estatus == EstatusPartido.FINALIZADO &&
                                ((it.equipoLocal.id == equipoId && it.golesLocal > it.golesVisitante) ||
                                        (it.equipoVisitante.id == equipoId && it.golesVisitante > it.golesLocal))
                    }
                    .sortedByDescending { it.fechaHora }
            }
        }
    }
}

enum class FiltroPartido {
    TODOS,
    PROXIMOS,
    JUGADOS,
    GANADOS
}