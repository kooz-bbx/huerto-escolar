package cl.duoc.huertoescolar.model.data.remote

import cl.duoc.huertoescolar.model.data.remote.dto.CultivoDto
import cl.duoc.huertoescolar.model.data.remote.dto.SectorDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Endpoints del microservicio Spring Boot (backendHuerto). Retrofit genera el código de cada llamada.
interface HuertoApiService {

    // ---------- Sectores ----------
    @GET("api/sectores")
    suspend fun listarSectores(): List<SectorDto>

    @POST("api/sectores")
    suspend fun crearSector(@Body sector: SectorDto): SectorDto

    @PUT("api/sectores/{id}")
    suspend fun actualizarSector(@Path("id") id: Int, @Body sector: SectorDto): SectorDto

    @DELETE("api/sectores/{id}")
    suspend fun eliminarSector(@Path("id") id: Int): Response<Unit>

    // ---------- Cultivos ----------
    @GET("api/cultivos")
    suspend fun listarCultivos(): List<CultivoDto>

    @POST("api/cultivos")
    suspend fun crearCultivo(@Body cultivo: CultivoDto): CultivoDto

    @PUT("api/cultivos/{id}")
    suspend fun actualizarCultivo(@Path("id") id: Int, @Body cultivo: CultivoDto): CultivoDto

    @DELETE("api/cultivos/{id}")
    suspend fun eliminarCultivo(@Path("id") id: Int): Response<Unit>
}
