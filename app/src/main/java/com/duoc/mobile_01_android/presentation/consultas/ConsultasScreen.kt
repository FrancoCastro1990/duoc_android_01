package com.duoc.mobile_01_android.presentation.consultas

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duoc.mobile_01_android.domain.model.Cliente
import com.duoc.mobile_01_android.domain.model.Consulta
import com.duoc.mobile_01_android.domain.model.Mascota
import com.duoc.mobile_01_android.domain.model.Medicamento
import com.duoc.mobile_01_android.presentation.components.AppTopBar
import com.duoc.mobile_01_android.presentation.components.LoadingIndicator
import com.duoc.mobile_01_android.util.Constants
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultasScreen(
    viewModel: ConsultasViewModel,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    var selectedCliente by remember { mutableStateOf<Cliente?>(null) }
    var selectedMascota by remember { mutableStateOf<Mascota?>(null) }
    var descripcion by remember { mutableStateOf("") }
    val selectedMedicamentos = remember { mutableStateListOf<Medicamento>() }

    var expandedCliente by remember { mutableStateOf(false) }
    var expandedMascota by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val loadingMessage by viewModel.loadingMessage.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    fun resetForm() {
        selectedCliente = null
        selectedMascota = null
        descripcion = ""
        selectedMedicamentos.clear()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Consultas",
                showBackButton = true,
                onBackClick = onBack,
                onNavigate = onNavigate
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
                Icon(Icons.Default.Add, contentDescription = "Nueva Consulta")
            }
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            when (val state = uiState) {
                is ConsultasUiState.Loading -> {}
                is ConsultasUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Lista de Consultas (${state.consultas.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (state.consultas.isEmpty()) {
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
                                        imageVector = Icons.Default.MedicalServices,
                                        contentDescription = null,
                                        modifier = Modifier.height(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No hay consultas registradas",
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
                                items(state.consultas) { consulta ->
                                    ConsultaCard(consulta = consulta)
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
                            title = { Text("Nueva Consulta") },
                            text = {
                                Column(
                                    modifier = Modifier.verticalScroll(rememberScrollState())
                                ) {
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

                                    OutlinedTextField(
                                        value = descripcion,
                                        onValueChange = { descripcion = it },
                                        label = { Text("Descripcion de la consulta") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 2,
                                        maxLines = 4
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Medicamentos:",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )

                                    state.medicamentosDisponibles.forEach { medicamento ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = selectedMedicamentos.contains(medicamento),
                                                onCheckedChange = { checked ->
                                                    if (checked) {
                                                        selectedMedicamentos.add(medicamento)
                                                    } else {
                                                        selectedMedicamentos.remove(medicamento)
                                                    }
                                                }
                                            )
                                            Column {
                                                Text(
                                                    text = "${medicamento.nombre} (${medicamento.dosificacion})",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                Text(
                                                    text = if (medicamento.descuento > 0) {
                                                        "$${String.format("%,.0f", medicamento.precio)} -> $${String.format("%,.0f", medicamento.precioConDescuento())} (-${(medicamento.descuento * 100).toInt()}%)"
                                                    } else {
                                                        "$${String.format("%,.0f", medicamento.precio)}"
                                                    },
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (medicamento.descuento > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    HorizontalDivider()

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Costo base consulta: $${String.format("%,.0f", Constants.COSTO_BASE_CONSULTA)}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Total medicamentos: $${String.format("%,.0f", selectedMedicamentos.sumOf { it.precioConDescuento() })}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "TOTAL: $${String.format("%,.0f", viewModel.calcularTotalConsulta(selectedMedicamentos.toList()))}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        selectedCliente?.let { cliente ->
                                            selectedMascota?.let { mascota ->
                                                viewModel.crearConsulta(
                                                    cliente = cliente,
                                                    mascota = mascota,
                                                    medicamentos = selectedMedicamentos.toList(),
                                                    descripcion = descripcion
                                                )
                                                showDialog = false
                                                resetForm()
                                            }
                                        }
                                    },
                                    enabled = selectedCliente != null && selectedMascota != null
                                ) {
                                    Text("Crear Consulta")
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
                is ConsultasUiState.Error -> {}
            }
        }

        LoadingIndicator(
            isLoading = isLoading,
            message = loadingMessage
        )
    }
}

@Composable
fun ConsultaCard(consulta: Consulta) {
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Consulta #${consulta.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = consulta.fecha,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cliente: ${consulta.cliente.nombre}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Mascota: ${consulta.mascota.nombre} (${consulta.mascota.especie})",
                style = MaterialTheme.typography.bodyMedium
            )

            if (consulta.descripcion.isNotBlank()) {
                Text(
                    text = "Motivo: ${consulta.descripcion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (consulta.medicamentos.isNotEmpty()) {
                Text(
                    text = "Medicamentos: ${consulta.medicamentos.joinToString { it.nombre }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total: $${String.format("%,.0f", consulta.total)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
