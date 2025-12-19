package com.duoc.mobile_01_android.data.repository

import com.duoc.mobile_01_android.data.datasource.InMemoryDataSource
import com.duoc.mobile_01_android.domain.model.Cliente
import com.duoc.mobile_01_android.domain.model.Consulta
import com.duoc.mobile_01_android.domain.model.Mascota
import com.duoc.mobile_01_android.domain.model.Medicamento
import com.duoc.mobile_01_android.domain.repository.ConsultaRepository
import com.duoc.mobile_01_android.util.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementación del repositorio de Consultas.
 * Maneja la lógica de negocio relacionada con consultas veterinarias.
 */
class ConsultaRepositoryImpl(
    private val dataSource: InMemoryDataSource = InMemoryDataSource
) : ConsultaRepository {

    override fun getConsultas(): Flow<List<Consulta>> {
        return dataSource.consultas
    }

    override suspend fun crearConsulta(
        cliente: Cliente,
        mascota: Mascota,
        medicamentos: List<Medicamento>,
        descripcion: String
    ) {
        delay(Constants.DELAY_CREAR_CONSULTA)

        val total = calcularTotalConsulta(medicamentos)
        val fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))

        val consulta = Consulta(
            id = 0, // El ID será asignado por el DataSource
            cliente = cliente,
            mascota = mascota,
            medicamentos = medicamentos,
            descripcion = descripcion,
            fecha = fecha,
            total = total
        )

        dataSource.agregarConsulta(consulta)
    }

    override fun calcularIngresosTotales(): Flow<Double> {
        return dataSource.consultas.map { consultas ->
            consultas.sumOf { it.total }
        }
    }

    override fun getConsultaById(id: Int): Flow<Consulta?> {
        return dataSource.consultas.map { consultas ->
            consultas.find { it.id == id }
        }
    }

    override suspend fun editarConsulta(consulta: Consulta) {
        delay(Constants.DELAY_EDITAR_CONSULTA)

        // Recalcular el total con los medicamentos actualizados
        val total = calcularTotalConsulta(consulta.medicamentos)
        val consultaActualizada = consulta.copy(total = total)

        dataSource.editarConsulta(consultaActualizada)
    }

    override suspend fun eliminarConsulta(id: Int) {
        delay(Constants.DELAY_ELIMINAR_CONSULTA)
        dataSource.eliminarConsulta(id)
    }

    /**
     * Calcula el total de una consulta sumando el costo base más los medicamentos con descuento.
     */
    private fun calcularTotalConsulta(medicamentos: List<Medicamento>): Double {
        var total = Constants.COSTO_BASE_CONSULTA

        medicamentos.forEach { medicamento ->
            total += medicamento.precioConDescuento()
        }

        return total
    }
}
