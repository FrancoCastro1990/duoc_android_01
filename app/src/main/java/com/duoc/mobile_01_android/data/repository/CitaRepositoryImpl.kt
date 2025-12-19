package com.duoc.mobile_01_android.data.repository

import com.duoc.mobile_01_android.data.datasource.InMemoryDataSource
import com.duoc.mobile_01_android.domain.model.Cita
import com.duoc.mobile_01_android.domain.model.EstadoCita
import com.duoc.mobile_01_android.domain.repository.CitaRepository
import com.duoc.mobile_01_android.util.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del repositorio de Citas.
 * Aplica el patrón Repository para abstraer el acceso a datos.
 *
 * Responsabilidades:
 * - Coordinar operaciones con la fuente de datos
 * - Aplicar delays para simular operaciones asíncronas
 * - Proporcionar Flow para reactividad con filtros específicos
 */
class CitaRepositoryImpl(
    private val dataSource: InMemoryDataSource = InMemoryDataSource
) : CitaRepository {

    override fun getCitas(): Flow<List<Cita>> {
        return dataSource.citas
    }

    override fun getCitasPorMascota(mascotaId: Int): Flow<List<Cita>> {
        return dataSource.citas.map { citas ->
            citas.filter { it.mascotaId == mascotaId }
        }
    }

    override fun getCitasPorFecha(fecha: String): Flow<List<Cita>> {
        return dataSource.citas.map { citas ->
            citas.filter { it.fecha == fecha }
        }
    }

    override fun getCitasPendientes(): Flow<List<Cita>> {
        return dataSource.citas.map { citas ->
            citas.filter { it.estado == EstadoCita.PENDIENTE }
        }
    }

    override suspend fun agregarCita(cita: Cita): Cita {
        delay(Constants.DELAY_AGREGAR)
        return dataSource.agregarCita(cita)
    }

    override suspend fun editarCita(cita: Cita) {
        delay(Constants.DELAY_EDITAR)
        dataSource.editarCita(cita)
    }

    override suspend fun eliminarCita(id: Int) {
        delay(Constants.DELAY_ELIMINAR)
        dataSource.eliminarCita(id)
    }

    override suspend fun cambiarEstado(id: Int, estado: EstadoCita) {
        delay(Constants.DELAY_EDITAR)
        dataSource.cambiarEstadoCita(id, estado)
    }
}
