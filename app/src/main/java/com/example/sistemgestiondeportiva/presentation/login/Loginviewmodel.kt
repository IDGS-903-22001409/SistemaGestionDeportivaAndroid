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
import android.util.Log

class LoginViewModel(private val context: Context) : ViewModel() {

    private val apiService = RetrofitClient.apiService
    private val userPreferences = UserPreferences(context)
    private val gson = Gson()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun login(
        email: String,
        password: String,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val request = LoginRequest(email = email, password = password)
                val response = apiService.login(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    val loginResponse = response.body()!!.data!!

                    // Crear objeto Usuario desde LoginResponse
                    val usuario = Usuario(
                        usuaId = loginResponse.usuaId,
                        nombre = loginResponse.nombre,
                        email = loginResponse.email,
                        telefono = null,
                        rolID = loginResponse.rolID,
                        rolNombre = loginResponse.rolNombre
                    )

                    userPreferences.saveAuthData(
                        token = "Bearer ${loginResponse.token}",
                        usuario = usuario,
                        rolID = loginResponse.rolID
                    )

                    onSuccess(loginResponse.rolID)
                } else {
                    onError(response.body()?.message ?: "Error al iniciar sesión")
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
                val response = apiService.validarQR(qrCode)

                if (response.isSuccessful && response.body()?.success == true) {
                    val qrData = response.body()!!.data!!
                    onSuccess(qrData)
                } else {
                    onError(response.body()?.message ?: "Código QR inválido")
                }
            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
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
                    telefono = telefono
                )

                val response = apiService.registrarCapitan(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    val loginResponse = response.body()!!.data!!

                    // ✅ CORRECCIÓN: LoginResponse tiene estructura plana, igual que login()
                    val usuario = Usuario(
                        usuaId = loginResponse.usuaId,
                        nombre = loginResponse.nombre,
                        email = loginResponse.email,
                        telefono = telefono,
                        rolID = loginResponse.rolID,
                        rolNombre = loginResponse.rolNombre
                    )

                    userPreferences.saveAuthData(
                        token = "Bearer ${loginResponse.token}",
                        usuario = usuario,
                        rolID = loginResponse.rolID
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
                    nombre = nombre,
                    email = email,
                    password = password,
                    telefono = telefono,
                    licencia = licencia
                )

                val response = apiService.registrarArbitro(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    val loginResponse = response.body()!!.data!!

                    // ✅ CORRECCIÓN: LoginResponse tiene estructura plana
                    val usuario = Usuario(
                        usuaId = loginResponse.usuaId,
                        nombre = loginResponse.nombre,
                        email = loginResponse.email,
                        telefono = telefono,
                        rolID = loginResponse.rolID,
                        rolNombre = loginResponse.rolNombre
                    )

                    userPreferences.saveAuthData(
                        token = "Bearer ${loginResponse.token}",
                        usuario = usuario,
                        rolID = loginResponse.rolID
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

                    // ✅ CORRECCIÓN: LoginResponse tiene estructura plana
                    val usuario = Usuario(
                        usuaId = loginResponse.usuaId,
                        nombre = loginResponse.nombre,
                        email = loginResponse.email,
                        telefono = telefono,
                        rolID = loginResponse.rolID,
                        rolNombre = loginResponse.rolNombre
                    )

                    userPreferences.saveAuthData(
                        token = "Bearer ${loginResponse.token}",
                        usuario = usuario,
                        rolID = loginResponse.rolID
                    )

                    onSuccess()
                } else {
                    onError(response.body()?.message ?: "Error al registrar jugador")
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