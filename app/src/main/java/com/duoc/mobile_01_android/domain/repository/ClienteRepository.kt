package com.duoc.mobile_01_android.domain.repository

import com.duoc.mobile_01_android.domain.model.Cliente
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define el contrato para el repositorio de Clientes.
 * Principio de Inversión de Dependencias (D de SOLID): Los ViewModels
 * dependen de esta abstracción, no de la implementación concreta.
 */
interface ClienteRepository {
    /**
     * Obtiene todos los clientes como un Flow para reactividad.
     */
    fun getClientes(): Flow<List<Cliente>>

    /**
     * Agrega un nuevo cliente.
     * @param cliente El cliente a agregar (sin ID, será asignado automáticamente)
     */
    suspend fun agregarCliente(cliente: Cliente)

    /**
     * Actualiza un cliente existente.
     * @param cliente El cliente con los datos actualizados
     */
    suspend fun editarCliente(cliente: Cliente)

    /**
     * Elimina un cliente por su ID.
     * También elimina las mascotas asociadas en cascada.
     * @param id El ID del cliente a eliminar
     */
    suspend fun eliminarCliente(id: Int)
}
