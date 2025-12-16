package com.duoc.mobile_01_android.provider

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.runner.RunWith

/**
 * Test instrumentado para VeterinariaProvider.
 *
 * Valida que el Content Provider funcione correctamente para:
 * - Consultas de mascotas (todas y por ID)
 * - Consultas de consultas veterinarias (todas y por ID)
 * - Operaciones de escritura (deben fallar, es read-only)
 *
 * Estos tests requieren ejecutarse en un dispositivo/emulador Android
 * porque necesitan acceso al ContentResolver y contexto real de la aplicación.
 *
 * @see VeterinariaProvider
 */
@RunWith(AndroidJUnit4::class)
class VeterinariaProviderTest {

    private lateinit var contentResolver: ContentResolver

    /**
     * URI base del Content Provider.
     */
    private val baseUri = "content://com.duoc.mobile_01_android.provider"

    /**
     * URI para acceder a todas las mascotas.
     */
    private val mascotasUri = Uri.parse("$baseUri/mascotas")

    /**
     * URI para acceder a todas las consultas.
     */
    private val consultasUri = Uri.parse("$baseUri/consultas")

    /**
     * Configuración inicial antes de cada test.
     *
     * Obtiene el ContentResolver del contexto de la aplicación bajo test.
     */
    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        contentResolver = context.contentResolver
    }

    /**
     * Verifica que la consulta de todas las mascotas retorne un Cursor válido.
     *
     * El Cursor puede estar vacío si no hay datos, pero nunca debe ser null.
     * Valida también que las columnas esperadas estén presentes.
     */
    @Test
    fun queryMascotas_returnsCursor() {
        val cursor = contentResolver.query(
            mascotasUri,
            null,  // Todas las columnas
            null,  // Sin selección
            null,  // Sin argumentos de selección
            null   // Sin orden específico
        )

        // El cursor no debe ser null
        assertNotNull("El cursor de mascotas no debe ser null", cursor)

        cursor?.use {
            // Verificar que tiene las columnas esperadas
            val columnNames = it.columnNames
            assertTrue("Debe contener columna 'id'", columnNames.contains("id"))
            assertTrue("Debe contener columna 'nombre'", columnNames.contains("nombre"))
            assertTrue("Debe contener columna 'especie'", columnNames.contains("especie"))
            assertTrue("Debe contener columna 'edad'", columnNames.contains("edad"))
            assertTrue("Debe contener columna 'peso'", columnNames.contains("peso"))
            assertTrue("Debe contener columna 'clienteId'", columnNames.contains("clienteId"))

            // El cursor puede tener 0 o más filas, dependiendo de los datos en InMemoryDataSource
            assertTrue("El conteo de filas debe ser >= 0", it.count >= 0)
        }
    }

    /**
     * Verifica que la consulta de todas las consultas retorne un Cursor válido.
     *
     * Similar al test de mascotas, valida estructura del cursor.
     */
    @Test
    fun queryConsultas_returnsCursor() {
        val cursor = contentResolver.query(
            consultasUri,
            null,
            null,
            null,
            null
        )

        // El cursor no debe ser null
        assertNotNull("El cursor de consultas no debe ser null", cursor)

        cursor?.use {
            // Verificar que tiene las columnas esperadas
            val columnNames = it.columnNames
            assertTrue("Debe contener columna 'id'", columnNames.contains("id"))
            assertTrue("Debe contener columna 'clienteNombre'", columnNames.contains("clienteNombre"))
            assertTrue("Debe contener columna 'mascotaNombre'", columnNames.contains("mascotaNombre"))
            assertTrue("Debe contener columna 'descripcion'", columnNames.contains("descripcion"))
            assertTrue("Debe contener columna 'fecha'", columnNames.contains("fecha"))
            assertTrue("Debe contener columna 'total'", columnNames.contains("total"))

            // El cursor puede tener 0 o más filas
            assertTrue("El conteo de filas debe ser >= 0", it.count >= 0)
        }
    }

    /**
     * Verifica que la consulta de una mascota por ID retorne un Cursor válido.
     *
     * Nota: Este test asume que puede no haber datos en InMemoryDataSource.
     * El comportamiento correcto es retornar un cursor vacío, no null.
     */
    @Test
    fun querySingleMascota_withValidId_returnsCursor() {
        // Intentar consultar mascota con ID 1
        val mascotaUri = Uri.parse("$baseUri/mascotas/1")

        val cursor = contentResolver.query(
            mascotaUri,
            null,
            null,
            null,
            null
        )

        // El cursor no debe ser null (aunque puede estar vacío si no existe la mascota)
        assertNotNull("El cursor no debe ser null incluso si la mascota no existe", cursor)

        cursor?.use {
            // El cursor debe tener 0 o 1 fila
            assertTrue("El cursor debe tener 0 o 1 fila", it.count <= 1)

            // Si hay una fila, verificar que tenga las columnas correctas
            if (it.count == 1) {
                val columnNames = it.columnNames
                assertTrue("Debe contener columna 'id'", columnNames.contains("id"))
                assertTrue("Debe contener columna 'nombre'", columnNames.contains("nombre"))
            }
        }
    }

    /**
     * Verifica que las operaciones de inserción no estén soportadas.
     *
     * VeterinariaProvider es read-only, por lo que insert debe retornar null.
     */
    @Test
    fun insert_returnsNull_providerIsReadOnly() {
        val values = ContentValues().apply {
            put("nombre", "Nuevo Perro")
            put("especie", "Perro")
            put("edad", 2)
            put("peso", 8.5)
            put("clienteId", 1)
        }

        val resultUri = contentResolver.insert(mascotasUri, values)

        // La inserción debe retornar null porque el provider es read-only
        assertNull("Insert debe retornar null en un provider read-only", resultUri)
    }

    /**
     * Verifica que las operaciones de eliminación no estén soportadas.
     *
     * VeterinariaProvider es read-only, por lo que delete debe retornar 0.
     */
    @Test
    fun delete_returnsZero_providerIsReadOnly() {
        val deletedRows = contentResolver.delete(
            mascotasUri,
            null,  // Sin selección (intentar borrar todo)
            null
        )

        // La eliminación debe retornar 0 porque el provider es read-only
        assertEquals("Delete debe retornar 0 en un provider read-only", 0, deletedRows)
    }

    /**
     * Verifica que las operaciones de actualización no estén soportadas.
     *
     * VeterinariaProvider es read-only, por lo que update debe retornar 0.
     */
    @Test
    fun update_returnsZero_providerIsReadOnly() {
        val values = ContentValues().apply {
            put("nombre", "Nombre Actualizado")
        }

        val updatedRows = contentResolver.update(
            mascotasUri,
            values,
            null,  // Sin selección
            null
        )

        // La actualización debe retornar 0 porque el provider es read-only
        assertEquals("Update debe retornar 0 en un provider read-only", 0, updatedRows)
    }
}
