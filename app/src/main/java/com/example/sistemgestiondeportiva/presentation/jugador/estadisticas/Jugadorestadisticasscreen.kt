package com.example.sistemgestiondeportiva.presentation.jugador.estadisticas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sistemgestiondeportiva.data.models.EstadisticasJugador
import com.example.sistemgestiondeportiva.presentation.jugador.home.JugadorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JugadorEstadisticasScreen(
    viewModel: JugadorViewModel,
    onBackClick: () -> Unit
) {
    val jugador by viewModel.jugador.collectAsState()
    val estadisticas by viewModel.estadisticas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarDatos()
    }

    Scaffold(
        topBar = {
            com.example.sistemgestiondeportiva.presentation.components.NeonTopAppBar(
                title = { Text("Estadísticas") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta de información del jugador
                jugador?.let { j ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    j.usuario?.nombre ?: "Jugador",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${j.posicion} - #${j.numeroCamiseta}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                estadisticas?.let { stats ->
                    // Resumen General
                    Text(
                        "Resumen General",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    com.example.sistemgestiondeportiva.presentation.components.GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                EstadisticaDetalladaItem(
                                    icon = Icons.Default.Info,
                                    label = "Partidos",
                                    value = stats.partidosJugados.toString(),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                EstadisticaDetalladaItem(
                                    icon = Icons.Default.Add,
                                    label = "Goles",
                                    value = stats.goles.toString(),
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                EstadisticaDetalladaItem(
                                    icon = Icons.Default.AccountCircle,
                                    label = "Asistencias",
                                    value = stats.asistencias.toString(),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }

                    // Promedios
                    Text(
                        "Promedios",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    com.example.sistemgestiondeportiva.presentation.components.GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PromedioItem(
                                label = "Goles por partido",
                                value = String.format("%.2f", stats.promedioGoles),
                                progress = (stats.promedioGoles.coerceIn(0.0, 3.0) / 3.0).toFloat()
                            )

                            val promedioAsistencias = if (stats.partidosJugados > 0) {
                                stats.asistencias.toDouble() / stats.partidosJugados
                            } else 0.0

                            PromedioItem(
                                label = "Asistencias por partido",
                                value = String.format("%.2f", promedioAsistencias),
                                progress = (promedioAsistencias.coerceIn(0.0, 2.0) / 2.0).toFloat()
                            )
                        }
                    }

                    // Disciplina
                    Text(
                        "Disciplina",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    com.example.sistemgestiondeportiva.presentation.components.GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                DisciplinaItem(
                                    label = "T. Amarillas",
                                    value = stats.tarjetasAmarillas.toString(),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                DisciplinaItem(
                                    label = "T. Rojas",
                                    value = stats.tarjetasRojas.toString(),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            Divider()

                            val totalTarjetas = stats.tarjetasAmarillas + stats.tarjetasRojas
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Total de tarjetas",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    totalTarjetas.toString(),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Efectividad
                    Text(
                        "Análisis de Rendimiento",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    com.example.sistemgestiondeportiva.presentation.components.GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val participacionesGol = stats.goles + stats.asistencias

                            RendimientoItem(
                                icon = Icons.Default.Star,
                                label = "Participaciones en gol",
                                value = participacionesGol.toString(),
                                description = "Goles + Asistencias"
                            )

                            Divider()

                            val promedioParticipaciones = if (stats.partidosJugados > 0) {
                                participacionesGol.toDouble() / stats.partidosJugados
                            } else 0.0

                            RendimientoItem(
                                icon = Icons.Default.Info,
                                label = "Promedio de participación",
                                value = String.format("%.2f", promedioParticipaciones),
                                description = "Por partido"
                            )
                        }
                    }
                }

                if (estadisticas == null && !isLoading) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No hay estadísticas disponibles",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EstadisticaDetalladaItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = color.copy(alpha = 0.2f),
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = color
                )
            }
        }
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PromedioItem(
    label: String,
    value: String,
    progress: Float
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun DisciplinaItem(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = color.copy(alpha = 0.2f),
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun RendimientoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}