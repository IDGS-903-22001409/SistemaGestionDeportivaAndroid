package com.example.sistemgestiondeportiva.presentation.arbitro

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
import androidx.compose.ui.window.Dialog
import com.example.sistemgestiondeportiva.data.models.EventoPartido
import com.example.sistemgestiondeportiva.data.models.Jugador
import com.example.sistemgestiondeportiva.data.models.Partido

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionPartidoScreen(
    partidoID: Int,
    viewModel: ArbitroViewModel,
    onBackClick: () -> Unit
) {
    val partido by viewModel.partidoActual.collectAsState()
    val eventos by viewModel.eventosPartido.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showRegistrarEventoDialog by remember { mutableStateOf(false) }
    var showIniciarDialog by remember { mutableStateOf(false) }
    var showFinalizarDialog by remember { mutableStateOf(false) }

    LaunchedEffect(partidoID) {
        viewModel.cargarPartido(partidoID)
    }

    Scaffold(
        topBar = {
            com.example.sistemgestiondeportiva.presentation.components.NeonTopAppBar(
                title = { Text("Gestión de Partido") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            if (partido?.estado == "En Curso") {
                ExtendedFloatingActionButton(
                    onClick = { showRegistrarEventoDialog = true },
                    icon = { Icon(Icons.Default.Add, "Registrar evento") },
                    text = { Text("Registrar Evento") }
                )
            }
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
                        PartidoInfoCard(partido = p)
                    }

                    // Marcador
                    item {
                        MarcadorCard(partido = p)
                    }

                    // Controles del partido
                    item {
                        ControlesPartidoCard(
                            partido = p,
                            onIniciar = { showIniciarDialog = true },
                            onFinalizar = { showFinalizarDialog = true }
                        )
                    }

                    // Lista de eventos
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
                            Card(
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
                            EventoCard(
                                evento = evento,
                                onEliminar = {
                                    evento.eventoID?.let { id ->
                                        viewModel.eliminarEvento(
                                            eventoID = id,
                                            partidoID = partidoID,
                                            onSuccess = {},
                                            onError = {}
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Diálogos
        if (showRegistrarEventoDialog && partido != null) {
            RegistrarEventoDialog(
                partido = partido!!,
                onDismiss = { showRegistrarEventoDialog = false },
                onConfirm = { jugadorID, tipoEvento, minuto, descripcion ->
                    viewModel.registrarEvento(
                        partidoID = partidoID,
                        jugadorID = jugadorID,
                        tipoEvento = tipoEvento,
                        minuto = minuto,
                        descripcion = descripcion,
                        onSuccess = {
                            showRegistrarEventoDialog = false
                        },
                        onError = {}
                    )
                }
            )
        }

        if (showIniciarDialog && partido != null) {
            AlertDialog(
                onDismissRequest = { showIniciarDialog = false },
                title = { Text("Iniciar Partido") },
                text = { Text("¿Deseas iniciar este partido?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.iniciarPartido(
                                partidoID = partidoID,
                                onSuccess = { showIniciarDialog = false },
                                onError = {}
                            )
                        }
                    ) {
                        Text("Iniciar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showIniciarDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (showFinalizarDialog && partido != null) {
            AlertDialog(
                onDismissRequest = { showFinalizarDialog = false },
                title = { Text("Finalizar Partido") },
                text = { Text("¿Deseas finalizar este partido? Esta acción no se puede deshacer.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.finalizarPartido(
                                partidoID = partidoID,
                                onSuccess = {
                                    showFinalizarDialog = false
                                    onBackClick()
                                },
                                onError = {}
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Finalizar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showFinalizarDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun PartidoInfoCard(partido: Partido) {
    com.example.sistemgestiondeportiva.presentation.components.GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    partido.equipoLocal?.nombreEquipo ?: "Local",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "VS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    partido.equipoVisitante?.nombreEquipo ?: "Visitante",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(partido.lugarPartido, style = MaterialTheme.typography.bodyMedium)
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
                    style = MaterialTheme.typography.titleMedium
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
                    style = MaterialTheme.typography.titleMedium
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
fun ControlesPartidoCard(
    partido: Partido,
    onIniciar: () -> Unit,
    onFinalizar: () -> Unit
) {
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
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (partido.estado) {
                "Programado" -> {
                    Button(
                        onClick = onIniciar,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PlayArrow, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Iniciar Partido")
                    }
                }
                "En Curso" -> {
                    Button(
                        onClick = onFinalizar,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Close, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Finalizar Partido")
                    }
                }
                "Finalizado" -> {
                    Text(
                        "El partido ha finalizado",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun EventoCard(
    evento: EventoPartido,
    onEliminar: () -> Unit
) {
    com.example.sistemgestiondeportiva.presentation.components.GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
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
                        "Gol" -> Icons.Default.Add              // Instead of SportsSoccer
                        "Asistencia" -> Icons.Default.AccountCircle  // Instead of AssistWalker
                        "TarjetaAmarilla" -> Icons.Default.Info  // Instead of Style (or use Bookmark)
                        "TarjetaRoja" -> Icons.Default.Close      // Instead of Block
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

                Column {
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
                }
            }

            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarEventoDialog(
    partido: Partido,
    onDismiss: () -> Unit,
    onConfirm: (jugadorID: Int, tipoEvento: String, minuto: Int, descripcion: String?) -> Unit
) {
    var equipoSeleccionado by remember { mutableStateOf("Local") }
    var jugadorSeleccionado by remember { mutableStateOf<Jugador?>(null) }
    var tipoEvento by remember { mutableStateOf("Gol") }
    var minuto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    val jugadores = remember(equipoSeleccionado) {
        if (equipoSeleccionado == "Local") {
            partido.equipoLocal?.let { emptyList<Jugador>() } ?: emptyList()
        } else {
            partido.equipoVisitante?.let { emptyList<Jugador>() } ?: emptyList()
        }
    }

    val tiposEvento = listOf("Gol", "Asistencia", "TarjetaAmarilla", "TarjetaRoja")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Registrar Evento",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Selector de equipo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = equipoSeleccionado == "Local",
                        onClick = { equipoSeleccionado = "Local" },
                        label = { Text(partido.equipoLocal?.nombreEquipo ?: "Local") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = equipoSeleccionado == "Visitante",
                        onClick = { equipoSeleccionado = "Visitante" },
                        label = { Text(partido.equipoVisitante?.nombreEquipo ?: "Visitante") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Tipo de evento
                var expandedTipo by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedTipo,
                    onExpandedChange = { expandedTipo = !expandedTipo }
                ) {
                    OutlinedTextField(
                        value = tipoEvento,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de evento") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTipo,
                        onDismissRequest = { expandedTipo = false }
                    ) {
                        tiposEvento.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    tipoEvento = tipo
                                    expandedTipo = false
                                }
                            )
                        }
                    }
                }

                // Minuto
                com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                    value = minuto,
                    onValueChange = { if (it.all { char -> char.isDigit() }) minuto = it },
                    label = { Text("Minuto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Descripción opcional
                com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    com.example.sistemgestiondeportiva.presentation.components.NeonButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        outline = true
                    ) {
                        Text("Cancelar")
                    }

                    com.example.sistemgestiondeportiva.presentation.components.NeonButton(
                        onClick = {
                            // Por ahora, usar un jugadorID ficticio (1)
                            // En producción, deberías tener un selector de jugadores
                            if (minuto.isNotBlank()) {
                                onConfirm(
                                    1, // jugadorID temporal
                                    tipoEvento,
                                    minuto.toInt(),
                                    descripcion.ifBlank { null }
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = minuto.isNotBlank()
                    ) {
                        Text("Registrar")
                    }
                }
            }
        }
    }
}