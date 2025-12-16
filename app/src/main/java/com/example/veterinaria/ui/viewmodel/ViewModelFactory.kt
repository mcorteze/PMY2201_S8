package com.example.veterinaria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.veterinaria.data.repository.ConsultaRepository
import com.example.veterinaria.data.repository.DuenoRepository
import com.example.veterinaria.data.repository.MascotaRepository
import com.example.veterinaria.data.repository.VeterinarioRepository

/**
 * Factory para la creación de ViewModels con inyección de dependencias.
 * Implementa el patrón Factory para cumplir con DIP (Dependency Inversion Principle).
 */
class ViewModelFactory(
    private val mascotaRepository: MascotaRepository,
    private val duenoRepository: DuenoRepository,
    private val consultaRepository: ConsultaRepository,
    private val veterinarioRepository: VeterinarioRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    mascotaRepository,
                    duenoRepository,
                    consultaRepository,
                    veterinarioRepository
                ) as T
            }
            modelClass.isAssignableFrom(MascotasViewModel::class.java) -> {
                MascotasViewModel(mascotaRepository) as T
            }
            modelClass.isAssignableFrom(MascotaFormViewModel::class.java) -> {
                MascotaFormViewModel(mascotaRepository) as T
            }
            modelClass.isAssignableFrom(DuenosViewModel::class.java) -> {
                DuenosViewModel(duenoRepository) as T
            }
            modelClass.isAssignableFrom(DuenoFormViewModel::class.java) -> {
                DuenoFormViewModel(duenoRepository) as T
            }
            modelClass.isAssignableFrom(VeterinariosViewModel::class.java) -> {
                VeterinariosViewModel(veterinarioRepository) as T
            }
            modelClass.isAssignableFrom(ConsultasViewModel::class.java) -> {
                ConsultasViewModel(consultaRepository, mascotaRepository) as T
            }
            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
