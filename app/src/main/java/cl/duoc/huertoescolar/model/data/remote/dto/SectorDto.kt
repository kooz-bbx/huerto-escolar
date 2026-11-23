package cl.duoc.huertoescolar.model.data.remote.dto

// Lo que viaja en JSON hacia y desde el microservicio. Mismos nombres que en Spring Boot.
data class SectorDto(
    val id: Int?,                 // null al crear: el servidor asigna el id
    val codigo: String,
    val nombre: String,
    val descripcion: String?,
    val ubicacion: String,
    val estado: String,           // "EN_USO", "DISPONIBLE"...
    val responsableId: Int,
    val fechaCreacion: String?,
    val fechaActualizacion: String?
)
