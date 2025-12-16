package com.example.veterinaria.data.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Modelo de dominio que representa una consulta veterinaria.
 * Incluye lógica de negocio para cálculos de costos y validaciones.
 */
data class Consulta(
    val id: Int,
    val mascotaId: Int,
    val duenoId: String,
    val descripcion: String,
    val costoBase: Double,
    val fecha: LocalDate
) {
    companion object {
        private const val COSTO_MINIMO = 5000.0
        private const val COSTO_MAXIMO = 500000.0
        private const val DESCUENTO_EMERGENCIA = 0.15
        private const val DESCUENTO_SENIOR = 0.10
    }

    /**
     * Valida que la descripción no esté vacía.
     */
    fun validarDescripcion(): Boolean {
        return descripcion.isNotBlank() && descripcion.length >= 5
    }

    /**
     * Valida que el costo esté dentro del rango permitido.
     */
    fun validarCosto(): Boolean {
        return costoBase in COSTO_MINIMO..COSTO_MAXIMO
    }

    /**
     * Valida que la fecha no sea futura.
     */
    fun validarFecha(): Boolean {
        return !fecha.isAfter(LocalDate.now())
    }

    /**
     * Determina si la consulta es reciente (últimos 7 días).
     */
    fun esReciente(): Boolean {
        val diasTranscurridos = ChronoUnit.DAYS.between(fecha, LocalDate.now())
        return diasTranscurridos <= 7
    }

    /**
     * Determina si es una consulta de emergencia basándose en la descripción.
     */
    fun esEmergencia(): Boolean {
        val palabrasEmergencia = listOf("emergencia", "urgente", "urgencia", "crítico", "grave")
        return palabrasEmergencia.any { descripcion.lowercase().contains(it) }
    }

    /**
     * Calcula el costo final aplicando descuentos si corresponde.
     */
    fun calcularCostoFinal(esMascotaSenior: Boolean = false): Double {
        var costoFinal = costoBase

        if (esEmergencia()) {
            costoFinal *= (1 - DESCUENTO_EMERGENCIA)
        }

        if (esMascotaSenior) {
            costoFinal *= (1 - DESCUENTO_SENIOR)
        }

        return costoFinal
    }

    /**
     * Obtiene la categoría de urgencia de la consulta.
     */
    fun obtenerCategoria(): String {
        return when {
            esEmergencia() -> "Emergencia"
            descripcion.lowercase().contains("control") -> "Control"
            descripcion.lowercase().contains("vacuna") -> "Vacunación"
            else -> "General"
        }
    }

    /**
     * Valida que todos los campos cumplan las reglas de negocio.
     */
    fun esValida(): Boolean {
        return validarDescripcion() && validarCosto() && validarFecha()
    }
}
