package navegacion.ui.screens.jugador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import navegacion.data.model.EstatusPartido
import navegacion.ui.components.MatchCard
import navegacion.ui.navigation.Screen
import navegacion.ui.theme.GradientEnd
import navegacion.ui.theme.GradientStart
import navegacion.ui.theme.PrimaryPurple
import navegacion.ui.theme.Surface
import navegacion.ui.theme.TextPrimary
import viewmodel.JugadorViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeJugadorScreen(
    navController: NavController,
    viewModel: JugadorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarDatosJugador(1) // Usuario mock ID = 1
    }

    Scaffold { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryPurple)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Header con gradiente
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(GradientStart, GradientEnd)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                text = "Bienvenido",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = uiState.usuario?.nombreCompleto ?: "",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(15.dp))

                            // Stats rápidas
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                QuickStatCard(
                                    value = uiState.estadisticas?.partidosJugados?.toString() ?: "0",
                                    label = "PARTIDOS",
                                    modifier = Modifier.weight(1f)
                                )
                                QuickStatCard(
                                    value = uiState.estadisticas?.goles?.toString() ?: "0",
                                    label = "GOLES",
                                    modifier = Modifier.weight(1f)
                                )
                                QuickStatCard(
                                    value = uiState.estadisticas?.asistencias?.toString() ?: "0",
                                    label = "ASISTENCIAS",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Acciones rápidas
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionButton(
                            icon = "⚽",
                            label = "Mis\nPartidos",
                            onClick = { navController.navigate(Screen.MisPartidos.route) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            icon = "📊",
                            label = "Estadísticas",
                            onClick = { navController.navigate(Screen.Estadisticas.route) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            icon = "🏆",
                            label = "Tabla",
                            onClick = { /* TODO */ },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            icon = "👥",
                            label = "Mi\nEquipo",
                            onClick = { /* TODO */ },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Últimos Resultados
                item {
                    Spacer(modifier = Modifier.height(25.dp))
                    SectionHeader(
                        title = "Últimos Resultados",
                        onSeeAllClick = { navController.navigate(Screen.MisPartidos.route) },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                }

                items(uiState.ultimosResultados) { partido ->
                    MatchCard(
                        partido = partido,
                        resultadoJugador = uiState.resultadosJugador[partido.id],
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 7.dp)
                    )
                }

                // Próximos Partidos
                item {
                    Spacer(modifier = Modifier.height(25.dp))
                    SectionHeader(
                        title = "Próximos Partidos",
                        onSeeAllClick = { navController.navigate(Screen.MisPartidos.route) },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                }

                items(uiState.proximosPartidos.filter {
                    it.estatus == EstatusPartido.PROGRAMADO || it.estatus == EstatusPartido.EN_VIVO
                }) { partido ->
                    MatchCard(
                        partido = partido,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 7.dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun QuickStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        TextButton(onClick = onSeeAllClick) {
            Text(
                text = "Ver todos →",
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryPurple,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}