package cl.duoc.huertoescolar

import cl.duoc.huertoescolar.model.data.remote.dto.ClimaActual
import cl.duoc.huertoescolar.model.data.remote.dto.ClimaDiario
import cl.duoc.huertoescolar.model.data.remote.dto.ClimaRespuesta
import cl.duoc.huertoescolar.model.rules.aResumen
import cl.duoc.huertoescolar.model.rules.descripcionClima
import cl.duoc.huertoescolar.model.rules.recomendacionRiego
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

// Pruebas de la lógica que usa la API externa del clima (no llaman a Internet:
// se arma una respuesta de ejemplo y se revisa qué hace la app con ella)
class ClimaTest {

    @Test
    fun si_esta_lloviendo_no_se_recomienda_regar() {
        assertEquals("Está lloviendo: no es necesario regar hoy", recomendacionRiego(10, 2.5, 15.0))
    }

    @Test
    fun alta_probabilidad_de_lluvia_recomienda_esperar() {
        assertEquals(
            "Alta probabilidad de lluvia: conviene esperar antes de regar",
            recomendacionRiego(80, 0.0, 18.0)
        )
    }

    @Test
    fun dia_caluroso_recomienda_regar_temprano() {
        assertEquals("Día caluroso: regar temprano o al atardecer", recomendacionRiego(5, 0.0, 31.0))
    }

    @Test
    fun codigos_del_clima_se_traducen() {
        assertEquals("Despejado", descripcionClima(0))
        assertEquals("Lluvia", descripcionClima(63))
        assertEquals("Sin datos", descripcionClima(-1))
    }

    @Test
    fun respuesta_de_la_api_se_convierte_en_resumen() {
        val respuesta = ClimaRespuesta(
            actual = ClimaActual(temperatura = 22.46, humedad = 40, precipitacion = 0.0, codigoClima = 0),
            diario = ClimaDiario(probabilidadLluvia = listOf(10))
        )

        val resumen = respuesta.aResumen()

        assertNotNull(resumen)
        assertEquals(22.5, resumen!!.temperatura, 0.001)   // se redondea a 1 decimal
        assertEquals("Despejado", resumen.descripcion)
        assertEquals(10, resumen.probabilidadLluvia)
    }

    @Test
    fun respuesta_sin_datos_devuelve_null() {
        assertNull(ClimaRespuesta(actual = null, diario = null).aResumen())
    }
}
