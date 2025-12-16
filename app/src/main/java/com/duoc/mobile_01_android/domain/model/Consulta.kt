package com.duoc.mobile_01_android.domain.model

/**
 * Modelo de dominio que representa una Consulta veterinaria.
 * Agrupa información de cliente, mascota, medicamentos aplicados y costos.
 */
data class Consulta(
    val id: Int = 0,
    val cliente: Cliente = Cliente(),
    val mascota: Mascota = Mascota(),
    val medicamentos: List<Medicamento> = emptyList(),
    val descripcion: String = "",
    val fecha: String = "",
    val total: Double = 0.0
)
