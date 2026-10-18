package cl.duoc.huertoescolar.data.modelo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TipoTarea(val etiqueta: String) {
    PREPARACION_SECTOR("Preparación del sector"),
    SIEMBRA("Siembra"),
    TRASPLANTE("Trasplante"),
    RIEGO("Riego"),
    LIMPIEZA("Limpieza"),
    RETIRO_MALEZAS("Retiro de malezas"),
    OBSERVACION("Observación del cultivo"),
    MANTENIMIENTO("Mantenimiento"),
    REVISION_SUELO("Revisión del suelo"),
    REGISTRO_CRECIMIENTO("Registro del crecimiento"),
    COSECHA("Cosecha"),
    OTRA("Otra actividad")
}

// Flujo: PENDIENTE → EN_DESARROLLO → PENDIENTE_VALIDACION → VALIDADA / RECHAZADA
// (ATRASADA no se guarda: se calcula según la fecha, ver ReglasTareas.kt)
enum class EstadoTarea(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    EN_DESARROLLO("En desarrollo"),
    PENDIENTE_VALIDACION("Pendiente de validación"),
    VALIDADA("Validada"),
    RECHAZADA("Rechazada"),
    ATRASADA("Atrasada")
}

// La tarea solo guarda QUÉ hay que hacer. Lo que entrega el estudiante va en Evidencia
// y la revisión del docente en Validacion (tablas separadas, modelo normalizado).
@Entity(
    tableName = "tareas",
    foreignKeys = [
        ForeignKey(entity = Sector::class, parentColumns = ["id"], childColumns = ["sectorId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = Cultivo::class, parentColumns = ["id"], childColumns = ["cultivoId"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["responsableId"]),
        ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["creadorId"])
    ],
    indices = [Index("sectorId"), Index("cultivoId"), Index("responsableId"), Index("creadorId")]
)
data class Tarea(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tipo: TipoTarea,
    val instrucciones: String,
    val sectorId: Int,
    val cultivoId: Int? = null,          // opcional (ej: limpieza de un sector completo)
    val fechaCreacion: String,
    val fechaProgramada: String,
    val responsableId: Int? = null,      // estudiante individual...
    val grupoAsignado: String? = null,   // ...o un grupo completo
    val creadorId: Int,                  // docente o tallerista que la creó
    val estado: EstadoTarea = EstadoTarea.PENDIENTE
)
