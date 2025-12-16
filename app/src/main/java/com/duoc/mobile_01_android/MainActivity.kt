package com.duoc.mobile_01_android

import android.os.Bundle
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

/**
 * Activity principal de la aplicación de veterinaria.
 * Configurada con MVVM completo y arquitectura clean.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
