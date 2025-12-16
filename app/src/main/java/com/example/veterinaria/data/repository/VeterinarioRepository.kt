package com.example.veterinaria.data.repository

import com.example.veterinaria.data.model.Veterinario
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz que define el contrato para operaciones con veterinarios.
 */
interface VeterinarioRepository {
    fun getAll(): Flow<List<Veterinario>>
    fun getById(id: Int): Veterinario?
    fun getByEspecialidad(especialidad: String): List<Veterinario>
    suspend fun add(veterinario: Veterinario): Result<Veterinario>
    suspend fun update(veterinario: Veterinario): Result<Veterinario>
    suspend fun delete(id: Int): Result<Unit>
}
