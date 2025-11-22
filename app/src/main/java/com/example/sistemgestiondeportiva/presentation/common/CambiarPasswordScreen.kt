package com.example.sistemgestiondeportiva.presentation.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CambiarPasswordScreen(
    onBackClick: () -> Unit,
    onPasswordChanged: suspend (passwordActual: String, passwordNuevo: String) -> Result<String>
) {
    var passwordActual by remember { mutableStateOf("") }
    var passwordNuevo by remember { mutableStateOf("") }
    var passwordConfirmar by remember { mutableStateOf("") }

    var passwordActualVisible by remember { mutableStateOf(false) }
    var passwordNuevoVisible by remember { mutableStateOf(false) }
    var passwordConfirmarVisible by remember { mutableStateOf(false) }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            com.example.sistemgestiondeportiva.presentation.components.NeonTopAppBar(
                title = { Text("Cambiar Contraseña") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Actualiza tu contraseña",
                style = MaterialTheme.typography.titleMedium
            )

            // Contraseña actual
            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = passwordActual,
                onValueChange = { passwordActual = it },
                label = { Text("Contraseña actual") },
                visualTransformation = if (passwordActualVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordActualVisible = !passwordActualVisible }) {
                        Icon(
                            if (passwordActualVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Divider()

            // Nueva contraseña
            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = passwordNuevo,
                onValueChange = { passwordNuevo = it },
                label = { Text("Nueva contraseña") },
                visualTransformation = if (passwordNuevoVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordNuevoVisible = !passwordNuevoVisible }) {
                        Icon(
                            if (passwordNuevoVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            // Confirmar contraseña
            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = passwordConfirmar,
                onValueChange = { passwordConfirmar = it },
                label = { Text("Confirmar nueva contraseña") },
                visualTransformation = if (passwordConfirmarVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordConfirmarVisible = !passwordConfirmarVisible }) {
                        Icon(
                            if (passwordConfirmarVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            if (showError) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        errorMessage,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            if (showSuccess) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        "✓ Contraseña actualizada correctamente",
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    onBackClick()
                }
            }

            com.example.sistemgestiondeportiva.presentation.components.NeonButton(
                onClick = {
                    when {
                        passwordActual.isBlank() || passwordNuevo.isBlank() || passwordConfirmar.isBlank() -> {
                            showError = true
                            errorMessage = "Todos los campos son obligatorios"
                        }
                        passwordNuevo.length < 6 -> {
                            showError = true
                            errorMessage = "La nueva contraseña debe tener al menos 6 caracteres"
                        }
                        passwordNuevo != passwordConfirmar -> {
                            showError = true
                            errorMessage = "Las contraseñas no coinciden"
                        }
                        else -> {
                            showError = false
                            isLoading = true

                            scope.launch {
                                val result = onPasswordChanged(passwordActual, passwordNuevo)
                                isLoading = false

                                result.fold(
                                    onSuccess = {
                                        showSuccess = true
                                    },
                                    onFailure = { error ->
                                        showError = true
                                        errorMessage = error.message ?: "Error al cambiar contraseña"
                                    }
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && !showSuccess
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Cambiar Contraseña")
                }
            }
        }
    }
}