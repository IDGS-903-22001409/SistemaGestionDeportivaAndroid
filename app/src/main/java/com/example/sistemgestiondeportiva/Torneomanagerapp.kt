package com.example.sistemgestiondeportiva

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import navegacion.ui.navigation.Screen
import navegacion.ui.screens.auth.LoginScreen
import navegacion.ui.screens.jugador.HomeJugadorScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TorneoManagerApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determinar si mostrar bottom navigation
    val showBottomNav = when (currentDestination?.route) {
        Screen.HomeJugador.route,
        Screen.MisPartidos.route,
        Screen.Estadisticas.route,
        Screen.PerfilJugador.route,
        Screen.HomeArbitro.route,
        "historial_arbitro",
        Screen.PerfilArbitro.route -> true
        else -> false
    }

    // Determinar qué tipo de bottom nav mostrar
    val isJugadorNav = when (currentDestination?.route) {
        Screen.HomeJugador.route,
        Screen.MisPartidos.route,
        Screen.Estadisticas.route,
        Screen.PerfilJugador.route -> true
        else -> false
    }

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                if (isJugadorNav) {
                    JugadorBottomNavigationBar(
                        navController = navController,
                        currentRoute = currentDestination?.route
                    )
                } else {
                    ArbitroBottomNavigationBar(
                        navController = navController,
                        currentRoute = currentDestination?.route
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Autenticación
            composable(Screen.Login.route) {
                LoginScreen(navController)
            }

            composable(Screen.QRScanner.route) {
                // TODO: QRScannerScreen(navController)
                // Placeholder
                LoginScreen(navController)
            }

            composable(Screen.RegistroJugador.route) {
                // TODO: RegistroJugadorScreen(navController)
                // Placeholder
                LoginScreen(navController)
            }

            // Jugador
            composable(Screen.HomeJugador.route) {
                HomeJugadorScreen(navController)
            }

            composable(Screen.MisPartidos.route) {
                // TODO: MisPartidosScreen(navController)
                // Placeholder - mostramos home por ahora
                HomeJugadorScreen(navController)
            }

            composable(Screen.Estadisticas.route) {
                // TODO: EstadisticasScreen(navController)
                // Placeholder
                HomeJugadorScreen(navController)
            }

            composable(Screen.PerfilJugador.route) {
                // TODO: PerfilJugadorScreen(navController)
                // Placeholder
                HomeJugadorScreen(navController)
            }

            // Árbitro
            composable(Screen.HomeArbitro.route) {
                // TODO: HomeArbitroScreen(navController)
                // Placeholder
                LoginScreen(navController)
            }

            composable(
                route = Screen.RegistroPartido.route,
                arguments = listOf(navArgument("partidoId") { type = NavType.IntType })
            ) { backStackEntry ->
                val partidoId = backStackEntry.arguments?.getInt("partidoId") ?: 0
                // TODO: RegistroPartidoScreen(navController, partidoId)
                // Placeholder
                LoginScreen(navController)
            }

            composable(Screen.PerfilArbitro.route) {
                // TODO: PerfilArbitroScreen(navController)
                // Placeholder
                LoginScreen(navController)
            }

            composable("historial_arbitro") {
                // TODO: HistorialArbitroScreen(navController)
                // Placeholder
                LoginScreen(navController)
            }
        }
    }
}

@Composable
fun JugadorBottomNavigationBar(
    navController: androidx.navigation.NavController,
    currentRoute: String?
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        getJugadorBottomNavItems().forEach { item ->
            NavigationBarItem(
                icon = {
                    Text(
                        text = item.icon,
                        fontSize = 24.sp
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination to avoid building a large stack
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when navigating back
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryPurple,
                    selectedTextColor = PrimaryPurple,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = PrimaryPurple.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Composable
fun ArbitroBottomNavigationBar(
    navController: androidx.navigation.NavController,
    currentRoute: String?
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        getArbitroBottomNavItems().forEach { item ->
            NavigationBarItem(
                icon = {
                    Text(
                        text = item.icon,
                        fontSize = 24.sp
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TextPrimary,
                    selectedTextColor = TextPrimary,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = TextPrimary.copy(alpha = 0.1f)
                )
            )
        }
    }
}
