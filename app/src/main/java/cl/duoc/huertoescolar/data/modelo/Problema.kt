package cl.duoc.huertoescolar.data.modelo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TipoProblema(val etiqueta: String) {
    FALTA_AGUA("Falta de agua"),
    DANO_PLANTA("Daño en la planta"),
    PLAGA_SIMULADA("Plaga (simulada)"),
    DETERIORO_SECTOR("Deterioro del sector"),
    MANTENIMIENTO("Problema de mantenimiento"),
    OTRO("Otro problema")
}

enum class EstadoProblema(val etiqueta: String) {
    INFORMADO("Informado"),
    EN_REVISION("En revisión"),
    ATENDIDO("Atendido"),
    CERRADO("Cerrado")
}

// Reporte de un problema. Se relaciona con el sector, el cultivo (opcional) y quien lo reportó.
@Entity(
    tableName = "problemas",
    foreignKeys = [
        ForeignKey(entity = Sector::class, parentColumns = ["id"], childColumns = ["sectorId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = Cultivo::class, parentColumns = ["id"], childColumns = ["cultivoId"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["reportadoPorId"])
    ],
    indices = [Index("sectorId"), Index("cultivoId"), Index("reportadoPorId")]
)
data class Problema(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sectorId: Int,
    val cultivoId: Int? = null,
    val tipo: TipoProblema,
    val descripcion: String,
    val fechaReporte: String,            // dd/MM/yyyy HH:mm
    val reportadoPorId: Int,
    val fotoUri: String = "",
    val estado: EstadoProblema = EstadoProblema.INFORMADO,
    val comentarioDocente: String = "",
    val fechaCierre: String = ""
)
