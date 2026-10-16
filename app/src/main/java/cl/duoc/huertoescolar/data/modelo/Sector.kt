package cl.duoc.huertoescolar.data.modelo

enum class EstadoSector(val etiqueta: String) {
    DISPONIBLE("Disponible"),
    EN_USO("En uso"),
    EN_MANTENIMIENTO("En mantenimiento"),
    FUERA_DE_USO("Fuera de uso")
}

data class Sector(
    val id: Int = 0,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val ubicacion: String,
    val estado: EstadoSector = EstadoSector.DISPONIBLE,
    val responsableId: Int,
    val fechaCreacion: String,
    val fechaActualizacion: String
)
