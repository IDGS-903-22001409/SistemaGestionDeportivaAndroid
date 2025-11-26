package com.example.sistemgestiondeportiva.data.models

import com.google.gson.annotations.SerializedName

// ============ AUTH MODELS ============
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("usua_Id") val usuaId: Int,
    @SerializedName("usua_NombreCompleto") val nombre: String,
    @SerializedName("usua_Email") val email: String,
    @SerializedName("rol_Id") val rolID: Int,
    @SerializedName("rol_Nombre") val rolNombre: String,
    @SerializedName("token") val token: String
)

data class Usuario(
    @SerializedName("usua_Id") val usuaId: Int,
    @SerializedName("usua_NombreCompleto") val nombre: String,
    @SerializedName("usua_Email") val email: String,
    @SerializedName("usua_Telefono") val telefono: String?,
    @SerializedName("rol_Id") val rolID: Int,           // ✅ El rolID viene aquí
    @SerializedName("rol_Nombre") val rolNombre: String  // ✅ El nombre del rol también
)
data class Rol(
    @SerializedName("rolID") val rolID: Int,
    @SerializedName("nombreRol") val nombreRol: String,
    @SerializedName("descripcion") val descripcion: String? = null
)

// ============ QR MODELS ============
data class QRData(
    @SerializedName("type") val type: String, // "CAPITAN" o "ARBITRO"
    @SerializedName("token") val token: String
)

data class RegistroCapitanRequest(
    @SerializedName("token") val token: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("telefono") val telefono: String? = null
)

data class RegistroJugadorRequest(
    @SerializedName("token") val token: String,
    @SerializedName("equipoID") val equipoID: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("numeroCamiseta") val numeroCamiseta: Int,
    @SerializedName("posicion") val posicion: String
)

data class RegistroArbitroRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("licencia") val licencia: String
)

// ============ JUGADOR MODELS ============
data class Jugador(
    @SerializedName("jugadorID") val jugadorID: Int,
    @SerializedName("usuarioID") val usuarioID: Int,
    @SerializedName("equipoID") val equipoID: Int?,  // ← PUEDE SER NULL
    @SerializedName("numeroCamiseta") val numeroCamiseta: Int,
    @SerializedName("posicion") val posicion: String,
    @SerializedName("esCapitan") val esCapitan: Boolean,
    @SerializedName("fechaIngreso") val fechaIngreso: String,
    @SerializedName("activo") val activo: Boolean,
    @SerializedName("usuario") val usuario: Usuario? = null,
    @SerializedName("equipo") val equipo: Equipo? = null
)

data class Equipo(
    @SerializedName("equipoID") val equipoID: Int,
    @SerializedName("nombreEquipo") val nombreEquipo: String,
    @SerializedName("logo") val logo: String? = null,
    @SerializedName("fechaCreacion") val fechaCreacion: String,
    @SerializedName("capitanID") val capitanID: Int,
    @SerializedName("activo") val activo: Boolean
)

data class EstadisticasJugador(
    @SerializedName("jugadorID") val jugadorID: Int,
    @SerializedName("partidosJugados") val partidosJugados: Int,
    @SerializedName("goles") val goles: Int,
    @SerializedName("asistencias") val asistencias: Int,
    @SerializedName("tarjetasAmarillas") val tarjetasAmarillas: Int,
    @SerializedName("tarjetasRojas") val tarjetasRojas: Int,
    @SerializedName("promedioGoles") val promedioGoles: Double,
    @SerializedName("jugador") val jugador: Jugador? = null
)

// ============ PARTIDO MODELS ============
data class Partido(
    @SerializedName("partidoID") val partidoID: Int,
    @SerializedName("equipoLocalID") val equipoLocalID: Int,
    @SerializedName("equipoVisitanteID") val equipoVisitanteID: Int,
    @SerializedName("fechaPartido") val fechaPartido: String,
    @SerializedName("lugarPartido") val lugarPartido: String,
    @SerializedName("golesLocal") val golesLocal: Int? = null,
    @SerializedName("golesVisitante") val golesVisitante: Int? = null,
    @SerializedName("estado") val estado: String,
    @SerializedName("arbitroID") val arbitroID: Int? = null,
    @SerializedName("equipoLocal") val equipoLocal: Equipo? = null,
    @SerializedName("equipoVisitante") val equipoVisitante: Equipo? = null,
    @SerializedName("arbitro") val arbitro: Arbitro? = null,
    @SerializedName("sede") val sede: Sede? = null  // ✅ AGREGAR ESTA LÍNEA
) {
    // ✅ AGREGAR ESTE COMPUTED PROPERTY
    val fechaHora: String
        get() = fechaPartido
}

// ============ ARBITRO MODELS ============
data class Arbitro(
    @SerializedName("arbitroID") val arbitroID: Int,
    @SerializedName("usuarioID") val usuarioID: Int,
    @SerializedName("licencia") val licencia: String,
    @SerializedName("fechaRegistro") val fechaRegistro: String,
    @SerializedName("activo") val activo: Boolean,
    @SerializedName("usuario") val usuario: Usuario? = null
)

data class EstadisticasArbitro(
    @SerializedName("arbitroID") val arbitroID: Int,
    @SerializedName("partidosArbitrados") val partidosArbitrados: Int,
    @SerializedName("tarjetasAmarillasOtorgadas") val tarjetasAmarillasOtorgadas: Int,
    @SerializedName("tarjetasRojasOtorgadas") val tarjetasRojasOtorgadas: Int,
    @SerializedName("promedioTarjetasPorPartido") val promedioTarjetasPorPartido: Double
)

// ============ EVENTO PARTIDO MODELS ============
data class EventoPartido(
    @SerializedName("eventoID") val eventoID: Int? = null,
    @SerializedName("partidoID") val partidoID: Int,
    @SerializedName("jugadorID") val jugadorID: Int,
    @SerializedName("tipoEvento") val tipoEvento: String, // "Gol", "Asistencia", "TarjetaAmarilla", "TarjetaRoja"
    @SerializedName("minuto") val minuto: Int,
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("jugador") val jugador: Jugador? = null
)

data class RegistrarEventoRequest(
    @SerializedName("partidoID") val partidoID: Int,
    @SerializedName("jugadorID") val jugadorID: Int,
    @SerializedName("tipoEvento") val tipoEvento: String,
    @SerializedName("minuto") val minuto: Int,
    @SerializedName("descripcion") val descripcion: String? = null
)

// ============ UPDATE MODELS ============
data class UpdateUsuarioRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("telefono") val telefono: String? = null
)

data class UpdatePasswordRequest(
    @SerializedName("passwordActual") val passwordActual: String,
    @SerializedName("passwordNuevo") val passwordNuevo: String
)

data class UpdateJugadorRequest(
    @SerializedName("numeroCamiseta") val numeroCamiseta: Int,
    @SerializedName("posicion") val posicion: String
)

// ============ RESPONSE WRAPPERS ============
data class ApiResponse<T>(
    @SerializedName("isSuccess") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: T?
)

data class GenericResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?
)
data class UpdatePerfilCompletoRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("numeroCamiseta") val numeroCamiseta: Int,
    @SerializedName("posicion") val posicion: String
)

data class JugadoresPartido(
    @SerializedName("equipoLocal") val equipoLocal: EquipoConJugadores,
    @SerializedName("equipoVisitante") val equipoVisitante: EquipoConJugadores
)

data class EquipoConJugadores(
    @SerializedName("equipoID") val equipoID: Int,
    @SerializedName("nombreEquipo") val nombreEquipo: String,
    @SerializedName("jugadores") val jugadores: List<JugadorSimple>
)

data class JugadorSimple(
    @SerializedName("jugadorID") val jugadorID: Int,
    @SerializedName("numeroCamiseta") val numeroCamiseta: Int,
    @SerializedName("posicion") val posicion: String,
    @SerializedName("nombre") val nombre: String
)

data class ActualizarArbitroRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("email") val email: String,
    @SerializedName("telefono") val telefono: String?
)

data class Sede(
    @SerializedName("sedeID") val sedeID: Int,
    @SerializedName("nombreSede") val nombreSede: String,
    @SerializedName("direccion") val direccion: String? = null,
    @SerializedName("capacidad") val capacidad: Int? = null
)