package com.example.veterinaria.data.repository

import com.example.veterinaria.data.model.Mascota
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz que define el contrato para operaciones con mascotas.
 * Aplica el principio de inversión de dependencias (DIP).
 */
interface MascotaRepository {
    fun getAll(): Flow<List<Mascota>>
    fun getById(id: Int): Mascota?
    fun getByDueno(duenoId: String): List<Mascota>
    suspend fun add(mascota: Mascota): Result<Mascota>
    suspend fun update(mascota: Mascota): Result<Mascota>
    suspend fun delete(id: Int): Result<Unit>
}
