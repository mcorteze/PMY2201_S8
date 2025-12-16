package com.example.veterinaria

import com.example.veterinaria.data.model.Mascota
import com.example.veterinaria.data.repository.ConsultaRepositoryImpl
import com.example.veterinaria.data.repository.DuenoRepositoryImpl
import com.example.veterinaria.data.repository.MascotaRepositoryImpl
import com.example.veterinaria.data.repository.VeterinarioRepositoryImpl
import com.example.veterinaria.ui.viewmodel.HomeViewModel
import com.example.veterinaria.ui.viewmodel.MascotaFormViewModel
import com.example.veterinaria.ui.viewmodel.MascotasViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Pruebas de integración del patrón MVVM.
 * Verifica la comunicación correcta entre Model, ViewModel y View.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MVVMIntegrationTests {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mascotaRepository: MascotaRepositoryImpl
    private lateinit var duenoRepository: DuenoRepositoryImpl
    private lateinit var consultaRepository: ConsultaRepositoryImpl
    private lateinit var veterinarioRepository: VeterinarioRepositoryImpl

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mascotaRepository = MascotaRepositoryImpl()
        duenoRepository = DuenoRepositoryImpl()
        consultaRepository = ConsultaRepositoryImpl()
        veterinarioRepository = VeterinarioRepositoryImpl()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `HomeViewModel carga estadisticas desde repositorios`() = runBlocking {
        mascotaRepository.add(Mascota(0, "1-1", "Firulais", "Perro", 5, 12.5))

        val viewModel = HomeViewModel(
            mascotaRepository,
            duenoRepository,
            consultaRepository,
            veterinarioRepository
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals("Debe mostrar 1 mascota", 1, state.totalMascotas)
        assertFalse("No debe estar cargando", state.isLoading)
    }

    @Test
    fun `MascotasViewModel refleja cambios del repositorio`() = runBlocking {
        mascotaRepository.add(Mascota(0, "1-1", "Firulais", "Perro", 5, 12.5))
        mascotaRepository.add(Mascota(0, "1-1", "Michi", "Gato", 3, 4.0))

        val viewModel = MascotasViewModel(mascotaRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals("Debe mostrar 2 mascotas", 2, state.mascotas.size)
    }

    @Test
    fun `MascotaFormViewModel valida datos antes de guardar`() = runBlocking {
        val viewModel = MascotaFormViewModel(mascotaRepository)

        viewModel.actualizarNombre("Fi")
        viewModel.actualizarEspecie("Perro")
        viewModel.actualizarEdad("5")
        viewModel.actualizarPeso("12.5")
        viewModel.establecerDueno("1-1")

        var guardadoExitoso = false
        viewModel.guardarMascota(null) { guardadoExitoso = true }

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertFalse("No debe guardar nombre muy corto", guardadoExitoso)
        assertNotNull("Debe mostrar error de validación", state.mensajeError)
    }

    @Test
    fun `MascotasViewModel elimina mascota del repositorio`() = runBlocking {
        val mascota = mascotaRepository.add(
            Mascota(0, "1-1", "Firulais", "Perro", 5, 12.5)
        ).getOrNull()!!

        val viewModel = MascotasViewModel(mascotaRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.eliminarMascota(mascota)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals("Debe tener 0 mascotas después de eliminar", 0, state.mascotas.size)
        assertNotNull("Debe mostrar mensaje de éxito", state.mensajeExito)
    }

    @Test
    fun `Cambios en repositorio se propagan a ViewModels reactivamente`() = runBlocking {
        val viewModel = MascotasViewModel(mascotaRepository)

        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("Debe iniciar vacío", 0, viewModel.uiState.first().mascotas.size)

        mascotaRepository.add(Mascota(0, "1-1", "Firulais", "Perro", 5, 12.5))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Debe reflejar el cambio", 1, viewModel.uiState.first().mascotas.size)
    }

    @Test
    fun `ViewModel solo expone estado inmutable a la vista`() {
        val viewModel = MascotasViewModel(mascotaRepository)

        val state1 = viewModel.uiState.value
        val state2 = viewModel.uiState.value

        assertSame("Debe ser el mismo estado", state1, state2)
    }

    @Test
    fun `Modelo valida antes de ser aceptado por repositorio`() = runBlocking {
        val mascotaInvalida = Mascota(0, "", "", "Perro", -5, -10.0)

        assertFalse("Mascota debe ser inválida", mascotaInvalida.esValida())

        val resultado = mascotaRepository.add(mascotaInvalida)

        assertTrue("Repositorio debe aceptar aunque sea inválida (validación en ViewModel)", resultado.isSuccess)
    }

    @Test
    fun `HomeViewModel actualiza estadísticas al refrescar`() = runBlocking {
        val viewModel = HomeViewModel(
            mascotaRepository,
            duenoRepository,
            consultaRepository,
            veterinarioRepository
        )

        testDispatcher.scheduler.advanceUntilIdle()

        mascotaRepository.add(Mascota(0, "1-1", "Firulais", "Perro", 5, 12.5))

        viewModel.refrescarEstadisticas()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.first()
        assertEquals("Debe reflejar nueva mascota", 1, state.totalMascotas)
    }
}
