package com.example.veterinaria.data.repository

import com.example.veterinaria.data.model.Dueño
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementación del repositorio de dueños.
 * Maneja la persistencia y validación de datos de dueños.
 */
class DuenoRepositoryImpl : DuenoRepository {

    private val _duenos = MutableStateFlow<List<Dueño>>(emptyList())

    override fun getAll(): Flow<List<Dueño>> = _duenos.asStateFlow()

    /**
     * Obtiene todos los dueños de forma síncrona.
     * Usado principalmente por el ContentProvider.
     */
    fun getAllSync(): List<Dueño> = _duenos.value

    override fun getById(id: String): Dueño? {
        return _duenos.value.find { it.id == id }
    }

    override suspend fun add(dueno: Dueño): Result<Dueño> {
        return try {
            if (exists(dueno.id)) {
                return Result.failure(Exception("Ya existe un dueño con el RUT ${dueno.id}"))
            }
            delay(800)
            _duenos.value = _duenos.value + dueno
            Result.success(dueno)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun update(dueno: Dueño): Result<Dueño> {
        return try {
            delay(800)
            _duenos.value = _duenos.value.map {
                if (it.id == dueno.id) dueno else it
            }
            Result.success(dueno)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun delete(id: String): Result<Unit> {
        return try {
            delay(800)
            _duenos.value = _duenos.value.filter { it.id != id }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun exists(id: String): Boolean {
        return _duenos.value.any { it.id == id }
    }

    /**
     * Método para inicializar datos de prueba.
     */
    fun initializeData(duenos: List<Dueño>) {
        _duenos.value = duenos
    }
}
