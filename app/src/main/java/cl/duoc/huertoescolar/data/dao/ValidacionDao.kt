package cl.duoc.huertoescolar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.huertoescolar.data.modelo.Validacion
import kotlinx.coroutines.flow.Flow

@Dao
interface ValidacionDao {

    @Query("SELECT * FROM validaciones WHERE tareaId = :tareaId ORDER BY id DESC")
    fun obtenerDeTarea(tareaId: Int): Flow<List<Validacion>>

    @Query("SELECT * FROM validaciones ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<Validacion>>

    @Insert
    suspend fun insertar(validacion: Validacion)

    @Insert
    suspend fun insertarTodas(validaciones: List<Validacion>)
}
