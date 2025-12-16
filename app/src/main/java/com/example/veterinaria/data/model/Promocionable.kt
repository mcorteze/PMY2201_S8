package com.example.veterinaria.data.model

// Anotación para identificar servicios con promoción
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Promocionable(val descuento: Double)
