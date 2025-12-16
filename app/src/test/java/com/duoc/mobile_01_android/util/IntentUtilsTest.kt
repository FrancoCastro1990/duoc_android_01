package com.duoc.mobile_01_android.util

import com.duoc.mobile_01_android.domain.model.Cliente
import com.duoc.mobile_01_android.domain.model.Consulta
import com.duoc.mobile_01_android.domain.model.Mascota
import com.duoc.mobile_01_android.domain.model.Medicamento
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals

/**
 * Tests unitarios para IntentUtils.
 *
 * Valida que la función formatConsultaForShare genere texto correcto y completo
 * para compartir consultas veterinarias mediante Intents de Android.
 *
 * La funcionalidad de compartir es crítica para:
 * - Enviar resúmenes de consultas por WhatsApp/Email
 * - Generar recibos legibles para clientes
 * - Exportar información para seguimiento externo
 */
class IntentUtilsTest {

    /**
     * Cliente de prueba para usar en los tests.
     */
    private val testCliente = Cliente(
        id = 1,
        nombre = "Juan Pérez",
        email = "juan@email.com",
        telefono = "+56 912 345 678"
    )

    /**
     * Mascota de prueba para usar en los tests.
     */
    private val testMascota = Mascota(
        id = 1,
        nombre = "Firulais",
        especie = "Perro",
        edad = 3,
        peso = 10.5,
        clienteId = 1
    )

    /**
     * Medicamento de prueba con descuento.
     */
    private val testMedicamento = Medicamento(
        id = 1,
        nombre = "Amoxicilina",
        dosificacion = "500mg",
        precio = 15000.0,
        stock = 100,
        descuento = 0.2  // 20% de descuento
    )

    /**
     * Verifica que formatConsultaForShare genera texto con todos los campos principales.
     *
     * Valida que el texto generado contenga:
     * - Encabezado de consulta
     * - Secciones claramente delimitadas
     * - Formato legible y estructurado
     */
    @Test
    fun `formatConsultaForShare generates correct text with all fields`() {
        val consulta = Consulta(
            id = 1,
            cliente = testCliente,
            mascota = testMascota,
            medicamentos = listOf(testMedicamento),
            descripcion = "Vacunación anual",
            fecha = "2025-12-15",
            total = 45000.0
        )

        val formattedText = IntentUtils.formatConsultaForShare(consulta)

        // Verificar que contiene el título principal
        assertTrue(formattedText.contains("DETALLE DE CONSULTA VETERINARIA"))

        // Verificar que contiene secciones principales
        assertTrue(formattedText.contains("CLIENTE:"))
        assertTrue(formattedText.contains("MASCOTA:"))
        assertTrue(formattedText.contains("CONSULTA:"))
        assertTrue(formattedText.contains("MEDICAMENTOS APLICADOS:"))
        assertTrue(formattedText.contains("TOTAL:"))

        // Verificar que contiene la descripción de la consulta
        assertTrue(formattedText.contains("Vacunación anual"))

        // Verificar que contiene la fecha
        assertTrue(formattedText.contains("2025-12-15"))

        // Verificar que contiene el total formateado
        assertTrue(formattedText.contains("45,000"))
    }

    /**
     * Verifica que el texto formateado incluya toda la información del cliente.
     *
     * Es importante que el cliente pueda identificar claramente
     * a quién pertenece la consulta.
     */
    @Test
    fun `formatConsultaForShare includes client information`() {
        val consulta = Consulta(
            id = 1,
            cliente = testCliente,
            mascota = testMascota,
            medicamentos = emptyList(),
            descripcion = "Chequeo general",
            fecha = "2025-12-15",
            total = 30000.0
        )

        val formattedText = IntentUtils.formatConsultaForShare(consulta)

        // Verificar datos del cliente
        assertTrue(formattedText.contains("Juan Pérez"))
        assertTrue(formattedText.contains("juan@email.com"))
        assertTrue(formattedText.contains("+56 912 345 678"))
    }

    /**
     * Verifica que el texto formateado incluya toda la información de la mascota.
     *
     * Los datos de la mascota son fundamentales para el registro veterinario.
     */
    @Test
    fun `formatConsultaForShare includes pet information`() {
        val consulta = Consulta(
            id = 1,
            cliente = testCliente,
            mascota = testMascota,
            medicamentos = emptyList(),
            descripcion = "Control de peso",
            fecha = "2025-12-15",
            total = 25000.0
        )

        val formattedText = IntentUtils.formatConsultaForShare(consulta)

        // Verificar datos de la mascota
        assertTrue(formattedText.contains("Firulais"))
        assertTrue(formattedText.contains("Perro"))
        assertTrue(formattedText.contains("3 años"))
        assertTrue(formattedText.contains("10.5 kg"))
    }

    /**
     * Verifica que los medicamentos se incluyan con precios y descuentos correctos.
     *
     * El cálculo y formato de precios con descuento debe ser preciso
     * para evitar confusiones con el cliente.
     */
    @Test
    fun `formatConsultaForShare includes medications when present`() {
        val consulta = Consulta(
            id = 1,
            cliente = testCliente,
            mascota = testMascota,
            medicamentos = listOf(testMedicamento),
            descripcion = "Tratamiento antibiótico",
            fecha = "2025-12-15",
            total = 12000.0  // Precio con descuento aplicado
        )

        val formattedText = IntentUtils.formatConsultaForShare(consulta)

        // Verificar que incluye el nombre y dosificación del medicamento
        assertTrue(formattedText.contains("Amoxicilina"))
        assertTrue(formattedText.contains("500mg"))

        // Verificar que muestra el precio original
        assertTrue(formattedText.contains("15,000"))

        // Verificar que muestra el descuento
        assertTrue(formattedText.contains("20%"))

        // Verificar que calcula y muestra el precio final (15000 * 0.8 = 12000)
        assertTrue(formattedText.contains("12,000"))
    }

    /**
     * Verifica que el formato funcione correctamente cuando no hay medicamentos.
     *
     * No todas las consultas requieren medicamentos, el formato debe
     * manejarlo sin errores ni secciones vacías confusas.
     */
    @Test
    fun `formatConsultaForShare handles empty medications list`() {
        val consulta = Consulta(
            id = 1,
            cliente = testCliente,
            mascota = testMascota,
            medicamentos = emptyList(),  // Sin medicamentos
            descripcion = "Consulta de rutina",
            fecha = "2025-12-15",
            total = 20000.0
        )

        val formattedText = IntentUtils.formatConsultaForShare(consulta)

        // Verificar que NO incluye la sección de medicamentos cuando está vacía
        assertFalse(formattedText.contains("MEDICAMENTOS APLICADOS:"))

        // Verificar que aún incluye el total
        assertTrue(formattedText.contains("TOTAL:"))
        assertTrue(formattedText.contains("20,000"))
    }

    /**
     * Verifica que el formato de moneda (total) sea consistente.
     *
     * Los totales deben mostrarse con separadores de miles para
     * facilitar la lectura (ej: 45,000 en lugar de 45000).
     */
    @Test
    fun `formatConsultaForShare formats total correctly`() {
        val consulta = Consulta(
            id = 1,
            cliente = testCliente,
            mascota = testMascota,
            medicamentos = emptyList(),
            descripcion = "Cirugía menor",
            fecha = "2025-12-15",
            total = 45000.0
        )

        val formattedText = IntentUtils.formatConsultaForShare(consulta)

        // Verificar formato con separador de miles
        assertTrue(formattedText.contains("$45,000"))

        // Verificar que no tenga decimales innecesarios
        assertFalse(formattedText.contains("45,000.0"))
    }
}
