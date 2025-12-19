package com.duoc.mobile_01_android.domain.model

/**
 * Estados posibles de la sesión del usuario usando sealed class.
 * Patrón recomendado para representar estados mutuamente excluyentes.
 */
sealed class SessionState {
    /**
     * Usuario no autenticado - debe mostrar pantalla de login
     */
    object NotAuthenticated : SessionState()

    /**
     * Usuario autenticado con información del usuario
     */
    data class Authenticated(val usuario: Usuario) : SessionState()
}
