package com.example.veterinaria.data.repository

import com.example.veterinaria.data.model.Veterinario
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementación del repositorio de veterinarios.
 * Administra los datos del personal médico.
 */
class VeterinarioRepositoryImpl : VeterinarioRepository {

    private val _veterinarios = MutableStateFlow<List<Veterinario>>(emptyList())
    private var nextId = 1

    override fun getAll(): Flow<List<Veterinario>> = _veterinarios.asStateFlow()

    override fun getById(id: Int): Veterinario? {
        return _veterinarios.value.find { it.id == id }
    }

    override fun getByEspecialidad(especialidad: String): List<Veterinario> {
        return _veterinarios.value.filter { it.especialidad == especialidad }
    }

    override suspend fun add(veterinario: Veterinario): Result<Veterinario> {
        return try {
            delay(800)
            val nuevoVeterinario = veterinario.copy(id = nextId++)
            _veterinarios.value = _veterinarios.value + nuevoVeterinario
            Result.success(nuevoVeterinario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun update(veterinario: Veterinario): Result<Veterinario> {
        return try {
            delay(800)
            _veterinarios.value = _veterinarios.value.map {
                if (it.id == veterinario.id) veterinario else it
            }
            Result.success(veterinario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun delete(id: Int): Result<Unit> {
        return try {
            delay(800)
            _veterinarios.value = _veterinarios.value.filter { it.id != id }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Método para inicializar datos de prueba.
     */
    fun initializeData(veterinarios: List<Veterinario>) {
        _veterinarios.value = veterinarios
        nextId = (veterinarios.maxOfOrNull { it.id } ?: 0) + 1
    }
}
