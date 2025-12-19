package com.duoc.mobile_01_android.presentation.citas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.mobile_01_android.domain.model.Cita
import com.duoc.mobile_01_android.domain.model.Cliente
import com.duoc.mobile_01_android.domain.model.EstadoCita
import com.duoc.mobile_01_android.domain.model.Mascota
import com.duoc.mobile_01_android.domain.repository.CitaRepository
import com.duoc.mobile_01_android.domain.repository.ClienteRepository
import com.duoc.mobile_01_android.domain.repository.MascotaRepository
import com.duoc.mobile_01_android.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Citas/Agenda.
 * Maneja la gestión completa de citas incluyendo filtrado por estado.
 *
 * Extiende AndroidViewModel para acceder al Application context de forma segura,
 * necesario para enviar notificaciones cuando se crea una cita.
 *
 * @param clienteIdFiltro Si se proporciona, filtra las citas solo de ese cliente (para rol DUENO)
 */
class CitasViewModel(
    application: Application,
    private val citaRepository: CitaRepository,
    private val clienteRepository: ClienteRepository,
    private val mascotaRepository: MascotaRepository,
    private val clienteIdFiltro: Int? = null
) : AndroidViewModel(application) {

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadingMessage = MutableStateFlow("")
    val loadingMessage: StateFlow<String> = _loadingMessage.asStateFlow()

    // Estado de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Limpia el mensaje de error actual.
     * Se llama desde la UI cuando el usuario cierra el snackbar o diálogo de error.
     */
    fun clearError() {
        _errorMessage.value = null
    }

    // Filtro de estado seleccionado
    private val _estadoFiltro = MutableStateFlow<EstadoCita?>(null)
    val estadoFiltro: StateFlow<EstadoCita?> = _estadoFiltro.asStateFlow()

    /**
     * Estado de la UI combinando todas las entidades necesarias.
     * Incluye filtrado por estado y por cliente (para rol DUENO).
     */
    val uiState: StateFlow<CitasUiState> = combine(
        citaRepository.getCitas(),
        clienteRepository.getClientes(),
        mascotaRepository.getMascotas(),
        _estadoFiltro
    ) { citas, clientes, mascotas, filtroEstado ->
        // Filtrar por clienteId si se proporciona (para rol DUENO)
        val citasPorCliente = if (clienteIdFiltro != null) {
            citas.filter { it.clienteId == clienteIdFiltro }
        } else {
            citas
        }

        // Filtrar por estado
        val citasFiltradas = if (filtroEstado != null) {
            citasPorCliente.filter { it.estado == filtroEstado }
        } else {
            citasPorCliente
        }

        CitasUiState.Success(
            citas = citasFiltradas,
            clientesDisponibles = clientes,
            mascotasDisponibles = mascotas
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CitasUiState.Loading
    )

    // ==================== Acciones de Usuario ====================

    fun agregarCita(
        clienteId: Int,
        mascotaId: Int,
        fecha: String,
        hora: String,
        motivo: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Creando cita..."
            try {
                val nuevaCita = Cita(
                    clienteId = clienteId,
                    mascotaId = mascotaId,
                    fecha = fecha,
                    hora = hora,
                    motivo = motivo,
                    estado = EstadoCita.PENDIENTE
                )

                // Agregar la cita al repositorio
                citaRepository.agregarCita(nuevaCita)

                // Obtener el nombre de la mascota para la notificación
                val mascotas = mascotaRepository.getMascotas().firstOrNull() ?: emptyList()
                val mascota = mascotas.find { it.id == mascotaId }
                val mascotaNombre = mascota?.nombre ?: "Mascota"

                // Mostrar notificación de confirmación
                NotificationHelper.showCitaConfirmacion(
                    context = getApplication(),
                    cita = nuevaCita,
                    mascotaNombre = mascotaNombre
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error al crear cita: ${e.message ?: "Error desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun editarCita(cita: Cita) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Editando cita..."
            try {
                citaRepository.editarCita(cita)
            } catch (e: Exception) {
                _errorMessage.value = "Error al editar cita: ${e.message ?: "Error desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarCita(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Eliminando cita..."
            try {
                citaRepository.eliminarCita(id)
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar cita: ${e.message ?: "Error desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cambiarEstado(id: Int, estado: EstadoCita) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Actualizando estado..."
            try {
                citaRepository.cambiarEstado(id, estado)
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar estado: ${e.message ?: "Error desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setFiltroEstado(estado: EstadoCita?) {
        _estadoFiltro.value = estado
    }

    /**
     * Obtiene el nombre del cliente dado su ID.
     * Función auxiliar para la UI.
     */
    fun getClienteName(clienteId: Int, clientes: List<Cliente>): String {
        return clientes.find { it.id == clienteId }?.nombre ?: "Desconocido"
    }

    /**
     * Obtiene los datos de la mascota dado su ID.
     * Función auxiliar para la UI.
     */
    fun getMascota(mascotaId: Int, mascotas: List<Mascota>): Mascota? {
        return mascotas.find { it.id == mascotaId }
    }
}

/**
 * Estados posibles de la UI de Citas.
 */
sealed class CitasUiState {
    object Loading : CitasUiState()
    data class Success(
        val citas: List<Cita>,
        val clientesDisponibles: List<Cliente>,
        val mascotasDisponibles: List<Mascota>
    ) : CitasUiState()
    data class Error(val message: String) : CitasUiState()
}
