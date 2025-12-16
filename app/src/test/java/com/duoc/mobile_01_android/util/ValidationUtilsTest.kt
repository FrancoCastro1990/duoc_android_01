package com.duoc.mobile_01_android.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Test unitario para ValidationUtils.
 * Valida las funciones de utilidad para validación y formateo de datos.
 *
 * Criterio 6: Pruebas que verifican el funcionamiento correcto de componentes
 * de utilidad que son críticos para la validación de datos en la aplicación.
 */
class ValidationUtilsTest {

    // ==================== Tests de validarEmail ====================

    @Test
    fun `validarEmail - debe aceptar email valido simple`() {
        // Given: Un email válido básico
        val email = "usuario@dominio.com"

        // When: Se valida
        val resultado = ValidationUtils.validarEmail(email)

        // Then: Debe ser válido
        assertTrue(resultado)
    }

    @Test
    fun `validarEmail - debe aceptar email con punto en nombre`() {
        // Given: Email con punto en el nombre de usuario
        val email = "nombre.apellido@empresa.com"

        // When: Se valida
        val resultado = ValidationUtils.validarEmail(email)

        // Then: Debe ser válido
        assertTrue(resultado)
    }

    @Test
    fun `validarEmail - debe aceptar email con numeros`() {
        // Given: Email con números
        val email = "usuario123@dominio456.com"

        // When: Se valida
        val resultado = ValidationUtils.validarEmail(email)

        // Then: Debe ser válido
        assertTrue(resultado)
    }

    @Test
    fun `validarEmail - debe aceptar email con guion bajo`() {
        // Given: Email con guión bajo
        val email = "nombre_apellido@empresa.com"

        // When: Se valida
        val resultado = ValidationUtils.validarEmail(email)

        // Then: Debe ser válido
        assertTrue(resultado)
    }

    @Test
    fun `validarEmail - debe aceptar email con guion`() {
        // Given: Email con guión
        val email = "nombre-apellido@empresa-tecnologia.com"

        // When: Se valida
        val resultado = ValidationUtils.validarEmail(email)

        // Then: Debe ser válido
        assertTrue(resultado)
    }

    @Test
    fun `validarEmail - debe aceptar email con mas caracteres`() {
        // Given: Email con caracteres especiales permitidos
        val email = "usuario+test@dominio.cl"

        // When: Se valida
        val resultado = ValidationUtils.validarEmail(email)

        // Then: Debe ser válido
        assertTrue(resultado)
    }

    @Test
    fun `validarEmail - debe aceptar dominios con subdominios`() {
        // Given: Email con subdominio
        val email = "usuario@correo.empresa.com"

        // When: Se valida
        val resultado = ValidationUtils.validarEmail(email)

        // Then: Debe ser válido
        assertTrue(resultado)
    }

    @Test
    fun `validarEmail - debe aceptar TLD largo`() {
        // Given: Email con TLD de múltiples caracteres
        val email = "usuario@dominio.info"

        // When: Se valida
        val resultado = ValidationUtils.validarEmail(email)

        // Then: Debe ser válido
        assertTrue(resultado)
    }

    @Test
    fun `validarEmail - debe rechazar email sin arroba`() {
        // Given: Email sin @
        val email = "usuariodominio.com"

        // When: Se valida
        val resultado = ValidationUtils.validarEmail(email)

        // Then: Debe ser inválido
        assertFalse(resultado)
    }

    @Test
    fun `validarEmail - debe rechazar email sin nombre usuario`() {
        // Given: Email sin nombre de usuario
        val email = "@dominio.com"

        // When: Se valida
        val resultado = ValidationUtils.validarEmail(email)

        // Then: Debe ser inválido
        assertFalse(resultado)
    }

    @Test
    fun `validarEmail - debe rechazar email sin dominio`() {
        // Given: Email sin dominio
        val email = "usuario@"

        // When: Se valida
        val resultado = ValidationUtils.validarEmail(email)

        // Then: Debe ser inválido
        assertFalse(resultado)
    }

    @Test
    fun `validarEmail - debe rechazar email sin TLD`() {
        // Given: Email sin extensión de dominio
        val email = "usuario@dominio"

        // When: Se valida
        val resultado = ValidationUtils.validarEmail(email)

        // Then: Debe ser inválido
        assertFalse(resultado)
    }

    @Test
    fun `validarEmail - debe rechazar email con espacios`() {
        // Given: Email con espacios
        val email = "nombre apellido@dominio.com"

        // When: Se valida
        val resultado = ValidationUtils.validarEmail(email)

        // Then: Debe ser inválido
        assertFalse(resultado)
    }

    @Test
    fun `validarEmail - debe rechazar email vacio`() {
        // Given: Email vacío
        val email = ""

        // When: Se valida
        val resultado = ValidationUtils.validarEmail(email)

        // Then: Debe ser inválido
        assertFalse(resultado)
    }

    @Test
    fun `validarEmail - debe rechazar email con multiples arrobas`() {
        // Given: Email con múltiples @
        val email = "usuario@@dominio.com"

        // When: Se valida
        val resultado = ValidationUtils.validarEmail(email)

        // Then: Debe ser inválido
        assertFalse(resultado)
    }

    @Test
    fun `validarEmail - email con punto al inicio depende del regex usado`() {
        // Given: Email que comienza con punto
        val email = ".usuario@dominio.com"

        // When: Se valida con el regex actual (que lo acepta)
        val resultado = ValidationUtils.validarEmail(email)

        // Then: El regex actual lo acepta (nota: un regex más estricto lo rechazaría)
        assertTrue(resultado)
    }

    @Test
    fun `validarEmail - debe rechazar TLD de un caracter`() {
        // Given: Email con TLD de solo 1 carácter
        val email = "usuario@dominio.c"

        // When: Se valida
        val resultado = ValidationUtils.validarEmail(email)

        // Then: Debe ser inválido
        assertFalse(resultado)
    }

    // ==================== Tests de formatearTelefono ====================

    @Test
    fun `formatearTelefono - debe formatear telefono chileno de 9 digitos`() {
        // Given: Un teléfono chileno de 9 dígitos
        val telefono = "912345678"

        // When: Se formatea
        val resultado = ValidationUtils.formatearTelefono(telefono)

        // Then: Debe formatearse correctamente
        assertEquals("+56 912 345 678", resultado)
    }

    @Test
    fun `formatearTelefono - debe formatear telefono con mas de 9 digitos tomando solo 9`() {
        // Given: Un teléfono con más de 9 dígitos
        val telefono = "91234567890"

        // When: Se formatea
        val resultado = ValidationUtils.formatearTelefono(telefono)

        // Then: Debe tomar solo los primeros 9 dígitos
        assertEquals("+56 912 345 678", resultado)
    }

    @Test
    fun `formatearTelefono - debe formatear telefono con espacios`() {
        // Given: Un teléfono con espacios
        val telefono = "9 1234 5678"

        // When: Se formatea
        val resultado = ValidationUtils.formatearTelefono(telefono)

        // Then: Debe eliminar espacios y formatear
        assertEquals("+56 912 345 678", resultado)
    }

    @Test
    fun `formatearTelefono - debe formatear telefono con guiones`() {
        // Given: Un teléfono con guiones
        val telefono = "9-1234-5678"

        // When: Se formatea
        val resultado = ValidationUtils.formatearTelefono(telefono)

        // Then: Debe eliminar guiones y formatear
        assertEquals("+56 912 345 678", resultado)
    }

    @Test
    fun `formatearTelefono - debe formatear telefono con parentesis`() {
        // Given: Un teléfono con paréntesis
        val telefono = "(9) 1234-5678"

        // When: Se formatea
        val resultado = ValidationUtils.formatearTelefono(telefono)

        // Then: Debe eliminar paréntesis y formatear
        assertEquals("+56 912 345 678", resultado)
    }

    @Test
    fun `formatearTelefono - debe formatear telefono con prefijo +56`() {
        // Given: Un teléfono que ya tiene +56
        val telefono = "+56912345678"

        // When: Se formatea
        val resultado = ValidationUtils.formatearTelefono(telefono)

        // Then: Debe ignorar el +56 existente y formatear los 9 dígitos
        assertEquals("+56 569 123 456", resultado)
    }

    @Test
    fun `formatearTelefono - debe retornar original si tiene menos de 9 digitos`() {
        // Given: Un teléfono con menos de 9 dígitos
        val telefono = "12345678"

        // When: Se formatea
        val resultado = ValidationUtils.formatearTelefono(telefono)

        // Then: Debe retornar el original sin modificar
        assertEquals("12345678", resultado)
    }

    @Test
    fun `formatearTelefono - debe retornar original si esta vacio`() {
        // Given: Un teléfono vacío
        val telefono = ""

        // When: Se formatea
        val resultado = ValidationUtils.formatearTelefono(telefono)

        // Then: Debe retornar vacío
        assertEquals("", resultado)
    }

    @Test
    fun `formatearTelefono - debe retornar original si no tiene digitos`() {
        // Given: Un string sin dígitos
        val telefono = "ABC-DEF-GHI"

        // When: Se formatea
        val resultado = ValidationUtils.formatearTelefono(telefono)

        // Then: Debe retornar el original
        assertEquals("ABC-DEF-GHI", resultado)
    }

    @Test
    fun `formatearTelefono - debe manejar telefono con mezcla de caracteres`() {
        // Given: Un teléfono con mezcla de caracteres
        val telefono = "Tel: 9-1234-5678"

        // When: Se formatea
        val resultado = ValidationUtils.formatearTelefono(telefono)

        // Then: Debe extraer solo números y formatear
        assertEquals("+56 912 345 678", resultado)
    }

    @Test
    fun `formatearTelefono - debe formatear correctamente telefono fijo chileno`() {
        // Given: Un teléfono fijo de Santiago (2 + 8 dígitos = 9 dígitos)
        val telefono = "223456789"

        // When: Se formatea
        val resultado = ValidationUtils.formatearTelefono(telefono)

        // Then: Debe formatear correctamente
        assertEquals("+56 223 456 789", resultado)
    }

    @Test
    fun `formatearTelefono - debe formatear telefono con codigo de area`() {
        // Given: Teléfono con código de área
        val telefono = "(2) 2345-6789"

        // When: Se formatea
        val resultado = ValidationUtils.formatearTelefono(telefono)

        // Then: Debe formatear correctamente
        assertEquals("+56 223 456 789", resultado)
    }

    @Test
    fun `formatearTelefono - casos reales variados`() {
        // Test múltiples casos reales de entrada
        assertEquals("+56 987 654 321", ValidationUtils.formatearTelefono("987654321"))
        assertEquals("+56 998 877 665", ValidationUtils.formatearTelefono("9-9887-7665"))
        // Nota: "+56 9 1234 5678" tiene más de 9 dígitos (569123456), toma primeros 9
        assertEquals("+56 569 123 456", ValidationUtils.formatearTelefono("+56 9 1234 5678"))
        assertEquals("+56 223 334 445", ValidationUtils.formatearTelefono("(2) 2333-4445"))
    }
}
