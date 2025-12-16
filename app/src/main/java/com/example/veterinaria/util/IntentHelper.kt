package com.example.veterinaria.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.veterinaria.data.model.Dueño
import com.example.veterinaria.data.model.Mascota

/**
 * Clase utilitaria para gestionar Intents implícitos y explícitos.
 * Facilita la creación de intents para compartir información,
 * abrir enlaces, enviar correos, etc.
 */
object IntentHelper {

    /**
     * Comparte información de una mascota usando un Intent implícito.
     * Permite al usuario elegir la aplicación con la que desea compartir.
     *
     * @param context Contexto de la aplicación
     * @param mascota Mascota cuya información se compartirá
     */
    fun compartirMascota(context: Context, mascota: Mascota) {
        val texto = construirTextoMascota(mascota)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Información de Mascota - ${mascota.nombre}")
            putExtra(Intent.EXTRA_TEXT, texto)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Compartir información de ${mascota.nombre}"))
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo compartir la información", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Comparte información de un dueño usando un Intent implícito.
     *
     * @param context Contexto de la aplicación
     * @param dueno Dueño cuya información se compartirá
     */
    fun compartirDueno(context: Context, dueno: Dueño) {
        val texto = construirTextoDueno(dueno)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Información de Dueño - ${dueno.nombre}")
            putExtra(Intent.EXTRA_TEXT, texto)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Compartir información de ${dueno.nombre}"))
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo compartir la información", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Abre el marcador telefónico con el número proporcionado.
     * Utiliza un Intent implícito ACTION_DIAL.
     *
     * @param context Contexto de la aplicación
     * @param telefono Número de teléfono a marcar
     */
    fun llamarTelefono(context: Context, telefono: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$telefono")
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir el marcador", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Abre la aplicación de correo para enviar un email.
     * Utiliza un Intent implícito ACTION_SENDTO.
     *
     * @param context Contexto de la aplicación
     * @param correo Dirección de correo electrónico
     * @param asunto Asunto del correo
     * @param mensaje Cuerpo del mensaje
     */
    fun enviarCorreo(context: Context, correo: String, asunto: String = "", mensaje: String = "") {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(correo))
            putExtra(Intent.EXTRA_SUBJECT, asunto)
            putExtra(Intent.EXTRA_TEXT, mensaje)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Enviar correo a $correo"))
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir la aplicación de correo", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Abre una dirección en la aplicación de mapas.
     * Utiliza un Intent implícito con geo URI.
     *
     * @param context Contexto de la aplicación
     * @param direccion Dirección a buscar
     */
    fun abrirMapa(context: Context, direccion: String) {
        val uri = Uri.parse("geo:0,0?q=${Uri.encode(direccion)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir la aplicación de mapas", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Abre un enlace web en el navegador.
     * Utiliza un Intent implícito ACTION_VIEW.
     *
     * @param context Contexto de la aplicación
     * @param url URL del sitio web
     */
    fun abrirEnlaceWeb(context: Context, url: String) {
        val uri = Uri.parse(if (url.startsWith("http")) url else "https://$url")
        val intent = Intent(Intent.ACTION_VIEW, uri)

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir el navegador", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Comparte múltiples mascotas como texto.
     *
     * @param context Contexto de la aplicación
     * @param mascotas Lista de mascotas a compartir
     */
    fun compartirListaMascotas(context: Context, mascotas: List<Mascota>) {
        if (mascotas.isEmpty()) {
            Toast.makeText(context, "No hay mascotas para compartir", Toast.LENGTH_SHORT).show()
            return
        }

        val texto = buildString {
            appendLine("Lista de Mascotas Registradas")
            appendLine()
            mascotas.forEachIndexed { index, mascota ->
                appendLine("${index + 1}. ${mascota.nombre}")
                appendLine("   Especie: ${mascota.especie}")
                appendLine("   Edad: ${mascota.edad} años")
                appendLine()
            }
            appendLine("Total: ${mascotas.size} mascotas")
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Lista de Mascotas")
            putExtra(Intent.EXTRA_TEXT, texto)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Compartir lista de mascotas"))
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo compartir la lista", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Construye el texto formateado con la información de una mascota.
     */
    private fun construirTextoMascota(mascota: Mascota): String {
        return buildString {
            appendLine("Información de Mascota")
            appendLine()
            appendLine("Nombre: ${mascota.nombre}")
            appendLine("Especie: ${mascota.especie}")
            appendLine("Edad: ${mascota.edad} años")
            appendLine("Peso: ${mascota.peso} kg")
            if (mascota.raza.isNotBlank()) {
                appendLine("Raza: ${mascota.raza}")
            }
            appendLine()
            appendLine("Compartido desde Veterinaria Animales Fantásticos")
        }
    }

    /**
     * Construye el texto formateado con la información de un dueño.
     */
    private fun construirTextoDueno(dueno: Dueño): String {
        return buildString {
            appendLine("Información de Dueño")
            appendLine()
            appendLine("Nombre: ${dueno.nombre}")
            appendLine("RUT: ${dueno.id}")
            appendLine("Teléfono: ${dueno.telefono}")
            if (dueno.email.isNotBlank()) {
                appendLine("Email: ${dueno.email}")
            }
            appendLine()
            appendLine("Compartido desde Veterinaria Animales Fantásticos")
        }
    }

    /**
     * Crea un Intent para abrir una Activity específica de la aplicación.
     * Este es un Intent explícito.
     *
     * @param context Contexto de la aplicación
     * @param activityClass Clase de la Activity a abrir
     * @param extras Map con los extras a pasar (opcional)
     */
    fun <T> crearIntentExplicito(
        context: Context,
        activityClass: Class<T>,
        extras: Map<String, Any>? = null
    ): Intent {
        return Intent(context, activityClass).apply {
            extras?.forEach { (key, value) ->
                when (value) {
                    is String -> putExtra(key, value)
                    is Int -> putExtra(key, value)
                    is Boolean -> putExtra(key, value)
                    is Long -> putExtra(key, value)
                    is Float -> putExtra(key, value)
                    is Double -> putExtra(key, value)
                }
            }
        }
    }

    /**
     * Verifica si hay una aplicación que pueda manejar el Intent.
     *
     * @param context Contexto de la aplicación
     * @param intent Intent a verificar
     * @return true si hay una app que puede manejarlo, false en caso contrario
     */
    fun puedeResolverIntent(context: Context, intent: Intent): Boolean {
        return intent.resolveActivity(context.packageManager) != null
    }
}
