package cl.duoc.huertoescolar.domain

import cl.duoc.huertoescolar.data.modelo.EstadoLectura
import cl.duoc.huertoescolar.data.modelo.LecturaSensor
import kotlin.random.Random

// Genera lecturas FICTICIAS. No hay sensores reales, Arduino ni ESP32 (regla del caso).
// La app solo muestra el dato: no riega ni activa nada automáticamente.

// Rangos considerados "normales" para el huerto
const val HUMEDAD_SUELO_MIN = 30
const val HUMEDAD_SUELO_MAX = 80
const val TEMPERATURA_MIN = 5.0
const val TEMPERATURA_MAX = 32.0

fun calcularEstadoLectura(humedadSuelo: Int, temperatura: Double): EstadoLectura {
    return when {
        humedadSuelo < HUMEDAD_SUELO_MIN || temperatura < TEMPERATURA_MIN -> EstadoLectura.BAJO
        humedadSuelo > HUMEDAD_SUELO_MAX || temperatura > TEMPERATURA_MAX -> EstadoLectura.ALTO
        else -> EstadoLectura.NORMAL
    }
}

fun generarLecturaSimulada(sectorId: Int): LecturaSensor {
    val humedadSuelo = Random.nextInt(15, 95)
    // temperatura con un decimal, ej: 18.4
    val temperatura = Math.round(Random.nextDouble(3.0, 35.0) * 10) / 10.0
    val humedadAmbiente = Random.nextInt(30, 90)

    return LecturaSensor(
        sectorId = sectorId,
        fechaHora = fechaHoraActual(),
        humedadSuelo = humedadSuelo,
        temperatura = temperatura,
        humedadAmbiente = humedadAmbiente,
        estado = calcularEstadoLectura(humedadSuelo, temperatura)
    )
}
