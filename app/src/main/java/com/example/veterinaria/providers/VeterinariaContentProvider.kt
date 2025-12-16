package com.example.veterinaria.providers

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import com.example.veterinaria.VeterinariaApplication
import com.example.veterinaria.data.repository.MascotaRepositoryImpl
import com.example.veterinaria.data.repository.DuenoRepositoryImpl

/**
 * Content Provider que permite compartir datos de la veterinaria con otras aplicaciones.
 * Proporciona acceso a información de mascotas y dueños de manera segura.
 *
 * Permite a otras aplicaciones consultar:
 * - Lista de mascotas registradas
 * - Información de dueños
 * - Datos específicos de una mascota o dueño
 *
 * URI de acceso:
 * - content://com.example.veterinaria.provider/mascotas
 * - content://com.example.veterinaria.provider/mascotas/[id]
 * - content://com.example.veterinaria.provider/duenos
 * - content://com.example.veterinaria.provider/duenos/[cedula]
 */
class VeterinariaContentProvider : ContentProvider() {

    companion object {
        private const val TAG = "VeterinariaProvider"

        // Autoridad del Content Provider
        const val AUTHORITY = "com.example.veterinaria.provider"

        // URIs base
        private val BASE_URI = Uri.parse("content://$AUTHORITY")

        // Códigos para el UriMatcher
        private const val MASCOTAS = 1
        private const val MASCOTA_ID = 2
        private const val DUENOS = 3
        private const val DUENO_CEDULA = 4

        // UriMatcher para identificar las URIs recibidas
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "mascotas", MASCOTAS)
            addURI(AUTHORITY, "mascotas/#", MASCOTA_ID)
            addURI(AUTHORITY, "duenos", DUENOS)
            addURI(AUTHORITY, "duenos/*", DUENO_CEDULA)
        }

        // URIs públicas para acceso externo
        val MASCOTAS_URI: Uri = Uri.withAppendedPath(BASE_URI, "mascotas")
        val DUENOS_URI: Uri = Uri.withAppendedPath(BASE_URI, "duenos")

        // Tipos MIME para las URIs
        const val MASCOTAS_TYPE = "vnd.android.cursor.dir/vnd.$AUTHORITY.mascotas"
        const val MASCOTA_TYPE = "vnd.android.cursor.item/vnd.$AUTHORITY.mascota"
        const val DUENOS_TYPE = "vnd.android.cursor.dir/vnd.$AUTHORITY.duenos"
        const val DUENO_TYPE = "vnd.android.cursor.item/vnd.$AUTHORITY.dueno"
    }

    /**
     * Se ejecuta cuando se crea el Content Provider.
     * Aquí se inicializan los recursos necesarios.
     */
    override fun onCreate(): Boolean {
        Log.d(TAG, "Content Provider inicializado")
        return true
    }

    /**
     * Determina el tipo MIME de los datos en la URI especificada.
     * Necesario para que otras aplicaciones sepan qué tipo de datos están consultando.
     */
    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            MASCOTAS -> MASCOTAS_TYPE
            MASCOTA_ID -> MASCOTA_TYPE
            DUENOS -> DUENOS_TYPE
            DUENO_CEDULA -> DUENO_TYPE
            else -> null
        }
    }

    /**
     * Consulta datos del Content Provider.
     * Otras aplicaciones pueden usar este método para obtener información.
     *
     * @param uri URI de los datos a consultar
     * @param projection Columnas a retornar
     * @param selection Cláusula WHERE
     * @param selectionArgs Argumentos para la cláusula WHERE
     * @param sortOrder Orden de clasificación
     * @return Cursor con los datos solicitados
     */
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d(TAG, "Consultando URI: $uri")

        val cursor: MatrixCursor

        when (uriMatcher.match(uri)) {
            MASCOTAS -> {
                // Consultar todas las mascotas
                cursor = MatrixCursor(arrayOf("id", "nombre", "especie", "edad", "dueno_cedula"))
                val repository = obtenerRepositorioMascotas() as? MascotaRepositoryImpl
                val mascotas = repository?.getAllSync() ?: emptyList()

                mascotas.forEach { mascota ->
                    cursor.addRow(arrayOf(
                        mascota.id,
                        mascota.nombre,
                        mascota.especie,
                        mascota.edad,
                        mascota.duenoCedula
                    ))
                }
                Log.d(TAG, "Retornando ${mascotas.size} mascotas")
            }

            MASCOTA_ID -> {
                // Consultar una mascota específica por ID
                val id = uri.lastPathSegment?.toIntOrNull()
                cursor = MatrixCursor(arrayOf("id", "nombre", "especie", "edad", "dueno_cedula"))

                if (id != null) {
                    val repository = obtenerRepositorioMascotas() as? MascotaRepositoryImpl
                    val mascota = repository?.getAllSync()?.find { it.id == id }
                    mascota?.let {
                        cursor.addRow(arrayOf(it.id, it.nombre, it.especie, it.edad, it.duenoCedula))
                    }
                    Log.d(TAG, "Retornando mascota con ID: $id")
                }
            }

            DUENOS -> {
                // Consultar todos los dueños
                cursor = MatrixCursor(arrayOf("id", "nombre", "telefono", "email"))
                val repository = obtenerRepositorioDuenos() as? DuenoRepositoryImpl
                val duenos = repository?.getAllSync() ?: emptyList()

                duenos.forEach { dueno ->
                    cursor.addRow(arrayOf(
                        dueno.id,
                        dueno.nombre,
                        dueno.telefono,
                        dueno.email
                    ))
                }
                Log.d(TAG, "Retornando ${duenos.size} dueños")
            }

            DUENO_CEDULA -> {
                // Consultar un dueño específico por ID
                val id = uri.lastPathSegment
                cursor = MatrixCursor(arrayOf("id", "nombre", "telefono", "email"))

                if (id != null) {
                    val repository = obtenerRepositorioDuenos() as? DuenoRepositoryImpl
                    val dueno = repository?.getAllSync()?.find { it.id == id }
                    dueno?.let {
                        cursor.addRow(arrayOf(it.id, it.nombre, it.telefono, it.email))
                    }
                    Log.d(TAG, "Retornando dueño con ID: $id")
                }
            }

            else -> {
                Log.e(TAG, "URI no reconocida: $uri")
                return null
            }
        }

        // Notificar a los observadores de cambios en los datos
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    /**
     * Inserta nuevos datos en el Content Provider.
     * Por seguridad, este método está restringido.
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d(TAG, "Intento de inserción en URI: $uri")
        // Por seguridad, no permitimos inserciones desde aplicaciones externas
        throw UnsupportedOperationException("Inserción no permitida por seguridad")
    }

    /**
     * Actualiza datos existentes en el Content Provider.
     * Por seguridad, este método está restringido.
     */
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        Log.d(TAG, "Intento de actualización en URI: $uri")
        // Por seguridad, no permitimos actualizaciones desde aplicaciones externas
        throw UnsupportedOperationException("Actualización no permitida por seguridad")
    }

    /**
     * Elimina datos del Content Provider.
     * Por seguridad, este método está restringido.
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.d(TAG, "Intento de eliminación en URI: $uri")
        // Por seguridad, no permitimos eliminaciones desde aplicaciones externas
        throw UnsupportedOperationException("Eliminación no permitida por seguridad")
    }

    /**
     * Obtiene el repositorio de mascotas desde la aplicación.
     */
    private fun obtenerRepositorioMascotas() =
        (context?.applicationContext as? VeterinariaApplication)?.mascotaRepository

    /**
     * Obtiene el repositorio de dueños desde la aplicación.
     */
    private fun obtenerRepositorioDuenos() =
        (context?.applicationContext as? VeterinariaApplication)?.duenoRepository
}
