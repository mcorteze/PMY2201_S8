package com.example.veterinaria

import android.app.Application
import com.example.veterinaria.data.model.Consulta
import com.example.veterinaria.data.model.Dueño
import com.example.veterinaria.data.model.Mascota
import com.example.veterinaria.data.model.Veterinario
import com.example.veterinaria.data.repository.ConsultaRepository
import com.example.veterinaria.data.repository.ConsultaRepositoryImpl
import com.example.veterinaria.data.repository.DuenoRepository
import com.example.veterinaria.data.repository.DuenoRepositoryImpl
import com.example.veterinaria.data.repository.MascotaRepository
import com.example.veterinaria.data.repository.MascotaRepositoryImpl
import com.example.veterinaria.data.repository.VeterinarioRepository
import com.example.veterinaria.data.repository.VeterinarioRepositoryImpl
import com.example.veterinaria.ui.viewmodel.ViewModelFactory
import java.time.LocalDate

/**
 * Clase Application que gestiona el ciclo de vida de la aplicación.
 * Implementa el patrón Singleton para los repositorios.
 */
class VeterinariaApplication : Application() {

    lateinit var mascotaRepository: MascotaRepository
        private set

    lateinit var duenoRepository: DuenoRepository
        private set

    lateinit var consultaRepository: ConsultaRepository
        private set

    lateinit var veterinarioRepository: VeterinarioRepository
        private set

    lateinit var viewModelFactory: ViewModelFactory
        private set

    override fun onCreate() {
        super.onCreate()
        inicializarRepositorios()
        inicializarDatosDePrueba()
        crearViewModelFactory()
    }

    /**
     * Inicializa las instancias de los repositorios.
     * Aplica el patrón Singleton a nivel de aplicación.
     */
    private fun inicializarRepositorios() {
        mascotaRepository = MascotaRepositoryImpl()
        duenoRepository = DuenoRepositoryImpl()
        consultaRepository = ConsultaRepositoryImpl()
        veterinarioRepository = VeterinarioRepositoryImpl()
    }

    /**
     * Carga datos iniciales para demostración.
     */
    private fun inicializarDatosDePrueba() {
        val dueno1 = Dueño("1-1", "Juan Pérez", "+56 9 1234 5678", "juan@email.com")
        val dueno2 = Dueño("2-2", "María López", "+56 9 8765 4321", "maria@email.com")
        (duenoRepository as DuenoRepositoryImpl).initializeData(listOf(dueno1, dueno2))

        val mascota1 = Mascota(1, dueno1.id, "Firulais", "Perro", 5, 12.5)
        val mascota2 = Mascota(2, dueno1.id, "Michi", "Gato", 3, 4.0)
        val mascota3 = Mascota(3, dueno2.id, "Rex", "Perro", 2, 8.0)
        (mascotaRepository as MascotaRepositoryImpl).initializeData(
            listOf(mascota1, mascota2, mascota3)
        )

        val consulta1 = Consulta(
            1, mascota1.id, dueno1.id,
            "Vacunación antirrábica", 15000.0, LocalDate.now().minusDays(2)
        )
        val consulta2 = Consulta(
            2, mascota2.id, dueno1.id,
            "Revisión General", 20000.0, LocalDate.now()
        )
        (consultaRepository as ConsultaRepositoryImpl).initializeData(
            listOf(consulta1, consulta2)
        )

        val vet1 = Veterinario(1, "Dr. Smith", "Cardiología")
        val vet2 = Veterinario(2, "Dra. Jones", "Dermatología")
        (veterinarioRepository as VeterinarioRepositoryImpl).initializeData(
            listOf(vet1, vet2)
        )
    }

    /**
     * Crea la factory de ViewModels con las dependencias inyectadas.
     */
    private fun crearViewModelFactory() {
        viewModelFactory = ViewModelFactory(
            mascotaRepository,
            duenoRepository,
            consultaRepository,
            veterinarioRepository
        )
    }
}
