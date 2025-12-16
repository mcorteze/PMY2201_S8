package com.example.veterinaria.data.repository

import com.example.veterinaria.data.model.Consulta
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz que define el contrato para operaciones con consultas.
 */
interface ConsultaRepository {
    fun getAll(): Flow<List<Consulta>>
    fun getById(id: Int): Consulta?
    fun getByMascota(mascotaId: Int): List<Consulta>
    fun getByDueno(duenoId: String): List<Consulta>
    suspend fun add(consulta: Consulta): Result<Consulta>
    suspend fun update(consulta: Consulta): Result<Consulta>
    suspend fun delete(id: Int): Result<Unit>
}
