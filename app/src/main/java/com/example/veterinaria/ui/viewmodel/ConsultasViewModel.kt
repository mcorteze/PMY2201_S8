package com.example.veterinaria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.veterinaria.data.model.Consulta
import com.example.veterinaria.data.repository.ConsultaRepository
import com.example.veterinaria.data.repository.MascotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de listado de consultas.
 * Maneja el estado y las operaciones CRUD de consultas.
 */
class ConsultasViewModel(
    private val consultaRepository: ConsultaRepository,
    private val mascotaRepository: MascotaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConsultasUiState())
    val uiState: StateFlow<ConsultasUiState> = _uiState.asStateFlow()

    init {
        cargarConsultas()
    }

    /**
     * Carga la lista de consultas desde el repositorio.
     */
    private fun cargarConsultas() {
        viewModelScope.launch {
            consultaRepository.getAll().collect { consultas ->
                val consultasConInfo = consultas.map { consulta ->
                    val mascota = mascotaRepository.getById(consulta.mascotaId)
                    ConsultaConInfo(
                        consulta = consulta,
                        nombreMascota = mascota?.nombre ?: "Desconocido",
                        esMascotaSenior = mascota?.esSenior() ?: false
                    )
                }

                _uiState.value = _uiState.value.copy(
                    consultas = consultasConInfo,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Elimina una consulta del sistema.
     */
    fun eliminarConsulta(consulta: Consulta) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val resultado = consultaRepository.delete(consulta.id)

            _uiState.value = if (resultado.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    mensajeExito = "Consulta eliminada correctamente"
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
 * Clase auxiliar que combina consulta con información relacionada.
 */
data class ConsultaConInfo(
    val consulta: Consulta,
    val nombreMascota: String,
    val esMascotaSenior: Boolean
)

/**
 * Estado de UI para la pantalla de consultas.
 */
data class ConsultasUiState(
    val consultas: List<ConsultaConInfo> = emptyList(),
    val isLoading: Boolean = true,
    val mensajeExito: String? = null,
    val mensajeError: String? = null
)
