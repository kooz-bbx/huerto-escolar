package cl.duoc.huertoescolar.model.data.remote

import cl.duoc.huertoescolar.model.data.remote.dto.ClimaRespuesta
import retrofit2.http.GET
import retrofit2.http.Query

// API externa: pronóstico del tiempo de Open-Meteo (no necesita clave)
// Ejemplo: https://api.open-meteo.com/v1/forecast?latitude=-33.59&longitude=-70.70&current=temperature_2m
interface ClimaApiService {

    @GET("v1/forecast")
    suspend fun obtenerClima(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query("current") actual: String = "temperature_2m,relative_humidity_2m,precipitation,weather_code",
        @Query("daily") diario: String = "precipitation_probability_max",
        @Query("timezone") zonaHoraria: String = "America/Santiago",
        @Query("forecast_days") dias: Int = 1
    ): ClimaRespuesta
}
