package com.duoc.mobile_01_android.domain.repository

import com.duoc.mobile_01_android.domain.model.SessionState
import com.duoc.mobile_01_android.domain.model.Usuario
import kotlinx.coroutines.flow.StateFlow

/**
 * Repositorio de autenticación que define las operaciones de inicio de sesión.
 * Principio de Inversión de Dependencias (D de SOLID):
 * Los ViewModels dependen de esta interfaz, no de la implementación concreta.
 */
interface AuthRepository {

    /**
     * Obtiene el estado actual de la sesión como StateFlow reactivo.
     * Permite a la UI observar cambios en el estado de autenticación.
     */
    fun getCurrentSession(): StateFlow<SessionState>

    /**
     * Intenta iniciar sesión con las credenciales proporcionadas.
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return Result con el Usuario si las credenciales son válidas, o un error
     */
    suspend fun login(email: String, password: String): Result<Usuario>

    /**
     * Cierra la sesión del usuario actual.
     */
    suspend fun logout()

    /**
     * Simula el proceso de recuperación de contraseña.
     * @param email Email del usuario
     * @return Result con true si el email existe, false en caso contrario
     */
    suspend fun recuperarPassword(email: String): Result<Boolean>
}
