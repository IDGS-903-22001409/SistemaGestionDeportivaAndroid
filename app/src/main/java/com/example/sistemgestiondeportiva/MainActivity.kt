package com.example.sistemgestiondeportiva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sistemgestiondeportiva.data.local.UserPreferences
import com.example.sistemgestiondeportiva.presentation.arbitro.ArbitroHomeScreen
import com.example.sistemgestiondeportiva.presentation.arbitro.ArbitroViewModel
import com.example.sistemgestiondeportiva.presentation.arbitro.ArbitroViewModelFactory
import com.example.sistemgestiondeportiva.presentation.arbitro.GestionPartidoScreen
import com.example.sistemgestiondeportiva.presentation.arbitro.ArbitroPerfilScreen
import com.example.sistemgestiondeportiva.presentation.components.QRScannerScreen
import com.example.sistemgestiondeportiva.presentation.jugador.home.JugadorHomeScreen
import com.example.sistemgestiondeportiva.presentation.jugador.home.JugadorViewModel
import com.example.sistemgestiondeportiva.presentation.jugador.home.JugadorViewModelFactory
import com.example.sistemgestiondeportiva.presentation.jugador.equipo.JugadorGenerarQRScreen
import com.example.sistemgestiondeportiva.presentation.jugador.equipo.JugadorEquipoScreen
import com.example.sistemgestiondeportiva.presentation.jugador.estadisticas.JugadorEstadisticasScreen
import com.example.sistemgestiondeportiva.presentation.jugador.partidos.JugadorPartidosScreen
import com.example.sistemgestiondeportiva.presentation.jugador.partidos.JugadorDetallePartidoScreen
import com.example.sistemgestiondeportiva.presentation.jugador.perfil.JugadorEditarPerfilScreen
import com.example.sistemgestiondeportiva.presentation.jugador.perfil.JugadorPerfilScreen
import com.example.sistemgestiondeportiva.presentation.login.*
import com.example.sistemgestiondeportiva.theme.AplicationDemoTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.util.Base64
import com.example.sistemgestiondeportiva.presentation.arbitro.ArbitroEditarPerfilScreen
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AplicationDemoTheme(darkTheme = true, dynamicColor = false) {
                // Futuristic gradient background applied globally
                com.example.sistemgestiondeportiva.presentation.components.FuturisticBackground(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userPreferences = remember { UserPreferences(navController.context) }
    val scope = rememberCoroutineScope()

    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            val isLoggedIn = userPreferences.isLoggedIn.first()
            val rolID = userPreferences.rolID.first()

            startDestination = when {
                !isLoggedIn -> "login"
                rolID == 3 -> {
                    // Capitán: verificar si tiene equipo
                    val usuario = userPreferences.userData.first()
                    // Si no tiene equipo asignado, mostrar pantalla de espera
                    "jugador/home" // La lógica de "sin equipo" se maneja en JugadorHomeScreen
                }
                rolID == 4 -> "arbitro/home" // Árbitro
                else -> "login"
            }
        }
    }

    if (startDestination != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination!!
        ) {
            // ========== LOGIN ==========
            composable("login") {
                val viewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(navController.context)
                )
                LoginScreen(
                    onLoginSuccess = { rolID ->
                        when (rolID) {
                            3 -> navController.navigate("jugador/home") {
                                popUpTo("login") { inclusive = true }
                            }
                            4 -> navController.navigate("arbitro/home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    },
                    onScanQR = {
                        navController.navigate("scan-qr")
                    },
                    viewModel = viewModel
                )
            }


            composable("scan-qr") {
                val viewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(navController.context)
                )
                QRScannerScreen(
                    onQRCodeScanned = { qrCode ->
                        val encodedToken = Base64.encodeToString(
                            qrCode.toByteArray(),
                            Base64.URL_SAFE or Base64.NO_WRAP
                        )

                        viewModel.validarQR(
                            qrCode = qrCode,
                            onSuccess = { qrData ->
                                when (qrData.type) {
                                    "CAPITAN" -> navController.navigate("registro-capitan/$encodedToken") {
                                        popUpTo("scan-qr") { inclusive = true }
                                    }
                                    "ARBITRO" -> navController.navigate("registro-arbitro/$encodedToken") {
                                        popUpTo("scan-qr") { inclusive = true }
                                    }
                                    else -> {
                                        navController.popBackStack()
                                    }
                                }
                            },
                            onError = { error ->
                                println("Error al validar QR: $error")
                                navController.popBackStack()
                            }
                        )
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                "registro-capitan/{token}",
                arguments = listOf(navArgument("token") { type = NavType.StringType })
            ) { backStackEntry ->
                val encodedToken = backStackEntry.arguments?.getString("token") ?: ""
                val token = try {
                    String(Base64.decode(encodedToken, Base64.URL_SAFE or Base64.NO_WRAP))
                } catch (e: Exception) {
                    encodedToken
                }

                val viewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(navController.context)
                )
                RegistroCapitanScreen(
                    token = token,
                    onRegistroSuccess = {
                        navController.navigate("jugador/home") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    viewModel = viewModel
                )
            }

            composable(
                "registro-arbitro/{token}",
                arguments = listOf(navArgument("token") { type = NavType.StringType })
            ) { backStackEntry ->
                val encodedToken = backStackEntry.arguments?.getString("token") ?: ""
                val token = try {
                    String(Base64.decode(encodedToken, Base64.URL_SAFE or Base64.NO_WRAP))
                } catch (e: Exception) {
                    encodedToken
                }

                val viewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(navController.context)
                )
                RegistroArbitroScreen(
                    token = token,
                    onRegistroSuccess = {
                        navController.navigate("arbitro/home") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    viewModel = viewModel
                )
            }

            composable(
                "registro-jugador/{token}/{equipoID}",
                arguments = listOf(
                    navArgument("token") { type = NavType.StringType },
                    navArgument("equipoID") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val encodedToken = backStackEntry.arguments?.getString("token") ?: ""
                val equipoID = backStackEntry.arguments?.getInt("equipoID") ?: 0

                val token = try {
                    String(Base64.decode(encodedToken, Base64.URL_SAFE or Base64.NO_WRAP))
                } catch (e: Exception) {
                    encodedToken
                }

                val viewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(navController.context)
                )
                RegistroJugadorScreen(
                    token = token,
                    equipoID = equipoID,
                    onRegistroSuccess = {
                        navController.navigate("jugador/home") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    viewModel = viewModel
                )
            }

            composable(
                "registro-arbitro/{token}",
                arguments = listOf(navArgument("token") { type = NavType.StringType })
            ) { backStackEntry ->
                val encodedToken = backStackEntry.arguments?.getString("token") ?: ""

                val token = try {
                    String(Base64.decode(encodedToken, Base64.URL_SAFE or Base64.NO_WRAP))
                } catch (e: Exception) {
                    encodedToken
                }

                val viewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(navController.context)
                )
                RegistroArbitroScreen(
                    token = token,
                    onRegistroSuccess = {
                        navController.navigate("arbitro/home") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    viewModel = viewModel
                )
            }

            // ========================================
            // JUGADOR - PANTALLAS
            // ========================================
            composable("jugador/home") {
                val viewModel: JugadorViewModel = viewModel(
                    factory = JugadorViewModelFactory(navController.context)
                )
                JugadorHomeScreen(
                    viewModel = viewModel,
                    onNavigateToStats = {
                        navController.navigate("jugador/estadisticas")
                    },
                    onNavigateToMatches = {
                        navController.navigate("jugador/partidos")
                    },
                    onNavigateToProfile = {
                        navController.navigate("jugador/perfil")
                    },
                    onNavigateToGenerarQR = {
                        navController.navigate("jugador/generar-qr")
                    },
                    onNavigateToEquipo = {  // ⬅️ NUEVO
                        navController.navigate("jugador/equipo")
                    }
                )
            }

            composable("jugador/equipo") {
                val viewModel: JugadorViewModel = viewModel(
                    factory = JugadorViewModelFactory(navController.context)
                )
                JugadorEquipoScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("jugador/estadisticas") {
                val viewModel: JugadorViewModel = viewModel(
                    factory = JugadorViewModelFactory(navController.context)
                )
                JugadorEstadisticasScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // ⭐ NUEVA: Lista de Partidos del Jugador
            composable("jugador/partidos") {
                val viewModel: JugadorViewModel = viewModel(
                    factory = JugadorViewModelFactory(navController.context)
                )
                JugadorPartidosScreen(
                    viewModel = viewModel,
                    onNavigateToPartido = { partidoID ->  // ⬅️ CAMBIO AQUÍ
                        navController.navigate("jugador/partido/$partidoID")
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // ⭐ NUEVA: Detalle de Partido del Jugador
            composable(
                "jugador/partido/{partidoID}",
                arguments = listOf(navArgument("partidoID") { type = NavType.IntType })
            ) { backStackEntry ->
                val partidoID = backStackEntry.arguments?.getInt("partidoID") ?: 0
                val viewModel: ArbitroViewModel = viewModel(
                    factory = ArbitroViewModelFactory(navController.context)
                )
                JugadorDetallePartidoScreen(
                    partidoID = partidoID,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // ⭐ NUEVA: Equipo y Compañeros
            composable("jugador/equipo") {
                val viewModel: JugadorViewModel = viewModel(
                    factory = JugadorViewModelFactory(navController.context)
                )
                JugadorEquipoScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("jugador/generar-qr") {
                val viewModel: JugadorViewModel = viewModel(
                    factory = JugadorViewModelFactory(navController.context)
                )
                JugadorGenerarQRScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("jugador/perfil") {
                val viewModel: JugadorViewModel = viewModel(
                    factory = JugadorViewModelFactory(navController.context)
                )
                JugadorPerfilScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onEditProfile = { navController.navigate("jugador/editar-perfil") },
                    onChangePassword = { navController.navigate("jugador/cambiar-password") }, // ⬅️ AGREGAR
                    onLogout = {
                        scope.launch {
                            userPreferences.clearAuthData()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable("jugador/editar-perfil") {
                val viewModel: JugadorViewModel = viewModel(
                    factory = JugadorViewModelFactory(navController.context)
                )
                JugadorEditarPerfilScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            // ========================================
            // ÁRBITRO - PANTALLAS
            // ========================================
            composable("arbitro/home") {
                val viewModel: ArbitroViewModel = viewModel(
                    factory = ArbitroViewModelFactory(navController.context)
                )
                ArbitroHomeScreen(
                    viewModel = viewModel,
                    onNavigateToMatch = { partidoID ->
                        navController.navigate("arbitro/partido/$partidoID")
                    },
                    onNavigateToProfile = {
                        navController.navigate("arbitro/perfil")
                    }
                )
            }

            composable(
                "arbitro/partido/{partidoID}",
                arguments = listOf(navArgument("partidoID") { type = NavType.IntType })
            ) { backStackEntry ->
                val partidoID = backStackEntry.arguments?.getInt("partidoID") ?: 0
                val viewModel: ArbitroViewModel = viewModel(
                    factory = ArbitroViewModelFactory(navController.context)
                )
                GestionPartidoScreen(
                    partidoID = partidoID,
                    viewModel = viewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable("arbitro/perfil") {
                val viewModel: ArbitroViewModel = viewModel(
                    factory = ArbitroViewModelFactory(navController.context)
                )
                ArbitroPerfilScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onEditProfile = { navController.navigate("arbitro/editar-perfil") },
                    onChangePassword = { navController.navigate("arbitro/cambiar-password") }, // ⬅️ AGREGAR
                    onLogout = {
                        scope.launch {
                            userPreferences.clearAuthData()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable("jugador/cambiar-password") {
                val viewModel: JugadorViewModel = viewModel(
                    factory = JugadorViewModelFactory(navController.context)
                )
                com.example.sistemgestiondeportiva.presentation.common.CambiarPasswordScreen(
                    onBackClick = { navController.popBackStack() },
                    onPasswordChanged = { actual, nuevo ->
                        suspendCancellableCoroutine { continuation ->
                            viewModel.cambiarPassword(
                                passwordActual = actual,
                                passwordNuevo = nuevo,
                                onSuccess = { continuation.resume(Result.success("OK")) },
                                onError = { error -> continuation.resume(Result.failure(Exception(error))) }
                            )
                        }
                    }
                )
            }

            composable("arbitro/cambiar-password") {
                val viewModel: ArbitroViewModel = viewModel(
                    factory = ArbitroViewModelFactory(navController.context)
                )
                com.example.sistemgestiondeportiva.presentation.common.CambiarPasswordScreen(
                    onBackClick = { navController.popBackStack() },
                    onPasswordChanged = { actual, nuevo ->
                        suspendCancellableCoroutine { continuation ->
                            viewModel.cambiarPassword(
                                passwordActual = actual,
                                passwordNuevo = nuevo,
                                onSuccess = { continuation.resume(Result.success("OK")) },
                                onError = { error -> continuation.resume(Result.failure(Exception(error))) }
                            )
                        }
                    }
                )
            }
            composable("arbitro/editar-perfil") {
                val viewModel: ArbitroViewModel = viewModel(
                    factory = ArbitroViewModelFactory(navController.context)
                )
                ArbitroEditarPerfilScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
        }
    }
}