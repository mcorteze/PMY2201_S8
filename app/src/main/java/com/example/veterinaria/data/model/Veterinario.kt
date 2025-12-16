package com.example.veterinaria.data.model

/**
 * Modelo de dominio que representa un veterinario.
 * Contiene lógica de negocio relacionada con profesionales.
 */
data class Veterinario(
    val id: Int,
    val nombre: String,
    val especialidad: String
) {
    companion object {
        private val ESPECIALIDADES_VALIDAS = listOf(
            "Cardiología",
            "Dermatología",
            "General",
            "Cirugía",
            "Neurología"
        )
    }

    /**
     * Valida que el nombre no esté vacío.
     */
    fun validarNombre(): Boolean {
        return nombre.isNotBlank() && nombre.length >= 5
    }

    /**
     * Valida que la especialidad esté dentro de las opciones permitidas.
     */
    fun validarEspecialidad(): Boolean {
        return especialidad in ESPECIALIDADES_VALIDAS
    }

    /**
     * Determina si es un especialista (no general).
     */
    fun esEspecialista(): Boolean {
        return especialidad != "General"
    }

    /**
     * Obtiene el título profesional según la especialidad.
     */
    fun obtenerTitulo(): String {
        return when {
            nombre.startsWith("Dr.") || nombre.startsWith("Dra.") -> nombre
            else -> "Dr. $nombre"
        }
    }

    /**
     * Valida que todos los campos cumplan las reglas de negocio.
     */
    fun esValido(): Boolean {
        return validarNombre() && validarEspecialidad()
    }
}
