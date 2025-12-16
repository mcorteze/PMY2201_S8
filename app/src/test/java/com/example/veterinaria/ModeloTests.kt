package com.example.veterinaria

import com.example.veterinaria.data.model.Consulta
import com.example.veterinaria.data.model.Dueño
import com.example.veterinaria.data.model.Mascota
import com.example.veterinaria.data.model.Veterinario
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

/**
 * Pruebas unitarias para los modelos de dominio.
 * Verifica que la lógica de negocio en los modelos funcione correctamente.
 */
class ModeloTests {

    @Test
    fun `Mascota valida sus datos correctamente`() {
        val mascota = Mascota(1, "1-1", "Firulais", "Perro", 5, 12.5)

        assertTrue("Mascota debe ser válida", mascota.esValida())
        assertTrue("Nombre debe ser válido", mascota.validarNombre())
        assertTrue("Especie debe ser válida", mascota.validarEspecie())
        assertTrue("Edad debe ser válida", mascota.validarEdad())
        assertTrue("Peso debe ser válido", mascota.validarPeso())
    }

    @Test
    fun `Mascota detecta si es senior correctamente`() {
        val perroSenior = Mascota(1, "1-1", "Rex", "Perro", 8, 15.0)
        val gatoJoven = Mascota(2, "1-1", "Michi", "Gato", 5, 4.0)

        assertTrue("Perro de 8 años debe ser senior", perroSenior.esSenior())
        assertFalse("Gato de 5 años no debe ser senior", gatoJoven.esSenior())
    }

    @Test
    fun `Dueno valida RUT correctamente`() {
        val duenoValido = Dueño("12-3", "Juan Pérez", "+56912345678", "juan@email.com")
        val duenoInvalido = Dueño("123", "María López", "+56987654321", "maria@email.com")

        assertTrue("RUT 12-3 debe ser válido", duenoValido.validarRUT())
        assertFalse("RUT 123 debe ser inválido", duenoInvalido.validarRUT())
    }

    @Test
    fun `Dueno valida email correctamente`() {
        val duenoEmailValido = Dueño("1-1", "Juan", "+56912345678", "juan@email.com")
        val duenoEmailInvalido = Dueño("1-1", "María", "+56912345678", "maria@invalid")

        assertTrue("Email juan@email.com debe ser válido", duenoEmailValido.validarEmail())
        assertFalse("Email maria@invalid debe ser inválido", duenoEmailInvalido.validarEmail())
    }

    @Test
    fun `Consulta detecta emergencias correctamente`() {
        val emergencia = Consulta(1, 1, "1-1", "Urgencia médica", 50000.0, LocalDate.now())
        val consulta Normal = Consulta(2, 1, "1-1", "Control de rutina", 20000.0, LocalDate.now())

        assertTrue("Debe detectar emergencia", emergencia.esEmergencia())
        assertFalse("No debe detectar emergencia", consultaNormal.esEmergencia())
    }

    @Test
    fun `Consulta calcula costo con descuentos correctamente`() {
        val consulta = Consulta(1, 1, "1-1", "Emergencia", 100000.0, LocalDate.now())

        val costoConDescuentoEmergencia = consulta.calcularCostoFinal(esMascotaSenior = false)
        val costoConAmbosDescuentos = consulta.calcularCostoFinal(esMascotaSenior = true)

        assertEquals("Debe aplicar 15% descuento emergencia", 85000.0, costoConDescuentoEmergencia, 0.1)
        assertTrue("Debe aplicar ambos descuentos", costoConAmbosDescuentos < costoConDescuentoEmergencia)
    }

    @Test
    fun `Consulta categoriza tipo correctamente`() {
        val emergencia = Consulta(1, 1, "1-1", "Emergencia", 50000.0, LocalDate.now())
        val control = Consulta(2, 1, "1-1", "Control mensual", 20000.0, LocalDate.now())
        val vacuna = Consulta(3, 1, "1-1", "Vacuna antirrábica", 15000.0, LocalDate.now())

        assertEquals("Debe categorizar como Emergencia", "Emergencia", emergencia.obtenerCategoria())
        assertEquals("Debe categorizar como Control", "Control", control.obtenerCategoria())
        assertEquals("Debe categorizar como Vacunación", "Vacunación", vacuna.obtenerCategoria())
    }

    @Test
    fun `Veterinario valida especialidad correctamente`() {
        val veterinarioValido = Veterinario(1, "Dr. Smith", "Cardiología")
        val veterinarioInvalido = Veterinario(2, "Dr. Jones", "Astrología")

        assertTrue("Cardiología debe ser válida", veterinarioValido.validarEspecialidad())
        assertFalse("Astrología no debe ser válida", veterinarioInvalido.validarEspecialidad())
    }

    @Test
    fun `Veterinario diferencia especialistas de generales`() {
        val especialista = Veterinario(1, "Dr. Smith", "Cardiología")
        val general = Veterinario(2, "Dr. Jones", "General")

        assertTrue("Cardiólogo debe ser especialista", especialista.esEspecialista())
        assertFalse("General no debe ser especialista", general.esEspecialista())
    }
}
