package com.duoc.mobile_01_android.presentation.mascotas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.duoc.mobile_01_android.domain.model.Cliente
import com.duoc.mobile_01_android.domain.model.Mascota
import com.duoc.mobile_01_android.presentation.components.AppTopBar
import com.duoc.mobile_01_android.presentation.components.ErrorCard
import com.duoc.mobile_01_android.presentation.components.ErrorSnackbar
import com.duoc.mobile_01_android.presentation.components.LoadingIndicator
import com.duoc.mobile_01_android.presentation.components.SearchBar
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MascotasScreen(
    viewModel: MascotasViewModel,
    userRole: com.duoc.mobile_01_android.domain.model.Rol = com.duoc.mobile_01_android.domain.model.Rol.ADMIN,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var editingMascota by remember { mutableStateOf<Mascota?>(null) }

    var nombre by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var raza by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var selectedClienteId by remember { mutableStateOf<Int?>(null) }
    var expandedCliente by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val loadingMessage by viewModel.loadingMessage.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filtroEspecie by viewModel.filtroEspecie.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    fun resetForm() {
        nombre = ""
        especie = ""
        raza = ""
        edad = ""
        peso = ""
        selectedClienteId = null
        editingMascota = null
    }

    fun loadMascotaToEdit(mascota: Mascota) {
        nombre = mascota.nombre
        especie = mascota.especie
        raza = mascota.raza
        edad = mascota.edad.toString()
        peso = mascota.peso.toString()
        selectedClienteId = mascota.clienteId
        editingMascota = mascota
        showDialog = true
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Mascotas",
                showBackButton = true,
                userRole = userRole,
                onBackClick = onBack,
                onNavigate = onNavigate,
                onLogout = onLogout
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    resetForm()
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Mascota")
            }
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            when (val state = uiState) {
                is MascotasUiState.Loading -> {
                    // Mostrar loading
                }
                is MascotasUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Lista de Mascotas (${state.mascotas.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { viewModel.setSearchQuery(it) },
                            placeholder = "Buscar por nombre de mascota..."
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Filtros de especie
                        if (state.especiesDisponibles.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Chip "Todas"
                                item {
                                    FilterChip(
                                        selected = filtroEspecie == null || filtroEspecie == "Todas",
                                        onClick = { viewModel.setFiltroEspecie(null) },
                                        label = { Text("Todas") }
                                    )
                                }

                                // Chips para cada especie disponible
                                items(state.especiesDisponibles) { especie ->
                                    FilterChip(
                                        selected = filtroEspecie == especie,
                                        onClick = { viewModel.setFiltroEspecie(especie) },
                                        label = { Text(especie) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        if (state.mascotas.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Pets,
                                        contentDescription = null,
                                        modifier = Modifier.height(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No hay mascotas registradas",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Presiona + para agregar una",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.mascotas) { mascotaConCliente ->
                                    MascotaCard(
                                        mascota = mascotaConCliente.mascota,
                                        cliente = mascotaConCliente.cliente,
                                        onEdit = { loadMascotaToEdit(mascotaConCliente.mascota) },
                                        onDelete = { viewModel.eliminarMascota(mascotaConCliente.mascota.id) }
                                    )
                                }
                            }
                        }
                    }

                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showDialog = false
                                resetForm()
                            },
                            title = {
                                Text(if (editingMascota != null) "Editar Mascota" else "Nueva Mascota")
                            },
                            text = {
                                Column {
                                    OutlinedTextField(
                                        value = nombre,
                                        onValueChange = { nombre = it },
                                        label = { Text("Nombre") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = especie,
                                        onValueChange = { especie = it },
                                        label = { Text("Especie (Perro, Gato, etc.)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = raza,
                                        onValueChange = { raza = it },
                                        label = { Text("Raza (opcional)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row {
                                        OutlinedTextField(
                                            value = edad,
                                            onValueChange = { edad = it.filter { c -> c.isDigit() } },
                                            label = { Text("Edad") },
                                            modifier = Modifier.weight(1f),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        OutlinedTextField(
                                            value = peso,
                                            onValueChange = { peso = it },
                                            label = { Text("Peso (kg)") },
                                            modifier = Modifier.weight(1f),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                            singleLine = true
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    ExposedDropdownMenuBox(
                                        expanded = expandedCliente,
                                        onExpandedChange = { expandedCliente = !expandedCliente }
                                    ) {
                                        OutlinedTextField(
                                            value = state.clientesDisponibles.find { it.id == selectedClienteId }?.nombre ?: "",
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Dueno") },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCliente) },
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth()
                                        )

                                        ExposedDropdownMenu(
                                            expanded = expandedCliente,
                                            onDismissRequest = { expandedCliente = false }
                                        ) {
                                            state.clientesDisponibles.forEach { cliente ->
                                                DropdownMenuItem(
                                                    text = { Text(cliente.nombre) },
                                                    onClick = {
                                                        selectedClienteId = cliente.id
                                                        expandedCliente = false
                                                    }
                                                )
                                            }
                                            if (state.clientesDisponibles.isEmpty()) {
                                                DropdownMenuItem(
                                                    text = { Text("No hay clientes - Registra uno primero") },
                                                    onClick = { expandedCliente = false }
                                                )
                                            }
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        val mascota = Mascota(
                                            id = editingMascota?.id ?: 0,
                                            nombre = nombre,
                                            especie = especie,
                                            raza = raza,
                                            edad = edad.toIntOrNull() ?: 0,
                                            peso = peso.toDoubleOrNull() ?: 0.0,
                                            clienteId = selectedClienteId ?: 0
                                        )
                                        if (editingMascota != null) {
                                            viewModel.editarMascota(mascota)
                                        } else {
                                            viewModel.agregarMascota(mascota)
                                        }
                                        showDialog = false
                                        resetForm()
                                    },
                                    enabled = nombre.isNotBlank() && especie.isNotBlank()
                                ) {
                                    Text("Guardar")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showDialog = false
                                    resetForm()
                                }) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }
                }
                is MascotasUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        ErrorCard(
                            message = state.message,
                            onRetry = { /* Retry logic if needed */ }
                        )
                    }
                }
            }
        }

        LoadingIndicator(
            isLoading = isLoading,
            message = loadingMessage
        )

        // Mostrar snackbar de error cuando hay un mensaje de error
        errorMessage?.let { error ->
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.BottomCenter
            ) {
                ErrorSnackbar(
                    message = error,
                    onDismiss = { viewModel.clearError() }
                )
            }
        }
    }
}

@Composable
fun MascotaCard(
    mascota: Mascota,
    cliente: Cliente?,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mascota.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (mascota.raza.isNotBlank()) {
                        "${mascota.especie} | Raza: ${mascota.raza}"
                    } else {
                        mascota.especie
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${mascota.edad} anos | ${mascota.peso} kg",
                    style = MaterialTheme.typography.bodyMedium
                )
                cliente?.let {
                    Text(
                        text = "Dueno: ${it.nombre}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
