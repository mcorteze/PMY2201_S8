package com.example.veterinaria.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModelProvider
import com.example.veterinaria.MainActivity
import com.example.veterinaria.VeterinariaApplication
import com.example.veterinaria.ui.MascotasScreen
import com.example.veterinaria.ui.viewmodel.ViewModelFactory
import com.example.veterinaria.viewmodel.MainViewModel
import androidx.navigation.compose.rememberNavController

/**
 * Activity dedicada para mostrar el listado de mascotas registradas.
 * Esta pantalla permite visualizar todas las mascotas, buscar por nombre,
 * editar datos existentes y registrar nuevas mascotas.
 */
class ListaMascotasActivity : ComponentActivity() {

    // ViewModel compartido que gestiona el estado de las mascotas
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener la instancia de la aplicación para acceder a los repositorios
        val application = application as VeterinariaApplication

        // Crear el ViewModelFactory con los repositorios necesarios
        val factory = ViewModelFactory(
            application.mascotaRepository,
            application.duenoRepository,
            application.consultaRepository,
            application.veterinarioRepository
        )

        // Inicializar el ViewModel usando el factory
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        // Configurar la interfaz de usuario con Jetpack Compose
        setContent {
            MaterialTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    MascotasScreen(viewModel, navController)
                }
            }
        }
    }

    /**
     * Navega a la pantalla de formulario para crear una nueva mascota.
     * Utiliza un Intent explícito para abrir FormularioMascotaActivity.
     */
    private fun navegarAFormulario() {
        val intent = Intent(this, FormularioMascotaActivity::class.java)
        // Se puede pasar el ID de la mascota para edición
        intent.putExtra("MASCOTA_ID", 0)
        startActivity(intent)
    }

    /**
     * Navega de regreso a la pantalla principal.
     * Limpia el stack de navegación para evitar acumulación.
     */
    private fun volverAHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}
