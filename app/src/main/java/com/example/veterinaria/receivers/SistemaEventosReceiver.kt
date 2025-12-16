package com.example.veterinaria.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.veterinaria.R
import com.example.veterinaria.services.SincronizacionService

/**
 * Broadcast Receiver que escucha y responde a eventos del sistema Android.
 *
 * Eventos que maneja:
 * - Cambios en la conectividad de red (WiFi, datos móviles)
 * - Estado de carga de la batería
 * - Inicio del dispositivo (BOOT_COMPLETED)
 * - Cambios en el modo avión
 * - Alarmas y recordatorios programados
 *
 * Este componente permite que la aplicación reaccione a eventos del sistema
 * incluso cuando está cerrada o en segundo plano.
 */
class SistemaEventosReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "SistemaEventosReceiver"
        private const val CHANNEL_ID = "VeterinariaEventos"
        private const val NOTIFICATION_ID = 2001

        // Acciones personalizadas
        const val ACTION_RECORDATORIO_CITA = "com.example.veterinaria.RECORDATORIO_CITA"
        const val ACTION_SINCRONIZAR_DATOS = "com.example.veterinaria.SINCRONIZAR_DATOS"
    }

    /**
     * Método principal que se ejecuta cuando se recibe un broadcast.
     * Identifica el tipo de evento y ejecuta la acción correspondiente.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val accion = intent.action
        Log.d(TAG, "Broadcast recibido: $accion")

        when (accion) {
            // Evento: Dispositivo iniciado
            Intent.ACTION_BOOT_COMPLETED -> {
                manejarInicioDispositivo(context)
            }

            // Evento: Conectividad de red cambió
            ConnectivityManager.CONNECTIVITY_ACTION,
            "android.net.conn.CONNECTIVITY_CHANGE" -> {
                manejarCambioConectividad(context)
            }

            // Evento: Batería baja
            Intent.ACTION_BATTERY_LOW -> {
                manejarBateriaBaja(context)
            }

            // Evento: Batería OK (se recuperó de batería baja)
            Intent.ACTION_BATTERY_OKAY -> {
                manejarBateriaOK(context)
            }

            // Evento: Batería en carga
            Intent.ACTION_POWER_CONNECTED -> {
                manejarConexionEnergia(context)
            }

            // Evento: Batería desconectada de la carga
            Intent.ACTION_POWER_DISCONNECTED -> {
                manejarDesconexionEnergia(context)
            }

            // Evento: Modo avión activado/desactivado
            Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                manejarCambioModoAvion(context, intent)
            }

            // Eventos personalizados de la aplicación
            ACTION_RECORDATORIO_CITA -> {
                manejarRecordatorioCita(context, intent)
            }

            ACTION_SINCRONIZAR_DATOS -> {
                manejarSincronizacionDatos(context)
            }

            else -> {
                Log.d(TAG, "Acción no manejada: $accion")
            }
        }
    }

    /**
     * Maneja el evento de inicio del dispositivo.
     * Reinicia servicios y configuraciones necesarias.
     */
    private fun manejarInicioDispositivo(context: Context) {
        Log.d(TAG, "Dispositivo iniciado - Configurando servicios")

        // Iniciar el servicio de sincronización
        val intent = Intent(context, SincronizacionService::class.java).apply {
            action = SincronizacionService.ACTION_INICIAR_SYNC
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        mostrarNotificacion(
            context,
            "Veterinaria iniciada",
            "Los servicios de la aplicación están activos"
        )
    }

    /**
     * Maneja cambios en la conectividad de red.
     * Útil para sincronizar datos cuando hay conexión disponible.
     */
    private fun manejarCambioConectividad(context: Context) {
        val conectado = verificarConectividad(context)

        if (conectado) {
            Log.d(TAG, "Conexión a Internet disponible")

            // Iniciar sincronización automática
            val intent = Intent(context, SincronizacionService::class.java).apply {
                action = SincronizacionService.ACTION_INICIAR_SYNC
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }

            mostrarNotificacion(
                context,
                "Conexión restaurada",
                "Sincronizando datos de la veterinaria"
            )
        } else {
            Log.d(TAG, "Sin conexión a Internet")
            mostrarNotificacion(
                context,
                "Sin conexión",
                "Trabajando en modo offline"
            )
        }
    }

    /**
     * Maneja el evento de batería baja.
     * Puede detener servicios no críticos para ahorrar energía.
     */
    private fun manejarBateriaBaja(context: Context) {
        Log.d(TAG, "Batería baja detectada - Pausando servicios no críticos")

        // Detener servicio de sincronización para ahorrar batería
        val intent = Intent(context, SincronizacionService::class.java).apply {
            action = SincronizacionService.ACTION_DETENER_SYNC
        }
        context.startService(intent)

        mostrarNotificacion(
            context,
            "Batería baja",
            "Servicios pausados para ahorrar energía"
        )
    }

    /**
     * Maneja el evento de batería recuperada.
     * Reinicia servicios que fueron pausados.
     */
    private fun manejarBateriaOK(context: Context) {
        Log.d(TAG, "Batería recuperada - Reanudando servicios")

        // Reiniciar servicio de sincronización
        val intent = Intent(context, SincronizacionService::class.java).apply {
            action = SincronizacionService.ACTION_INICIAR_SYNC
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    /**
     * Maneja el evento de conexión a la energía eléctrica.
     * Buen momento para realizar tareas intensivas.
     */
    private fun manejarConexionEnergia(context: Context) {
        Log.d(TAG, "Dispositivo conectado a la corriente")

        // Aquí se pueden realizar tareas pesadas como respaldo completo
        mostrarNotificacion(
            context,
            "Dispositivo en carga",
            "Optimizando datos de la veterinaria"
        )
    }

    /**
     * Maneja el evento de desconexión de la energía eléctrica.
     */
    private fun manejarDesconexionEnergia(context: Context) {
        Log.d(TAG, "Dispositivo desconectado de la corriente")
    }

    /**
     * Maneja cambios en el modo avión.
     */
    private fun manejarCambioModoAvion(context: Context, intent: Intent) {
        val modoAvionActivado = intent.getBooleanExtra("state", false)

        if (modoAvionActivado) {
            Log.d(TAG, "Modo avión activado")
            mostrarNotificacion(
                context,
                "Modo avión activado",
                "Trabajando en modo offline"
            )
        } else {
            Log.d(TAG, "Modo avión desactivado")
        }
    }

    /**
     * Maneja recordatorios de citas programadas.
     */
    private fun manejarRecordatorioCita(context: Context, intent: Intent) {
        val nombreMascota = intent.getStringExtra("NOMBRE_MASCOTA") ?: "Mascota"
        val horaCita = intent.getStringExtra("HORA_CITA") ?: "Pronto"

        Log.d(TAG, "Recordatorio de cita para: $nombreMascota")

        mostrarNotificacion(
            context,
            "Recordatorio de Cita",
            "Cita de $nombreMascota a las $horaCita"
        )
    }

    /**
     * Maneja la sincronización manual de datos.
     */
    private fun manejarSincronizacionDatos(context: Context) {
        Log.d(TAG, "Iniciando sincronización manual de datos")

        val intent = Intent(context, SincronizacionService::class.java).apply {
            action = SincronizacionService.ACTION_INICIAR_SYNC
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    /**
     * Verifica si hay conectividad a Internet disponible.
     */
    private fun verificarConectividad(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            networkInfo?.isConnected == true
        }
    }

    /**
     * Muestra una notificación al usuario.
     */
    private fun mostrarNotificacion(context: Context, titulo: String, mensaje: String) {
        // Crear canal de notificación si es necesario
        crearCanalNotificacion(context)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificacion = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notificacion)
    }

    /**
     * Crea el canal de notificaciones para Android 8.0 y superior.
     */
    private fun crearCanalNotificacion(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Eventos del Sistema"
            val descripcion = "Notificaciones de eventos del sistema"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT

            val canal = NotificationChannel(CHANNEL_ID, nombre, importancia).apply {
                description = descripcion
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }
}
