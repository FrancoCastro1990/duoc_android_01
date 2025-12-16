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
 */
class MascotasViewModel(
    private val mascotaRepository: MascotaRepository,
    private val clienteRepository: ClienteRepository
) : ViewModel() {

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadingMessage = MutableStateFlow("")
    val loadingMessage: StateFlow<String> = _loadingMessage.asStateFlow()

    /**
     * Estado de la UI combinando mascotas con información de sus dueños.
     */
    val uiState: StateFlow<MascotasUiState> = combine(
        mascotaRepository.getMascotas(),
        clienteRepository.getClientes()
    ) { mascotas, clientes ->
        // Mapear cada mascota con su cliente dueño
        val mascotasConCliente = mascotas.map { mascota ->
            val cliente = clientes.find { it.id == mascota.clienteId }
            MascotaConCliente(mascota, cliente)
        }
        MascotasUiState.Success(mascotasConCliente, clientes)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MascotasUiState.Loading
    )

    // ==================== Acciones de Usuario ====================

    fun agregarMascota(mascota: Mascota) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Guardando mascota..."
            try {
                mascotaRepository.agregarMascota(mascota)
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
        val clientesDisponibles: List<Cliente>
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
