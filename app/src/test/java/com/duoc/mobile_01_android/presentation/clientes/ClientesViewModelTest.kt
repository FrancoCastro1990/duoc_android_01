package com.duoc.mobile_01_android.presentation.clientes

import app.cash.turbine.test
import com.duoc.mobile_01_android.domain.model.Cliente
import com.duoc.mobile_01_android.domain.model.Mascota
import com.duoc.mobile_01_android.domain.repository.ClienteRepository
import com.duoc.mobile_01_android.domain.repository.MascotaRepository
import com.duoc.mobile_01_android.util.ValidationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Test unitario para ClientesViewModel.
 * Valida la integración ViewModel -> Repository y el manejo de estado de UI.
 *
 * Criterio 6: Pruebas que verifican la comunicación efectiva entre la capa de presentación
 * y el repositorio, validando respuesta apropiada a diferentes escenarios de uso.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ClientesViewModelTest {

    private lateinit var viewModel: ClientesViewModel
    private lateinit var fakeClienteRepository: FakeClienteRepository
    private lateinit var fakeMascotaRepository: FakeMascotaRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Configurar dispatcher para pruebas de coroutines
        Dispatchers.setMain(testDispatcher)

        // Inicializar repositorios fake
        fakeClienteRepository = FakeClienteRepository()
        fakeMascotaRepository = FakeMascotaRepository()

        // Crear ViewModel con repositorios fake
        viewModel = ClientesViewModel(fakeClienteRepository, fakeMascotaRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState inicial debe ser Loading`() = runTest {
        // Given/When: ViewModel recién creado
        val initialState = viewModel.uiState.value

        // Then: Estado inicial debe ser Loading
        assertTrue(initialState is ClientesUiState.Loading)
    }

    @Test
    fun `agregarCliente - debe agregar cliente al repositorio`() = runTest {
        // Given: ViewModel inicializado
        val nuevoCliente = Cliente(0, "Test Cliente", "test@email.com", "123456789")

        // When: Se agrega un cliente
        viewModel.agregarCliente(nuevoCliente)
        advanceUntilIdle()

        // Then: El cliente debe estar en el repositorio
        val clientes = fakeClienteRepository.clientesList.value
        assertEquals(1, clientes.size)
        assertEquals("Test Cliente", clientes.first().nombre)
    }

    @Test
    fun `agregarCliente - debe mostrar mensaje de carga apropiado`() = runTest {
        // Given: ViewModel inicializado
        val nuevoCliente = Cliente(0, "Test Cliente", "test@email.com", "123456789")

        // When: Se agrega un cliente
        viewModel.loadingMessage.test {
            assertEquals("", awaitItem()) // Estado inicial

            viewModel.agregarCliente(nuevoCliente)

            // Then: Debe mostrar el mensaje correcto
            assertEquals("Guardando cliente...", awaitItem())
        }
    }

    @Test
    fun `agregarCliente - debe llamar al repositorio correctamente`() = runTest {
        // Given: Un cliente nuevo
        val nuevoCliente = Cliente(0, "Juan Pérez", "juan@email.com", "987654321")

        // When: Se agrega el cliente
        viewModel.agregarCliente(nuevoCliente)
        advanceUntilIdle()

        // Then: El repositorio debe contener el cliente
        fakeClienteRepository.clientesList.test {
            val clientes = awaitItem()
            assertEquals(1, clientes.size)
            assertEquals("Juan Pérez", clientes.first().nombre)
            assertEquals("juan@email.com", clientes.first().email)
        }
    }

    @Test
    fun `eliminarCliente - debe actualizar isLoading y mensaje`() = runTest {
        // Given: Un cliente existente
        val cliente = Cliente(1, "Cliente a Eliminar", "eliminar@email.com", "111111111")
        fakeClienteRepository.agregarCliente(cliente)

        // When: Se elimina el cliente
        viewModel.loadingMessage.test {
            assertEquals("", awaitItem())

            viewModel.eliminarCliente(1)

            // Then: Debe mostrar mensaje de eliminación
            assertEquals("Eliminando cliente...", awaitItem())
        }
    }

    @Test
    fun `eliminarCliente - debe llamar al repositorio correctamente`() = runTest {
        // Given: Un cliente existente
        val cliente = Cliente(1, "Cliente a Eliminar", "eliminar@email.com", "111111111")
        fakeClienteRepository.agregarCliente(cliente)

        // When: Se elimina el cliente
        viewModel.eliminarCliente(1)
        advanceUntilIdle()

        // Then: El repositorio no debe contener el cliente
        fakeClienteRepository.clientesList.test {
            val clientes = awaitItem()
            assertTrue(clientes.isEmpty())
        }
    }

    @Test
    fun `editarCliente - debe actualizar cliente en repositorio`() = runTest {
        // Given: Un cliente existente
        val clienteOriginal = Cliente(1, "Original", "original@email.com", "111111111")
        fakeClienteRepository.agregarCliente(clienteOriginal)
        advanceUntilIdle()

        // When: Se edita el cliente
        val clienteEditado = fakeClienteRepository.clientesList.value.first().copy(nombre = "Editado")
        viewModel.editarCliente(clienteEditado)
        advanceUntilIdle()

        // Then: El cliente debe estar actualizado
        val clientes = fakeClienteRepository.clientesList.value
        assertEquals(1, clientes.size)
        assertEquals("Editado", clientes.first().nombre)
    }

    @Test
    fun `clientes y mascotas se agregan correctamente`() = runTest {
        // Given: Un cliente con mascotas
        val cliente = Cliente(1, "Cliente con Mascotas", "cliente@email.com", "123456789")
        val mascota1 = Mascota(1, nombre = "Firulais", especie = "Perro", edad = 3, peso = 15.5, clienteId = 1)
        val mascota2 = Mascota(2, nombre = "Michi", especie = "Gato", edad = 2, peso = 4.2, clienteId = 1)

        // When: Se agregan al repositorio
        fakeClienteRepository.agregarCliente(cliente)
        fakeMascotaRepository.agregarMascota(mascota1)
        fakeMascotaRepository.agregarMascota(mascota2)
        advanceUntilIdle()

        // Then: Deben estar en los repositorios
        assertEquals(1, fakeClienteRepository.clientesList.value.size)
        assertEquals(2, fakeMascotaRepository.mascotasList.value.size)
        assertEquals("Cliente con Mascotas", fakeClienteRepository.clientesList.value.first().nombre)
    }

    @Test
    fun `validarEmail - debe validar correctamente emails válidos`() {
        // Given/When/Then: Emails válidos
        assertTrue(ValidationUtils.validarEmail("correcto@gmail.com"))
        assertTrue(ValidationUtils.validarEmail("usuario.nombre@dominio.com"))
        assertTrue(ValidationUtils.validarEmail("test123@empresa.cl"))
    }

    @Test
    fun `validarEmail - debe rechazar emails inválidos`() {
        // Given/When/Then: Emails inválidos
        assertFalse(ValidationUtils.validarEmail("sin-arroba.com"))
        assertFalse(ValidationUtils.validarEmail("@sinusuario.com"))
        assertFalse(ValidationUtils.validarEmail("usuario@"))
        assertFalse(ValidationUtils.validarEmail(""))
        assertFalse(ValidationUtils.validarEmail("espacios en medio@email.com"))
    }

    @Test
    fun `cliente sin mascotas se puede agregar correctamente`() = runTest {
        // Given: Un cliente sin mascotas
        val cliente = Cliente(1, "Cliente Solo", "solo@email.com", "987654321")

        // When: Se agrega el cliente
        fakeClienteRepository.agregarCliente(cliente)
        advanceUntilIdle()

        // Then: El cliente debe estar en el repositorio
        val clientes = fakeClienteRepository.clientesList.value
        assertEquals(1, clientes.size)
        assertEquals("Cliente Solo", clientes.first().nombre)

        // Y no debe haber mascotas
        val mascotas = fakeMascotaRepository.mascotasList.value
        assertTrue(mascotas.isEmpty())
    }
}

/**
 * Implementación fake de ClienteRepository para testing.
 * Simula el comportamiento del repositorio sin dependencias externas.
 */
class FakeClienteRepository : ClienteRepository {
    private val _clientesFlow = MutableStateFlow<List<Cliente>>(emptyList())

    // Propiedad para acceso directo en tests
    val clientesList: MutableStateFlow<List<Cliente>> get() = _clientesFlow

    override fun getClientes(): MutableStateFlow<List<Cliente>> = _clientesFlow

    override suspend fun agregarCliente(cliente: Cliente) {
        val nuevoCliente = cliente.copy(id = (_clientesFlow.value.maxOfOrNull { it.id } ?: 0) + 1)
        _clientesFlow.value = _clientesFlow.value + nuevoCliente
    }

    override suspend fun editarCliente(cliente: Cliente) {
        _clientesFlow.value = _clientesFlow.value.map {
            if (it.id == cliente.id) cliente else it
        }
    }

    override suspend fun eliminarCliente(id: Int) {
        _clientesFlow.value = _clientesFlow.value.filter { it.id != id }
    }
}

/**
 * Implementación fake de MascotaRepository para testing.
 */
class FakeMascotaRepository : MascotaRepository {
    private val _mascotasFlow = MutableStateFlow<List<Mascota>>(emptyList())

    // Propiedad para acceso directo en tests
    val mascotasList: MutableStateFlow<List<Mascota>> get() = _mascotasFlow

    override fun getMascotas(): MutableStateFlow<List<Mascota>> = _mascotasFlow

    override fun getMascotasPorCliente(clienteId: Int) = MutableStateFlow(
        _mascotasFlow.value.filter { it.clienteId == clienteId }
    )

    override suspend fun agregarMascota(mascota: Mascota) {
        val nuevaMascota = mascota.copy(id = (_mascotasFlow.value.maxOfOrNull { it.id } ?: 0) + 1)
        _mascotasFlow.value = _mascotasFlow.value + nuevaMascota
    }

    override suspend fun editarMascota(mascota: Mascota) {
        _mascotasFlow.value = _mascotasFlow.value.map {
            if (it.id == mascota.id) mascota else it
        }
    }

    override suspend fun eliminarMascota(id: Int) {
        _mascotasFlow.value = _mascotasFlow.value.filter { it.id != id }
    }
}
