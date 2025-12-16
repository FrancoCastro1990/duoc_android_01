package com.duoc.mobile_01_android.data.repository

import com.duoc.mobile_01_android.data.datasource.InMemoryDataSource
import com.duoc.mobile_01_android.domain.model.Cliente
import com.duoc.mobile_01_android.domain.repository.ClienteRepository
import com.duoc.mobile_01_android.util.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

/**
 * Implementación del repositorio de Clientes.
 * Aplica el patrón Repository para abstraer el acceso a datos.
 *
 * Responsabilidades:
 * - Coordinar operaciones con la fuente de datos
 * - Aplicar delays para simular operaciones asíncronas
 * - Proporcionar Flow para reactividad
 */
class ClienteRepositoryImpl(
    private val dataSource: InMemoryDataSource = InMemoryDataSource
) : ClienteRepository {

    override fun getClientes(): Flow<List<Cliente>> {
        return dataSource.clientes
    }

    override suspend fun agregarCliente(cliente: Cliente) {
        delay(Constants.DELAY_AGREGAR)
        dataSource.agregarCliente(cliente)
    }

    override suspend fun editarCliente(cliente: Cliente) {
        delay(Constants.DELAY_EDITAR)
        dataSource.editarCliente(cliente)
    }

    override suspend fun eliminarCliente(id: Int) {
        delay(Constants.DELAY_ELIMINAR)
        dataSource.eliminarCliente(id)
    }
}
