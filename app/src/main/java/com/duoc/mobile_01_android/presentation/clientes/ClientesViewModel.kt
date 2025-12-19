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

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Estado de búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /**
     * Estado de la UI combinando clientes y sus mascotas con filtrado de búsqueda.
     * StateFlow para reactividad con Compose.
     */
    val uiState: StateFlow<ClientesUiState> = combine(
        clienteRepository.getClientes(),
        mascotaRepository.getMascotas(),
        _searchQuery
    ) { clientes, mascotas, query ->
        // Filtrar clientes por nombre o email que contengan el query
        val clientesFiltrados = if (query.isBlank()) {
            clientes
        } else {
            clientes.filter { cliente ->
                cliente.nombre.contains(query, ignoreCase = true) ||
                cliente.email.contains(query, ignoreCase = true)
            }
        }

        // Mapear cada cliente filtrado con su conteo de mascotas
        val clientesConMascotas = clientesFiltrados.map { cliente ->
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

    /**
     * Limpia el mensaje de error actual.
     * Se llama desde la UI cuando el usuario cierra el snackbar o diálogo de error.
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Actualiza el query de búsqueda.
     * Automáticamente filtra la lista de clientes mediante combine().
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun agregarCliente(cliente: Cliente) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Guardando cliente..."
            try {
                clienteRepository.agregarCliente(cliente)
            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar cliente: ${e.message ?: "Error desconocido"}"
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
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar cliente: ${e.message ?: "Error desconocido"}"
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
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar cliente: ${e.message ?: "Error desconocido"}"
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
