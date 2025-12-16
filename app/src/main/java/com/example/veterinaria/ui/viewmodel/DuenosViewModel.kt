package com.example.veterinaria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.veterinaria.data.model.Dueño
import com.example.veterinaria.data.repository.DuenoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de listado de dueños.
 * Maneja el estado y las operaciones CRUD de dueños.
 */
class DuenosViewModel(
    private val repository: DuenoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DuenosUiState())
    val uiState: StateFlow<DuenosUiState> = _uiState.asStateFlow()

    init {
        cargarDuenos()
    }

    /**
     * Carga la lista de dueños desde el repositorio.
     */
    private fun cargarDuenos() {
        viewModelScope.launch {
            repository.getAll().collect { duenos ->
                _uiState.value = _uiState.value.copy(
                    duenos = duenos,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Elimina un dueño del sistema.
     */
    fun eliminarDueno(dueno: Dueño) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val resultado = repository.delete(dueno.id)

            _uiState.value = if (resultado.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    mensajeExito = "Dueño eliminado correctamente"
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
 * Estado de UI para la pantalla de dueños.
 */
data class DuenosUiState(
    val duenos: List<Dueño> = emptyList(),
    val isLoading: Boolean = true,
    val mensajeExito: String? = null,
    val mensajeError: String? = null
)
