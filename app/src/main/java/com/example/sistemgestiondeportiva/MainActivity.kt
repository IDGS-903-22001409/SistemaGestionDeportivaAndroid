package com.example.sistemgestiondeportiva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
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
import com.example.sistemgestiondeportiva.presentation.components.QRScannerScreen
import com.example.sistemgestiondeportiva.presentation.jugador.home.JugadorHomeScreen
import com.example.sistemgestiondeportiva.presentation.jugador.home.JugadorViewModel
import com.example.sistemgestiondeportiva.presentation.jugador.home.JugadorViewModelFactory
import com.example.sistemgestiondeportiva.presentation.login.*
import com.example.sistemgestiondeportiva.theme.AplicationDemoTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.util.Base64


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AplicationDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
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
                rolID == 3 -> "jugador/home" // Jugador
                rolID == 4 -> "arbitro/home" // Arbitro
                else -> "login"
            }
        }
    }

    if (startDestination != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination!!
        ) {
            // Login y registro
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
                        // Codificar el token en Base64 para navegación segura
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
                                    "JUGADOR" -> navController.navigate("registro-jugador/$encodedToken/${qrData.equipoID}") {
                                        popUpTo("scan-qr") { inclusive = true }
                                    }
                                    "ARBITRO" -> navController.navigate("registro-arbitro/$encodedToken") {
                                        popUpTo("scan-qr") { inclusive = true }
                                    }
                                    else -> {
                                        // QR no válido
                                        navController.popBackStack()
                                    }
                                }
                            },
                            onError = { error ->
                                // Mostrar error y volver
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

                // Decodificar el token de Base64
                val token = try {
                    String(Base64.decode(encodedToken, Base64.URL_SAFE or Base64.NO_WRAP))
                } catch (e: Exception) {
                    encodedToken // Si falla, usar el original
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
                "registro-jugador/{token}/{equipoID}",
                arguments = listOf(
                    navArgument("token") { type = NavType.StringType },
                    navArgument("equipoID") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val encodedToken = backStackEntry.arguments?.getString("token") ?: ""
                val equipoID = backStackEntry.arguments?.getInt("equipoID") ?: 0

                // Decodificar el token de Base64
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

                // Decodificar el token de Base64
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

            // Jugador
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
                    }
                )
            }

            composable("jugador/estadisticas") {
                // TODO: Implementar pantalla de estadísticas
            }

            composable("jugador/partidos") {
                // TODO: Implementar pantalla de partidos
            }

            composable("jugador/perfil") {
                // TODO: Implementar pantalla de perfil
            }

            // Árbitro
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
                // TODO: Implementar pantalla de perfil
            }
        }
    }
}