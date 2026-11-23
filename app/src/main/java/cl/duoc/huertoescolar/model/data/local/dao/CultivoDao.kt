package cl.duoc.huertoescolar.model.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import cl.duoc.huertoescolar.model.data.local.entity.Cultivo
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

    // Upsert: inserta si no existe o actualiza si ya existe (sin borrar la fila).
    // Se usa al recibir datos del microservicio.
    @Upsert
    suspend fun upsert(cultivo: Cultivo)

    @Upsert
    suspend fun upsertTodos(cultivos: List<Cultivo>)
}
