package com.duoc.mobile_01_android

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duoc.mobile_01_android.data.datasource.InMemoryDataSource
import com.duoc.mobile_01_android.data.repository.ClienteRepositoryImpl
import com.duoc.mobile_01_android.data.repository.MascotaRepositoryImpl
import com.duoc.mobile_01_android.domain.model.Cliente
import com.duoc.mobile_01_android.domain.model.Mascota
import com.duoc.mobile_01_android.presentation.clientes.ClientesUiState
import com.duoc.mobile_01_android.presentation.clientes.ClientesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test de Integración MVVM completo.
 * Valida el flujo completo: UI (ViewModel) -> Repository -> DataSource
 *
 * Criterio 6: Pruebas que verifican exhaustivamente la integración entre componentes
 * y el funcionamiento correcto de la aplicación, validando la comunicación efectiva
 * entre todas las capas (Presentation -> Domain -> Data) y la respuesta apropiada
 * a diferentes escenarios de uso.
 *
 * Este test usa componentes reales (no mocks) para verificar la integración real.
 */
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class MvvmIntegrationTest {

    private lateinit var viewModel: ClientesViewModel
    private lateinit var clienteRepository: ClienteRepositoryImpl
    private lateinit var mascotaRepository: MascotaRepositoryImpl
    private lateinit var dataSource: InMemoryDataSource

    @Before
    fun setup() {
        // Usar componentes REALES para test de integración
        dataSource = InMemoryDataSource
        clienteRepository = ClienteRepositoryImpl(dataSource)
        mascotaRepository = MascotaRepositoryImpl(dataSource)
        viewModel = ClientesViewModel(clienteRepository, mascotaRepository)
    }

    @After
    fun tearDown() = runTest {
        // Limpiar el estado del DataSource después de cada test
        val clientes = dataSource.clientes.value
        clientes.forEach { cliente ->
            dataSource.eliminarCliente(cliente.id)
        }
    }

    @Test
    fun testFlujoCrudCompletoClienteIntegracionMVVM() = runTest {
        // ESCENARIO 1: Agregar Cliente
        // Given: Un nuevo cliente
        val nuevoCliente = Cliente(
            id = 0,
            nombre = "Integration Test Cliente",
            email = "integration@test.com",
            telefono = "912345678"
        )

        // When: Se agrega a través del ViewModel
        viewModel.agregarCliente(nuevoCliente)

        // Wait para que se complete la operación asíncrona
        delay(1500) // DELAY_AGREGAR + margen

        // Then: Debe propagarse por todas las capas
        // 1. ViewModel debe tener isLoading = false
        assertFalse(viewModel.isLoading.value)

        // 2. UiState debe contener el cliente
        val uiState = viewModel.uiState.value
        assertTrue(uiState is ClientesUiState.Success)
        val clientesEnUi = (uiState as ClientesUiState.Success).clientes
        assertEquals(1, clientesEnUi.size)
        assertEquals("Integration Test Cliente", clientesEnUi.first().cliente.nombre)

        // 3. Repository debe tener el cliente
        val clientesEnRepo = clienteRepository.getClientes().first()
        assertEquals(1, clientesEnRepo.size)

        // 4. DataSource debe tener el cliente
        val clientesEnDataSource = dataSource.clientes.value
        assertEquals(1, clientesEnDataSource.size)

        // ESCENARIO 2: Editar Cliente
        // Given: El cliente agregado previamente
        val clienteGuardado = clientesEnDataSource.first()
        val clienteEditado = clienteGuardado.copy(
            nombre = "Cliente Editado",
            email = "editado@test.com"
        )

        // When: Se edita a través del ViewModel
        viewModel.editarCliente(clienteEditado)
        delay(1000) // DELAY_EDITAR + margen

        // Then: Los cambios deben reflejarse en todas las capas
        val uiStateEditado = viewModel.uiState.value
        assertTrue(uiStateEditado is ClientesUiState.Success)
        val clienteEnUiEditado = (uiStateEditado as ClientesUiState.Success).clientes.first().cliente
        assertEquals("Cliente Editado", clienteEnUiEditado.nombre)
        assertEquals("editado@test.com", clienteEnUiEditado.email)

        // ESCENARIO 3: Eliminar Cliente
        // When: Se elimina el cliente
        viewModel.eliminarCliente(clienteGuardado.id)
        delay(800) // DELAY_ELIMINAR + margen

        // Then: Todas las capas deben estar vacías
        val uiStateFinal = viewModel.uiState.value
        assertTrue(uiStateFinal is ClientesUiState.Success)
        val clientesFinales = (uiStateFinal as ClientesUiState.Success).clientes
        assertTrue(clientesFinales.isEmpty())

        val clientesEnRepoFinal = clienteRepository.getClientes().first()
        assertTrue(clientesEnRepoFinal.isEmpty())

        val clientesEnDataSourceFinal = dataSource.clientes.value
        assertTrue(clientesEnDataSourceFinal.isEmpty())
    }

    @Test
    fun testIntegracionClienteConMascotasMVVM() = runTest {
        // ESCENARIO: Cliente con múltiples mascotas
        // Given: Un cliente y varias mascotas
        val cliente = Cliente(
            id = 0,
            nombre = "Dueño de Mascotas",
            email = "dueno@mascotas.com",
            telefono = "987654321"
        )

        // When: Se agrega el cliente
        viewModel.agregarCliente(cliente)
        delay(1500)

        // Obtener el ID asignado
        val clienteGuardado = dataSource.clientes.value.first()

        // When: Se agregan mascotas asociadas al cliente
        val mascota1 = Mascota(
            id = 0,
            nombre = "Rex",
            especie = "Perro",
            edad = 5,
            peso = 20.0,
            clienteId = clienteGuardado.id
        )

        val mascota2 = Mascota(
            id = 0,
            nombre = "Miau",
            especie = "Gato",
            edad = 3,
            peso = 5.5,
            clienteId = clienteGuardado.id
        )

        val mascota3 = Mascota(
            id = 0,
            nombre = "Pelusa",
            especie = "Conejo",
            edad = 2,
            peso = 3.0,
            clienteId = clienteGuardado.id
        )

        dataSource.agregarMascota(mascota1)
        dataSource.agregarMascota(mascota2)
        dataSource.agregarMascota(mascota3)

        delay(500) // Esperar propagación de cambios

        // Then: El ViewModel debe combinar correctamente cliente con mascotas
        val uiState = viewModel.uiState.value
        assertTrue(uiState is ClientesUiState.Success)

        val clienteConMascotas = (uiState as ClientesUiState.Success).clientes.first()
        assertEquals("Dueño de Mascotas", clienteConMascotas.cliente.nombre)
        assertEquals(3, clienteConMascotas.mascotasCount)

        // Verificar en todas las capas
        val mascotasEnDataSource = dataSource.mascotas.value.filter {
            it.clienteId == clienteGuardado.id
        }
        assertEquals(3, mascotasEnDataSource.size)
    }

    @Test
    fun testEliminacionCascadaClienteMascotasMVVM() = runTest {
        // ESCENARIO: Eliminar cliente debe eliminar sus mascotas (cascada)
        // Given: Un cliente con mascotas
        val cliente = Cliente(0, "Cliente Cascada", "cascada@test.com", "111222333")
        viewModel.agregarCliente(cliente)
        delay(1500)

        val clienteId = dataSource.clientes.value.first().id

        val mascota = Mascota(
            id = 0,
            nombre = "Mascota Cascada",
            especie = "Perro",
            edad = 4,
            peso = 15.0,
            clienteId = clienteId
        )
        dataSource.agregarMascota(mascota)
        delay(500)

        // Verificar que la mascota existe
        val mascotasAntes = dataSource.mascotas.value
        assertEquals(1, mascotasAntes.size)

        // When: Se elimina el cliente
        viewModel.eliminarCliente(clienteId)
        delay(800)

        // Then: La mascota también debe ser eliminada (cascada)
        val mascotasDespues = dataSource.mascotas.value
        assertTrue(mascotasDespues.isEmpty())

        val clientesDespues = dataSource.clientes.value
        assertTrue(clientesDespues.isEmpty())
    }

    @Test
    fun testComunicacionReactivaEntreCapasMVVM() = runTest {
        // ESCENARIO: Verificar reactividad del Flow entre capas
        // Given: Estado inicial vacío
        var uiState = viewModel.uiState.value
        assertTrue(uiState is ClientesUiState.Loading || uiState is ClientesUiState.Success)

        // When: Se agrega un cliente directamente al DataSource (simula cambio externo)
        val cliente1 = Cliente(0, "Cliente 1", "cliente1@test.com", "111111111")
        dataSource.agregarCliente(cliente1)

        delay(500) // Esperar propagación reactiva

        // Then: El ViewModel debe reaccionar automáticamente
        uiState = viewModel.uiState.value
        assertTrue(uiState is ClientesUiState.Success)
        assertEquals(1, (uiState as ClientesUiState.Success).clientes.size)

        // When: Se agrega otro cliente
        val cliente2 = Cliente(0, "Cliente 2", "cliente2@test.com", "222222222")
        dataSource.agregarCliente(cliente2)

        delay(500)

        // Then: El ViewModel debe actualizar automáticamente
        uiState = viewModel.uiState.value
        assertTrue(uiState is ClientesUiState.Success)
        assertEquals(2, (uiState as ClientesUiState.Success).clientes.size)
    }

    @Test
    fun testEstadosLoadingViewModelIntegracion() = runTest {
        // ESCENARIO: Verificar estados de loading durante operaciones
        // Given: Estado inicial
        assertFalse(viewModel.isLoading.value)
        assertEquals("", viewModel.loadingMessage.value)

        // When: Se inicia una operación de agregar
        val cliente = Cliente(0, "Test Loading", "loading@test.com", "999999999")
        viewModel.agregarCliente(cliente)

        // Then: Inmediatamente debe mostrar loading
        assertTrue(viewModel.isLoading.value)
        assertEquals("Guardando cliente...", viewModel.loadingMessage.value)

        // Wait para que complete
        delay(1500)

        // Then: Loading debe volver a false
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun testMultiplesOperacionesSecuencialesMVVM() = runTest {
        // ESCENARIO: Múltiples operaciones CRUD secuenciales
        // Given: Lista vacía
        val clientesInicial = dataSource.clientes.value
        assertTrue(clientesInicial.isEmpty())

        // When: Se agregan múltiples clientes
        viewModel.agregarCliente(Cliente(0, "Cliente A", "a@test.com", "111"))
        delay(1500)
        viewModel.agregarCliente(Cliente(0, "Cliente B", "b@test.com", "222"))
        delay(1500)
        viewModel.agregarCliente(Cliente(0, "Cliente C", "c@test.com", "333"))
        delay(1500)

        // Then: Todos deben estar presentes
        val uiState = viewModel.uiState.value
        assertTrue(uiState is ClientesUiState.Success)
        assertEquals(3, (uiState as ClientesUiState.Success).clientes.size)

        // When: Se elimina uno
        val idParaEliminar = dataSource.clientes.value[1].id
        viewModel.eliminarCliente(idParaEliminar)
        delay(800)

        // Then: Deben quedar 2
        val uiStateFinal = viewModel.uiState.value
        assertTrue(uiStateFinal is ClientesUiState.Success)
        assertEquals(2, (uiStateFinal as ClientesUiState.Success).clientes.size)
    }
}
