package cl.duoc.huertoescolar.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.huertoescolar.data.modelo.Cultivo
import kotlinx.coroutines.flow.Flow

@Dao
interface CultivoDao {

    @Query("SELECT * FROM cultivos ORDER BY nombre")
    fun obtenerTodos(): Flow<List<Cultivo>>

    // Cultivos de un sector (para el detalle del sector)
    @Query("SELECT * FROM cultivos WHERE sectorId = :sectorId ORDER BY nombre")
    fun obtenerPorSector(sectorId: Int): Flow<List<Cultivo>>

    @Query("SELECT * FROM cultivos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Cultivo?

    @Insert
    suspend fun insertar(cultivo: Cultivo)

    @Insert
    suspend fun insertarTodos(cultivos: List<Cultivo>)

    @Update
    suspend fun actualizar(cultivo: Cultivo)

    @Delete
    suspend fun eliminar(cultivo: Cultivo)
}
