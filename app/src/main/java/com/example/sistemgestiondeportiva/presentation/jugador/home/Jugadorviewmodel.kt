package com.example.sistemgestiondeportiva.presentation.jugador.home

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

class JugadorViewModel(private val context: Context) : ViewModel() {

    private val apiService = RetrofitClient.apiService
    private val userPreferences = UserPreferences(context)

    private val _jugador = MutableStateFlow<Jugador?>(null)
    val jugador: StateFlow<Jugador?> = _jugador

    private val _estadisticas = MutableStateFlow<EstadisticasJugador?>(null)
    val estadisticas: StateFlow<EstadisticasJugador?> = _estadisticas

    private val _proximosPartidos = MutableStateFlow<List<Partido>>(emptyList())
    val proximosPartidos: StateFlow<List<Partido>> = _proximosPartidos

    private val _todosPartidos = MutableStateFlow<List<Partido>>(emptyList())
    val todosPartidos: StateFlow<List<Partido>> = _todosPartidos

    private val _equipo = MutableStateFlow<Equipo?>(null)
    val equipo: StateFlow<Equipo?> = _equipo

    private val _jugadoresEquipo = MutableStateFlow<List<Jugador>>(emptyList())
    val jugadoresEquipo: StateFlow<List<Jugador>> = _jugadoresEquipo

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
                    cargarPerfilJugador(token)
                    cargarEstadisticas(token)
                    cargarProximosPartidos(token)
                    cargarEquipo(token)
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun cargarPerfilJugador(token: String) {
        try {
            val response = apiService.obtenerPerfilJugador(token)
            if (response.isSuccessful && response.body()?.success == true) {
                _jugador.value = response.body()!!.data
            }
        } catch (e: Exception) {
            _error.value = "Error al cargar perfil: ${e.message}"
        }
    }

    private suspend fun cargarEstadisticas(token: String) {
        try {
            val response = apiService.obtenerEstadisticasJugador(token)
            if (response.isSuccessful && response.body()?.success == true) {
                _estadisticas.value = response.body()!!.data
            }
        } catch (e: Exception) {
            _error.value = "Error al cargar estadísticas: ${e.message}"
        }
    }

    private suspend fun cargarProximosPartidos(token: String) {
        try {
            val response = apiService.obtenerProximosPartidos(token)
            if (response.isSuccessful && response.body()?.success == true) {
                _proximosPartidos.value = response.body()!!.data ?: emptyList()
            }
        } catch (e: Exception) {
            _error.value = "Error al cargar próximos partidos: ${e.message}"
        }
    }

    fun cargarTodosPartidos() {
        viewModelScope.launch {
            try {
                val token = userPreferences.token.first()
                if (token != null) {
                    val response = apiService.obtenerPartidosJugador(token)
                    if (response.isSuccessful && response.body()?.success == true) {
                        _todosPartidos.value = response.body()!!.data ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar partidos: ${e.message}"
            }
        }
    }

    private suspend fun cargarEquipo(token: String) {
        try {
            val response = apiService.obtenerEquipo(token)
            if (response.isSuccessful && response.body()?.success == true) {
                _equipo.value = response.body()!!.data
            }
        } catch (e: Exception) {
            _error.value = "Error al cargar equipo: ${e.message}"
        }
    }

    fun cargarJugadoresEquipo() {
        viewModelScope.launch {
            try {
                val token = userPreferences.token.first()
                if (token != null) {
                    val response = apiService.obtenerJugadoresEquipo(token)
                    if (response.isSuccessful && response.body()?.success == true) {
                        _jugadoresEquipo.value = response.body()!!.data ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar jugadores del equipo: ${e.message}"
            }
        }
    }

    fun actualizarJugador(
        numeroCamiseta: Int,
        posicion: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = userPreferences.token.first()
                if (token != null) {
                    val request = UpdateJugadorRequest(numeroCamiseta, posicion)
                    val response = apiService.actualizarJugador(token, request)

                    if (response.isSuccessful && response.body()?.success == true) {
                        cargarPerfilJugador(token)
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

    fun generarQRJugadores(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val token = userPreferences.token.first()
                if (token != null) {
                    val response = apiService.generarQRJugadores(token)
                    if (response.isSuccessful && response.body()?.success == true) {
                        response.body()!!.data?.let { onSuccess(it) }
                    } else {
                        onError("Error al generar QR")
                    }
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }
}