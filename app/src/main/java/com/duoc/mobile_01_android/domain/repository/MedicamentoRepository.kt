package com.duoc.mobile_01_android.domain.repository

import com.duoc.mobile_01_android.domain.model.Medicamento
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define el contrato para el repositorio de Medicamentos.
 * En esta versión, los medicamentos son de solo lectura (catálogo predefinido).
 */
interface MedicamentoRepository {
    /**
     * Obtiene todos los medicamentos disponibles como un Flow.
     */
    fun getMedicamentos(): Flow<List<Medicamento>>

    /**
     * Obtiene un medicamento por su ID.
     * @param id El ID del medicamento
     * @return El medicamento o null si no existe
     */
    suspend fun getMedicamentoById(id: Int): Medicamento?
}
