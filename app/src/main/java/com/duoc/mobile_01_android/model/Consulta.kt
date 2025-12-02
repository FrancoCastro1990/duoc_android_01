package com.duoc.mobile_01_android.model

data class Consulta(
    val id: Int = 0,
    val cliente: Cliente = Cliente(),
    val mascota: Mascota = Mascota(),
    val medicamentos: List<Medicamento> = emptyList(),
    val descripcion: String = "",
    val fecha: String = "",
    val total: Double = 0.0
)
