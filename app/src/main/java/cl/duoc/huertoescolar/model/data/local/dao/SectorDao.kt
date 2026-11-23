package cl.duoc.huertoescolar.model.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import cl.duoc.huertoescolar.model.data.local.entity.Sector
import kotlinx.coroutines.flow.Flow

@Dao
interface SectorDao {

    // Flow: la lista se actualiza sola en pantalla cuando cambia la base de datos
    @Query("SELECT * FROM sectores ORDER BY codigo")
    fun obtenerTodos(): Flow<List<Sector>>

    @Query("SELECT * FROM sectores WHERE id = :id")
    fun observarPorId(id: Int): Flow<Sector?>

    @Query("SELECT * FROM sectores WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Sector?

    @Query("SELECT COUNT(*) FROM sectores WHERE codigo = :codigo AND id != :idActual")
    suspend fun contarPorCodigo(codigo: String, idActual: Int): Int

    @Insert
    suspend fun insertar(sector: Sector)

    @Insert
    suspend fun insertarTodos(sectores: List<Sector>)

    @Update
    suspend fun actualizar(sector: Sector)

    @Delete
    suspend fun eliminar(sector: Sector)

    // Upsert: inserta si no existe o actualiza si ya existe (sin borrar la fila).
    // Se usa al recibir datos del microservicio.
    @Upsert
    suspend fun upsert(sector: Sector)

    @Upsert
    suspend fun upsertTodos(sectores: List<Sector>)
}
