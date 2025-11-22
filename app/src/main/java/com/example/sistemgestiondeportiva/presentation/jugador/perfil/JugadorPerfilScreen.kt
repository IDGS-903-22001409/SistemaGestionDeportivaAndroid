package com.example.sistemgestiondeportiva.presentation.jugador.perfil

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
import com.example.sistemgestiondeportiva.presentation.jugador.home.JugadorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JugadorPerfilScreen(
    viewModel: JugadorViewModel,
    onBackClick: () -> Unit,
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit
) {
    val jugador by viewModel.jugador.collectAsState()
    val equipo by viewModel.equipo.collectAsState()
    val estadisticas by viewModel.estadisticas.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cargarDatos()
    }

    Scaffold(
        topBar = {
            com.example.sistemgestiondeportiva.presentation.components.NeonTopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onEditProfile) {
                        Icon(Icons.Default.Edit, "Editar perfil")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta principal del jugador
            jugador?.let { j ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(100.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "#${j.numeroCamiseta}",
                                    style = MaterialTheme.typography.displayLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            j.usuario?.nombre ?: "Jugador",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            j.posicion,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )

                        if (j.esCapitan) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.tertiary
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onTertiary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "CAPITÁN",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Información personal completa
            jugador?.usuario?.let { u ->
                com.example.sistemgestiondeportiva.presentation.components.GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Información Personal",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Nombre
                        InfoRow(
                            icon = Icons.Default.Person,
                            label = "Nombre",
                            value = u.nombre
                        )

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        // Email
                        InfoRow(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = u.email
                        )

                        if (!u.telefono.isNullOrBlank()) {
                            Divider(modifier = Modifier.padding(vertical = 12.dp))

                            // Teléfono
                            InfoRow(
                                icon = Icons.Default.Phone,
                                label = "Teléfono",
                                value = u.telefono
                            )
                        }
                    }
                }
            }

            // Información del jugador
            jugador?.let { j ->
                com.example.sistemgestiondeportiva.presentation.components.GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Información Deportiva",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Número de camiseta
                        InfoRow(
                            icon = Icons.Default.Create,
                            label = "Número de Camiseta",
                            value = "#${j.numeroCamiseta}"
                        )

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        // Posición
                        InfoRow(
                            icon = Icons.Default.Star,
                            label = "Posición",
                            value = j.posicion
                        )
                    }
                }
            }

            // Información del equipo
            equipo?.let { e ->
                com.example.sistemgestiondeportiva.presentation.components.GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Mi Equipo",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        InfoRow(
                            icon = Icons.Default.Face,
                            label = "Equipo",
                            value = e.nombreEquipo
                        )
                    }
                }
            }

            // Estadísticas
            estadisticas?.let { stats ->
                com.example.sistemgestiondeportiva.presentation.components.GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Estadísticas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatColumn("Partidos", stats.partidosJugados.toString())
                            StatColumn("Goles", stats.goles.toString())
                            StatColumn("Asistencias", stats.asistencias.toString())
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatColumn("T. Amarillas", stats.tarjetasAmarillas.toString())
                            StatColumn("T. Rojas", stats.tarjetasRojas.toString())
                            StatColumn("Promedio", String.format("%.2f", stats.promedioGoles))
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = onChangePassword,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Lock, "Cambiar contraseña")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cambiar Contraseña")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.ExitToApp, "Cerrar sesión")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cerrar Sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}