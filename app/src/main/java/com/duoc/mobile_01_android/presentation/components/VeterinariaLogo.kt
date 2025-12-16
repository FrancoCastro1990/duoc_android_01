package com.duoc.mobile_01_android.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Componente reutilizable que muestra el logo de la aplicación veterinaria.
 *
 * Diseño:
 * - Fondo circular con color primaryContainer
 * - Ícono de mascota (paw) con color onPrimaryContainer
 * - Texto opcional "Veterinaria App" debajo del logo
 *
 * Sigue principio KISS: Composable simple, sin lógica de negocio, solo UI declarativa.
 *
 * @param modifier Modificador para personalizar el diseño del contenedor
 * @param size Tamaño del círculo del logo (por defecto 80.dp)
 * @param showText Si es true, muestra el texto "Veterinaria App" debajo del logo (por defecto true)
 *
 * Ejemplo de uso:
 * ```
 * VeterinariaLogo(
 *     modifier = Modifier.align(Alignment.CenterHorizontally),
 *     size = 100.dp,
 *     showText = true
 * )
 * ```
 */
@Composable
fun VeterinariaLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    showText: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Círculo con ícono de mascota
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = "Logo Veterinaria",
                modifier = Modifier.size(size * 0.6f),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        // Texto opcional
        if (showText) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Veterinaria App",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
