package cl.duoc.huertoescolar.data.modelo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Lo que entrega el estudiante al marcar una tarea como realizada.
// Una tarea puede tener varias evidencias (si la rechazan y la repite).
// CASCADE: si se elimina la tarea, se eliminan sus evidencias.
@Entity(
    tableName = "evidencias",
    foreignKeys = [
        ForeignKey(entity = Tarea::class, parentColumns = ["id"], childColumns = ["tareaId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["autorId"])
    ],
    indices = [Index("tareaId"), Index("autorId")]
)
data class Evidencia(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tareaId: Int,
    val autorId: Int,            // estudiante que la entregó
    val fechaHora: String,       // dd/MM/yyyy HH:mm
    val comentario: String,
    val fotoUri: String = ""     // vacío si no adjuntó foto
)
