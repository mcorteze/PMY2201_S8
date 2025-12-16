package com.example.veterinaria

import com.example.veterinaria.data.model.Dueño
import com.example.veterinaria.data.model.Mascota
import com.example.veterinaria.data.repository.DuenoRepositoryImpl
import com.example.veterinaria.data.repository.MascotaRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Pruebas de integración para los repositorios.
 * Verifica la correcta gestión de datos y comunicación con la capa de dominio.
 */
class RepositoryTests {

    private lateinit var mascotaRepository: MascotaRepositoryImpl
    private lateinit var duenoRepository: DuenoRepositoryImpl

    @Before
    fun setup() {
        mascotaRepository = MascotaRepositoryImpl()
        duenoRepository = DuenoRepositoryImpl()
    }

    @Test
    fun `Repositorio de mascotas agrega correctamente`() = runBlocking {
        val mascota = Mascota(0, "1-1", "Firulais", "Perro", 5, 12.5)

        val resultado = mascotaRepository.add(mascota)

        assertTrue("Debe agregarse correctamente", resultado.isSuccess)
        val mascotaAgregada = resultado.getOrNull()
        assertNotNull("Debe retornar la mascota", mascotaAgregada)
        assertTrue("Debe asignar ID", mascotaAgregada!!.id > 0)
    }

    @Test
    fun `Repositorio de mascotas actualiza correctamente`() = runBlocking {
        val mascota = Mascota(0, "1-1", "Firulais", "Perro", 5, 12.5)
        val agregada = mascotaRepository.add(mascota).getOrNull()!!

        val modificada = agregada.copy(peso = 15.0)
        val resultado = mascotaRepository.update(modificada)

        assertTrue("Debe actualizarse correctamente", resultado.isSuccess)
        val mascotaActualizada = mascotaRepository.getById(agregada.id)
        assertEquals("Debe reflejar el nuevo peso", 15.0, mascotaActualizada?.peso ?: 0.0, 0.01)
    }

    @Test
    fun `Repositorio de mascotas elimina correctamente`() = runBlocking {
        val mascota = Mascota(0, "1-1", "Firulais", "Perro", 5, 12.5)
        val agregada = mascotaRepository.add(mascota).getOrNull()!!

        val resultado = mascotaRepository.delete(agregada.id)

        assertTrue("Debe eliminarse correctamente", resultado.isSuccess)
        assertNull("No debe encontrarse después de eliminar", mascotaRepository.getById(agregada.id))
    }

    @Test
    fun `Repositorio de mascotas filtra por dueno`() = runBlocking {
        val dueno1 = "1-1"
        val dueno2 = "2-2"

        mascotaRepository.add(Mascota(0, dueno1, "Firulais", "Perro", 5, 12.5))
        mascotaRepository.add(Mascota(0, dueno1, "Michi", "Gato", 3, 4.0))
        mascotaRepository.add(Mascota(0, dueno2, "Rex", "Perro", 2, 8.0))

        val mascotasDueno1 = mascotaRepository.getByDueno(dueno1)
        val mascotasDueno2 = mascotaRepository.getByDueno(dueno2)

        assertEquals("Dueño 1 debe tener 2 mascotas", 2, mascotasDueno1.size)
        assertEquals("Dueño 2 debe tener 1 mascota", 1, mascotasDueno2.size)
    }

    @Test
    fun `Repositorio de duenos no permite duplicados`() = runBlocking {
        val dueno = Dueño("1-1", "Juan Pérez", "+56912345678", "juan@email.com")

        duenoRepository.add(dueno)
        val resultado = duenoRepository.add(dueno)

        assertTrue("Debe fallar al agregar duplicado", resultado.isFailure)
        assertNotNull("Debe retornar excepción", resultado.exceptionOrNull())
    }

    @Test
    fun `Repositorio de duenos verifica existencia`() = runBlocking {
        val dueno = Dueño("1-1", "Juan Pérez", "+56912345678", "juan@email.com")

        assertFalse("No debe existir inicialmente", duenoRepository.exists("1-1"))
        duenoRepository.add(dueno)
        assertTrue("Debe existir después de agregar", duenoRepository.exists("1-1"))
    }

    @Test
    fun `Flow de repositorio emite cambios reactivamente`() = runBlocking {
        val mascota = Mascota(0, "1-1", "Firulais", "Perro", 5, 12.5)

        val estadoInicial = mascotaRepository.getAll().first()
        assertEquals("Debe estar vacío inicialmente", 0, estadoInicial.size)

        mascotaRepository.add(mascota)

        val estadoFinal = mascotaRepository.getAll().first()
        assertEquals("Debe contener 1 mascota", 1, estadoFinal.size)
    }
}
