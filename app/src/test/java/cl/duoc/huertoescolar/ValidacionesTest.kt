package cl.duoc.huertoescolar

import cl.duoc.huertoescolar.data.modelo.EstadoLectura
import cl.duoc.huertoescolar.data.modelo.EstadoTarea
import cl.duoc.huertoescolar.data.modelo.Tarea
import cl.duoc.huertoescolar.data.modelo.TipoTarea
import cl.duoc.huertoescolar.domain.calcularEstadoLectura
import cl.duoc.huertoescolar.domain.esClaveValida
import cl.duoc.huertoescolar.domain.esCodigoSectorValido
import cl.duoc.huertoescolar.domain.esFechaPosteriorOIgual
import cl.duoc.huertoescolar.domain.esFechaValida
import cl.duoc.huertoescolar.domain.esFrecuenciaRiegoValida
import cl.duoc.huertoescolar.domain.esUsuarioValido
import cl.duoc.huertoescolar.domain.estaAtrasada
import cl.duoc.huertoescolar.domain.fechaRelativa
import cl.duoc.huertoescolar.domain.puedeComenzarse
import cl.duoc.huertoescolar.domain.puedeEntregarse
import cl.duoc.huertoescolar.domain.puedeValidarse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas unitarias de la lógica (no necesitan emulador).
// Se ejecutan con clic derecho sobre el archivo → Run 'ValidacionesTest'.
class ValidacionesTest {

    @Test
    fun usuario_valido_e_invalido() {
        assertTrue(esUsuarioValido("docente"))
        assertFalse(esUsuarioValido("ab"))          // muy corto
        assertFalse(esUsuarioValido("ana maria"))   // tiene espacio
    }

    @Test
    fun clave_minimo_cuatro_caracteres() {
        assertTrue(esClaveValida("1234"))
        assertFalse(esClaveValida("12"))
    }

    @Test
    fun codigo_de_sector() {
        assertTrue(esCodigoSectorValido("S-05"))
        assertTrue(esCodigoSectorValido("s-12"))     // se acepta en minúscula
        assertFalse(esCodigoSectorValido("S5"))
        assertFalse(esCodigoSectorValido("S-123"))
    }

    @Test
    fun fechas_validas_e_invalidas() {
        assertTrue(esFechaValida("15/10/2026"))
        assertFalse(esFechaValida("31/02/2026"))     // febrero no tiene 31
        assertFalse(esFechaValida("2026-10-15"))     // formato incorrecto
    }

    @Test
    fun cosecha_debe_ser_despues_de_siembra() {
        assertTrue(esFechaPosteriorOIgual("01/09/2026", "15/12/2026"))
        assertFalse(esFechaPosteriorOIgual("15/12/2026", "01/09/2026"))
    }

    @Test
    fun frecuencia_de_riego_entre_1_y_30() {
        assertTrue(esFrecuenciaRiegoValida("3"))
        assertFalse(esFrecuenciaRiegoValida("0"))
        assertFalse(esFrecuenciaRiegoValida("45"))
        assertFalse(esFrecuenciaRiegoValida("dos"))
    }

    @Test
    fun tarea_atrasada_solo_si_no_se_entrego() {
        val base = Tarea(
            tipo = TipoTarea.RIEGO, instrucciones = "Regar", sectorId = 1,
            fechaCreacion = fechaRelativa(-5), fechaProgramada = fechaRelativa(-1), creadorId = 1
        )
        assertTrue(estaAtrasada(base))
        assertFalse(estaAtrasada(base.copy(estado = EstadoTarea.PENDIENTE_VALIDACION)))
        assertFalse(estaAtrasada(base.copy(fechaProgramada = fechaRelativa(2))))
    }

    @Test
    fun estado_de_lectura_simulada() {
        assertEquals(EstadoLectura.NORMAL, calcularEstadoLectura(50, 20.0))
        assertEquals(EstadoLectura.BAJO, calcularEstadoLectura(10, 20.0))
        assertEquals(EstadoLectura.ALTO, calcularEstadoLectura(90, 20.0))
    }

    @Test
    fun flujo_de_estados_de_una_tarea() {
        val pendiente = Tarea(
            tipo = TipoTarea.RIEGO, instrucciones = "Regar", sectorId = 1,
            fechaCreacion = fechaRelativa(0), fechaProgramada = fechaRelativa(1), creadorId = 1
        )
        // Pendiente: se puede comenzar, pero todavía no entregar
        assertTrue(puedeComenzarse(pendiente))
        assertFalse(puedeEntregarse(pendiente))
        // En desarrollo: ya se puede entregar
        assertTrue(puedeEntregarse(pendiente.copy(estado = EstadoTarea.EN_DESARROLLO)))
        // Pendiente de validación: solo el docente la revisa
        assertTrue(puedeValidarse(pendiente.copy(estado = EstadoTarea.PENDIENTE_VALIDACION)))
        // Rechazada: se corrige y se vuelve a entregar
        assertTrue(puedeEntregarse(pendiente.copy(estado = EstadoTarea.RECHAZADA)))
    }
}
