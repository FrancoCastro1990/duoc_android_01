package com.duoc.mobile_01_android.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.mobile_01_android.domain.model.Usuario
import com.duoc.mobile_01_android.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Login.
 * Gestiona el estado de autenticación y validación de credenciales.
 *
 * Principio de Responsabilidad Única (S de SOLID):
 * Solo maneja lógica de presentación relacionada con autenticación.
 */
class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Intenta iniciar sesión con las credenciales proporcionadas.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            authRepository.login(email, password)
                .onSuccess { usuario ->
                    _uiState.value = LoginUiState.Success(usuario)
                }
                .onFailure { error ->
                    _uiState.value = LoginUiState.Error(
                        error.message ?: "Error desconocido al iniciar sesión"
                    )
                }
        }
    }

    /**
     * Limpia el estado de error para permitir reintentos.
     */
    fun clearError() {
        _uiState.value = LoginUiState.Idle
    }
}

/**
 * Estados posibles de la UI de Login usando sealed class.
 * Patrón recomendado para representar estados mutuamente excluyentes.
 */
sealed class LoginUiState {
    /**
     * Estado inicial, esperando que el usuario ingrese credenciales
     */
    object Idle : LoginUiState()

    /**
     * Procesando login, mostrar loading
     */
    object Loading : LoginUiState()

    /**
     * Login exitoso con datos del usuario
     */
    data class Success(val usuario: Usuario) : LoginUiState()

    /**
     * Error en el login con mensaje descriptivo
     */
    data class Error(val message: String) : LoginUiState()
}
