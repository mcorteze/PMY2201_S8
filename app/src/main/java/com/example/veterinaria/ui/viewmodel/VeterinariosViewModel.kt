package com.example.veterinaria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.veterinaria.data.model.Veterinario
import com.example.veterinaria.data.repository.VeterinarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de listado de veterinarios.
 * Maneja el estado y las operaciones CRUD de veterinarios.
 */
class VeterinariosViewModel(
    private val repository: VeterinarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VeterinariosUiState())
    val uiState: StateFlow<VeterinariosUiState> = _uiState.asStateFlow()

    init {
        cargarVeterinarios()
    }

    /**
     * Carga la lista de veterinarios desde el repositorio.
     */
    private fun cargarVeterinarios() {
        viewModelScope.launch {
            repository.getAll().collect { veterinarios ->
                _uiState.value = _uiState.value.copy(
                    veterinarios = veterinarios,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Elimina un veterinario del sistema.
     */
    fun eliminarVeterinario(veterinario: Veterinario) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val resultado = repository.delete(veterinario.id)

            _uiState.value = if (resultado.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    mensajeExito = "Veterinario eliminado correctamente"
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
 * Estado de UI para la pantalla de veterinarios.
 */
data class VeterinariosUiState(
    val veterinarios: List<Veterinario> = emptyList(),
    val isLoading: Boolean = true,
    val mensajeExito: String? = null,
    val mensajeError: String? = null
)
