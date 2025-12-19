package com.duoc.mobile_01_android.data.repository

import com.duoc.mobile_01_android.data.datasource.InMemoryDataSource
import com.duoc.mobile_01_android.domain.model.Vacuna
import com.duoc.mobile_01_android.domain.repository.VacunaRepository
import com.duoc.mobile_01_android.util.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Implementación del repositorio de Vacunas.
 * Maneja la lógica de acceso a datos de vacunas y filtrado de vacunas próximas.
 */
class VacunaRepositoryImpl(
    private val dataSource: InMemoryDataSource = InMemoryDataSource
) : VacunaRepository {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun getVacunas(): Flow<List<Vacuna>> {
        return dataSource.vacunas
    }

    override fun getVacunasPorMascota(mascotaId: Int): Flow<List<Vacuna>> {
        return dataSource.vacunas.map { vacunas ->
            vacunas.filter { it.mascotaId == mascotaId }
        }
    }

    override fun getVacunasProximas(): Flow<List<Vacuna>> {
        return dataSource.vacunas.map { vacunas ->
            val hoy = Calendar.getInstance()
            val en30Dias = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, 30)
            }

            vacunas.filter { vacuna ->
                try {
                    val fechaProxima = dateFormat.parse(vacuna.proximaFecha)
                    fechaProxima?.let {
                        val calendario = Calendar.getInstance().apply { time = it }
                        // Incluir vacunas que vencen en los próximos 30 días
                        calendario.after(hoy) && calendario.before(en30Dias)
                    } ?: false
                } catch (e: Exception) {
                    false
                }
            }.sortedBy {
                try {
                    dateFormat.parse(it.proximaFecha)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    override suspend fun agregarVacuna(vacuna: Vacuna): Vacuna {
        delay(Constants.DELAY_AGREGAR)
        return dataSource.agregarVacuna(vacuna)
    }

    override suspend fun editarVacuna(vacuna: Vacuna) {
        delay(Constants.DELAY_EDITAR)
        dataSource.editarVacuna(vacuna)
    }

    override suspend fun eliminarVacuna(id: Int) {
        delay(Constants.DELAY_ELIMINAR)
        dataSource.eliminarVacuna(id)
    }
}
