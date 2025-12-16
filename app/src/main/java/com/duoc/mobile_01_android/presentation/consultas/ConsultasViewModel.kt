package com.duoc.mobile_01_android.presentation.consultas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.mobile_01_android.domain.model.Cliente
import com.duoc.mobile_01_android.domain.model.Consulta
import com.duoc.mobile_01_android.domain.model.Mascota
import com.duoc.mobile_01_android.domain.model.Medicamento
import com.duoc.mobile_01_android.domain.repository.ClienteRepository
import com.duoc.mobile_01_android.domain.repository.ConsultaRepository
import com.duoc.mobile_01_android.domain.repository.MascotaRepository
import com.duoc.mobile_01_android.domain.repository.MedicamentoRepository
import com.duoc.mobile_01_android.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Consultas.
 * Maneja la lógica más compleja: crear consultas combinando clientes, mascotas y medicamentos.
 */
class ConsultasViewModel(
    private val consultaRepository: ConsultaRepository,
    private val clienteRepository: ClienteRepository,
    private val mascotaRepository: MascotaRepository,
    private val medicamentoRepository: MedicamentoRepository
) : ViewModel() {

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadingMessage = MutableStateFlow("")
    val loadingMessage: StateFlow<String> = _loadingMessage.asStateFlow()

    /**
     * Estado de la UI combinando todas las entidades necesarias.
     */
    val uiState: StateFlow<ConsultasUiState> = combine(
        consultaRepository.getConsultas(),
        clienteRepository.getClientes(),
        mascotaRepository.getMascotas(),
        medicamentoRepository.getMedicamentos()
    ) { consultas, clientes, mascotas, medicamentos ->
        ConsultasUiState.Success(
            consultas = consultas,
            clientesDisponibles = clientes,
            mascotasDisponibles = mascotas,
            medicamentosDisponibles = medicamentos
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ConsultasUiState.Loading
    )

    // ==================== Acciones de Usuario ====================

    fun crearConsulta(
        cliente: Cliente,
        mascota: Mascota,
        medicamentos: List<Medicamento>,
        descripcion: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Creando consulta..."
            try {
                consultaRepository.crearConsulta(
                    cliente = cliente,
                    mascota = mascota,
                    medicamentos = medicamentos,
                    descripcion = descripcion
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Calcula el total de una consulta (costo base + medicamentos con descuento).
     * Lógica de presentación que ayuda a la UI a mostrar el precio antes de crear la consulta.
     */
    fun calcularTotalConsulta(medicamentos: List<Medicamento>): Double {
        var total = Constants.COSTO_BASE_CONSULTA
        medicamentos.forEach { medicamento ->
            total += medicamento.precioConDescuento()
        }
        return total
    }
}

/**
 * Estados posibles de la UI de Consultas.
 */
sealed class ConsultasUiState {
    object Loading : ConsultasUiState()
    data class Success(
        val consultas: List<Consulta>,
        val clientesDisponibles: List<Cliente>,
        val mascotasDisponibles: List<Mascota>,
        val medicamentosDisponibles: List<Medicamento>
    ) : ConsultasUiState()
    data class Error(val message: String) : ConsultasUiState()
}
