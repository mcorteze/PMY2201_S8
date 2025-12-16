package com.example.veterinaria.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.veterinaria.data.model.Consulta
import com.example.veterinaria.data.model.Dueño
import com.example.veterinaria.data.model.Mascota
import com.example.veterinaria.data.model.Veterinario
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadingMessage = MutableStateFlow("")
    val loadingMessage: StateFlow<String> = _loadingMessage.asStateFlow()

    // --- Listas de datos ---
    private val _mascotas = MutableStateFlow<List<Mascota>>(emptyList())
    val mascotas: StateFlow<List<Mascota>> = _mascotas.asStateFlow()

    private val _duenos = MutableStateFlow<List<Dueño>>(emptyList())
    val duenos: StateFlow<List<Dueño>> = _duenos.asStateFlow()

    private val _consultas = MutableStateFlow<List<Consulta>>(emptyList())
    val consultas: StateFlow<List<Consulta>> = _consultas.asStateFlow()

    private val _veterinarios = MutableStateFlow<List<Veterinario>>(emptyList())
    val veterinarios: StateFlow<List<Veterinario>> = _veterinarios.asStateFlow()

    // --- Listas para Dropdowns ---
    val especies = listOf("Perro", "Gato", "Pájaro", "Reptil", "Otro")
    val especialidades = listOf("Cardiología", "Dermatología", "General", "Cirugía", "Neurología")

    init {
        cargarDatosIniciales()
    }

    private fun cargarDatosIniciales() {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Cargando datos del sistema..."
            delay(2000)

            val dueno1 = Dueño("1-1", "Juan Pérez", "+56 9 1234 5678", "juan@email.com")
            val dueno2 = Dueño("2-2", "María López", "+56 9 8765 4321", "maria@email.com")
            _duenos.value = listOf(dueno1, dueno2)

            val mascota1 = Mascota(1, dueno1.id, "Firulais", "Perro", 5, 12.5)
            val mascota2 = Mascota(2, dueno1.id, "Michi", "Gato", 3, 4.0)
            val mascota3 = Mascota(3, dueno2.id, "Rex", "Perro", 2, 8.0)
            _mascotas.value = listOf(mascota1, mascota2, mascota3)

            _consultas.value = listOf(
                Consulta(1, mascota1.id, dueno1.id, "Vacunación", 15000.0, LocalDate.now().minusDays(2)),
                Consulta(2, mascota2.id, dueno1.id, "Revisión General", 20000.0, LocalDate.now())
            )

            _veterinarios.value = listOf(
                Veterinario(1, "Dr. Smith", "Cardiología"),
                Veterinario(2, "Dra. Jones", "Dermatología")
            )

            _isLoading.value = false
        }
    }

    // ========== Búsquedas ==========
    fun getMascotaById(id: Int): Mascota? {
        return _mascotas.value.find { it.id == id }
    }

    fun getDuenoById(id: String): Dueño? {
        return _duenos.value.find { it.id == id }
    }

    // ========== Mascotas ==========
    fun addMascota(mascota: Mascota) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Agregando mascota..."
            delay(1000)
            val newId = (_mascotas.value.maxOfOrNull { it.id } ?: 0) + 1
            _mascotas.value = _mascotas.value + mascota.copy(id = newId)
            _isLoading.value = false
        }
    }

    fun updateMascota(mascota: Mascota) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Actualizando mascota..."
            delay(1000)
            _mascotas.value = _mascotas.value.map { if (it.id == mascota.id) mascota else it }
            _isLoading.value = false
        }
    }

    fun deleteMascota(mascota: Mascota) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Eliminando mascota..."
            delay(1000)
            _mascotas.value = _mascotas.value.filter { it.id != mascota.id }
            _isLoading.value = false
        }
    }

    fun getMascotasByDueno(duenoId: String): List<Mascota> {
        return _mascotas.value.filter { it.duenoId == duenoId }
    }

    // ========== Dueños ==========
    fun addDueno(dueno: Dueño) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Agregando dueño..."
            delay(1000)
            _duenos.value = _duenos.value + dueno
            _isLoading.value = false
        }
    }

    fun updateDueno(dueno: Dueño) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Actualizando dueño..."
            delay(1000)
            _duenos.value = _duenos.value.map { if (it.id == dueno.id) dueno else it }
            _isLoading.value = false
        }
    }

    fun deleteDueno(dueno: Dueño) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Eliminando dueño..."
            delay(1000)
            _duenos.value = _duenos.value.filter { it.id != dueno.id }
            _isLoading.value = false
        }
    }

    // ========== Consultas ==========
    fun addConsulta(consulta: Consulta) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Agregando consulta..."
            delay(1000)
            val newId = (_consultas.value.maxOfOrNull { it.id } ?: 0) + 1
            _consultas.value = _consultas.value + consulta.copy(id = newId)
            _isLoading.value = false
        }
    }

    fun updateConsulta(consulta: Consulta) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Actualizando consulta..."
            delay(1000)
            _consultas.value = _consultas.value.map { if (it.id == consulta.id) consulta else it }
            _isLoading.value = false
        }
    }

    fun deleteConsulta(consulta: Consulta) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Eliminando consulta..."
            delay(1000)
            _consultas.value = _consultas.value.filter { it.id != consulta.id }
            _isLoading.value = false
        }
    }

    // ========== Veterinarios ==========
    fun addVeterinario(veterinario: Veterinario) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Agregando veterinario..."
            delay(1000)
            val newId = (_veterinarios.value.maxOfOrNull { it.id } ?: 0) + 1
            _veterinarios.value = _veterinarios.value + veterinario.copy(id = newId)
            _isLoading.value = false
        }
    }

    fun updateVeterinario(veterinario: Veterinario) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Actualizando veterinario..."
            delay(1000)
            _veterinarios.value = _veterinarios.value.map { if (it.id == veterinario.id) veterinario else it }
            _isLoading.value = false
        }
    }

    fun deleteVeterinario(veterinario: Veterinario) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Eliminando veterinario..."
            delay(1000)
            _veterinarios.value = _veterinarios.value.filter { it.id != veterinario.id }
            _isLoading.value = false
        }
    }

    fun refrescarResumen() {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingMessage.value = "Generando resumen actualizado..."
            delay(1500)
            _isLoading.value = false
        }
    }
}
