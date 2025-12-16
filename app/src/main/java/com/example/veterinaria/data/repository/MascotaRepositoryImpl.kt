package com.example.veterinaria.data.repository

import com.example.veterinaria.data.model.Mascota
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementación del repositorio de mascotas.
 * Centraliza el acceso a datos y simula operaciones asíncronas.
 */
class MascotaRepositoryImpl : MascotaRepository {

    private val _mascotas = MutableStateFlow<List<Mascota>>(emptyList())
    private var nextId = 1

    override fun getAll(): Flow<List<Mascota>> = _mascotas.asStateFlow()

    /**
     * Obtiene todas las mascotas de forma síncrona.
     * Usado principalmente por el ContentProvider.
     */
    fun getAllSync(): List<Mascota> = _mascotas.value

    override fun getById(id: Int): Mascota? {
        return _mascotas.value.find { it.id == id }
    }

    override fun getByDueno(duenoId: String): List<Mascota> {
        return _mascotas.value.filter { it.duenoId == duenoId }
    }

    override suspend fun add(mascota: Mascota): Result<Mascota> {
        return try {
            delay(800)
            val nuevaMascota = mascota.copy(id = nextId++)
            _mascotas.value = _mascotas.value + nuevaMascota
            Result.success(nuevaMascota)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun update(mascota: Mascota): Result<Mascota> {
        return try {
            delay(800)
            _mascotas.value = _mascotas.value.map {
                if (it.id == mascota.id) mascota else it
            }
            Result.success(mascota)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun delete(id: Int): Result<Unit> {
        return try {
            delay(800)
            _mascotas.value = _mascotas.value.filter { it.id != id }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Método para inicializar datos de prueba.
     */
    fun initializeData(mascotas: List<Mascota>) {
        _mascotas.value = mascotas
        nextId = (mascotas.maxOfOrNull { it.id } ?: 0) + 1
    }
}
