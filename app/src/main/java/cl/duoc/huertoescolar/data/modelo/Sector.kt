package cl.duoc.huertoescolar.data.modelo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class EstadoSector(val etiqueta: String) {
    DISPONIBLE("Disponible"),
    EN_USO("En uso"),
    EN_MANTENIMIENTO("En mantenimiento"),
    FUERA_DE_USO("Fuera de uso")
}

// Clave foránea: el responsable del sector es un usuario que existe
@Entity(
    tableName = "sectores",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["responsableId"]
        )
    ],
    indices = [Index("responsableId"), Index(value = ["codigo"], unique = true)]
)
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
