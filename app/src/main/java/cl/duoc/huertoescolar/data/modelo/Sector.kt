package cl.duoc.huertoescolar.data.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EstadoSector(val etiqueta: String) {
    DISPONIBLE("Disponible"),
    EN_USO("En uso"),
    EN_MANTENIMIENTO("En mantenimiento"),
    FUERA_DE_USO("Fuera de uso")
}

@Entity(tableName = "sectores")
data class Sector(
    @PrimaryKey(autoGenerate = true)
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
