package com.duoc.mobile_01_android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import com.duoc.mobile_01_android.data.datasource.InMemoryDataSource

/**
 * BroadcastReceiver que escucha cambios en la conectividad de red.
 *
 * Este receptor detecta cuando el dispositivo se conecta o desconecta de una red WiFi
 * y muestra un resumen de datos de la veterinaria al usuario mediante un Toast.
 *
 * El resumen incluye:
 * - Número de consultas pendientes/registradas
 * - Número de mascotas registradas en el sistema
 *
 * Soporta Android API 21+ (Lollipop) con manejo de APIs deprecated para compatibilidad.
 *
 * @see BroadcastReceiver
 */
class WifiConnectionReceiver : BroadcastReceiver() {

    /**
     * Método invocado cuando se recibe un broadcast de cambio de conectividad.
     *
     * Verifica si el dispositivo está conectado a WiFi y muestra un Toast con
     * información resumida de la aplicación de veterinaria.
     *
     * @param context Contexto de la aplicación para acceder a servicios del sistema
     * @param intent Intent que contiene información sobre el broadcast recibido
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        // Verificar si el dispositivo está conectado a WiFi
        val isWifiConnected = isConnectedToWifi(context)

        if (isWifiConnected) {
            // Obtener datos de InMemoryDataSource para el resumen
            val consultasCount = InMemoryDataSource.consultas.value.size
            val mascotasCount = InMemoryDataSource.mascotas.value.size

            // Construir mensaje de resumen
            val message = buildResumenMessage(consultasCount, mascotasCount)

            // Mostrar Toast con el resumen
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Verifica si el dispositivo está conectado a una red WiFi.
     *
     * Utiliza diferentes APIs según la versión de Android:
     * - API 23+: NetworkCapabilities (recomendado)
     * - API 21-22: NetworkInfo (deprecated pero necesario para compatibilidad)
     *
     * @param context Contexto para acceder al ConnectivityManager
     * @return true si está conectado a WiFi, false en caso contrario
     */
    private fun isConnectedToWifi(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // API 23+ (Android Marshmallow): Usar NetworkCapabilities
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            // API 21-22 (Android Lollipop): Usar NetworkInfo (deprecated)
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            networkInfo.isConnected && networkInfo.type == ConnectivityManager.TYPE_WIFI
        }
    }

    /**
     * Construye el mensaje de resumen con los datos de la veterinaria.
     *
     * @param consultasCount Número total de consultas registradas
     * @param mascotasCount Número total de mascotas registradas
     * @return Mensaje formateado para mostrar en el Toast
     */
    private fun buildResumenMessage(consultasCount: Int, mascotasCount: Int): String {
        return buildString {
            append("Conectado a WiFi\n")
            append("Veterinaria - Resumen:\n")
            append("Consultas: $consultasCount\n")
            append("Mascotas: $mascotasCount")
        }
    }
}
