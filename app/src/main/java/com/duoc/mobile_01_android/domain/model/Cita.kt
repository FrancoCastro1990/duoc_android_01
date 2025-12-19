package com.duoc.mobile_01_android.domain.model

/**
 * Modelo de dominio que representa una Cita/Agenda en la veterinaria.
 * Cada cita está asociada a una Mascota y a un Cliente.
 */
data class Cita(
    val id: Int = 0,
    val mascotaId: Int = 0,
    val clienteId: Int = 0,
    val fecha: String = "",       // formato: "dd/MM/yyyy"
    val hora: String = "",        // formato: "HH:mm"
    val motivo: String = "",
    val estado: EstadoCita = EstadoCita.PENDIENTE
)

/**
 * Estados posibles de una cita.
 */
enum class EstadoCita {
    PENDIENTE,
    COMPLETADA,
    CANCELADA
}
