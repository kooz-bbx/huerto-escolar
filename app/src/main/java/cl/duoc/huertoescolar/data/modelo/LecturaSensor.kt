package cl.duoc.huertoescolar.data.modelo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class EstadoLectura(val etiqueta: String) {
    NORMAL("Normal"),
    BAJO("Bajo"),
    ALTO("Alto"),
    PENDIENTE_REVISION("Pendiente de revisión")
}

// Lecturas SIMULADAS: no vienen de sensores reales (regla del caso).
// CASCADE: si se elimina el sector, se eliminan sus lecturas de prueba.
@Entity(
    tableName = "lecturas_sensor",
    foreignKeys = [
        ForeignKey(entity = Sector::class, parentColumns = ["id"], childColumns = ["sectorId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("sectorId")]
)
data class LecturaSensor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sectorId: Int,
    val fechaHora: String,
    val humedadSuelo: Int,          // %
    val temperatura: Double,        // °C
    val humedadAmbiente: Int,       // %
    val origen: String = "Simulación académica",
    val estado: EstadoLectura = EstadoLectura.NORMAL
)
