package com.duoc.mobile_01_android.util

/**
 * Utilidades de validación y formateo de datos.
 * Siguiendo el principio DRY para evitar duplicación de lógica de validación.
 */
object ValidationUtils {

    /**
     * Valida si un email tiene el formato correcto.
     * @param email El email a validar
     * @return true si el email es válido, false en caso contrario
     */
    fun validarEmail(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    /**
     * Formatea un número telefónico al formato chileno (+56 XXX XXX XXX).
     * @param telefono El número telefónico a formatear
     * @return El teléfono formateado o el original si no cumple requisitos
     */
    fun formatearTelefono(telefono: String): String {
        val soloNumeros = telefono.filter { it.isDigit() }
        return if (soloNumeros.length >= 9) {
            "+56 ${soloNumeros.take(9).chunked(3).joinToString(" ")}"
        } else {
            telefono
        }
    }
}
