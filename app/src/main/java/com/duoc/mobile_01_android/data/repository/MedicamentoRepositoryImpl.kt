package com.duoc.mobile_01_android.data.repository

import com.duoc.mobile_01_android.data.datasource.InMemoryDataSource
import com.duoc.mobile_01_android.domain.model.Medicamento
import com.duoc.mobile_01_android.domain.repository.MedicamentoRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementación del repositorio de Medicamentos.
 * Los medicamentos son de solo lectura (catálogo predefinido).
 */
class MedicamentoRepositoryImpl(
    private val dataSource: InMemoryDataSource = InMemoryDataSource
) : MedicamentoRepository {

    override fun getMedicamentos(): Flow<List<Medicamento>> {
        return dataSource.medicamentos
    }

    override suspend fun getMedicamentoById(id: Int): Medicamento? {
        return dataSource.getMedicamentoById(id)
    }
}
