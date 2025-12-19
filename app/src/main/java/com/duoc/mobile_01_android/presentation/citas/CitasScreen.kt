package com.duoc.mobile_01_android.presentation.citas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.unit.dp
import com.duoc.mobile_01_android.domain.model.Cita
import com.duoc.mobile_01_android.domain.model.Cliente
import com.duoc.mobile_01_android.domain.model.EstadoCita
import com.duoc.mobile_01_android.domain.model.Mascota
import com.duoc.mobile_01_android.presentation.components.AppTopBar
import com.duoc.mobile_01_android.presentation.components.ErrorCard
import com.duoc.mobile_01_android.presentation.components.ErrorSnackbar
import com.duoc.mobile_01_android.presentation.components.LoadingIndicator
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitasScreen(
    viewModel: CitasViewModel,
    userRole: com.duoc.mobile_01_android.domain.model.Rol = com.duoc.mobile_01_android.domain.model.Rol.ADMIN,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var citaEnEdicion by remember { mutableStateOf<Cita?>(null) }

    // Campos del formulario
    var selectedCliente by remember { mutableStateOf<Cliente?>(null) }
    var selectedMascota by remember { mutableStateOf<Mascota?>(null) }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }

    var expandedCliente by remember { mutableStateOf(false) }
    var expandedMascota by remember { mutableStateOf(false) }

    // DatePicker state
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    // TimePicker state
    val timePickerState = rememberTimePickerState()
    var showTimePicker by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val loadingMessage by viewModel.loadingMessage.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val estadoFiltro by viewModel.estadoFiltro.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    fun resetForm() {
        selectedCliente = null
        selectedMascota = null
        fecha = ""
        hora = ""
        motivo = ""
        citaEnEdicion = null
    }

    fun abrirDialogoEdicion(cita: Cita, clientes: List<Cliente>, mascotas: List<Mascota>) {
        citaEnEdicion = cita
        selectedCliente = clientes.find { it.id == cita.clienteId }
        selectedMascota = mascotas.find { it.id == cita.mascotaId }
        fecha = cita.fecha
        hora = cita.hora
        motivo = cita.motivo
        showDialog = true
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Citas",
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
                Icon(Icons.Default.Add, contentDescription = "Nueva Cita")
            }
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            when (val state = uiState) {
                is CitasUiState.Loading -> {}
                is CitasUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Agenda de Citas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Filtros por estado
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = estadoFiltro == null,
                                onClick = { viewModel.setFiltroEstado(null) },
                                label = { Text("Todas") }
                            )
                            FilterChip(
                                selected = estadoFiltro == EstadoCita.PENDIENTE,
                                onClick = { viewModel.setFiltroEstado(EstadoCita.PENDIENTE) },
                                label = { Text("Pendientes") }
                            )
                            FilterChip(
                                selected = estadoFiltro == EstadoCita.COMPLETADA,
                                onClick = { viewModel.setFiltroEstado(EstadoCita.COMPLETADA) },
                                label = { Text("Completadas") }
                            )
                            FilterChip(
                                selected = estadoFiltro == EstadoCita.CANCELADA,
                                onClick = { viewModel.setFiltroEstado(EstadoCita.CANCELADA) },
                                label = { Text("Canceladas") }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Citas (${state.citas.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (state.citas.isEmpty()) {
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
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        modifier = Modifier.height(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No hay citas registradas",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Presiona + para crear una",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.citas) { cita ->
                                    CitaCard(
                                        cita = cita,
                                        viewModel = viewModel,
                                        clientes = state.clientesDisponibles,
                                        mascotas = state.mascotasDisponibles,
                                        onEdit = { abrirDialogoEdicion(cita, state.clientesDisponibles, state.mascotasDisponibles) },
                                        onDelete = { viewModel.eliminarCita(cita.id) },
                                        onCompletar = { viewModel.cambiarEstado(cita.id, EstadoCita.COMPLETADA) },
                                        onCancelar = { viewModel.cambiarEstado(cita.id, EstadoCita.CANCELADA) }
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
                            title = { Text(if (citaEnEdicion == null) "Nueva Cita" else "Editar Cita") },
                            text = {
                                Column(
                                    modifier = Modifier.verticalScroll(rememberScrollState())
                                ) {
                                    // Dropdown Cliente
                                    ExposedDropdownMenuBox(
                                        expanded = expandedCliente,
                                        onExpandedChange = { expandedCliente = !expandedCliente }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedCliente?.nombre ?: "",
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Cliente") },
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
                                                        selectedCliente = cliente
                                                        selectedMascota = null
                                                        expandedCliente = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Dropdown Mascota (filtrado por cliente)
                                    val mascotasFiltradas = selectedCliente?.let { cliente ->
                                        state.mascotasDisponibles.filter { it.clienteId == cliente.id }
                                    } ?: emptyList()

                                    ExposedDropdownMenuBox(
                                        expanded = expandedMascota,
                                        onExpandedChange = { expandedMascota = !expandedMascota }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedMascota?.nombre ?: "",
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Mascota") },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMascota) },
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth(),
                                            enabled = selectedCliente != null
                                        )
                                        ExposedDropdownMenu(
                                            expanded = expandedMascota,
                                            onDismissRequest = { expandedMascota = false }
                                        ) {
                                            mascotasFiltradas.forEach { mascota ->
                                                DropdownMenuItem(
                                                    text = { Text("${mascota.nombre} (${mascota.especie})") },
                                                    onClick = {
                                                        selectedMascota = mascota
                                                        expandedMascota = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Fecha
                                    OutlinedTextField(
                                        value = fecha,
                                        onValueChange = { },
                                        readOnly = true,
                                        label = { Text("Fecha (dd/MM/yyyy)") },
                                        placeholder = { Text("01/01/2025") },
                                        trailingIcon = {
                                            IconButton(onClick = { showDatePicker = true }) {
                                                Icon(
                                                    imageVector = Icons.Default.CalendarMonth,
                                                    contentDescription = "Seleccionar fecha"
                                                )
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { showDatePicker = true }
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Hora
                                    OutlinedTextField(
                                        value = hora,
                                        onValueChange = { },
                                        readOnly = true,
                                        label = { Text("Hora (HH:mm)") },
                                        placeholder = { Text("09:00") },
                                        trailingIcon = {
                                            IconButton(onClick = { showTimePicker = true }) {
                                                Icon(
                                                    imageVector = Icons.Default.AccessTime,
                                                    contentDescription = "Seleccionar hora"
                                                )
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { showTimePicker = true }
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Motivo
                                    OutlinedTextField(
                                        value = motivo,
                                        onValueChange = { motivo = it },
                                        label = { Text("Motivo de la cita") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 2,
                                        maxLines = 4
                                    )
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        selectedCliente?.let { cliente ->
                                            selectedMascota?.let { mascota ->
                                                if (citaEnEdicion == null) {
                                                    viewModel.agregarCita(
                                                        clienteId = cliente.id,
                                                        mascotaId = mascota.id,
                                                        fecha = fecha,
                                                        hora = hora,
                                                        motivo = motivo
                                                    )
                                                } else {
                                                    viewModel.editarCita(
                                                        citaEnEdicion!!.copy(
                                                            clienteId = cliente.id,
                                                            mascotaId = mascota.id,
                                                            fecha = fecha,
                                                            hora = hora,
                                                            motivo = motivo
                                                        )
                                                    )
                                                }
                                                showDialog = false
                                                resetForm()
                                            }
                                        }
                                    },
                                    enabled = selectedCliente != null && selectedMascota != null &&
                                              fecha.isNotBlank() && hora.isNotBlank()
                                ) {
                                    Text(if (citaEnEdicion == null) "Crear" else "Guardar")
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

                    // DatePickerDialog
                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                        fecha = formatter.format(Date(millis))
                                    }
                                    showDatePicker = false
                                }) {
                                    Text("Aceptar")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text("Cancelar")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    // TimePickerDialog
                    if (showTimePicker) {
                        AlertDialog(
                            onDismissRequest = { showTimePicker = false },
                            title = { Text("Seleccionar hora") },
                            text = {
                                TimePicker(state = timePickerState)
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    hora = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                                    showTimePicker = false
                                }) {
                                    Text("Aceptar")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showTimePicker = false }) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }
                }
                is CitasUiState.Error -> {
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
fun CitaCard(
    cita: Cita,
    viewModel: CitasViewModel,
    clientes: List<Cliente>,
    mascotas: List<Mascota>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCompletar: () -> Unit,
    onCancelar: () -> Unit
) {
    val mascota = viewModel.getMascota(cita.mascotaId, mascotas)
    val clienteNombre = viewModel.getClienteName(cita.clienteId, clientes)

    val estadoColor = when (cita.estado) {
        EstadoCita.PENDIENTE -> MaterialTheme.colorScheme.primary
        EstadoCita.COMPLETADA -> MaterialTheme.colorScheme.tertiary
        EstadoCita.CANCELADA -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Cita #${cita.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = cita.estado.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = estadoColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row {
                    if (cita.estado == EstadoCita.PENDIENTE) {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cliente: $clienteNombre",
                style = MaterialTheme.typography.bodyMedium
            )
            mascota?.let {
                Text(
                    text = "Mascota: ${it.nombre} (${it.especie})",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Fecha: ${cita.fecha}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Hora: ${cita.hora}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (cita.motivo.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Motivo: ${cita.motivo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (cita.estado == EstadoCita.PENDIENTE) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onCompletar) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Completar")
                    }
                    TextButton(onClick = onCancelar) {
                        Icon(Icons.Default.Cancel, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}
