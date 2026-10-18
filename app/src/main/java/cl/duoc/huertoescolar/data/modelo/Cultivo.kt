package cl.duoc.huertoescolar.data.modelo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class EstadoCultivo(val etiqueta: String) {
    PLANIFICADO("Planificado"),
    SEMBRADO("Sembrado"),
    EN_CRECIMIENTO("En crecimiento"),
    LISTO_PARA_COSECHAR("Listo para cosechar"),
    COSECHADO("Cosechado"),
    CON_PROBLEMAS("Con problemas"),
    FINALIZADO("Finalizado")
}

// Cada cultivo pertenece a un sector (clave foránea sectorId → sectores.id).
// RESTRICT: no se puede borrar un sector que todavía tiene cultivos.
@Entity(
    tableName = "cultivos",
    foreignKeys = [
        ForeignKey(
            entity = Sector::class,
            parentColumns = ["id"],
            childColumns = ["sectorId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("sectorId")]
)
data class Cultivo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val sectorId: Int,
    val fechaSiembra: String,        // formato dd/MM/yyyy
    val fechaTrasplante: String,     // vacío si no corresponde
    val fechaCosecha: String,        // fecha estimada
    val frecuenciaRiegoDias: Int,    // cada cuántos días se riega
    val estado: EstadoCultivo = EstadoCultivo.PLANIFICADO,
    val observaciones: String = ""
)
