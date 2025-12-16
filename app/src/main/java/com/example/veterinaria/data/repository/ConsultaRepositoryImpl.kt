package com.example.veterinaria.data.repository

import com.example.veterinaria.data.model.Consulta
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementación del repositorio de consultas.
 * Gestiona las operaciones de consultas médicas.
 */
class ConsultaRepositoryImpl : ConsultaRepository {

    private val _consultas = MutableStateFlow<List<Consulta>>(emptyList())
    private var nextId = 1

    override fun getAll(): Flow<List<Consulta>> = _consultas.asStateFlow()

    override fun getById(id: Int): Consulta? {
        return _consultas.value.find { it.id == id }
    }

    override fun getByMascota(mascotaId: Int): List<Consulta> {
        return _consultas.value.filter { it.mascotaId == mascotaId }
    }

    override fun getByDueno(duenoId: String): List<Consulta> {
        return _consultas.value.filter { it.duenoId == duenoId }
    }

    override suspend fun add(consulta: Consulta): Result<Consulta> {
        return try {
            delay(800)
            val nuevaConsulta = consulta.copy(id = nextId++)
            _consultas.value = _consultas.value + nuevaConsulta
            Result.success(nuevaConsulta)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun update(consulta: Consulta): Result<Consulta> {
        return try {
            delay(800)
            _consultas.value = _consultas.value.map {
                if (it.id == consulta.id) consulta else it
            }
            Result.success(consulta)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun delete(id: Int): Result<Unit> {
        return try {
            delay(800)
            _consultas.value = _consultas.value.filter { it.id != id }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Método para inicializar datos de prueba.
     */
    fun initializeData(consultas: List<Consulta>) {
        _consultas.value = consultas
        nextId = (consultas.maxOfOrNull { it.id } ?: 0) + 1
    }
}
