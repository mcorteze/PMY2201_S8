package com.example.veterinaria.data.model

/**
 * Modelo de dominio que representa una mascota registrada en la veterinaria.
 * Contiene la información básica de cada mascota y validaciones de negocio.
 *
 * Este modelo se utiliza en toda la aplicación para representar mascotas
 * tanto en la interfaz de usuario como en la capa de datos.
 */
data class Mascota(
    val id: Int,                  // Identificador único de la mascota
    var duenoId: String,          // Referencia al ID del dueño (deprecated, usar duenoCedula)
    val nombre: String,           // Nombre de la mascota
    val especie: String,          // Especie: Perro, Gato, Pájaro, Reptil, Otro
    val edad: Int,                // Edad en años
    val peso: Double,             // Peso en kilogramos
    val raza: String = "",        // Raza específica de la mascota (opcional)
    val duenoCedula: String = duenoId  // Cédula del dueño propietario
) {
    companion object {
        private val ESPECIES_VALIDAS = listOf("Perro", "Gato", "Pájaro", "Reptil", "Otro")
        private const val EDAD_MINIMA = 0
        private const val EDAD_MAXIMA = 50
        private const val PESO_MINIMO = 0.1
        private const val PESO_MAXIMO = 200.0
    }

    /**
     * Valida que el nombre no esté vacío.
     */
    fun validarNombre(): Boolean {
        return nombre.isNotBlank() && nombre.length >= 2
    }

    /**
     * Valida que la especie esté dentro de las opciones permitidas.
     */
    fun validarEspecie(): Boolean {
        return especie in ESPECIES_VALIDAS
    }

    /**
     * Valida que la edad esté en un rango razonable.
     */
    fun validarEdad(): Boolean {
        return edad in EDAD_MINIMA..EDAD_MAXIMA
    }

    /**
     * Valida que el peso sea un valor positivo y razonable.
     */
    fun validarPeso(): Boolean {
        return peso in PESO_MINIMO..PESO_MAXIMO
    }

    /**
     * Determina si la mascota es considerada senior según su especie.
     */
    fun esSenior(): Boolean {
        return when (especie) {
            "Perro" -> edad >= 7
            "Gato" -> edad >= 10
            else -> edad >= 8
        }
    }

    /**
     * Calcula el índice de masa corporal aproximado.
     */
    fun calcularIMC(): String {
        return when {
            peso < 5.0 -> "Bajo peso"
            peso < 25.0 -> "Peso normal"
            peso < 50.0 -> "Sobrepeso"
            else -> "Obesidad"
        }
    }

    /**
     * Valida que todos los campos cumplan las reglas de negocio.
     */
    fun esValida(): Boolean {
        return validarNombre() && validarEspecie() && validarEdad() && validarPeso()
    }
}
