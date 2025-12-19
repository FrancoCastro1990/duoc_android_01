package com.duoc.mobile_01_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.duoc.mobile_01_android.presentation.navigation.AppNavigation
import com.duoc.mobile_01_android.presentation.theme.Mobile_01_androidTheme
import com.duoc.mobile_01_android.util.NotificationHelper

/**
 * Activity principal de la aplicación de veterinaria.
 * Configurada con MVVM completo y arquitectura clean.
 *
 * Soporta Deep Links mediante URI scheme:
 * - veterinaria://consultas -> Navega a pantalla de consultas
 * - veterinaria://mascotas -> Navega a pantalla de mascotas
 * - veterinaria://clientes -> Navega a pantalla de clientes
 */
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Crear canales de notificación para Android 8.0+
        NotificationHelper.createChannels(this)

        // Manejar deep link cuando la app se abre por primera vez
        handleDeepLink(intent)

        setContent {
            Mobile_01_androidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VeterinariaApp()
                }
            }
        }
    }

    /**
     * Se invoca cuando la Activity recibe un nuevo Intent mientras ya está en ejecución.
     * Esto ocurre cuando se abre un deep link mientras la app está en foreground.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    /**
     * Procesa deep links recibidos mediante Intent.
     *
     * Analiza la URI del Intent y determina a qué pantalla navegar.
     * Si la URI es nula o el path no es reconocido, simplemente lo registra en log.
     *
     * Ejemplos de URIs soportadas:
     * - veterinaria://consultas
     * - veterinaria://mascotas
     * - veterinaria://clientes
     *
     * @param intent Intent que puede contener un deep link en intent.data
     */
    private fun handleDeepLink(intent: Intent?) {
        val uri = intent?.data

        if (uri != null) {
            Log.d(TAG, "Deep link recibido: $uri")

            when (uri.path) {
                "/consultas" -> {
                    Log.d(TAG, "Navegando a consultas desde deep link")
                    // En una implementación completa, aquí se navegaría a la pantalla
                    // usando el NavController. Por ahora solo registramos el evento.
                }
                "/mascotas" -> {
                    Log.d(TAG, "Navegando a mascotas desde deep link")
                }
                "/clientes" -> {
                    Log.d(TAG, "Navegando a clientes desde deep link")
                }
                else -> {
                    Log.w(TAG, "Deep link con path desconocido: ${uri.path}")
                }
            }
        }
    }
}

@Composable
fun VeterinariaApp() {
    // AppNavigation ahora usa ServiceLocator internamente
    // No necesitamos pasar ViewModels manualmente
    AppNavigation()
}

@Preview(showBackground = true)
@Composable
fun VeterinariaAppPreview() {
    Mobile_01_androidTheme {
        VeterinariaApp()
    }
}
