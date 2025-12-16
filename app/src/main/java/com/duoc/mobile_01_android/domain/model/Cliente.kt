package com.duoc.mobile_01_android.domain.model

/**
 * Modelo de dominio que representa a un Cliente de la veterinaria.
 * Datos inmutables para garantizar consistencia del estado.
 */
data class Cliente(
    val id: Int = 0,
    val nombre: String = "",
    val email: String = "",
    val telefono: String = ""
)
