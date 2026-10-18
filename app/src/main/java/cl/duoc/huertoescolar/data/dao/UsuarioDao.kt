package cl.duoc.huertoescolar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.huertoescolar.data.modelo.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE usuario = :usuario AND clave = :clave LIMIT 1")
    suspend fun buscarPorCredenciales(usuario: String, clave: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE perfil = 'ESTUDIANTE' AND estadoCuenta = 'ACTIVA' ORDER BY nombre")
    fun obtenerEstudiantes(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios ORDER BY nombre")
    fun obtenerTodos(): Flow<List<Usuario>>

    // Para validar que el responsable de una tarea existe y es un estudiante activo
    @Query("SELECT COUNT(*) FROM usuarios WHERE id = :id AND perfil = 'ESTUDIANTE' AND estadoCuenta = 'ACTIVA'")
    suspend fun contarEstudianteActivo(id: Int): Int

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Insert
    suspend fun insertarTodos(usuarios: List<Usuario>)
}
