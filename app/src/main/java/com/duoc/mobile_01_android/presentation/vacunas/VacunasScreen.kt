package com.duoc.mobile_01_android.presentation.vacunas

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.duoc.mobile_01_android.domain.model.Mascota
import com.duoc.mobile_01_android.domain.model.Vacuna
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
fun VacunasScreen(
    viewModel: VacunasViewModel,
    userRole: com.duoc.mobile_01_android.domain.model.Rol = com.duoc.mobile_01_android.domain.model.Rol.ADMIN,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var editingVacuna by remember { mutableStateOf<Vacuna?>(null) }

    var nombre by remember { mutableStateOf("") }
    var fechaAplicacion by remember { mutableStateOf("") }
    var proximaFecha by remember { mutableStateOf("") }
    var selectedMascotaId by remember { mutableStateOf<Int?>(null) }
    var expandedMascota by remember { mutableStateOf(false) }

    // DatePicker states
    var showDatePickerAplicacion by remember { mutableStateOf(false) }
    var showDatePickerProxima by remember { mutableStateOf(false) }
    val datePickerStateAplicacion = rememberDatePickerState()
    val datePickerStateProxima = rememberDatePickerState()

    val isLoading by viewModel.isLoading.collectAsState()
    val loadingMessage by viewModel.loadingMessage.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    fun resetForm() {
        nombre = ""
        fechaAplicacion = ""
        proximaFecha = ""
        selectedMascotaId = null
        editingVacuna = null
    }

    fun loadVacunaToEdit(vacuna: Vacuna) {
        nombre = vacuna.nombre
        fechaAplicacion = vacuna.fechaAplicacion
        proximaFecha = vacuna.proximaFecha
        selectedMascotaId = vacuna.mascotaId
        editingVacuna = vacuna
        showDialog = true
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Vacunas",
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
                Icon(Icons.Default.Add, contentDescription = "Agregar Vacuna")
            }
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            when (val state = uiState) {
                is VacunasUiState.Loading -> {
                    // Mostrar loading
                }
                is VacunasUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        // Sección de Vacunas Próximas
                        if (state.vacunasProximas.isNotEmpty()) {
                            Text(
                                text = "Próximas Vacunas (30 días)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            state.vacunasProximas.forEach { vacunaConMascota ->
                                ProximaVacunaCard(
                                    vacuna = vacunaConMascota.vacuna,
                                    mascota = vacunaConMascota.mascota
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Lista de todas las vacunas
                        Text(
                            text = "Lista de Vacunas (${state.vacunas.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (state.vacunas.isEmpty()) {
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
                                        imageVector = Icons.Default.LocalHospital,
                                        contentDescription = null,
                                        modifier = Modifier.height(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No hay vacunas registradas",
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
                                items(state.vacunas) { vacunaConMascota ->
                                    VacunaCard(
                                        vacuna = vacunaConMascota.vacuna,
                                        mascota = vacunaConMascota.mascota,
                                        onEdit = { loadVacunaToEdit(vacunaConMascota.vacuna) },
                                        onDelete = { viewModel.eliminarVacuna(vacunaConMascota.vacuna.id) }
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
                                Text(if (editingVacuna != null) "Editar Vacuna" else "Nueva Vacuna")
                            },
                            text = {
                                Column {
                                    ExposedDropdownMenuBox(
                                        expanded = expandedMascota,
                                        onExpandedChange = { expandedMascota = !expandedMascota }
                                    ) {
                                        OutlinedTextField(
                                            value = state.mascotasDisponibles.find { it.id == selectedMascotaId }?.nombre ?: "",
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Mascota") },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMascota) },
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth()
                                        )

                                        ExposedDropdownMenu(
                                            expanded = expandedMascota,
                                            onDismissRequest = { expandedMascota = false }
                                        ) {
                                            state.mascotasDisponibles.forEach { mascota ->
                                                DropdownMenuItem(
                                                    text = { Text(mascota.nombre) },
                                                    onClick = {
                                                        selectedMascotaId = mascota.id
                                                        expandedMascota = false
                                                    }
                                                )
                                            }
                                            if (state.mascotasDisponibles.isEmpty()) {
                                                DropdownMenuItem(
                                                    text = { Text("No hay mascotas - Registra una primero") },
                                                    onClick = { expandedMascota = false }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = nombre,
                                        onValueChange = { nombre = it },
                                        label = { Text("Nombre de la vacuna") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = fechaAplicacion,
                                        onValueChange = { },
                                        readOnly = true,
                                        label = { Text("Fecha aplicacion (dd/MM/yyyy)") },
                                        trailingIcon = {
                                            IconButton(onClick = { showDatePickerAplicacion = true }) {
                                                Icon(Icons.Default.CalendarMonth, "Seleccionar fecha")
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { showDatePickerAplicacion = true },
                                        singleLine = true,
                                        placeholder = { Text("01/01/2024") }
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = proximaFecha,
                                        onValueChange = { },
                                        readOnly = true,
                                        label = { Text("Proxima fecha (dd/MM/yyyy)") },
                                        trailingIcon = {
                                            IconButton(onClick = { showDatePickerProxima = true }) {
                                                Icon(Icons.Default.CalendarMonth, "Seleccionar fecha")
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { showDatePickerProxima = true },
                                        singleLine = true,
                                        placeholder = { Text("01/01/2025") }
                                    )
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        val vacuna = Vacuna(
                                            id = editingVacuna?.id ?: 0,
                                            nombre = nombre,
                                            mascotaId = selectedMascotaId ?: 0,
                                            fechaAplicacion = fechaAplicacion,
                                            proximaFecha = proximaFecha
                                        )
                                        if (editingVacuna != null) {
                                            viewModel.editarVacuna(vacuna)
                                        } else {
                                            viewModel.agregarVacuna(vacuna)
                                        }
                                        showDialog = false
                                        resetForm()
                                    },
                                    enabled = nombre.isNotBlank() && fechaAplicacion.isNotBlank() && proximaFecha.isNotBlank() && selectedMascotaId != null
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

                    // DatePickerDialog para fecha de aplicacion
                    if (showDatePickerAplicacion) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePickerAplicacion = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerStateAplicacion.selectedDateMillis?.let { millis ->
                                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                        fechaAplicacion = formatter.format(Date(millis))
                                    }
                                    showDatePickerAplicacion = false
                                }) {
                                    Text("Aceptar")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePickerAplicacion = false }) {
                                    Text("Cancelar")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerStateAplicacion)
                        }
                    }

                    // DatePickerDialog para proxima fecha
                    if (showDatePickerProxima) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePickerProxima = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerStateProxima.selectedDateMillis?.let { millis ->
                                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                        proximaFecha = formatter.format(Date(millis))
                                    }
                                    showDatePickerProxima = false
                                }) {
                                    Text("Aceptar")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePickerProxima = false }) {
                                    Text("Cancelar")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerStateProxima)
                        }
                    }
                }
                is VacunasUiState.Error -> {
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
fun ProximaVacunaCard(
    vacuna: Vacuna,
    mascota: Mascota?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vacuna.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                mascota?.let {
                    Text(
                        text = "Mascota: ${it.nombre}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Text(
                    text = "Proxima dosis: ${vacuna.proximaFecha}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
fun VacunaCard(
    vacuna: Vacuna,
    mascota: Mascota?,
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
                    text = vacuna.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                mascota?.let {
                    Text(
                        text = "Mascota: ${it.nombre} (${it.especie})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = "Aplicada: ${vacuna.fechaAplicacion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Proxima: ${vacuna.proximaFecha}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
