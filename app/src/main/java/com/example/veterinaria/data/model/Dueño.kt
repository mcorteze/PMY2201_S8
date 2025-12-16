package com.example.veterinaria.data.model

/**
 * Modelo de dominio que representa un dueño de mascota.
 * Solo contiene propiedades y lógica de validación de negocio.
 */
data class Dueño(
    val id: String,
    val nombre: String,
    val telefono: String,
    val email: String
) {
    /**
     * Valida que el RUT tenga un formato correcto.
     */
    fun validarRUT(): Boolean {
        val rutPattern = Regex("^\\d{1,2}-\\d{1}$")
        return rutPattern.matches(id)
    }

    /**
     * Valida que el email tenga formato válido.
     */
    fun validarEmail(): Boolean {
        val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailPattern.matches(email.trim())
    }

    /**
     * Valida que el nombre no esté vacío.
     */
    fun validarNombre(): Boolean {
        return nombre.isNotBlank() && nombre.length >= 3
    }

    /**
     * Valida que el teléfono contenga al menos 8 dígitos.
     */
    fun validarTelefono(): Boolean {
        val digitos = telefono.filter { it.isDigit() }
        return digitos.length >= 8
    }

    /**
     * Valida que todos los campos cumplan las reglas de negocio.
     */
    fun esValido(): Boolean {
        return validarRUT() && validarEmail() && validarNombre() && validarTelefono()
    }
}
