package com.duoc.mobile_01_android.domain.model

/**
 * Modelo de dominio que representa a un Usuario del sistema.
 * Datos inmutables para garantizar consistencia del estado.
 */
data class Usuario(
    val id: Int = 0,
    val email: String = "",
    val password: String = "",
    val rol: Rol = Rol.DUENO,
    val clienteId: Int? = null
)

/**
 * Enum que representa los roles de usuario en el sistema.
 * - ADMIN: Administrador con acceso completo al sistema
 * - DUENO: Dueño de mascota con acceso limitado a sus propios datos
 */
enum class Rol {
    ADMIN,
    DUENO
}
