package com.duoc.mobile_01_android.data.repository

import com.duoc.mobile_01_android.data.datasource.InMemoryDataSource
import com.duoc.mobile_01_android.domain.model.Mascota
import com.duoc.mobile_01_android.domain.repository.MascotaRepository
import com.duoc.mobile_01_android.util.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del repositorio de Mascotas.
 * Maneja la lógica de acceso a datos de mascotas.
 */
class MascotaRepositoryImpl(
    private val dataSource: InMemoryDataSource = InMemoryDataSource
) : MascotaRepository {

    override fun getMascotas(): Flow<List<Mascota>> {
        return dataSource.mascotas
    }

    override fun getMascotasPorCliente(clienteId: Int): Flow<List<Mascota>> {
        return dataSource.mascotas.map { mascotas ->
            mascotas.filter { it.clienteId == clienteId }
        }
    }

    override suspend fun agregarMascota(mascota: Mascota) {
        delay(Constants.DELAY_AGREGAR)
        dataSource.agregarMascota(mascota)
    }

    override suspend fun editarMascota(mascota: Mascota) {
        delay(Constants.DELAY_EDITAR)
        dataSource.editarMascota(mascota)
    }

    override suspend fun eliminarMascota(id: Int) {
        delay(Constants.DELAY_ELIMINAR)
        dataSource.eliminarMascota(id)
    }
}
