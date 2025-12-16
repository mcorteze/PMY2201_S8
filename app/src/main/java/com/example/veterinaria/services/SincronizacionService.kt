package com.example.veterinaria.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.veterinaria.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Service que se ejecuta en segundo plano para realizar tareas de sincronización.
 * Este servicio realiza operaciones como:
 * - Respaldo automático de datos
 * - Limpieza de datos temporales
 * - Verificación de citas pendientes
 * - Notificaciones de recordatorios
 *
 * El servicio continúa ejecutándose incluso cuando la aplicación está en segundo plano.
 */
class SincronizacionService : Service() {

    companion object {
        private const val TAG = "SincronizacionService"
        private const val CHANNEL_ID = "VeterinariaSync"
        private const val NOTIFICATION_ID = 1001

        // Intervalo de sincronización en milisegundos (5 minutos)
        private const val INTERVALO_SINCRONIZACION = 5 * 60 * 1000L

        // Acciones que puede realizar el servicio
        const val ACTION_INICIAR_SYNC = "com.example.veterinaria.INICIAR_SYNC"
        const val ACTION_DETENER_SYNC = "com.example.veterinaria.DETENER_SYNC"
    }

    // Handler para programar tareas periódicas
    private val handler = Handler(Looper.getMainLooper())

    // Variable para controlar si el servicio está activo
    private var servicioActivo = false

    // Contador de sincronizaciones realizadas
    private var contadorSync = 0

    /**
     * Tarea periódica que se ejecuta cada cierto intervalo.
     * Realiza la sincronización de datos en segundo plano.
     */
    private val tareaSincronizacion = object : Runnable {
        override fun run() {
            if (servicioActivo) {
                realizarSincronizacion()
                // Volver a programar la tarea
                handler.postDelayed(this, INTERVALO_SINCRONIZACION)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Servicio de sincronización creado")
        crearCanalNotificacion()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Servicio iniciado con acción: ${intent?.action}")

        when (intent?.action) {
            ACTION_INICIAR_SYNC -> {
                iniciarSincronizacion()
            }
            ACTION_DETENER_SYNC -> {
                detenerSincronizacion()
            }
            else -> {
                iniciarSincronizacion()
            }
        }

        // Si el sistema mata el servicio, lo reinicia automáticamente
        return START_STICKY
    }

    /**
     * Inicia el proceso de sincronización periódica.
     * Muestra una notificación para indicar que el servicio está activo.
     */
    private fun iniciarSincronizacion() {
        if (servicioActivo) {
            Log.d(TAG, "La sincronización ya está activa")
            return
        }

        servicioActivo = true
        contadorSync = 0

        // Crear notificación para mostrar que el servicio está activo
        val notificacion = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Veterinaria - Sincronización Activa")
            .setContentText("Sincronizando datos en segundo plano...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        // Iniciar como servicio foreground para evitar que sea detenido
        startForeground(NOTIFICATION_ID, notificacion)

        // Iniciar la tarea periódica
        handler.post(tareaSincronizacion)

        Log.d(TAG, "Sincronización iniciada")
    }

    /**
     * Detiene el proceso de sincronización.
     * Cancela las tareas programadas y detiene la notificación.
     */
    private fun detenerSincronizacion() {
        servicioActivo = false
        handler.removeCallbacks(tareaSincronizacion)
        stopForeground(true)
        Log.d(TAG, "Sincronización detenida. Total de sincronizaciones: $contadorSync")
    }

    /**
     * Realiza las operaciones de sincronización.
     * En una implementación real, aquí se harían llamadas a APIs,
     * respaldos de base de datos, etc.
     */
    private fun realizarSincronizacion() {
        contadorSync++
        val horaActual = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        Log.d(TAG, "Sincronización #$contadorSync realizada a las $horaActual")

        // Simular operaciones de sincronización
        try {
            // Aquí se pueden realizar tareas como:
            // 1. Verificar citas pendientes para hoy
            verificarCitasPendientes()

            // 2. Limpiar datos temporales antiguos
            limpiarDatosTemporales()

            // 3. Realizar respaldo de datos críticos
            respaldarDatos()

            // 4. Enviar notificaciones de recordatorio
            enviarRecordatorios()

            // Actualizar la notificación con el estado
            actualizarNotificacion("Última sincronización: $horaActual")

        } catch (e: Exception) {
            Log.e(TAG, "Error durante la sincronización", e)
        }
    }

    /**
     * Verifica si hay citas pendientes para el día actual.
     */
    private fun verificarCitasPendientes() {
        Log.d(TAG, "Verificando citas pendientes...")
        // Lógica para verificar citas pendientes
    }

    /**
     * Limpia datos temporales que ya no son necesarios.
     */
    private fun limpiarDatosTemporales() {
        Log.d(TAG, "Limpiando datos temporales...")
        // Lógica para limpiar caché y datos temporales
    }

    /**
     * Realiza un respaldo de los datos importantes.
     */
    private fun respaldarDatos() {
        Log.d(TAG, "Respaldando datos...")
        // Lógica para respaldar datos en almacenamiento local o nube
    }

    /**
     * Envía notificaciones de recordatorio a los usuarios.
     */
    private fun enviarRecordatorios() {
        Log.d(TAG, "Enviando recordatorios...")
        // Lógica para enviar notificaciones de recordatorios
    }

    /**
     * Actualiza el contenido de la notificación del servicio.
     */
    private fun actualizarNotificacion(mensaje: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificacion = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Veterinaria - Sincronización")
            .setContentText(mensaje)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notificacion)
    }

    /**
     * Crea el canal de notificaciones necesario para Android 8.0 y superior.
     */
    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Sincronización de Veterinaria"
            val descripcion = "Notificaciones del servicio de sincronización"
            val importancia = NotificationManager.IMPORTANCE_LOW

            val canal = NotificationChannel(CHANNEL_ID, nombre, importancia).apply {
                description = descripcion
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Este servicio no permite binding
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        detenerSincronizacion()
        Log.d(TAG, "Servicio de sincronización destruido")
    }
}
