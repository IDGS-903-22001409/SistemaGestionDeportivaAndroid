package com.example.sistemgestiondeportiva.presentation.arbitro

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemgestiondeportiva.data.api.RetrofitClient
import com.example.sistemgestiondeportiva.data.local.UserPreferences
import com.example.sistemgestiondeportiva.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ArbitroViewModel(private val context: Context) : ViewModel() {

    private val apiService = RetrofitClient.apiService
    private val userPreferences = UserPreferences(context)

    private val _arbitro = MutableStateFlow<Arbitro?>(null)
    val arbitro: StateFlow<Arbitro?> = _arbitro

    private val _estadisticas = MutableStateFlow<EstadisticasArbitro?>(null)
    val estadisticas: StateFlow<EstadisticasArbitro?> = _estadisticas

    private val _proximosPartidos = MutableStateFlow<List<Partido>>(emptyList())
    val proximosPartidos: StateFlow<List<Partido>> = _proximosPartidos

    private val _partidoActual = MutableStateFlow<Partido?>(null)
    val partidoActual: StateFlow<Partido?> = _partidoActual

    private val _eventosPartido = MutableStateFlow<List<EventoPartido>>(emptyList())
    val eventosPartido: StateFlow<List<EventoPartido>> = _eventosPartido

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cargarDatos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = userPreferences.token.first()
                if (token != null) {
                    cargarPerfilArbitro(token)
                    cargarEstadisticas(token)
                    cargarProximosPartidos(token)
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun cargarPerfilArbitro(token: String) {
        try {
            val response = apiService.obtenerPerfilArbitro(token)
            if (response.isSuccessful && response.body()?.success == true) {
                _arbitro.value = response.body()!!.data
            }
        } catch (e: Exception) {
            _error.value = "Error al cargar perfil: ${e.message}"
        }
    }

    private suspend fun cargarEstadisticas(token: String) {
        try {
            val response = apiService.obtenerEstadisticasArbitro(token)
            if (response.isSuccessful && response.body()?.success == true) {
                _estadisticas.value = response.body()!!.data
            }
        } catch (e: Exception) {
            _error.value = "Error al cargar estadísticas: ${e.message}"
        }
    }

    private suspend fun cargarProximosPartidos(token: String) {
        try {
            val response = apiService.obtenerProximosPartidosArbitro(token)
            if (response.isSuccessful && response.body()?.success == true) {
                _proximosPartidos.value = response.body()!!.data ?: emptyList()
            }
        } catch (e: Exception) {
            _error.value = "Error al cargar partidos: ${e.message}"
        }
    }

    fun cargarPartido(partidoID: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = userPreferences.token.first()
                if (token != null) {
                    val responsePartido = apiService.obtenerPartido(token, partidoID)
                    if (responsePartido.isSuccessful && responsePartido.body()?.success == true) {
                        _partidoActual.value = responsePartido.body()!!.data
                    }

                    val responseEventos = apiService.obtenerEventosPartido(token, partidoID)
                    if (responseEventos.isSuccessful && responseEventos.body()?.success == true) {
                        _eventosPartido.value = responseEventos.body()!!.data ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar partido: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun iniciarPartido(
        partidoID: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = userPreferences.token.first()
                if (token != null) {
                    val response = apiService.iniciarPartido(token, partidoID)
                    if (response.isSuccessful && response.body()?.success == true) {
                        cargarPartido(partidoID)
                        onSuccess()
                    } else {
                        onError(response.body()?.message ?: "Error al iniciar partido")
                    }
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    fun finalizarPartido(
        partidoID: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = userPreferences.token.first()
                if (token != null) {
                    val response = apiService.finalizarPartido(token, partidoID)
                    if (response.isSuccessful && response.body()?.success == true) {
                        cargarPartido(partidoID)
                        cargarProximosPartidos(token)
                        onSuccess()
                    } else {
                        onError(response.body()?.message ?: "Error al finalizar partido")
                    }
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    fun registrarEvento(
        partidoID: Int,
        jugadorID: Int,
        tipoEvento: String,
        minuto: Int,
        descripcion: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = userPreferences.token.first()
                if (token != null) {
                    val request = RegistrarEventoRequest(
                        partidoID = partidoID,
                        jugadorID = jugadorID,
                        tipoEvento = tipoEvento,
                        minuto = minuto,
                        descripcion = descripcion
                    )

                    val response = apiService.registrarEvento(token, request)
                    if (response.isSuccessful && response.body()?.success == true) {
                        cargarPartido(partidoID)
                        onSuccess()
                    } else {
                        onError(response.body()?.message ?: "Error al registrar evento")
                    }
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    fun eliminarEvento(
        eventoID: Int,
        partidoID: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = userPreferences.token.first()
                if (token != null) {
                    val response = apiService.eliminarEvento(token, eventoID)
                    if (response.isSuccessful && response.body()?.success == true) {
                        cargarPartido(partidoID)
                        onSuccess()
                    } else {
                        onError(response.body()?.message ?: "Error al eliminar evento")
                    }
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    private val _jugadoresPartido = MutableStateFlow<JugadoresPartido?>(null)
    val jugadoresPartido: StateFlow<JugadoresPartido?> = _jugadoresPartido

    fun cargarJugadoresPartido(partidoID: Int) {
        viewModelScope.launch {
            try {
                val token = userPreferences.token.first()
                if (token != null) {
                    val response = apiService.obtenerJugadoresPartido(token, partidoID)
                    if (response.isSuccessful && response.body()?.success == true) {
                        _jugadoresPartido.value = response.body()!!.data
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar jugadores: ${e.message}"
            }
        }
    }

    fun cambiarPassword(
        passwordActual: String,
        passwordNuevo: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = userPreferences.token.first()
                if (token != null) {
                    val request = mapOf(
                        "passwordActual" to passwordActual,
                        "passwordNuevo" to passwordNuevo
                    )

                    // Necesitas agregar este endpoint en ApiService.kt:
                    val response = apiService.cambiarPassword(token, request)

                    if (response.isSuccessful && response.body()?.success == true) {
                        onSuccess()
                    } else {
                        onError(response.body()?.message ?: "Error al cambiar contraseña")
                    }
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    fun actualizarPerfil(
        nombre: String,
        email: String,
        telefono: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = userPreferences.token.first()
                if (token != null) {
                    val request = ActualizarArbitroRequest(
                        nombre = nombre,
                        email = email,
                        telefono = telefono
                    )
                    val response = apiService.actualizarPerfilArbitro(token, request)

                    if (response.isSuccessful && response.body()?.success == true) {
                        cargarPerfilArbitro(token)
                        onSuccess()
                    } else {
                        onError(response.body()?.message ?: "Error al actualizar")
                    }
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }
}