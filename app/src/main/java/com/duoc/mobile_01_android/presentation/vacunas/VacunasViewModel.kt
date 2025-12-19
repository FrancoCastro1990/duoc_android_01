package com.duoc.mobile_01_android.presentation.vacunas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.mobile_01_android.domain.model.Mascota
import com.duoc.mobile_01_android.domain.model.Vacuna
import com.duoc.mobile_01_android.domain.repository.MascotaRepository
import com.duoc.mobile_01_android.domain.repository.VacunaRepository
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
 * ViewModel para la pantalla de Vacunas.
 * Gestiona el estado de la lista de vacunas y operaciones CRUD.
 *
 * Extiende AndroidViewModel para acceder al Application context de forma segura,
 * necesario para enviar notificaciones cuando se registra una vacuna.
 *
 * @param clienteIdFiltro Si se proporciona, filtra las vacunas solo de mascotas de ese cliente (para rol DUENO)
 */
class VacunasViewModel(
    application: Application,
    private val vacunaRepository: VacunaRepository,
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

    /**
     * Estado de la UI combinando vacunas con información de las mascotas y vacunas próximas.
     * Filtra por cliente si se proporciona clienteIdFiltro (para rol DUENO).
     */
    val uiState: StateFlow<VacunasUiState> = combine(
        vacunaRepository.getVacunas(),
        mascotaRepository.getMascotas(),
        vacunaRepository.getVacunasProximas()
    ) { vacunas, mascotas, vacunasProximas ->
        // Filtrar mascotas si se proporciona clienteIdFiltro (para rol DUENO)
        val mascotasFiltradas = if (clienteIdFiltro != null) {
            mascotas.filter { it.clienteId == clienteIdFiltro }
        } else {
            mascotas
        }

        // Obtener IDs de mascotas filtradas
        val mascotaIds = mascotasFiltradas.map { it.id }.toSet()

        // Filtrar vacunas solo de las mascotas del cliente
        val vacunasFiltradas = if (clienteIdFiltro != null) {
            vacunas.filter { it.mascotaId in mascotaIds }
        } else {
            vacunas
        }

        val vacunasProximasFiltradas = if (clienteIdFiltro != null) {
            vacunasProximas.filter { it.mascotaId in mascotaIds }
        } else {
            vacunasProximas
        }

        // Mapear cada vacuna con su mascota
        val vacunasConMascota = vacunasFiltradas.map { vacuna ->
            val mascota = mascotasFiltradas.find { it.id == vacuna.mascotaId }
            VacunaConMascota(vacuna, mascota)
        }

        val vacunasProximasConMascota = vacunasProximasFiltradas.map { vacuna ->
            val mascota = mascotasFiltradas.find { it.id == vacuna.mascotaId }
            VacunaConMascota(vacuna, mascota)
        }

        VacunasUiState.Success(
            vacunas = vacunasConMascota,
            vacunasProximas = vacunasProximasConMascota,
            mascotasDisponibles = mascotasFiltradas
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VacunasUiState.Loading
    )

    // ==================== Acciones de Usuario ====================

    fun agregarVacuna(vacuna: Vacuna) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Guardando vacuna..."
            try {
                // Agregar la vacuna al repositorio
                vacunaRepository.agregarVacuna(vacuna)

                // Obtener el nombre de la mascota para la notificación
                val mascotas = mascotaRepository.getMascotas().firstOrNull() ?: emptyList()
                val mascota = mascotas.find { it.id == vacuna.mascotaId }
                val mascotaNombre = mascota?.nombre ?: "Mascota"

                // Mostrar notificación de confirmación
                NotificationHelper.showVacunaConfirmacion(
                    context = getApplication(),
                    vacuna = vacuna,
                    mascotaNombre = mascotaNombre
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar vacuna: ${e.message ?: "Error desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun editarVacuna(vacuna: Vacuna) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Actualizando vacuna..."
            try {
                vacunaRepository.editarVacuna(vacuna)
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar vacuna: ${e.message ?: "Error desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarVacuna(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Eliminando vacuna..."
            try {
                vacunaRepository.eliminarVacuna(id)
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar vacuna: ${e.message ?: "Error desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

/**
 * Estados posibles de la UI de Vacunas.
 */
sealed class VacunasUiState {
    object Loading : VacunasUiState()
    data class Success(
        val vacunas: List<VacunaConMascota>,
        val vacunasProximas: List<VacunaConMascota>,
        val mascotasDisponibles: List<Mascota>
    ) : VacunasUiState()
    data class Error(val message: String) : VacunasUiState()
}

/**
 * Clase auxiliar que combina una vacuna con su mascota.
 */
data class VacunaConMascota(
    val vacuna: Vacuna,
    val mascota: Mascota?
)
