package com.duoc.mobile_01_android.di

import com.duoc.mobile_01_android.data.repository.ClienteRepositoryImpl
import com.duoc.mobile_01_android.data.repository.ConsultaRepositoryImpl
import com.duoc.mobile_01_android.data.repository.MascotaRepositoryImpl
import com.duoc.mobile_01_android.data.repository.MedicamentoRepositoryImpl
import com.duoc.mobile_01_android.domain.repository.ClienteRepository
import com.duoc.mobile_01_android.domain.repository.ConsultaRepository
import com.duoc.mobile_01_android.domain.repository.MascotaRepository
import com.duoc.mobile_01_android.domain.repository.MedicamentoRepository
import com.duoc.mobile_01_android.presentation.clientes.ClientesViewModel
import com.duoc.mobile_01_android.presentation.consultas.ConsultasViewModel
import com.duoc.mobile_01_android.presentation.home.HomeViewModel
import com.duoc.mobile_01_android.presentation.mascotas.MascotasViewModel
import com.duoc.mobile_01_android.presentation.resumen.ResumenViewModel

/**
 * ServiceLocator simple para inyección de dependencias manual.
 * Proporciona instancias singleton de repositorios y factories para ViewModels.
 *
 * Ventajas:
 * - Simple y sin dependencias externas (no requiere Hilt/Koin para esta app pequeña)
 * - Centraliza la creación de dependencias
 * - Facilita testing al permitir inyectar mocks
 *
 * Principio de Inversión de Dependencias (D de SOLID):
 * Los ViewModels reciben interfaces, no implementaciones concretas.
 */
object ServiceLocator {

    // ==================== Repositories (Singleton) ====================

    private val clienteRepository: ClienteRepository by lazy {
        ClienteRepositoryImpl()
    }

    private val mascotaRepository: MascotaRepository by lazy {
        MascotaRepositoryImpl()
    }

    private val consultaRepository: ConsultaRepository by lazy {
        ConsultaRepositoryImpl()
    }

    private val medicamentoRepository: MedicamentoRepository by lazy {
        MedicamentoRepositoryImpl()
    }

    // ==================== ViewModel Factories ====================

    fun provideHomeViewModel(): HomeViewModel {
        return HomeViewModel(
            clienteRepository = clienteRepository,
            mascotaRepository = mascotaRepository,
            consultaRepository = consultaRepository
        )
    }

    fun provideClientesViewModel(): ClientesViewModel {
        return ClientesViewModel(
            clienteRepository = clienteRepository,
            mascotaRepository = mascotaRepository
        )
    }

    fun provideMascotasViewModel(): MascotasViewModel {
        return MascotasViewModel(
            mascotaRepository = mascotaRepository,
            clienteRepository = clienteRepository
        )
    }

    fun provideConsultasViewModel(): ConsultasViewModel {
        return ConsultasViewModel(
            consultaRepository = consultaRepository,
            clienteRepository = clienteRepository,
            mascotaRepository = mascotaRepository,
            medicamentoRepository = medicamentoRepository
        )
    }

    fun provideResumenViewModel(): ResumenViewModel {
        return ResumenViewModel(
            clienteRepository = clienteRepository,
            mascotaRepository = mascotaRepository,
            consultaRepository = consultaRepository
        )
    }
}
