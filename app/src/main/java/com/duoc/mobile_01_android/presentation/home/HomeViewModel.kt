package com.duoc.mobile_01_android.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.mobile_01_android.domain.repository.ClienteRepository
import com.duoc.mobile_01_android.domain.repository.ConsultaRepository
import com.duoc.mobile_01_android.domain.repository.MascotaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel para la pantalla Home.
 * Responsabilidad única: Gestionar el estado de la pantalla de inicio.
 *
 * No contiene lógica de negocio, solo lógica de presentación.
 * Los repositorios manejan la lógica de acceso a datos.
 */
class HomeViewModel(
    clienteRepository: ClienteRepository,
    mascotaRepository: MascotaRepository,
    consultaRepository: ConsultaRepository
) : ViewModel() {

    /**
     * Estado combinado de la pantalla Home.
     * Usa combine para crear un único Flow que agrega múltiples fuentes.
     */
    val uiState: StateFlow<HomeUiState> = combine(
        clienteRepository.getClientes(),
        mascotaRepository.getMascotas(),
        consultaRepository.getConsultas(),
        consultaRepository.calcularIngresosTotales()
    ) { clientes, mascotas, consultas, ingresosTotales ->
        HomeUiState(
            totalClientes = clientes.size,
            totalMascotas = mascotas.size,
            totalConsultas = consultas.size,
            ingresosTotales = ingresosTotales
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )
}

/**
 * Estado inmutable de la UI de Home.
 * Data class para garantizar inmutabilidad y facilitar testing.
 */
data class HomeUiState(
    val totalClientes: Int = 0,
    val totalMascotas: Int = 0,
    val totalConsultas: Int = 0,
    val ingresosTotales: Double = 0.0
)
