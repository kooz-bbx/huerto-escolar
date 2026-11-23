package cl.duoc.huertoescolar.model.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Crea los clientes Retrofit (uno por cada servidor). Patrón Singleton: un solo objeto en toda la app.
object RetrofitClient {

    // Microservicio Spring Boot que corre en el PC.
    // 10.0.2.2 es "localhost del PC" visto desde el emulador de Android.
    // En un celular real: cambiar por la IP del PC en la red Wi-Fi (ej: http://192.168.1.10:8080/).
    const val URL_MICROSERVICIO = "http://10.0.2.2:8080/"

    // API externa del clima (gratuita, sin clave): https://open-meteo.com
    const val URL_CLIMA = "https://api.open-meteo.com/"

    // Si el servidor no responde en pocos segundos, se avisa en vez de quedarse esperando
    private val clienteHttp = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    val huertoApi: HuertoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(URL_MICROSERVICIO)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())   // JSON ↔ objetos Kotlin
            .build()
            .create(HuertoApiService::class.java)
    }

    val climaApi: ClimaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(URL_CLIMA)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClimaApiService::class.java)
    }
}
