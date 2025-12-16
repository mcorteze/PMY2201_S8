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
 * ViewModel para el formulario de dueños.
 * Gestiona la creación y edición de dueños.
 */
class DuenoFormViewModel(
    private val repository: DuenoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DuenoFormUiState())
    val uiState: StateFlow<DuenoFormUiState> = _uiState.asStateFlow()

    /**
     * Carga un dueño existente para edición.
     */
    fun cargarDueno(id: String) {
        val dueno = repository.getById(id)
        dueno?.let {
            _uiState.value = _uiState.value.copy(
                rut = it.id,
                nombre = it.nombre,
                telefono = it.telefono,
                email = it.email,
                modoEdicion = true
            )
        }
    }

    /**
     * Actualiza el RUT del dueño.
     */
    fun actualizarRut(valor: String) {
        _uiState.value = _uiState.value.copy(rut = valor)
    }

    /**
     * Actualiza el nombre del dueño.
     */
    fun actualizarNombre(valor: String) {
        _uiState.value = _uiState.value.copy(nombre = valor)
    }

    /**
     * Actualiza el teléfono del dueño.
     */
    fun actualizarTelefono(valor: String) {
        _uiState.value = _uiState.value.copy(telefono = valor)
    }

    /**
     * Actualiza el email del dueño.
     */
    fun actualizarEmail(valor: String) {
        _uiState.value = _uiState.value.copy(email = valor)
    }

    /**
     * Valida y guarda el dueño.
     */
    fun guardarDueno(onSuccess: () -> Unit) {
        val state = _uiState.value

        val dueno = Dueño(
            id = state.rut,
            nombre = state.nombre,
            telefono = state.telefono,
            email = state.email
        )

        if (!dueno.esValido()) {
            _uiState.value = state.copy(
                mensajeError = "Verifica que todos los campos sean correctos"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)

            val resultado = if (state.modoEdicion) {
                repository.update(dueno)
            } else {
                repository.add(dueno)
            }

            if (resultado.isSuccess) {
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    mensajeError = resultado.exceptionOrNull()?.message ?: "Error al guardar"
                )
            }
        }
    }

    /**
     * Limpia los mensajes de error.
     */
    fun limpiarError() {
        _uiState.value = _uiState.value.copy(mensajeError = null)
    }
}

/**
 * Estado de UI para el formulario de dueños.
 */
data class DuenoFormUiState(
    val rut: String = "",
    val nombre: String = "",
    val telefono: String = "",
    val email: String = "",
    val modoEdicion: Boolean = false,
    val isLoading: Boolean = false,
    val mensajeError: String? = null
)
