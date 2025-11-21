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
import com.example.sistemgestiondeportiva.data.models.Partido
import com.example.sistemgestiondeportiva.presentation.jugador.home.JugadorViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JugadorPartidosScreen(
    viewModel: JugadorViewModel,
    onNavigateToPartido: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val todosPartidos by viewModel.todosPartidos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var filtroSeleccionado by remember { mutableStateOf("Todos") }

    LaunchedEffect(Unit) {
        viewModel.cargarTodosPartidos()
    }

    val partidosFiltrados = remember(todosPartidos, filtroSeleccionado) {
        when (filtroSeleccionado) {
            "Programados" -> todosPartidos.filter { it.estado == "Programado" }
            "En Curso" -> todosPartidos.filter { it.estado == "En Curso" }
            "Finalizados" -> todosPartidos.filter { it.estado == "Finalizado" }
            else -> todosPartidos
        }
    }

    Scaffold(
        topBar = {
            com.example.sistemgestiondeportiva.presentation.components.NeonTopAppBar(
                title = { Text("Mis Partidos") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filtros
            ScrollableTabRow(
                selectedTabIndex = when (filtroSeleccionado) {
                    "Todos" -> 0
                    "Programados" -> 1
                    "En Curso" -> 2
                    "Finalizados" -> 3
                    else -> 0
                },
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = filtroSeleccionado == "Todos",
                    onClick = { filtroSeleccionado = "Todos" },
                    text = { Text("Todos (${todosPartidos.size})") }
                )
                Tab(
                    selected = filtroSeleccionado == "Programados",
                    onClick = { filtroSeleccionado = "Programados" },
                    text = { Text("Programados") }
                )
                Tab(
                    selected = filtroSeleccionado == "En Curso",
                    onClick = { filtroSeleccionado = "En Curso" },
                    text = { Text("En Curso") }
                )
                Tab(
                    selected = filtroSeleccionado == "Finalizados",
                    onClick = { filtroSeleccionado = "Finalizados" },
                    text = { Text("Finalizados") }
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (partidosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "No hay partidos ${filtroSeleccionado.lowercase()}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(partidosFiltrados) { partido ->
                        PartidoDetailCard(
                            partido = partido,
                            onClick = { onNavigateToPartido(partido.partidoID) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartidoDetailCard(
    partido: Partido,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val fecha = try {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(partido.fechaPartido)
        dateFormat.format(date!!)
    } catch (e: Exception) {
        partido.fechaPartido
    }

    val hora = try {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(partido.fechaPartido)
        timeFormat.format(date!!)
    } catch (e: Exception) {
        ""
    }

    com.example.sistemgestiondeportiva.presentation.components.GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = when (partido.estado) {
                        "Programado" -> MaterialTheme.colorScheme.primaryContainer
                        "En Curso" -> MaterialTheme.colorScheme.tertiaryContainer
                        "Finalizado" -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        partido.estado,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (partido.estado != "Finalizado") {
                    IconButton(onClick = onClick) {
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = "Ver detalles"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Equipos y marcador
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Equipo Local
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        partido.equipoLocal?.nombreEquipo ?: "Equipo Local",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )
                }

                // Marcador o VS
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    if (partido.golesLocal != null && partido.golesVisitante != null) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                partido.golesLocal.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "-",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                partido.golesVisitante.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        Text(
                            "VS",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Equipo Visitante
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        partido.equipoVisitante?.nombreEquipo ?: "Equipo Visitante",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            // Información adicional
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        fecha,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        hora,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    partido.lugarPartido,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}