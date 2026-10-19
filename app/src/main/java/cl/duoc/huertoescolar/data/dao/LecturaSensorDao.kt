package cl.duoc.huertoescolar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.huertoescolar.data.modelo.LecturaSensor
import kotlinx.coroutines.flow.Flow

@Dao
interface LecturaSensorDao {

    // Las más nuevas primero
    @Query("SELECT * FROM lecturas_sensor ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<LecturaSensor>>

    @Insert
    suspend fun insertarTodas(lecturas: List<LecturaSensor>)
}
