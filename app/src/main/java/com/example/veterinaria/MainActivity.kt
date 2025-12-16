package com.example.veterinaria

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.veterinaria.services.SincronizacionService
import com.example.veterinaria.ui.AgendaSelectionScreen
import com.example.veterinaria.ui.CalendarioScreen
import com.example.veterinaria.ui.ConsultaFormScreen
import com.example.veterinaria.ui.DuenoFormScreen
import com.example.veterinaria.ui.DuenosScreen
import com.example.veterinaria.ui.HomeScreen
import com.example.veterinaria.ui.MascotaFormScreen
import com.example.veterinaria.ui.MascotasScreen
import com.example.veterinaria.ui.TopBar
import com.example.veterinaria.ui.VeterinarioFormScreen
import com.example.veterinaria.ui.VeterinariosScreen
import com.example.veterinaria.ui.viewmodel.HomeViewModel
import com.example.veterinaria.viewmodel.MainViewModel

/**
 * Actividad principal de la aplicación Veterinaria Animales Fantásticos.
 *
 * Esta actividad sirve como punto de entrada de la aplicación y gestiona:
 * - Configuración del tema Material Design 3
 * - Sistema de navegación con Jetpack Compose
 * - Inicio de servicios en segundo plano
 * - Manejo de intents externos
 *
 * La aplicación sigue el patrón MVVM (Model-View-ViewModel) con arquitectura limpia,
 * separando claramente las responsabilidades entre capas.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Iniciar el servicio de sincronización en segundo plano
        iniciarServicioSincronizacion()

        // Configurar la interfaz de usuario con Jetpack Compose
        setContent {
            // Aplicar el tema Material Design 3
            MaterialTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    VeterinariaApp()
                }
            }
        }

        // Manejar intents externos si los hay
        manejarIntentExterno(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Manejar nuevos intents cuando la actividad ya está activa
        intent?.let { manejarIntentExterno(it) }
    }

    /**
     * Inicia el servicio de sincronización en segundo plano.
     * Este servicio se encarga de sincronizar datos, enviar recordatorios, etc.
     */
    private fun iniciarServicioSincronizacion() {
        val intent = Intent(this, SincronizacionService::class.java).apply {
            action = SincronizacionService.ACTION_INICIAR_SYNC
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // En Android 8.0+ se requiere iniciar como foreground service
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (e: Exception) {
            // Registrar el error pero no bloquear la aplicación
            e.printStackTrace()
        }
    }

    /**
     * Maneja intents externos que pueden abrir la aplicación.
     * Por ejemplo, enlaces desde el navegador o compartir desde otras apps.
     *
     * @param intent Intent recibido
     */
    private fun manejarIntentExterno(intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_VIEW -> {
                // Manejar enlaces tipo veterinaria://mascotas o https://veterinaria.example.com
                val data = intent.data
                // Se navega a la pantalla correspondiente según la URI recibida
            }
            Intent.ACTION_SEND -> {
                // Manejar cuando otras apps comparten datos con esta app
                if (intent.type == "text/plain") {
                    val textoCompartido = intent.getStringExtra(Intent.EXTRA_TEXT)
                    // Procesar el texto compartido
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // El servicio continúa ejecutándose en segundo plano
        // Si se desea detenerlo, descomentar la siguiente línea:
        // stopService(Intent(this, SincronizacionService::class.java))
    }
}

/**
 * Composable principal que configura la navegación de la aplicación.
 * Utiliza ViewModels independientes para cada pantalla siguiendo MVVM.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeterinariaApp() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()

    Scaffold(
        topBar = {
            TopBar(onNavigateTo = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            })
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) {
            composable("home") {
                HomeScreen(onNavigate = { route -> navController.navigate(route) })
            }
            composable("mascotas") {
                MascotasScreen(viewModel, navController)
            }
            composable("duenos") {
                DuenosScreen(viewModel, navController)
            }
            composable("veterinarios") {
                VeterinariosScreen(viewModel, navController)
            }
            composable("agenda") {
                AgendaSelectionScreen(viewModel, navController)
            }
            composable("calendario") {
                CalendarioScreen(viewModel)
            }

            composable(
                "mascotaForm/{mascotaId}?duenoId={duenoId}",
                arguments = listOf(
                    navArgument("mascotaId") { type = NavType.IntType },
                    navArgument("duenoId") { type = NavType.StringType; nullable = true }
                )
            ) { backStackEntry ->
                val mascotaId = backStackEntry.arguments?.getInt("mascotaId")
                val duenoId = backStackEntry.arguments?.getString("duenoId")
                MascotaFormScreen(
                    viewModel,
                    navController,
                    if (mascotaId == 0) null else mascotaId,
                    duenoId
                )
            }
            composable(
                "duenoForm/{duenoId}",
                arguments = listOf(navArgument("duenoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val duenoId = backStackEntry.arguments?.getString("duenoId")
                DuenoFormScreen(
                    viewModel,
                    navController,
                    if (duenoId == "0") null else duenoId
                )
            }
            composable(
                "veterinarioForm/{veterinarioId}",
                arguments = listOf(navArgument("veterinarioId") { type = NavType.IntType })
            ) { backStackEntry ->
                val veterinarioId = backStackEntry.arguments?.getInt("veterinarioId")
                VeterinarioFormScreen(
                    viewModel,
                    navController,
                    if (veterinarioId == 0) null else veterinarioId
                )
            }
            composable(
                "consultaForm/{consultaId}?duenoId={duenoId}",
                arguments = listOf(
                    navArgument("consultaId") { type = NavType.IntType },
                    navArgument("duenoId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val consultaId = backStackEntry.arguments?.getInt("consultaId")
                val duenoId = backStackEntry.arguments?.getString("duenoId")!!
                ConsultaFormScreen(
                    viewModel,
                    navController,
                    if (consultaId == 0) null else consultaId,
                    duenoId
                )
            }
        }
    }
}
