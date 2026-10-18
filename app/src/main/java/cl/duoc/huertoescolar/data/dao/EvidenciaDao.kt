package cl.duoc.huertoescolar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.huertoescolar.data.modelo.Evidencia
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenciaDao {

    // Las entregas de una tarea, la más nueva primero
    @Query("SELECT * FROM evidencias WHERE tareaId = :tareaId ORDER BY id DESC")
    fun obtenerDeTarea(tareaId: Int): Flow<List<Evidencia>>

    @Query("SELECT * FROM evidencias ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<Evidencia>>

    @Query("SELECT * FROM evidencias WHERE tareaId = :tareaId ORDER BY id DESC LIMIT 1")
    suspend fun obtenerUltimaDeTarea(tareaId: Int): Evidencia?

    @Insert
    suspend fun insertar(evidencia: Evidencia): Long

    @Insert
    suspend fun insertarTodas(evidencias: List<Evidencia>)
}
