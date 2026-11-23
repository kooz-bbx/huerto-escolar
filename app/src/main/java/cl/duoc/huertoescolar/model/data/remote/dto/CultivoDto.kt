package cl.duoc.huertoescolar.model.data.remote.dto

data class CultivoDto(
    val id: Int?,
    val nombre: String,
    val descripcion: String?,
    val sectorId: Int,
    val fechaSiembra: String,
    val fechaTrasplante: String?,
    val fechaCosecha: String,
    val frecuenciaRiegoDias: Int,
    val estado: String,           // "EN_CRECIMIENTO", "SEMBRADO"...
    val observaciones: String?
)
