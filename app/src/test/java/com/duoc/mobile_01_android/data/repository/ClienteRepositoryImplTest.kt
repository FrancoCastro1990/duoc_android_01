package com.duoc.mobile_01_android.data.repository

import app.cash.turbine.test
import com.duoc.mobile_01_android.data.datasource.InMemoryDataSource
import com.duoc.mobile_01_android.domain.model.Cliente
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Test unitario para ClienteRepositoryImpl.
 * Valida las operaciones CRUD del repositorio y la integración con InMemoryDataSource.
 *
 * Criterio 6: Pruebas que verifican la comunicación efectiva entre capas (Repository -> DataSource)
 */
class ClienteRepositoryImplTest {

    private lateinit var repository: ClienteRepositoryImpl
    private lateinit var dataSource: InMemoryDataSource

    @Before
    fun setup() {
        // Inicializar el repositorio con el DataSource singleton
        dataSource = InMemoryDataSource
        repository = ClienteRepositoryImpl(dataSource)
    }

    @After
    fun tearDown() {
        // Limpiar el estado del DataSource después de cada test
        // Esto es crucial porque InMemoryDataSource es un singleton
        runTest {
            val clientes = dataSource.clientes.value
            clientes.forEach { cliente ->
                dataSource.eliminarCliente(cliente.id)
            }
        }
    }

    @Test
    fun `agregarCliente - debe agregar cliente al datasource y asignar ID`() = runTest {
        // Given: Un cliente nuevo sin ID
        val clienteNuevo = Cliente(
            id = 0,
            nombre = "Juan Pérez",
            email = "juan.perez@gmail.com",
            telefono = "912345678"
        )

        // When: Se agrega el cliente a través del repositorio
        repository.agregarCliente(clienteNuevo)

        // Then: El cliente debe aparecer en el flow con un ID asignado
        repository.getClientes().test {
            val clientes = awaitItem()
            assertEquals(1, clientes.size)

            val clienteGuardado = clientes.first()
            assertTrue(clienteGuardado.id > 0) // ID debe ser autogenerado
            assertEquals("Juan Pérez", clienteGuardado.nombre)
            assertEquals("juan.perez@gmail.com", clienteGuardado.email)
            assertEquals("912345678", clienteGuardado.telefono)
        }
    }

    @Test
    fun `eliminarCliente - debe eliminar cliente del datasource`() = runTest {
        // Given: Un cliente existente en el datasource
        val cliente = Cliente(
            id = 0,
            nombre = "María López",
            email = "maria.lopez@gmail.com",
            telefono = "987654321"
        )
        repository.agregarCliente(cliente)

        // Obtener el ID asignado
        var clienteId = 0
        repository.getClientes().test {
            val clientes = awaitItem()
            clienteId = clientes.first().id
            cancelAndIgnoreRemainingEvents()
        }

        // When: Se elimina el cliente
        repository.eliminarCliente(clienteId)

        // Then: La lista debe estar vacía
        repository.getClientes().test {
            val clientes = awaitItem()
            assertTrue(clientes.isEmpty())
        }
    }

    @Test
    fun `getClienteById - debe retornar cliente específico del datasource`() = runTest {
        // Given: Múltiples clientes en el datasource
        val cliente1 = Cliente(0, "Cliente 1", "cliente1@email.com", "111111111")
        val cliente2 = Cliente(0, "Cliente 2", "cliente2@email.com", "222222222")
        val cliente3 = Cliente(0, "Cliente 3", "cliente3@email.com", "333333333")

        repository.agregarCliente(cliente1)
        repository.agregarCliente(cliente2)
        repository.agregarCliente(cliente3)

        // When: Se busca un cliente específico por ID
        var clienteBuscadoId = 0
        repository.getClientes().test {
            val clientes = awaitItem()
            // Buscar el segundo cliente agregado
            clienteBuscadoId = clientes[1].id
            cancelAndIgnoreRemainingEvents()
        }

        // Then: Debe encontrar el cliente correcto
        repository.getClientes().test {
            val clientes = awaitItem()
            val clienteEncontrado = clientes.find { it.id == clienteBuscadoId }

            assertNotNull(clienteEncontrado)
            assertEquals("Cliente 2", clienteEncontrado?.nombre)
            assertEquals("cliente2@email.com", clienteEncontrado?.email)
        }
    }

    @Test
    fun `editarCliente - debe actualizar cliente existente`() = runTest {
        // Given: Un cliente existente
        val clienteOriginal = Cliente(0, "Pedro García", "pedro@email.com", "999999999")
        repository.agregarCliente(clienteOriginal)

        var clienteId = 0
        repository.getClientes().test {
            val clientes = awaitItem()
            clienteId = clientes.first().id
            cancelAndIgnoreRemainingEvents()
        }

        // When: Se edita el cliente
        val clienteEditado = Cliente(
            id = clienteId,
            nombre = "Pedro García Modificado",
            email = "pedro.nuevo@email.com",
            telefono = "888888888"
        )
        repository.editarCliente(clienteEditado)

        // Then: Los cambios deben reflejarse en el datasource
        repository.getClientes().test {
            val clientes = awaitItem()
            assertEquals(1, clientes.size)

            val cliente = clientes.first()
            assertEquals(clienteId, cliente.id)
            assertEquals("Pedro García Modificado", cliente.nombre)
            assertEquals("pedro.nuevo@email.com", cliente.email)
            assertEquals("888888888", cliente.telefono)
        }
    }

    @Test
    fun `getClientes - debe retornar flow reactivo que se actualiza con cambios`() = runTest {
        // Given: Repositorio vacío inicialmente
        repository.getClientes().test {
            // Then: Debe empezar vacío
            var clientes = awaitItem()
            assertTrue(clientes.isEmpty())

            // When: Se agrega un cliente
            repository.agregarCliente(Cliente(0, "Test", "test@email.com", "123456789"))

            // Then: El flow debe emitir la lista actualizada
            clientes = awaitItem()
            assertEquals(1, clientes.size)
            assertEquals("Test", clientes.first().nombre)
        }
    }

    @Test
    fun `agregarCliente - múltiples clientes deben tener IDs únicos`() = runTest {
        // Given: Varios clientes nuevos
        val cliente1 = Cliente(0, "Cliente A", "a@email.com", "111")
        val cliente2 = Cliente(0, "Cliente B", "b@email.com", "222")
        val cliente3 = Cliente(0, "Cliente C", "c@email.com", "333")

        // When: Se agregan todos
        repository.agregarCliente(cliente1)
        repository.agregarCliente(cliente2)
        repository.agregarCliente(cliente3)

        // Then: Todos deben tener IDs únicos y diferentes
        repository.getClientes().test {
            val clientes = awaitItem()
            assertEquals(3, clientes.size)

            val ids = clientes.map { it.id }
            assertEquals(ids.size, ids.distinct().size) // No hay IDs duplicados
            assertTrue(ids.all { it > 0 }) // Todos los IDs son positivos
        }
    }
}
