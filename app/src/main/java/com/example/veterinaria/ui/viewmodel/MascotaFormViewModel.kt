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
 * ViewModel para el formulario de mascotas.
 * Gestiona la creación y edición de mascotas.
 */
class MascotaFormViewModel(
    private val repository: MascotaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MascotaFormUiState())
    val uiState: StateFlow<MascotaFormUiState> = _uiState.asStateFlow()

    /**
     * Carga una mascota existente para edición.
     */
    fun cargarMascota(id: Int) {
        val mascota = repository.getById(id)
        mascota?.let {
            _uiState.value = _uiState.value.copy(
                nombre = it.nombre,
                especie = it.especie,
                edad = it.edad.toString(),
                peso = it.peso.toString(),
                duenoId = it.duenoId,
                modoEdicion = true
            )
        }
    }

    /**
     * Establece el dueño inicial para una nueva mascota.
     */
    fun establecerDueno(duenoId: String) {
        _uiState.value = _uiState.value.copy(duenoId = duenoId)
    }

    /**
     * Actualiza el nombre de la mascota.
     */
    fun actualizarNombre(valor: String) {
        _uiState.value = _uiState.value.copy(nombre = valor)
    }

    /**
     * Actualiza la especie de la mascota.
     */
    fun actualizarEspecie(valor: String) {
        _uiState.value = _uiState.value.copy(especie = valor)
    }

    /**
     * Actualiza la edad de la mascota.
     */
    fun actualizarEdad(valor: String) {
        _uiState.value = _uiState.value.copy(edad = valor)
    }

    /**
     * Actualiza el peso de la mascota.
     */
    fun actualizarPeso(valor: String) {
        _uiState.value = _uiState.value.copy(peso = valor)
    }

    /**
     * Valida y guarda la mascota.
     */
    fun guardarMascota(mascotaId: Int?, onSuccess: () -> Unit) {
        val state = _uiState.value

        val edadInt = state.edad.toIntOrNull()
        val pesoDouble = state.peso.toDoubleOrNull()

        if (edadInt == null || pesoDouble == null) {
            _uiState.value = state.copy(
                mensajeError = "Edad y peso deben ser valores numéricos válidos"
            )
            return
        }

        val mascota = Mascota(
            id = mascotaId ?: 0,
            duenoId = state.duenoId,
            nombre = state.nombre,
            especie = state.especie,
            edad = edadInt,
            peso = pesoDouble
        )

        if (!mascota.esValida()) {
            _uiState.value = state.copy(
                mensajeError = "Los datos de la mascota no son válidos"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)

            val resultado = if (mascotaId == null) {
                repository.add(mascota)
            } else {
                repository.update(mascota)
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
 * Estado de UI para el formulario de mascotas.
 */
data class MascotaFormUiState(
    val nombre: String = "",
    val especie: String = "Perro",
    val edad: String = "",
    val peso: String = "",
    val duenoId: String = "",
    val modoEdicion: Boolean = false,
    val isLoading: Boolean = false,
    val mensajeError: String? = null
)
