package cl.duoc.huertoescolar.data.modelo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ResultadoValidacion(val etiqueta: String) {
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada")
}

// Revisión del docente: quién validó, cuándo y con qué resultado.
@Entity(
    tableName = "validaciones",
    foreignKeys = [
        ForeignKey(entity = Tarea::class, parentColumns = ["id"], childColumns = ["tareaId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Evidencia::class, parentColumns = ["id"], childColumns = ["evidenciaId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["validadorId"])
    ],
    indices = [Index("tareaId"), Index("evidenciaId"), Index("validadorId")]
)
data class Validacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tareaId: Int,
    val evidenciaId: Int,        // la entrega que se revisó
    val validadorId: Int,        // docente o tallerista
    val fechaHora: String,
    val resultado: ResultadoValidacion,
    val comentario: String       // obligatorio si se rechaza
)
