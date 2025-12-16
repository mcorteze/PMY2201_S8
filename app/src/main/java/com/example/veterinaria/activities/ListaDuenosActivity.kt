package com.example.veterinaria.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModelProvider
import com.example.veterinaria.VeterinariaApplication
import com.example.veterinaria.ui.DuenosScreen
import com.example.veterinaria.ui.viewmodel.ViewModelFactory
import com.example.veterinaria.viewmodel.MainViewModel
import androidx.navigation.compose.rememberNavController

/**
 * Activity dedicada para mostrar el listado de dueños registrados.
 * Permite visualizar información de los dueños, sus mascotas asociadas,
 * editar datos y registrar nuevos dueños en el sistema.
 */
class ListaDuenosActivity : ComponentActivity() {

    // ViewModel que gestiona el estado de los dueños
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
                    DuenosScreen(viewModel, navController)
                }
            }
        }
    }

    /**
     * Navega a la pantalla de formulario para crear un nuevo dueño.
     * Utiliza un Intent explícito para abrir FormularioDuenoActivity.
     */
    private fun navegarAFormulario() {
        val intent = Intent(this, FormularioDuenoActivity::class.java)
        intent.putExtra("DUENO_ID", "0")
        startActivity(intent)
    }

    /**
     * Navega a la lista de mascotas asociadas a un dueño específico.
     */
    private fun verMascotasDueno(duenoId: String) {
        val intent = Intent(this, ListaMascotasActivity::class.java)
        intent.putExtra("DUENO_ID", duenoId)
        startActivity(intent)
    }
}
