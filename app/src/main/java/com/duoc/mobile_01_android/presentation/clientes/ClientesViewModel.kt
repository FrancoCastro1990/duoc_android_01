package com.duoc.mobile_01_android.presentation.clientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.mobile_01_android.domain.model.Cliente
import com.duoc.mobile_01_android.domain.repository.ClienteRepository
import com.duoc.mobile_01_android.domain.repository.MascotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Clientes.
 * Gestiona el estado de la lista de clientes y operaciones CRUD.
 *
 * Principio de Responsabilidad Única (S de SOLID):
 * Solo maneja lógica de presentación relacionada con clientes.
 */
class ClientesViewModel(
    private val clienteRepository: ClienteRepository,
    private val mascotaRepository: MascotaRepository
) : ViewModel() {

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadingMessage = MutableStateFlow("")
    val loadingMessage: StateFlow<String> = _loadingMessage.asStateFlow()

    /**
     * Estado de la UI combinando clientes y sus mascotas.
     * StateFlow para reactividad con Compose.
     */
    val uiState: StateFlow<ClientesUiState> = combine(
        clienteRepository.getClientes(),
        mascotaRepository.getMascotas()
    ) { clientes, mascotas ->
        // Mapear cada cliente con su conteo de mascotas
        val clientesConMascotas = clientes.map { cliente ->
            val mascotasCount = mascotas.count { it.clienteId == cliente.id }
            ClienteConMascotas(cliente, mascotasCount)
        }
        ClientesUiState.Success(clientesConMascotas)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ClientesUiState.Loading
    )

    // ==================== Acciones de Usuario ====================

    fun agregarCliente(cliente: Cliente) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Guardando cliente..."
            try {
                clienteRepository.agregarCliente(cliente)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun editarCliente(cliente: Cliente) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Actualizando cliente..."
            try {
                clienteRepository.editarCliente(cliente)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarCliente(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Eliminando cliente..."
            try {
                clienteRepository.eliminarCliente(id)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

/**
 * Estados posibles de la UI de Clientes usando sealed class.
 * Patrón recomendado para representar estados mutuamente excluyentes.
 */
sealed class ClientesUiState {
    object Loading : ClientesUiState()
    data class Success(val clientes: List<ClienteConMascotas>) : ClientesUiState()
    data class Error(val message: String) : ClientesUiState()
}

/**
 * Clase auxiliar que combina un cliente con su conteo de mascotas.
 */
data class ClienteConMascotas(
    val cliente: Cliente,
    val mascotasCount: Int
)
