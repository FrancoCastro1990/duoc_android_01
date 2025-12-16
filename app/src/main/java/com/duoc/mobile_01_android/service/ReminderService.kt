package com.duoc.mobile_01_android.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.duoc.mobile_01_android.MainActivity
import com.duoc.mobile_01_android.R

/**
 * Servicio en primer plano (Foreground Service) para gestionar recordatorios de consultas veterinarias.
 *
 * Este servicio muestra una notificación persistente que permite al usuario acceder rápidamente
 * a la aplicación de veterinaria. Se ejecuta en primer plano para garantizar que no sea
 * eliminado por el sistema operativo cuando hay restricciones de memoria.
 *
 * El servicio crea automáticamente un canal de notificaciones en Android O+ y maneja
 * las acciones START y STOP para controlar su ciclo de vida.
 *
 * @see Service
 */
class ReminderService : Service() {

    companion object {
        /**
         * Acción para iniciar el servicio de recordatorios.
         */
        const val ACTION_START = "com.duoc.mobile_01_android.service.ACTION_START"

        /**
         * Acción para detener el servicio de recordatorios.
         */
        const val ACTION_STOP = "com.duoc.mobile_01_android.service.ACTION_STOP"

        /**
         * ID de la notificación del servicio en primer plano.
         */
        private const val NOTIFICATION_ID = 1001

        /**
         * ID del canal de notificaciones para recordatorios.
         */
        private const val CHANNEL_ID = "veterinaria_reminder_channel"

        /**
         * Nombre visible del canal de notificaciones.
         */
        private const val CHANNEL_NAME = "Recordatorios Veterinaria"

        /**
         * Descripción del canal de notificaciones.
         */
        private const val CHANNEL_DESCRIPTION = "Notificaciones para recordatorios de consultas veterinarias"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startForegroundService()
            }
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        // START_STICKY: Si el sistema mata el servicio, lo recrea cuando hay recursos disponibles
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Este servicio no soporta binding
        return null
    }

    /**
     * Crea el canal de notificaciones requerido para Android O (API 26) y superior.
     *
     * Los canales de notificaciones permiten a los usuarios controlar las preferencias
     * de notificación por categoría. Este método es seguro llamarlo múltiples veces,
     * ya que el sistema ignora las llamadas duplicadas.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW // Importancia baja para no interrumpir al usuario
            ).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Inicia el servicio en primer plano mostrando una notificación persistente.
     *
     * La notificación incluye:
     * - Icono de la aplicación
     * - Título y texto descriptivo
     * - PendingIntent para abrir MainActivity al tocar la notificación
     *
     * Según las políticas de Android, un Foreground Service debe mostrar una notificación
     * visible al usuario mientras esté en ejecución.
     */
    private fun startForegroundService() {
        // Crear intent para abrir MainActivity al tocar la notificación
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE // Requerido para API 31+
        )

        // Construir la notificación usando NotificationCompat para compatibilidad
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Servicio de Recordatorios Activo")
            .setContentText("La aplicación está monitoreando tus consultas veterinarias")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Icono de mascota
            .setContentIntent(pendingIntent)
            .setOngoing(true) // La notificación no se puede deslizar para descartar
            .setPriority(NotificationCompat.PRIORITY_LOW) // Prioridad baja para Android 7.1 y anteriores
            .build()

        // Iniciar el servicio en primer plano con la notificación
        startForeground(NOTIFICATION_ID, notification)
    }
}
