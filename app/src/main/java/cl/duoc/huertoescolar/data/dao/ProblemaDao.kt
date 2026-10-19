package cl.duoc.huertoescolar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.huertoescolar.data.modelo.Problema
import kotlinx.coroutines.flow.Flow

@Dao
interface ProblemaDao {

    @Query("SELECT * FROM problemas ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<Problema>>

    @Query("SELECT * FROM problemas WHERE reportadoPorId = :idUsuario ORDER BY id DESC")
    fun obtenerDeUsuario(idUsuario: Int): Flow<List<Problema>>

    @Query("SELECT * FROM problemas WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Problema?

    @Insert
    suspend fun insertar(problema: Problema)

    @Insert
    suspend fun insertarTodos(problemas: List<Problema>)

    @Update
    suspend fun actualizar(problema: Problema)
}
