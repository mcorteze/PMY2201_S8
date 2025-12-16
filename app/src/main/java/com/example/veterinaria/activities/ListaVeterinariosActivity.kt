package com.example.veterinaria.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModelProvider
import com.example.veterinaria.VeterinariaApplication
import com.example.veterinaria.ui.VeterinariosScreen
import com.example.veterinaria.ui.viewmodel.ViewModelFactory
import com.example.veterinaria.viewmodel.MainViewModel
import androidx.navigation.compose.rememberNavController

/**
 * Activity dedicada para mostrar el listado de veterinarios.
 * Muestra todos los veterinarios registrados con sus especialidades,
 * años de experiencia y horarios de atención.
 */
class ListaVeterinariosActivity : ComponentActivity() {

    // ViewModel que gestiona el estado de los veterinarios
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener la instancia de la aplicación
        val application = application as VeterinariaApplication

        // Crear el ViewModelFactory con los repositorios necesarios
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
                    VeterinariosScreen(viewModel, navController)
                }
            }
        }
    }

    /**
     * Navega a la pantalla de formulario para crear un nuevo veterinario.
     */
    private fun navegarAFormulario() {
        val intent = Intent(this, FormularioVeterinarioActivity::class.java)
        intent.putExtra("VETERINARIO_ID", 0)
        startActivity(intent)
    }
}
