package com.duoc.mobile_01_android.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.duoc.mobile_01_android.MainActivity
import com.duoc.mobile_01_android.R
import com.duoc.mobile_01_android.domain.model.Cita
import com.duoc.mobile_01_android.domain.model.Vacuna

/**
 * Helper class para gestionar notificaciones de la aplicación veterinaria.
 *
 * Maneja la creación de canales de notificación y el envío de notificaciones
 * para confirmaciones de citas y registro de vacunas.
 *
 * Para Android 8.0 (API 26) y superior, las notificaciones requieren canales.
 * Este helper crea dos canales separados: uno para citas y otro para vacunas.
 */
object NotificationHelper {

    // IDs de canales de notificación
    private const val CHANNEL_CITAS = "citas_channel"
    private const val CHANNEL_VACUNAS = "vacunas_channel"

    // Nombres visibles de los canales
    private const val CHANNEL_CITAS_NAME = "Citas Veterinarias"
    private const val CHANNEL_VACUNAS_NAME = "Vacunas"

    // Descripciones de los canales
    private const val CHANNEL_CITAS_DESCRIPTION = "Notificaciones de confirmación de citas agendadas"
    private const val CHANNEL_VACUNAS_DESCRIPTION = "Notificaciones de registro de vacunas aplicadas"

    // IDs de notificación (deben ser únicos para cada tipo)
    private const val NOTIFICATION_CITA_ID = 1000
    private const val NOTIFICATION_VACUNA_ID = 2000

    /**
     * Crea los canales de notificación requeridos para Android 8.0+.
     *
     * Este método debe llamarse al inicio de la aplicación, típicamente
     * en MainActivity.onCreate() o en la clase Application.
     *
     * Es seguro llamar este método múltiples veces ya que el sistema
     * ignora las llamadas duplicadas para canales existentes.
     *
     * Los canales se crean con importancia HIGH para que las notificaciones
     * se muestren inmediatamente con sonido y vibración.
     *
     * @param context Contexto de la aplicación o actividad
     */
    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Canal para notificaciones de citas
            val citasChannel = NotificationChannel(
                CHANNEL_CITAS,
                CHANNEL_CITAS_NAME,
                NotificationManager.IMPORTANCE_HIGH // Importancia alta para mostrar inmediatamente
            ).apply {
                description = CHANNEL_CITAS_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }

            // Canal para notificaciones de vacunas
            val vacunasChannel = NotificationChannel(
                CHANNEL_VACUNAS,
                CHANNEL_VACUNAS_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_VACUNAS_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }

            // Crear ambos canales
            notificationManager.createNotificationChannel(citasChannel)
            notificationManager.createNotificationChannel(vacunasChannel)
        }
    }

    /**
     * Muestra una notificación de confirmación cuando se agenda una cita.
     *
     * La notificación incluye:
     * - Nombre de la mascota
     * - Fecha y hora de la cita
     * - Motivo de la cita
     *
     * Al tocar la notificación, se abre la MainActivity.
     *
     * @param context Contexto de la aplicación
     * @param cita Datos de la cita agendada
     * @param mascotaNombre Nombre de la mascota asociada a la cita
     */
    fun showCitaConfirmacion(context: Context, cita: Cita, mascotaNombre: String) {
        // Intent para abrir MainActivity al tocar la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_CITA_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Construir la notificación
        val notification = NotificationCompat.Builder(context, CHANNEL_CITAS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Cita Agendada")
            .setContentText("Cita para $mascotaNombre el ${cita.fecha} a las ${cita.hora}")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Cita agendada para $mascotaNombre\nFecha: ${cita.fecha} a las ${cita.hora}\nMotivo: ${cita.motivo}")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // La notificación se elimina al tocarla
            .setContentIntent(pendingIntent)
            .build()

        // Mostrar la notificación
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_CITA_ID, notification)
    }

    /**
     * Muestra una notificación de confirmación cuando se registra una vacuna.
     *
     * La notificación incluye:
     * - Nombre de la vacuna
     * - Nombre de la mascota
     * - Fecha de aplicación
     * - Próxima fecha (si está disponible)
     *
     * Al tocar la notificación, se abre la MainActivity.
     *
     * @param context Contexto de la aplicación
     * @param vacuna Datos de la vacuna registrada
     * @param mascotaNombre Nombre de la mascota que recibió la vacuna
     */
    fun showVacunaConfirmacion(context: Context, vacuna: Vacuna, mascotaNombre: String) {
        // Intent para abrir MainActivity al tocar la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_VACUNA_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Construir mensaje con información de próxima fecha si existe
        val proximaFechaText = if (vacuna.proximaFecha.isNotEmpty()) {
            "\nPróxima dosis: ${vacuna.proximaFecha}"
        } else {
            ""
        }

        // Construir la notificación
        val notification = NotificationCompat.Builder(context, CHANNEL_VACUNAS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Vacuna Registrada")
            .setContentText("Vacuna ${vacuna.nombre} registrada para $mascotaNombre")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Vacuna ${vacuna.nombre} registrada para $mascotaNombre\nFecha de aplicación: ${vacuna.fechaAplicacion}$proximaFechaText")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Mostrar la notificación
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_VACUNA_ID, notification)
    }
}
