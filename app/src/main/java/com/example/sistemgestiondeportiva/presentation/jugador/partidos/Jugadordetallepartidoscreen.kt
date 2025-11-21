package com.example.sistemgestiondeportiva.presentation.jugador.partidos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sistemgestiondeportiva.data.models.EventoPartido
import com.example.sistemgestiondeportiva.data.models.Partido
import com.example.sistemgestiondeportiva.presentation.arbitro.ArbitroViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JugadorDetallePartidoScreen(
    partidoID: Int,
    viewModel: ArbitroViewModel, // Reutilizamos este ViewModel porque tiene la lógica de partidos
    onBackClick: () -> Unit
) {
    val partido by viewModel.partidoActual.collectAsState()
    val eventos by viewModel.eventosPartido.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(partidoID) {
        viewModel.cargarPartido(partidoID)
    }

    Scaffold(
        topBar = {
            com.example.sistemgestiondeportiva.presentation.components.NeonTopAppBar(
                title = { Text("Detalles del Partido") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading && partido == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            partido?.let { p ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Información del partido
                    item {
                        InfoPartidoCard(partido = p)
                    }

                    // Marcador
                    item {
                        MarcadorCard(partido = p)
                    }

                    // Estado del partido
                    item {
                        EstadoPartidoCard(partido = p)
                    }

                    // Eventos
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Eventos del Partido",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${eventos.size} eventos",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (eventos.isEmpty()) {
                        item {
                            com.example.sistemgestiondeportiva.presentation.components.GlassCard(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No hay eventos registrados")
                                }
                            }
                        }
                    } else {
                        items(eventos.sortedByDescending { it.minuto }) { evento ->
                            EventoPartidoCard(evento = evento)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoPartidoCard(partido: Partido) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val fecha = try {
        dateFormat.format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(partido.fechaPartido))
    } catch (e: Exception) {
        partido.fechaPartido
    }

    com.example.sistemgestiondeportiva.presentation.components.GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    partido.equipoLocal?.nombreEquipo ?: "Local",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "VS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text(
                    partido.equipoVisitante?.nombreEquipo ?: "Visitante",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(fecha, style = MaterialTheme.typography.bodyMedium)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(partido.lugarPartido, style = MaterialTheme.typography.bodyMedium)
            }

            if (partido.arbitro != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Árbitro: ${partido.arbitro.usuario?.nombre ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun MarcadorCard(partido: Partido) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    partido.equipoLocal?.nombreEquipo ?: "Local",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    (partido.golesLocal ?: 0).toString(),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                "-",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    partido.equipoVisitante?.nombreEquipo ?: "Visitante",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    (partido.golesVisitante ?: 0).toString(),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EstadoPartidoCard(partido: Partido) {
    com.example.sistemgestiondeportiva.presentation.components.GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Estado del Partido",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Surface(
                shape = MaterialTheme.shapes.small,
                color = when (partido.estado) {
                    "Programado" -> MaterialTheme.colorScheme.secondaryContainer
                    "En Curso" -> MaterialTheme.colorScheme.tertiaryContainer
                    "Finalizado" -> MaterialTheme.colorScheme.surfaceVariant
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Text(
                    partido.estado,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EventoPartidoCard(evento: EventoPartido) {
    com.example.sistemgestiondeportiva.presentation.components.GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    "${evento.minuto}'",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                when (evento.tipoEvento) {
                    "Gol" -> Icons.Default.Star
                    "Asistencia" -> Icons.Default.AccountCircle
                    "TarjetaAmarilla" -> Icons.Default.Info
                    "TarjetaRoja" -> Icons.Default.Close
                    else -> Icons.Default.Info
                },
                contentDescription = null,
                tint = when (evento.tipoEvento) {
                    "Gol" -> MaterialTheme.colorScheme.primary
                    "Asistencia" -> MaterialTheme.colorScheme.tertiary
                    "TarjetaAmarilla" -> MaterialTheme.colorScheme.secondary
                    "TarjetaRoja" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    evento.tipoEvento,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    evento.jugador?.usuario?.nombre ?: "Jugador",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!evento.descripcion.isNullOrBlank()) {
                    Text(
                        evento.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}