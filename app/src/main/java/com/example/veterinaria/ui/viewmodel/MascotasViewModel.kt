package com.example.veterinaria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.veterinaria.data.model.Mascota
import com.example.veterinaria.data.repository.MascotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de listado de mascotas.
 * Maneja el estado y las operaciones CRUD de mascotas.
 */
class MascotasViewModel(
    private val repository: MascotaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MascotasUiState())
    val uiState: StateFlow<MascotasUiState> = _uiState.asStateFlow()

    init {
        cargarMascotas()
    }

    /**
     * Carga la lista de mascotas desde el repositorio.
     */
    private fun cargarMascotas() {
        viewModelScope.launch {
            repository.getAll().collect { mascotas ->
                _uiState.value = _uiState.value.copy(
                    mascotas = mascotas,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Elimina una mascota del sistema.
     */
    fun eliminarMascota(mascota: Mascota) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val resultado = repository.delete(mascota.id)

            _uiState.value = if (resultado.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    mensajeExito = "Mascota eliminada correctamente"
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    mensajeError = resultado.exceptionOrNull()?.message ?: "Error al eliminar"
                )
            }
        }
    }

    /**
     * Limpia los mensajes de estado.
     */
    fun limpiarMensajes() {
        _uiState.value = _uiState.value.copy(
            mensajeExito = null,
            mensajeError = null
        )
    }
}

/**
 * Estado de UI para la pantalla de mascotas.
 */
data class MascotasUiState(
    val mascotas: List<Mascota> = emptyList(),
    val isLoading: Boolean = true,
    val mensajeExito: String? = null,
    val mensajeError: String? = null
)
