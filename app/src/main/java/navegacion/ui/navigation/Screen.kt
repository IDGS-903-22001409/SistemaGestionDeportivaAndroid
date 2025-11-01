package navegacion.ui.navigation

// Definición de rutas de navegación
sealed class Screen(val route: String) {
    // Autenticación
    object Login : Screen("login")
    object QRScanner : Screen("qr_scanner")
    object RegistroJugador : Screen("registro_jugador")

    // Jugador
    object HomeJugador : Screen("home_jugador")
    object MisPartidos : Screen("mis_partidos")
    object Estadisticas : Screen("estadisticas")
    object PerfilJugador : Screen("perfil_jugador")

    // Árbitro
    object HomeArbitro : Screen("home_arbitro")
    object RegistroPartido : Screen("registro_partido/{partidoId}") {
        fun createRoute(partidoId: Int) = "registro_partido/$partidoId"
    }
    object PerfilArbitro : Screen("perfil_arbitro")
}

// Items de navegación inferior para Jugador
sealed class JugadorBottomNavItem(
    val route: String,
    val icon: String,
    val label: String
) {
    object Home : JugadorBottomNavItem(Screen.HomeJugador.route, "🏠", "Inicio")
    object Partidos : JugadorBottomNavItem(Screen.MisPartidos.route, "⚽", "Partidos")
    object Stats : JugadorBottomNavItem(Screen.Estadisticas.route, "📊", "Stats")
    object Perfil : JugadorBottomNavItem(Screen.PerfilJugador.route, "👤", "Perfil")
}

// Items de navegación inferior para Árbitro
sealed class ArbitroBottomNavItem(
    val route: String,
    val icon: String,
    val label: String
) {
    object Home : ArbitroBottomNavItem(Screen.HomeArbitro.route, "🏠", "Inicio")
    object Historial : ArbitroBottomNavItem("historial_arbitro", "📋", "Historial")
    object Perfil : ArbitroBottomNavItem(Screen.PerfilArbitro.route, "👤", "Perfil")
}

fun getJugadorBottomNavItems() = listOf(
    JugadorBottomNavItem.Home,
    JugadorBottomNavItem.Partidos,
    JugadorBottomNavItem.Stats,
    JugadorBottomNavItem.Perfil
)

fun getArbitroBottomNavItems() = listOf(
    ArbitroBottomNavItem.Home,
    ArbitroBottomNavItem.Historial,
    ArbitroBottomNavItem.Perfil
)