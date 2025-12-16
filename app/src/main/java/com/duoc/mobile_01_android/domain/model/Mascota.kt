package com.duoc.mobile_01_android.domain.model

/**
 * Modelo de dominio que representa a una Mascota registrada en la veterinaria.
 * Cada mascota está asociada a un Cliente mediante clienteId.
 */
data class Mascota(
    val id: Int = 0,
    val nombre: String = "",
    val especie: String = "",
    val edad: Int = 0,
    val peso: Double = 0.0,
    val clienteId: Int = 0
)
