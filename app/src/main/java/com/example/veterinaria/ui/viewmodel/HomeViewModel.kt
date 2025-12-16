package com.example.veterinaria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.veterinaria.data.repository.ConsultaRepository
import com.example.veterinaria.data.repository.DuenoRepository
import com.example.veterinaria.data.repository.MascotaRepository
import com.example.veterinaria.data.repository.VeterinarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla principal del dashboard.
 * Gestiona el estado y lógica de presentación del home.
 */
class HomeViewModel(
    private val mascotaRepository: MascotaRepository,
    private val duenoRepository: DuenoRepository,
    private val consultaRepository: ConsultaRepository,
    private val veterinarioRepository: VeterinarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        cargarEstadisticas()
    }

    /**
     * Carga las estadísticas principales del sistema.
     */
    private fun cargarEstadisticas() {
        viewModelScope.launch {
            combine(
                mascotaRepository.getAll(),
                duenoRepository.getAll(),
                consultaRepository.getAll(),
                veterinarioRepository.getAll()
            ) { mascotas, duenos, consultas, veterinarios ->
                HomeUiState(
                    totalMascotas = mascotas.size,
                    totalDuenos = duenos.size,
                    totalConsultas = consultas.size,
                    totalVeterinarios = veterinarios.size,
                    isLoading = false
                )
            }.collect { estado ->
                _uiState.value = estado
            }
        }
    }

    /**
     * Refresca las estadísticas del dashboard.
     */
    fun refrescarEstadisticas() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        cargarEstadisticas()
    }
}

/**
 * Estado de UI para la pantalla Home.
 */
data class HomeUiState(
    val totalMascotas: Int = 0,
    val totalDuenos: Int = 0,
    val totalConsultas: Int = 0,
    val totalVeterinarios: Int = 0,
    val isLoading: Boolean = true
)
