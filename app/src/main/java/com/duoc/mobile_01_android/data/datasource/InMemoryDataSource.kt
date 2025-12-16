package com.duoc.mobile_01_android.data.datasource

import com.duoc.mobile_01_android.domain.model.Cliente
import com.duoc.mobile_01_android.domain.model.Consulta
import com.duoc.mobile_01_android.domain.model.Mascota
import com.duoc.mobile_01_android.domain.model.Medicamento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fuente de datos en memoria que actúa como base de datos local simulada.
 * Singleton pattern para garantizar una única instancia compartida.
 *
 * Usa StateFlow para proporcionar reactividad a los repositorios.
 * Cuando los datos cambian, todos los observadores son notificados automáticamente.
 */
object InMemoryDataSource {

    // IDs autoincrementales para cada entidad
    private var nextClienteId = 1
    private var nextMascotaId = 1
    private var nextConsultaId = 1

    // StateFlows privados (mutable) para actualizar los datos
    private val _clientes = MutableStateFlow<List<Cliente>>(emptyList())
    private val _mascotas = MutableStateFlow<List<Mascota>>(emptyList())
    private val _consultas = MutableStateFlow<List<Consulta>>(emptyList())
    private val _medicamentos = MutableStateFlow<List<Medicamento>>(emptyList())

    // StateFlows públicos (inmutables) para exposición
    val clientes: StateFlow<List<Cliente>> = _clientes.asStateFlow()
    val mascotas: StateFlow<List<Mascota>> = _mascotas.asStateFlow()
    val consultas: StateFlow<List<Consulta>> = _consultas.asStateFlow()
    val medicamentos: StateFlow<List<Medicamento>> = _medicamentos.asStateFlow()

    init {
        // Inicializar medicamentos predefinidos con descuentos promocionales
        _medicamentos.value = listOf(
            Medicamento(1, "Amoxicilina", "500mg", 15000.0, 50, 0.20),
            Medicamento(2, "Ivermectina", "10ml", 12000.0, 30, 0.15),
            Medicamento(3, "Vacuna Triple", "1 dosis", 25000.0, 20, 0.10),
            Medicamento(4, "Antiinflamatorio", "100mg", 8000.0, 40, 0.0),
            Medicamento(5, "Desparasitante", "5ml", 10000.0, 35, 0.15)
        )
    }

    // ==================== CRUD Clientes ====================

    fun agregarCliente(cliente: Cliente): Cliente {
        val nuevoCliente = cliente.copy(id = nextClienteId++)
        _clientes.value = _clientes.value + nuevoCliente
        return nuevoCliente
    }

    fun editarCliente(cliente: Cliente) {
        _clientes.value = _clientes.value.map {
            if (it.id == cliente.id) cliente else it
        }
    }

    fun eliminarCliente(id: Int) {
        _clientes.value = _clientes.value.filter { it.id != id }
        // Eliminar mascotas asociadas en cascada
        _mascotas.value = _mascotas.value.filter { it.clienteId != id }
    }

    // ==================== CRUD Mascotas ====================

    fun agregarMascota(mascota: Mascota): Mascota {
        val nuevaMascota = mascota.copy(id = nextMascotaId++)
        _mascotas.value = _mascotas.value + nuevaMascota
        return nuevaMascota
    }

    fun editarMascota(mascota: Mascota) {
        _mascotas.value = _mascotas.value.map {
            if (it.id == mascota.id) mascota else it
        }
    }

    fun eliminarMascota(id: Int) {
        _mascotas.value = _mascotas.value.filter { it.id != id }
    }

    // ==================== CRUD Consultas ====================

    fun agregarConsulta(consulta: Consulta): Consulta {
        val nuevaConsulta = consulta.copy(id = nextConsultaId++)
        _consultas.value = _consultas.value + nuevaConsulta
        return nuevaConsulta
    }

    // ==================== Queries ====================

    fun getMedicamentoById(id: Int): Medicamento? {
        return _medicamentos.value.find { it.id == id }
    }
}
