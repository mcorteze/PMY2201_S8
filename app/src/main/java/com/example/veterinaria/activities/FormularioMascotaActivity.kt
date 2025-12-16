package com.example.veterinaria.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModelProvider
import com.example.veterinaria.VeterinariaApplication
import com.example.veterinaria.ui.MascotaFormScreen
import com.example.veterinaria.ui.viewmodel.ViewModelFactory
import com.example.veterinaria.viewmodel.MainViewModel
import androidx.navigation.compose.rememberNavController

/**
 * Activity dedicada para el formulario de registro y edición de mascotas.
 * Permite crear nuevas mascotas o modificar los datos de una existente.
 * Incluye validación de campos y gestión de errores.
 */
class FormularioMascotaActivity : ComponentActivity() {

    // ViewModel que gestiona la lógica del formulario
    private lateinit var viewModel: MainViewModel

    // ID de la mascota a editar (null si es un registro nuevo)
    private var mascotaId: Int? = null

    // ID del dueño asociado (opcional)
    private var duenoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperar parámetros enviados desde la Activity anterior
        mascotaId = intent.getIntExtra("MASCOTA_ID", 0).takeIf { it != 0 }
        duenoId = intent.getStringExtra("DUENO_ID")

        // Obtener la instancia de la aplicación
        val application = application as VeterinariaApplication

        // Crear el ViewModelFactory con los repositorios
        val factory = ViewModelFactory(
            application.mascotaRepository,
            application.duenoRepository,
            application.consultaRepository,
            application.veterinarioRepository
        )

        // Inicializar el ViewModel
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        // Configurar la interfaz de usuario
        setContent {
            MaterialTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    MascotaFormScreen(
                        viewModel,
                        navController,
                        mascotaId,
                        duenoId
                    )
                }
            }
        }
    }

    /**
     * Guarda la mascota y regresa a la lista.
     * Este método puede ser llamado desde la UI.
     */
    private fun guardarYVolver() {
        // La lógica de guardado está en el ViewModel
        // Aquí solo navegamos de regreso
        finish()
    }

    /**
     * Cancela la operación y vuelve a la pantalla anterior.
     */
    private fun cancelar() {
        finish()
    }

    /**
     * Comparte la información de la mascota con otras aplicaciones.
     * Utiliza un Intent implícito para compartir.
     */
    private fun compartirMascota(nombreMascota: String, especie: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Información de Mascota")
            putExtra(Intent.EXTRA_TEXT, "Mascota: $nombreMascota\nEspecie: $especie")
        }
        startActivity(Intent.createChooser(intent, "Compartir información de mascota"))
    }
}
