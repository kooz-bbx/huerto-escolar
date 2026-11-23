package cl.duoc.huertoescolar.model.rules

import cl.duoc.huertoescolar.model.data.remote.dto.ClimaRespuesta
import kotlin.math.roundToInt

// Datos del clima ya listos para mostrar en pantalla
data class ResumenClima(
    val temperatura: Double,
    val humedad: Int,
    val probabilidadLluvia: Int,
    val descripcion: String,
    val recomendacion: String
)

// Códigos del clima de Open-Meteo (estándar WMO) traducidos a texto
fun descripcionClima(codigo: Int): String = when (codigo) {
    0 -> "Despejado"
    1, 2 -> "Parcialmente nublado"
    3 -> "Nublado"
    45, 48 -> "Niebla"
    in 51..57 -> "Llovizna"
    in 61..67 -> "Lluvia"
    in 71..77 -> "Nieve"
    in 80..82 -> "Chubascos"
    in 95..99 -> "Tormenta"
    else -> "Sin datos"
}

// Regla del huerto: recomendación de riego según el clima real (solo es un consejo, no riega nada)
fun recomendacionRiego(probabilidadLluvia: Int, precipitacionActual: Double, temperatura: Double): String = when {
    precipitacionActual > 0.0 -> "Está lloviendo: no es necesario regar hoy"
    probabilidadLluvia >= 60 -> "Alta probabilidad de lluvia: conviene esperar antes de regar"
    temperatura >= 28.0 -> "Día caluroso: regar temprano o al atardecer"
    else -> "Clima normal: seguir la frecuencia de riego de cada cultivo"
}

// Convierte la respuesta de la API en el resumen. Devuelve null si faltan datos.
fun ClimaRespuesta.aResumen(): ResumenClima? {
    val temperatura = actual?.temperatura ?: return null
    val humedad = actual.humedad ?: 0
    val lluviaAhora = actual.precipitacion ?: 0.0
    val probabilidad = diario?.probabilidadLluvia?.firstOrNull() ?: 0
    return ResumenClima(
        temperatura = (temperatura * 10).roundToInt() / 10.0,
        humedad = humedad,
        probabilidadLluvia = probabilidad,
        descripcion = descripcionClima(actual.codigoClima ?: -1),
        recomendacion = recomendacionRiego(probabilidad, lluviaAhora, temperatura)
    )
}
