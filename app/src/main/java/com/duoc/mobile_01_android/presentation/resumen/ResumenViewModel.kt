package com.duoc.mobile_01_android.presentation.resumen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.mobile_01_android.domain.model.Consulta
import com.duoc.mobile_01_android.domain.repository.ClienteRepository
import com.duoc.mobile_01_android.domain.repository.ConsultaRepository
import com.duoc.mobile_01_android.domain.repository.MascotaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel para la pantalla de Resumen.
 * Agrega y presenta estadísticas del sistema.
 */
class ResumenViewModel(
    clienteRepository: ClienteRepository,
    mascotaRepository: MascotaRepository,
    consultaRepository: ConsultaRepository
) : ViewModel() {

    /**
     * Estado de la UI combinando todas las estadísticas.
     */
    val uiState: StateFlow<ResumenUiState> = combine(
        clienteRepository.getClientes(),
        mascotaRepository.getMascotas(),
        consultaRepository.getConsultas(),
        consultaRepository.calcularIngresosTotales()
    ) { clientes, mascotas, consultas, ingresosTotales ->

        // Calcular especies más comunes
        val especiesMasComunes = mascotas
            .groupBy { it.especie }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .take(3)

        // Obtener últimas 5 consultas
        val ultimasConsultas = consultas.takeLast(5).reversed()

        ResumenUiState.Success(
            totalMascotas = mascotas.size,
            totalClientes = clientes.size,
            totalConsultas = consultas.size,
            ingresosTotales = ingresosTotales,
            especiesMasComunes = especiesMasComunes,
            ultimasConsultas = ultimasConsultas
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ResumenUiState.Loading
    )
}

/**
 * Estados posibles de la UI de Resumen.
 */
sealed class ResumenUiState {
    object Loading : ResumenUiState()
    data class Success(
        val totalMascotas: Int,
        val totalClientes: Int,
        val totalConsultas: Int,
        val ingresosTotales: Double,
        val especiesMasComunes: List<Pair<String, Int>>,
        val ultimasConsultas: List<Consulta>
    ) : ResumenUiState()
    data class Error(val message: String) : ResumenUiState()
}
