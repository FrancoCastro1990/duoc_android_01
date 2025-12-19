package com.duoc.mobile_01_android.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.mobile_01_android.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Recuperar Contraseña.
 * Gestiona el proceso de recuperación de contraseña simulado.
 *
 * Principio de Responsabilidad Única (S de SOLID):
 * Solo maneja lógica de presentación relacionada con recuperación de contraseña.
 */
class RecuperarPasswordViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<RecuperarPasswordUiState>(RecuperarPasswordUiState.Idle)
    val uiState: StateFlow<RecuperarPasswordUiState> = _uiState.asStateFlow()

    /**
     * Intenta recuperar la contraseña para el email proporcionado.
     * En una app real, esto enviaría un email con instrucciones.
     */
    fun recuperarPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = RecuperarPasswordUiState.Loading

            authRepository.recuperarPassword(email)
                .onSuccess { existe ->
                    if (existe) {
                        _uiState.value = RecuperarPasswordUiState.Success
                    } else {
                        _uiState.value = RecuperarPasswordUiState.Error(
                            "No se encontró una cuenta con este email"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.value = RecuperarPasswordUiState.Error(
                        error.message ?: "Error al procesar la solicitud"
                    )
                }
        }
    }

    /**
     * Resetea el estado para permitir nuevos intentos.
     */
    fun reset() {
        _uiState.value = RecuperarPasswordUiState.Idle
    }
}

/**
 * Estados posibles de la UI de Recuperar Contraseña usando sealed class.
 * Patrón recomendado para representar estados mutuamente excluyentes.
 */
sealed class RecuperarPasswordUiState {
    /**
     * Estado inicial, esperando que el usuario ingrese email
     */
    object Idle : RecuperarPasswordUiState()

    /**
     * Procesando solicitud, mostrar loading
     */
    object Loading : RecuperarPasswordUiState()

    /**
     * Solicitud enviada exitosamente
     */
    object Success : RecuperarPasswordUiState()

    /**
     * Error en la solicitud con mensaje descriptivo
     */
    data class Error(val message: String) : RecuperarPasswordUiState()
}
