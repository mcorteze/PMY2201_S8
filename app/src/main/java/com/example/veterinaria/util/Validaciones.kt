package com.example.veterinaria.util

import java.time.LocalDate

object Validaciones {
    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")

    fun esEmailValido(email: String): Boolean = emailRegex.matches(email.trim())

    fun formatearTelefono(telefono: String): String {
        val digits = telefono.filter { it.isDigit() }
        if (digits.length < 10) return telefono
        val base10 = digits.takeLast(10)
        val area = base10.substring(0, 3)
        val block1 = base10.substring(3, 6)
        val block2 = base10.substring(6, 10)
        val cc = digits.dropLast(10).ifEmpty { "56" }
        return "+$cc ($area) $block1-$block2"
    }

    fun cantidadValida(c: Int): Boolean = c in 1..100

    fun enRangoPromocion(fecha: LocalDate, inicio: LocalDate, fin: LocalDate): Boolean {
        val rango = inicio..fin
        return fecha in rango
    }
}
