package com.duoc.mobile_01_android.di

import android.app.Application
import com.duoc.mobile_01_android.data.repository.AuthRepositoryImpl
import com.duoc.mobile_01_android.data.repository.CitaRepositoryImpl
import com.duoc.mobile_01_android.data.repository.ClienteRepositoryImpl
import com.duoc.mobile_01_android.data.repository.ConsultaRepositoryImpl
import com.duoc.mobile_01_android.data.repository.MascotaRepositoryImpl
import com.duoc.mobile_01_android.data.repository.MedicamentoRepositoryImpl
import com.duoc.mobile_01_android.data.repository.VacunaRepositoryImpl
import com.duoc.mobile_01_android.domain.repository.AuthRepository
import com.duoc.mobile_01_android.domain.repository.CitaRepository
import com.duoc.mobile_01_android.domain.repository.ClienteRepository
import com.duoc.mobile_01_android.domain.repository.ConsultaRepository
import com.duoc.mobile_01_android.domain.repository.MascotaRepository
import com.duoc.mobile_01_android.domain.repository.MedicamentoRepository
import com.duoc.mobile_01_android.domain.repository.VacunaRepository
import com.duoc.mobile_01_android.presentation.auth.LoginViewModel
import com.duoc.mobile_01_android.presentation.auth.RecuperarPasswordViewModel
import com.duoc.mobile_01_android.presentation.citas.CitasViewModel
import com.duoc.mobile_01_android.presentation.clientes.ClientesViewModel
import com.duoc.mobile_01_android.presentation.consultas.ConsultasViewModel
import com.duoc.mobile_01_android.presentation.home.HomeViewModel
import com.duoc.mobile_01_android.presentation.mascotas.MascotasViewModel
import com.duoc.mobile_01_android.presentation.resumen.ResumenViewModel
import com.duoc.mobile_01_android.presentation.vacunas.VacunasViewModel

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

    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl()
    }

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

    private val citaRepository: CitaRepository by lazy {
        CitaRepositoryImpl()
    }

    private val vacunaRepository: VacunaRepository by lazy {
        VacunaRepositoryImpl()
    }

    // ==================== Public Repository Access ====================

    /**
     * Proporciona acceso al repositorio de autenticación.
     * Necesario para que AppNavigation observe el estado de sesión.
     */
    fun provideAuthRepository(): AuthRepository = authRepository

    // ==================== ViewModel Factories ====================

    fun provideHomeViewModel(): HomeViewModel {
        return HomeViewModel(
            clienteRepository = clienteRepository,
            mascotaRepository = mascotaRepository,
            consultaRepository = consultaRepository,
            citaRepository = citaRepository,
            vacunaRepository = vacunaRepository
        )
    }

    fun provideClientesViewModel(): ClientesViewModel {
        return ClientesViewModel(
            clienteRepository = clienteRepository,
            mascotaRepository = mascotaRepository
        )
    }

    fun provideMascotasViewModel(clienteIdFiltro: Int? = null): MascotasViewModel {
        return MascotasViewModel(
            mascotaRepository = mascotaRepository,
            clienteRepository = clienteRepository,
            clienteIdFiltro = clienteIdFiltro
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

    fun provideCitasViewModel(application: Application, clienteIdFiltro: Int? = null): CitasViewModel {
        return CitasViewModel(
            application = application,
            citaRepository = citaRepository,
            clienteRepository = clienteRepository,
            mascotaRepository = mascotaRepository,
            clienteIdFiltro = clienteIdFiltro
        )
    }

    fun provideVacunasViewModel(application: Application, clienteIdFiltro: Int? = null): VacunasViewModel {
        return VacunasViewModel(
            application = application,
            vacunaRepository = vacunaRepository,
            mascotaRepository = mascotaRepository,
            clienteIdFiltro = clienteIdFiltro
        )
    }

    fun provideLoginViewModel(): LoginViewModel {
        return LoginViewModel(
            authRepository = authRepository
        )
    }

    fun provideRecuperarPasswordViewModel(): RecuperarPasswordViewModel {
        return RecuperarPasswordViewModel(
            authRepository = authRepository
        )
    }
}
