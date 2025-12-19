package com.duoc.mobile_01_android.domain.model

/**
 * Modelo de dominio que representa una Vacuna aplicada a una mascota.
 * Cada vacuna está asociada a una Mascota mediante mascotaId.
 */
data class Vacuna(
    val id: Int = 0,
    val nombre: String = "",
    val mascotaId: Int = 0,
    val fechaAplicacion: String = "",  // formato: "dd/MM/yyyy"
    val proximaFecha: String = ""       // formato: "dd/MM/yyyy"
)
