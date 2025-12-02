package com.duoc.mobile_01_android.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.mobile_01_android.model.Cliente
import com.duoc.mobile_01_android.model.Consulta
import com.duoc.mobile_01_android.model.Mascota
import com.duoc.mobile_01_android.model.Medicamento
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainViewModel : ViewModel() {

    companion object {
        const val COSTO_BASE_CONSULTA = 30000.0
        const val DESCUENTO_MULTIPLES_MASCOTAS = 0.15
    }

    // Estados de carga
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _loadingMessage = mutableStateOf("")
    val loadingMessage: State<String> = _loadingMessage

    // Datos en memoria
    private val _clientes = mutableStateListOf<Cliente>()
    val clientes: List<Cliente> get() = _clientes

    private val _mascotas = mutableStateListOf<Mascota>()
    val mascotas: List<Mascota> get() = _mascotas

    private val _consultas = mutableStateListOf<Consulta>()
    val consultas: List<Consulta> get() = _consultas

    private val _medicamentos = mutableStateListOf<Medicamento>()
    val medicamentos: List<Medicamento> get() = _medicamentos

    // IDs autoincrementales
    private var nextClienteId = 1
    private var nextMascotaId = 1
    private var nextConsultaId = 1

    init {
        // Cargar medicamentos predefinidos con descuentos promocionales
        _medicamentos.addAll(
            listOf(
                Medicamento(1, "Amoxicilina", "500mg", 15000.0, 50, 0.20),
                Medicamento(2, "Ivermectina", "10ml", 12000.0, 30, 0.15),
                Medicamento(3, "Vacuna Triple", "1 dosis", 25000.0, 20, 0.10),
                Medicamento(4, "Antiinflamatorio", "100mg", 8000.0, 40, 0.0),
                Medicamento(5, "Desparasitante", "5ml", 10000.0, 35, 0.15)
            )
        )
    }

    // ==================== CRUD Clientes ====================

    fun agregarCliente(cliente: Cliente) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Guardando cliente..."
            delay(1000)
            val nuevoCliente = cliente.copy(id = nextClienteId++)
            _clientes.add(nuevoCliente)
            _isLoading.value = false
        }
    }

    fun editarCliente(cliente: Cliente) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Actualizando cliente..."
            delay(800)
            val index = _clientes.indexOfFirst { it.id == cliente.id }
            if (index != -1) {
                _clientes[index] = cliente
            }
            _isLoading.value = false
        }
    }

    fun eliminarCliente(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Eliminando cliente..."
            delay(500)
            _clientes.removeAll { it.id == id }
            // También eliminar mascotas asociadas
            _mascotas.removeAll { it.clienteId == id }
            _isLoading.value = false
        }
    }

    // ==================== CRUD Mascotas ====================

    fun agregarMascota(mascota: Mascota) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Guardando mascota..."
            delay(1000)
            val nuevaMascota = mascota.copy(id = nextMascotaId++)
            _mascotas.add(nuevaMascota)
            _isLoading.value = false
        }
    }

    fun editarMascota(mascota: Mascota) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Actualizando mascota..."
            delay(800)
            val index = _mascotas.indexOfFirst { it.id == mascota.id }
            if (index != -1) {
                _mascotas[index] = mascota
            }
            _isLoading.value = false
        }
    }

    fun eliminarMascota(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Eliminando mascota..."
            delay(500)
            _mascotas.removeAll { it.id == id }
            _isLoading.value = false
        }
    }

    fun getMascotasPorCliente(clienteId: Int): List<Mascota> {
        return _mascotas.filter { it.clienteId == clienteId }
    }

    // ==================== CRUD Consultas ====================

    fun crearConsulta(
        cliente: Cliente,
        mascota: Mascota,
        medicamentosSeleccionados: List<Medicamento>,
        descripcion: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Creando consulta..."
            delay(1500)

            val total = calcularTotalConsulta(medicamentosSeleccionados)
            val fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))

            val consulta = Consulta(
                id = nextConsultaId++,
                cliente = cliente,
                mascota = mascota,
                medicamentos = medicamentosSeleccionados,
                descripcion = descripcion,
                fecha = fecha,
                total = total
            )

            _consultas.add(consulta)
            _isLoading.value = false
        }
    }

    fun calcularTotalConsulta(medicamentosSeleccionados: List<Medicamento>): Double {
        var total = COSTO_BASE_CONSULTA

        // Sumar precio de medicamentos con descuento aplicado
        medicamentosSeleccionados.forEach { med ->
            total += med.precioConDescuento()
        }

        return total
    }

    // ==================== Resumen ====================

    fun calcularIngresosTotales(): Double {
        return _consultas.sumOf { it.total }
    }

    // ==================== Validaciones ====================

    fun validarEmail(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    fun formatearTelefono(telefono: String): String {
        val soloNumeros = telefono.filter { it.isDigit() }
        return if (soloNumeros.length >= 9) {
            "+56 ${soloNumeros.take(9).chunked(3).joinToString(" ")}"
        } else {
            telefono
        }
    }

    // ==================== Loading Helper ====================

    fun withLoading(message: String, delayMs: Long = 1500L, action: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = message
            delay(delayMs)
            action()
            _isLoading.value = false
        }
    }
}
