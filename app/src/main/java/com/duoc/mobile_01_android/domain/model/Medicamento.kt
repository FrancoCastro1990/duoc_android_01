package com.duoc.mobile_01_android.domain.model

/**
 * Modelo de dominio que representa un Medicamento disponible en la veterinaria.
 * Incluye lógica de negocio para calcular el precio con descuento.
 */
data class Medicamento(
    val id: Int = 0,
    val nombre: String = "",
    val dosificacion: String = "",
    val precio: Double = 0.0,
    val stock: Int = 0,
    val descuento: Double = 0.0
) {
    /**
     * Calcula el precio final del medicamento aplicando el descuento.
     * Lógica de negocio encapsulada en el modelo.
     */
    fun precioConDescuento(): Double {
        return precio * (1 - descuento)
    }
}
