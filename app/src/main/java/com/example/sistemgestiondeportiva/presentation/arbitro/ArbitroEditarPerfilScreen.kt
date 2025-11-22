package com.example.sistemgestiondeportiva.presentation.arbitro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArbitroEditarPerfilScreen(
    viewModel: ArbitroViewModel,
    onBackClick: () -> Unit,
    onSaved: () -> Unit
) {
    val arbitro by viewModel.arbitro.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(arbitro) {
        arbitro?.usuario?.let { u ->
            nombre = u.nombre
            email = u.email
            telefono = u.telefono ?: ""
        }
    }

    Scaffold(
        topBar = {
            com.example.sistemgestiondeportiva.presentation.components.NeonTopAppBar(
                title = { Text("Editar Perfil") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Información Personal",
                style = MaterialTheme.typography.titleMedium
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                enabled = !isLoading
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                enabled = !isLoading
            )

            if (showError) {
                com.example.sistemgestiondeportiva.presentation.components.GlassCard {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            com.example.sistemgestiondeportiva.presentation.components.NeonButton(
                onClick = {
                    when {
                        nombre.isBlank() -> {
                            showError = true
                            errorMessage = "El nombre es obligatorio"
                        }
                        email.isBlank() -> {
                            showError = true
                            errorMessage = "El email es obligatorio"
                        }
                        else -> {
                            showError = false
                            isLoading = true
                            viewModel.actualizarPerfil(
                                nombre = nombre,
                                email = email,
                                telefono = telefono.ifBlank { null },
                                onSuccess = {
                                    isLoading = false
                                    onSaved()
                                },
                                onError = { error ->
                                    isLoading = false
                                    showError = true
                                    errorMessage = error
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar Cambios")
                }
            }
        }
    }
}