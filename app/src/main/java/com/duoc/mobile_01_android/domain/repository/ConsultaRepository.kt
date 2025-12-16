package com.duoc.mobile_01_android.domain.repository

import com.duoc.mobile_01_android.domain.model.Cliente
import com.duoc.mobile_01_android.domain.model.Consulta
import com.duoc.mobile_01_android.domain.model.Mascota
import com.duoc.mobile_01_android.domain.model.Medicamento
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define el contrato para el repositorio de Consultas.
 * Maneja la lógica de acceso a datos de las consultas veterinarias.
 */
interface ConsultaRepository {
    /**
     * Obtiene todas las consultas como un Flow para reactividad.
     */
    fun getConsultas(): Flow<List<Consulta>>

    /**
     * Crea una nueva consulta veterinaria.
     * @param cliente El cliente asociado a la consulta
     * @param mascota La mascota que recibe la consulta
     * @param medicamentos Lista de medicamentos aplicados
     * @param descripcion Descripción de la consulta
     */
    suspend fun crearConsulta(
        cliente: Cliente,
        mascota: Mascota,
        medicamentos: List<Medicamento>,
        descripcion: String
    )

    /**
     * Calcula el total de ingresos de todas las consultas.
     * @return El total de ingresos
     */
    fun calcularIngresosTotales(): Flow<Double>
}
