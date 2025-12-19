package com.duoc.mobile_01_android.domain.repository

import com.duoc.mobile_01_android.domain.model.Cita
import com.duoc.mobile_01_android.domain.model.EstadoCita
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define el contrato para el repositorio de Citas.
 * Principio de Inversión de Dependencias (D de SOLID): Los ViewModels
 * dependen de esta abstracción, no de la implementación concreta.
 */
interface CitaRepository {
    /**
     * Obtiene todas las citas como un Flow para reactividad.
     */
    fun getCitas(): Flow<List<Cita>>

    /**
     * Obtiene las citas de una mascota específica.
     * @param mascotaId El ID de la mascota
     */
    fun getCitasPorMascota(mascotaId: Int): Flow<List<Cita>>

    /**
     * Obtiene las citas de una fecha específica.
     * @param fecha La fecha en formato "dd/MM/yyyy"
     */
    fun getCitasPorFecha(fecha: String): Flow<List<Cita>>

    /**
     * Obtiene solo las citas pendientes.
     */
    fun getCitasPendientes(): Flow<List<Cita>>

    /**
     * Agrega una nueva cita.
     * @param cita La cita a agregar (sin ID, será asignado automáticamente)
     * @return La cita creada con su ID asignado
     */
    suspend fun agregarCita(cita: Cita): Cita

    /**
     * Actualiza una cita existente.
     * @param cita La cita con los datos actualizados
     */
    suspend fun editarCita(cita: Cita)

    /**
     * Elimina una cita por su ID.
     * @param id El ID de la cita a eliminar
     */
    suspend fun eliminarCita(id: Int)

    /**
     * Cambia el estado de una cita.
     * @param id El ID de la cita
     * @param estado El nuevo estado
     */
    suspend fun cambiarEstado(id: Int, estado: EstadoCita)
}
