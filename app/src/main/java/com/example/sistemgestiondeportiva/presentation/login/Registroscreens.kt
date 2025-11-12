package com.example.sistemgestiondeportiva.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroCapitanScreen(
    token: String,
    onRegistroSuccess: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: LoginViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var nombreEquipo by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            com.example.sistemgestiondeportiva.presentation.components.NeonTopAppBar(
                title = { Text("Registro de Capitán") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Crea tu perfil de capitán",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Completa los datos para registrar tu equipo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                singleLine = true
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Datos del equipo",
                style = MaterialTheme.typography.titleMedium
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = nombreEquipo,
                onValueChange = { nombreEquipo = it },
                label = { Text("Nombre del equipo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (showError) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            com.example.sistemgestiondeportiva.presentation.components.NeonButton(
                onClick = {
                    when {
                        nombre.isBlank() || email.isBlank() || password.isBlank() || nombreEquipo.isBlank() -> {
                            showError = true
                            errorMessage = "Por favor completa todos los campos obligatorios"
                        }
                        password != confirmPassword -> {
                            showError = true
                            errorMessage = "Las contraseñas no coinciden"
                        }
                        password.length < 6 -> {
                            showError = true
                            errorMessage = "La contraseña debe tener al menos 6 caracteres"
                        }
                        else -> {
                            showError = false
                            viewModel.registrarCapitan(
                                token = token,
                                nombre = nombre,
                                email = email,
                                password = password,
                                telefono = telefono.ifBlank { null },
                                nombreEquipo = nombreEquipo,
                                onSuccess = onRegistroSuccess,
                                onError = { error ->
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
                    Text("Registrar")
                }
            }

            com.example.sistemgestiondeportiva.presentation.components.NeonButton(
                onClick = onBackClick,
                outline = true
            ) {
                Text("Cancelar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroJugadorScreen(
    token: String,
    equipoID: Int,
    onRegistroSuccess: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: LoginViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var numeroCamiseta by remember { mutableStateOf("") }
    var posicion by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var expandedPosicion by remember { mutableStateOf(false) }

    val posiciones = listOf("Portero", "Defensa", "Mediocampista", "Delantero")
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            com.example.sistemgestiondeportiva.presentation.components.NeonTopAppBar(
                title = { Text("Registro de Jugador") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Únete al equipo",
                style = MaterialTheme.typography.headlineSmall
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                singleLine = true
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Datos del jugador",
                style = MaterialTheme.typography.titleMedium
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = numeroCamiseta,
                onValueChange = { if (it.length <= 3 && it.all { char -> char.isDigit() }) numeroCamiseta = it },
                label = { Text("Número de camiseta") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = expandedPosicion,
                onExpandedChange = { expandedPosicion = !expandedPosicion }
            ) {
                OutlinedTextField(
                    value = posicion,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Posición") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPosicion) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedPosicion,
                    onDismissRequest = { expandedPosicion = false }
                ) {
                    posiciones.forEach { pos ->
                        DropdownMenuItem(
                            text = { Text(pos) },
                            onClick = {
                                posicion = pos
                                expandedPosicion = false
                            }
                        )
                    }
                }
            }

            if (showError) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            com.example.sistemgestiondeportiva.presentation.components.NeonButton(
                onClick = {
                    when {
                        nombre.isBlank() || email.isBlank() || password.isBlank() || numeroCamiseta.isBlank() || posicion.isBlank() -> {
                            showError = true
                            errorMessage = "Por favor completa todos los campos obligatorios"
                        }
                        password != confirmPassword -> {
                            showError = true
                            errorMessage = "Las contraseñas no coinciden"
                        }
                        password.length < 6 -> {
                            showError = true
                            errorMessage = "La contraseña debe tener al menos 6 caracteres"
                        }
                        else -> {
                            showError = false
                            viewModel.registrarJugador(
                                token = token,
                                equipoID = equipoID,
                                nombre = nombre,
                                email = email,
                                password = password,
                                telefono = telefono.ifBlank { null },
                                numeroCamiseta = numeroCamiseta.toInt(),
                                posicion = posicion,
                                onSuccess = onRegistroSuccess,
                                onError = { error ->
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
                    Text("Registrar")
                }
            }

            com.example.sistemgestiondeportiva.presentation.components.NeonButton(
                onClick = onBackClick,
                outline = true
            ) {
                Text("Cancelar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroArbitroScreen(
    token: String,
    onRegistroSuccess: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: LoginViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var licencia by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            com.example.sistemgestiondeportiva.presentation.components.NeonTopAppBar(
                title = { Text("Registro de Árbitro") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Registro de Árbitro",
                style = MaterialTheme.typography.headlineSmall
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = licencia,
                onValueChange = { licencia = it },
                label = { Text("Número de licencia") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                singleLine = true
            )

            com.example.sistemgestiondeportiva.presentation.components.NeonOutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true
            )

            if (showError) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            com.example.sistemgestiondeportiva.presentation.components.NeonButton(
                onClick = {
                    when {
                        nombre.isBlank() || email.isBlank() || password.isBlank() || licencia.isBlank() -> {
                            showError = true
                            errorMessage = "Por favor completa todos los campos obligatorios"
                        }
                        password != confirmPassword -> {
                            showError = true
                            errorMessage = "Las contraseñas no coinciden"
                        }
                        password.length < 6 -> {
                            showError = true
                            errorMessage = "La contraseña debe tener al menos 6 caracteres"
                        }
                        else -> {
                            showError = false
                            viewModel.registrarArbitro(
                                token = token,
                                nombre = nombre,
                                email = email,
                                password = password,
                                telefono = telefono.ifBlank { null },
                                licencia = licencia,
                                onSuccess = onRegistroSuccess,
                                onError = { error ->
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
                    Text("Registrar")
                }
            }

            com.example.sistemgestiondeportiva.presentation.components.NeonButton(
                onClick = onBackClick,
                outline = true
            ) {
                Text("Cancelar")
            }
        }
    }
}