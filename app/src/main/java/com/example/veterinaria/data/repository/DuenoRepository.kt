package com.example.veterinaria.data.repository

import com.example.veterinaria.data.model.Dueño
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz que define el contrato para operaciones con dueños.
 */
interface DuenoRepository {
    fun getAll(): Flow<List<Dueño>>
    fun getById(id: String): Dueño?
    suspend fun add(dueno: Dueño): Result<Dueño>
    suspend fun update(dueno: Dueño): Result<Dueño>
    suspend fun delete(id: String): Result<Unit>
    fun exists(id: String): Boolean
}
