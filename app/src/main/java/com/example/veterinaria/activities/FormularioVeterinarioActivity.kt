package com.example.veterinaria.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModelProvider
import com.example.veterinaria.VeterinariaApplication
import com.example.veterinaria.ui.VeterinarioFormScreen
import com.example.veterinaria.ui.viewmodel.ViewModelFactory
import com.example.veterinaria.viewmodel.MainViewModel
import androidx.navigation.compose.rememberNavController

/**
 * Activity dedicada para el formulario de registro y edición de veterinarios.
 * Permite ingresar datos como nombre, especialidad, años de experiencia
 * y horarios de atención. Incluye validación de campos.
 */
class FormularioVeterinarioActivity : ComponentActivity() {

    // ViewModel que gestiona la lógica del formulario
    private lateinit var viewModel: MainViewModel

    // ID del veterinario a editar (null si es un registro nuevo)
    private var veterinarioId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperar parámetros enviados desde la Activity anterior
        veterinarioId = intent.getIntExtra("VETERINARIO_ID", 0).takeIf { it != 0 }

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
                    VeterinarioFormScreen(
                        viewModel,
                        navController,
                        veterinarioId
                    )
                }
            }
        }
    }

    /**
     * Guarda el veterinario y regresa a la lista.
     */
    private fun guardarYVolver() {
        finish()
    }

    /**
     * Cancela la operación y vuelve a la pantalla anterior.
     */
    private fun cancelar() {
        finish()
    }
}
