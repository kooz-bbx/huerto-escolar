package cl.duoc.huertoescolar.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.huertoescolar.data.modelo.Tarea
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {

    @Query("SELECT * FROM tareas")
    fun obtenerTodas(): Flow<List<Tarea>>

    // Tareas de un estudiante: las suyas o las de su grupo
    @Query("SELECT * FROM tareas WHERE responsableId = :idEstudiante OR grupoAsignado = :grupo")
    fun obtenerDeEstudiante(idEstudiante: Int, grupo: String): Flow<List<Tarea>>

    @Query("SELECT * FROM tareas WHERE id = :id")
    fun observarPorId(id: Int): Flow<Tarea?>

    @Query("SELECT * FROM tareas WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Tarea?

    // Devuelve el id generado (lo usamos al crear datos de prueba)
    @Insert
    suspend fun insertar(tarea: Tarea): Long

    @Insert
    suspend fun insertarTodas(tareas: List<Tarea>)

    @Update
    suspend fun actualizar(tarea: Tarea)

    @Delete
    suspend fun eliminar(tarea: Tarea)
}
