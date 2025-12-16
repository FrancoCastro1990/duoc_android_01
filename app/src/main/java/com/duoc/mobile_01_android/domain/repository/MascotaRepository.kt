package com.duoc.mobile_01_android.domain.repository

import com.duoc.mobile_01_android.domain.model.Mascota
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define el contrato para el repositorio de Mascotas.
 * Separación de responsabilidades: cada entidad tiene su propio repositorio.
 */
interface MascotaRepository {
    /**
     * Obtiene todas las mascotas como un Flow para reactividad.
     */
    fun getMascotas(): Flow<List<Mascota>>

    /**
     * Obtiene las mascotas de un cliente específico.
     * @param clienteId El ID del cliente
     */
    fun getMascotasPorCliente(clienteId: Int): Flow<List<Mascota>>

    /**
     * Agrega una nueva mascota.
     * @param mascota La mascota a agregar (sin ID, será asignado automáticamente)
     */
    suspend fun agregarMascota(mascota: Mascota)

    /**
     * Actualiza una mascota existente.
     * @param mascota La mascota con los datos actualizados
     */
    suspend fun editarMascota(mascota: Mascota)

    /**
     * Elimina una mascota por su ID.
     * @param id El ID de la mascota a eliminar
     */
    suspend fun eliminarMascota(id: Int)
}
