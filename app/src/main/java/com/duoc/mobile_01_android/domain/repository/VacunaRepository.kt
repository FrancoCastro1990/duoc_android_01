package com.duoc.mobile_01_android.domain.repository

import com.duoc.mobile_01_android.domain.model.Vacuna
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define el contrato para el repositorio de Vacunas.
 * Separación de responsabilidades: cada entidad tiene su propio repositorio.
 */
interface VacunaRepository {
    /**
     * Obtiene todas las vacunas como un Flow para reactividad.
     */
    fun getVacunas(): Flow<List<Vacuna>>

    /**
     * Obtiene las vacunas de una mascota específica.
     * @param mascotaId El ID de la mascota
     */
    fun getVacunasPorMascota(mascotaId: Int): Flow<List<Vacuna>>

    /**
     * Obtiene las vacunas próximas a vencer (próximos 30 días).
     * Útil para mostrar alertas y recordatorios.
     */
    fun getVacunasProximas(): Flow<List<Vacuna>>

    /**
     * Agrega una nueva vacuna.
     * @param vacuna La vacuna a agregar (sin ID, será asignado automáticamente)
     * @return La vacuna agregada con su ID asignado
     */
    suspend fun agregarVacuna(vacuna: Vacuna): Vacuna

    /**
     * Actualiza una vacuna existente.
     * @param vacuna La vacuna con los datos actualizados
     */
    suspend fun editarVacuna(vacuna: Vacuna)

    /**
     * Elimina una vacuna por su ID.
     * @param id El ID de la vacuna a eliminar
     */
    suspend fun eliminarVacuna(id: Int)
}
