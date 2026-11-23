package cl.duoc.huertoescolar.model.data.remote.dto

import com.google.gson.annotations.SerializedName

// Respuesta de Open-Meteo. @SerializedName conecta el nombre del JSON con el nombre en Kotlin.
data class ClimaRespuesta(
    @SerializedName("current") val actual: ClimaActual?,
    @SerializedName("daily") val diario: ClimaDiario?
)

data class ClimaActual(
    @SerializedName("temperature_2m") val temperatura: Double?,
    @SerializedName("relative_humidity_2m") val humedad: Int?,
    @SerializedName("precipitation") val precipitacion: Double?,
    @SerializedName("weather_code") val codigoClima: Int?
)

data class ClimaDiario(
    @SerializedName("precipitation_probability_max") val probabilidadLluvia: List<Int?>?
)
