package com.duoc.mobile_01_android.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.mobile_01_android.domain.model.Cita
import com.duoc.mobile_01_android.domain.model.Vacuna
import com.duoc.mobile_01_android.domain.repository.CitaRepository
import com.duoc.mobile_01_android.domain.repository.ClienteRepository
import com.duoc.mobile_01_android.domain.repository.ConsultaRepository
import com.duoc.mobile_01_android.domain.repository.MascotaRepository
import com.duoc.mobile_01_android.domain.repository.VacunaRepository
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
    consultaRepository: ConsultaRepository,
    citaRepository: CitaRepository,
    vacunaRepository: VacunaRepository
) : ViewModel() {

    /**
     * Estado combinado de la pantalla Home.
     * Usa combine para crear un único Flow que agrega múltiples fuentes.
     * Como combine solo soporta hasta 5 flows, combinamos primero algunos datos
     * y luego los combinamos con los restantes.
     */
    val uiState: StateFlow<HomeUiState> = combine(
        combine(
            clienteRepository.getClientes(),
            mascotaRepository.getMascotas(),
            consultaRepository.getConsultas()
        ) { clientes, mascotas, consultas ->
            Triple(clientes.size, mascotas.size, consultas.size)
        },
        consultaRepository.calcularIngresosTotales(),
        citaRepository.getCitasPendientes(),
        vacunaRepository.getVacunasProximas()
    ) { counts, ingresosTotales, citasPendientes, vacunasProximas ->
        HomeUiState(
            totalClientes = counts.first,
            totalMascotas = counts.second,
            totalConsultas = counts.third,
            ingresosTotales = ingresosTotales,
            citasPendientes = citasPendientes,
            vacunasProximas = vacunasProximas
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
    val ingresosTotales: Double = 0.0,
    val citasPendientes: List<Cita> = emptyList(),
    val vacunasProximas: List<Vacuna> = emptyList()
)
