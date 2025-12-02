package com.duoc.mobile_01_android.model

data class Medicamento(
    val id: Int = 0,
    val nombre: String = "",
    val dosificacion: String = "",
    val precio: Double = 0.0,
    val stock: Int = 0,
    val descuento: Double = 0.0
) {
    fun precioConDescuento(): Double {
        return precio * (1 - descuento)
    }
}
