package com.duoc.mobile_01_android.data.repository

import com.duoc.mobile_01_android.data.datasource.InMemoryDataSource
import com.duoc.mobile_01_android.domain.model.SessionState
import com.duoc.mobile_01_android.domain.model.Usuario
import com.duoc.mobile_01_android.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

/**
 * Implementación del repositorio de autenticación.
 * Coordina entre el DataSource y el ViewModel siguiendo Clean Architecture.
 *
 * Principio de Inversión de Dependencias (D de SOLID):
 * Implementa la interfaz AuthRepository definida en el dominio.
 */
class AuthRepositoryImpl : AuthRepository {

    private val dataSource = InMemoryDataSource

    /**
     * Obtiene el estado actual de la sesión reactivamente.
     * El StateFlow se actualiza automáticamente cuando cambia la sesión.
     */
    override fun getCurrentSession(): StateFlow<SessionState> {
        return dataSource.currentSession
    }

    /**
     * Intenta iniciar sesión con las credenciales proporcionadas.
     * Simula un delay de red para hacer la operación más realista.
     *
     * Credenciales válidas:
     * - admin@vet.com / admin123 (Rol ADMIN)
     * - [email de cliente] / cliente123 (Rol DUENO)
     */
    override suspend fun login(email: String, password: String): Result<Usuario> {
        return try {
            // Simular delay de red (500ms)
            delay(500)

            // Validar que no estén vacíos
            if (email.isBlank() || password.isBlank()) {
                return Result.failure(IllegalArgumentException("Email y contraseña son requeridos"))
            }

            // Intentar login en el data source
            val usuario = dataSource.login(email, password)

            if (usuario != null) {
                Result.success(usuario)
            } else {
                Result.failure(IllegalArgumentException("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     * Simula un delay para hacer la operación más realista.
     */
    override suspend fun logout() {
        try {
            delay(300)
            dataSource.logout()
        } catch (e: Exception) {
            // Log error pero no fallar
        }
    }

    /**
     * Simula el proceso de recuperación de contraseña.
     * En una app real, esto enviaría un email con instrucciones.
     *
     * @return Result con true si el email existe, false si no
     */
    override suspend fun recuperarPassword(email: String): Result<Boolean> {
        return try {
            // Simular delay de red (1 segundo)
            delay(1000)

            if (email.isBlank()) {
                return Result.failure(IllegalArgumentException("El email es requerido"))
            }

            val existe = dataSource.existeEmail(email)
            Result.success(existe)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
