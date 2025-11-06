package com.example.sistemgestiondeportiva.data.api

import com.example.sistemgestiondeportiva.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ============ AUTH ============
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/registro-capitan")
    suspend fun registrarCapitan(@Body request: RegistroCapitanRequest): Response<ApiResponse<LoginResponse>>

    @POST("api/auth/registro-jugador")
    suspend fun registrarJugador(@Body request: RegistroJugadorRequest): Response<ApiResponse<LoginResponse>>

    @POST("api/auth/registro-arbitro")
    suspend fun registrarArbitro(@Body request: RegistroArbitroRequest): Response<ApiResponse<LoginResponse>>

    @GET("api/auth/validar-qr/{token}")
    suspend fun validarQR(@Path("token") token: String): Response<ApiResponse<QRData>>

    // ============ USUARIO ============
    @GET("api/usuario/perfil")
    suspend fun obtenerPerfil(@Header("Authorization") token: String): Response<ApiResponse<Usuario>>

    @PUT("api/usuario/actualizar")
    suspend fun actualizarUsuario(
        @Header("Authorization") token: String,
        @Body request: UpdateUsuarioRequest
    ): Response<GenericResponse>

    @PUT("api/usuario/cambiar-password")
    suspend fun cambiarPassword(
        @Header("Authorization") token: String,
        @Body request: UpdatePasswordRequest
    ): Response<GenericResponse>

    // ============ JUGADOR ============
    @GET("api/jugador/mi-perfil")
    suspend fun obtenerPerfilJugador(@Header("Authorization") token: String): Response<ApiResponse<Jugador>>

    @PUT("api/jugador/actualizar")
    suspend fun actualizarJugador(
        @Header("Authorization") token: String,
        @Body request: UpdateJugadorRequest
    ): Response<GenericResponse>

    @GET("api/jugador/estadisticas")
    suspend fun obtenerEstadisticasJugador(@Header("Authorization") token: String): Response<ApiResponse<EstadisticasJugador>>

    @GET("api/jugador/partidos")
    suspend fun obtenerPartidosJugador(@Header("Authorization") token: String): Response<ApiResponse<List<Partido>>>

    @GET("api/jugador/proximos-partidos")
    suspend fun obtenerProximosPartidos(@Header("Authorization") token: String): Response<ApiResponse<List<Partido>>>

    @GET("api/jugador/equipo")
    suspend fun obtenerEquipo(@Header("Authorization") token: String): Response<ApiResponse<Equipo>>

    @GET("api/jugador/equipo/jugadores")
    suspend fun obtenerJugadoresEquipo(@Header("Authorization") token: String): Response<ApiResponse<List<Jugador>>>

    @POST("api/jugador/equipo/generar-qr")
    suspend fun generarQRJugadores(@Header("Authorization") token: String): Response<ApiResponse<String>>

    // ============ ARBITRO ============
    @GET("api/arbitro/mi-perfil")
    suspend fun obtenerPerfilArbitro(@Header("Authorization") token: String): Response<ApiResponse<Arbitro>>

    @GET("api/arbitro/estadisticas")
    suspend fun obtenerEstadisticasArbitro(@Header("Authorization") token: String): Response<ApiResponse<EstadisticasArbitro>>

    @GET("api/arbitro/partidos")
    suspend fun obtenerPartidosArbitro(@Header("Authorization") token: String): Response<ApiResponse<List<Partido>>>

    @GET("api/arbitro/proximos-partidos")
    suspend fun obtenerProximosPartidosArbitro(@Header("Authorization") token: String): Response<ApiResponse<List<Partido>>>

    // ============ PARTIDO ============
    @GET("api/partido/{id}")
    suspend fun obtenerPartido(
        @Header("Authorization") token: String,
        @Path("id") partidoID: Int
    ): Response<ApiResponse<Partido>>

    @GET("api/partido/{id}/eventos")
    suspend fun obtenerEventosPartido(
        @Header("Authorization") token: String,
        @Path("id") partidoID: Int
    ): Response<ApiResponse<List<EventoPartido>>>

    @POST("api/partido/{id}/iniciar")
    suspend fun iniciarPartido(
        @Header("Authorization") token: String,
        @Path("id") partidoID: Int
    ): Response<GenericResponse>

    @POST("api/partido/{id}/finalizar")
    suspend fun finalizarPartido(
        @Header("Authorization") token: String,
        @Path("id") partidoID: Int
    ): Response<GenericResponse>

    @POST("api/partido/evento")
    suspend fun registrarEvento(
        @Header("Authorization") token: String,
        @Body request: RegistrarEventoRequest
    ): Response<ApiResponse<EventoPartido>>

    @DELETE("api/partido/evento/{id}")
    suspend fun eliminarEvento(
        @Header("Authorization") token: String,
        @Path("id") eventoID: Int
    ): Response<GenericResponse>
}