package com.duoc.mobile_01_android.presentation.mascotas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.mobile_01_android.domain.model.Cliente
import com.duoc.mobile_01_android.domain.model.Mascota
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
 * ViewModel para la pantalla de Mascotas.
 * Gestiona el estado de la lista de mascotas y operaciones CRUD.
 *
 * @param clienteIdFiltro Si se proporciona, filtra las mascotas solo de ese cliente (para rol DUENO)
 */
class MascotasViewModel(
    private val mascotaRepository: MascotaRepository,
    private val clienteRepository: ClienteRepository,
    private val clienteIdFiltro: Int? = null
) : ViewModel() {

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadingMessage = MutableStateFlow("")
    val loadingMessage: StateFlow<String> = _loadingMessage.asStateFlow()

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Estado de búsqueda y filtros
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filtroEspecie = MutableStateFlow<String?>(null)
    val filtroEspecie: StateFlow<String?> = _filtroEspecie.asStateFlow()

    /**
     * Estado de la UI combinando mascotas con información de sus dueños,
     * aplicando filtros de búsqueda, especie y cliente (para rol DUENO).
     */
    val uiState: StateFlow<MascotasUiState> = combine(
        mascotaRepository.getMascotas(),
        clienteRepository.getClientes(),
        _searchQuery,
        _filtroEspecie
    ) { mascotas, clientes, query, especieFiltro ->
        // Aplicar filtros
        val mascotasFiltradas = mascotas
            .filter { mascota ->
                // Filtrar por clienteId si se proporciona (para rol DUENO)
                if (clienteIdFiltro != null) {
                    mascota.clienteId == clienteIdFiltro
                } else {
                    true
                }
            }
            .filter { mascota ->
                // Filtrar por nombre de mascota
                if (query.isBlank()) {
                    true
                } else {
                    mascota.nombre.contains(query, ignoreCase = true)
                }
            }
            .filter { mascota ->
                // Filtrar por especie
                if (especieFiltro == null || especieFiltro == "Todas") {
                    true
                } else {
                    mascota.especie.equals(especieFiltro, ignoreCase = true)
                }
            }

        // Mapear cada mascota filtrada con su cliente dueño
        val mascotasConCliente = mascotasFiltradas.map { mascota ->
            val cliente = clientes.find { it.id == mascota.clienteId }
            MascotaConCliente(mascota, cliente)
        }

        // Extraer especies únicas de todas las mascotas (no solo filtradas)
        val especiesDisponibles = mascotas.map { it.especie }.distinct().sorted()

        MascotasUiState.Success(mascotasConCliente, clientes, especiesDisponibles)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MascotasUiState.Loading
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
     * Automáticamente filtra la lista de mascotas mediante combine().
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Actualiza el filtro de especie.
     * Automáticamente filtra la lista de mascotas mediante combine().
     */
    fun setFiltroEspecie(especie: String?) {
        _filtroEspecie.value = especie
    }

    fun agregarMascota(mascota: Mascota) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Guardando mascota..."
            try {
                mascotaRepository.agregarMascota(mascota)
            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar mascota: ${e.message ?: "Error desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun editarMascota(mascota: Mascota) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Actualizando mascota..."
            try {
                mascotaRepository.editarMascota(mascota)
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar mascota: ${e.message ?: "Error desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarMascota(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Eliminando mascota..."
            try {
                mascotaRepository.eliminarMascota(id)
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar mascota: ${e.message ?: "Error desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

/**
 * Estados posibles de la UI de Mascotas.
 */
sealed class MascotasUiState {
    object Loading : MascotasUiState()
    data class Success(
        val mascotas: List<MascotaConCliente>,
        val clientesDisponibles: List<Cliente>,
        val especiesDisponibles: List<String> = emptyList()
    ) : MascotasUiState()
    data class Error(val message: String) : MascotasUiState()
}

/**
 * Clase auxiliar que combina una mascota con su cliente dueño.
 */
data class MascotaConCliente(
    val mascota: Mascota,
    val cliente: Cliente?
)
