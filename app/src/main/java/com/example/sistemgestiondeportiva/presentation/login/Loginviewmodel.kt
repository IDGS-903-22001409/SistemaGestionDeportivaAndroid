package com.example.sistemgestiondeportiva.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sistemgestiondeportiva.data.api.RetrofitClient
import com.example.sistemgestiondeportiva.data.local.UserPreferences
import com.example.sistemgestiondeportiva.data.models.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val context: Context) : ViewModel() {

    private val apiService = RetrofitClient.apiService
    private val userPreferences = UserPreferences(context)
    private val gson = Gson()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun login(
        email: String,
        password: String,
        onSuccess: (Int) -> Unit, // Devuelve rolID
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = apiService.login(LoginRequest(email, password))

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    // Guardar datos de autenticación
                    userPreferences.saveAuthData(
                        token = "Bearer ${loginResponse.token}",
                        usuario = loginResponse.usuario,
                        rolID = loginResponse.rol.rolID
                    )

                    onSuccess(loginResponse.rol.rolID)
                } else {
                    onError("Credenciales incorrectas")
                }
            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun validarQR(
        qrCode: String,
        onSuccess: (QRData) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Intentar parsear el QR como JSON
                val qrData = try {
                    gson.fromJson(qrCode, QRData::class.java)
                } catch (e: Exception) {
                    null
                }

                if (qrData != null && qrData.token.isNotBlank()) {
                    // Validar con el backend
                    val response = apiService.validarQR(qrData.token)

                    if (response.isSuccessful && response.body()?.success == true) {
                        onSuccess(response.body()!!.data!!)
                    } else {
                        onError("Código QR inválido o expirado")
                    }
                } else {
                    onError("Código QR no válido")
                }
            } catch (e: Exception) {
                onError("Error al validar QR: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registrarCapitan(
        token: String,
        nombre: String,
        email: String,
        password: String,
        telefono: String?,
        nombreEquipo: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val request = RegistroCapitanRequest(
                    token = token,
                    nombre = nombre,
                    email = email,
                    password = password,
                    telefono = telefono,
                    nombreEquipo = nombreEquipo
                )

                val response = apiService.registrarCapitan(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    val loginResponse = response.body()!!.data!!

                    // Guardar datos de autenticación
                    userPreferences.saveAuthData(
                        token = "Bearer ${loginResponse.token}",
                        usuario = loginResponse.usuario,
                        rolID = loginResponse.rol.rolID
                    )

                    onSuccess()
                } else {
                    onError(response.body()?.message ?: "Error al registrar")
                }
            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registrarJugador(
        token: String,
        equipoID: Int,
        nombre: String,
        email: String,
        password: String,
        telefono: String?,
        numeroCamiseta: Int,
        posicion: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val request = RegistroJugadorRequest(
                    token = token,
                    equipoID = equipoID,
                    nombre = nombre,
                    email = email,
                    password = password,
                    telefono = telefono,
                    numeroCamiseta = numeroCamiseta,
                    posicion = posicion
                )

                val response = apiService.registrarJugador(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    val loginResponse = response.body()!!.data!!

                    userPreferences.saveAuthData(
                        token = "Bearer ${loginResponse.token}",
                        usuario = loginResponse.usuario,
                        rolID = loginResponse.rol.rolID
                    )

                    onSuccess()
                } else {
                    onError(response.body()?.message ?: "Error al registrar")
                }
            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registrarArbitro(
        token: String,
        nombre: String,
        email: String,
        password: String,
        telefono: String?,
        licencia: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val request = RegistroArbitroRequest(
                    token = token,
                    nombre = nombre,
                    email = email,
                    password = password,
                    telefono = telefono,
                    licencia = licencia
                )

                val response = apiService.registrarArbitro(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    val loginResponse = response.body()!!.data!!

                    userPreferences.saveAuthData(
                        token = "Bearer ${loginResponse.token}",
                        usuario = loginResponse.usuario,
                        rolID = loginResponse.rol.rolID
                    )

                    onSuccess()
                } else {
                    onError(response.body()?.message ?: "Error al registrar")
                }
            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clearAuthData()
            onComplete()
        }
    }
}