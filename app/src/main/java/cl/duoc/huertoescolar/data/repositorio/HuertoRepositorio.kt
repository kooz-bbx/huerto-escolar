package cl.duoc.huertoescolar.data.repositorio

import androidx.room.withTransaction
import cl.duoc.huertoescolar.data.DatosPrueba
import cl.duoc.huertoescolar.data.local.HuertoDatabase
import cl.duoc.huertoescolar.data.modelo.Cultivo
import cl.duoc.huertoescolar.data.modelo.EstadoTarea
import cl.duoc.huertoescolar.data.modelo.Evidencia
import cl.duoc.huertoescolar.data.modelo.ResultadoValidacion
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.modelo.Tarea
import cl.duoc.huertoescolar.data.modelo.Usuario
import cl.duoc.huertoescolar.data.modelo.Validacion
import cl.duoc.huertoescolar.domain.fechaHoraActual
import kotlinx.coroutines.flow.Flow

// El repositorio es el único que habla con la base de datos.
// Los ViewModel le piden datos a él y no a los DAO directamente.
class HuertoRepositorio(private val db: HuertoDatabase) {

    // ---------- Datos iniciales ----------
    // Se insertan en orden (padres antes que hijos) por las claves foráneas
    suspend fun cargarDatosIniciales() {
        if (db.usuarioDao().contar() > 0) return
        db.withTransaction {
            db.usuarioDao().insertarTodos(DatosPrueba.usuarios)
            db.sectorDao().insertarTodos(DatosPrueba.sectores)
            db.cultivoDao().insertarTodos(DatosPrueba.cultivos)
            db.tareaDao().insertarTodas(DatosPrueba.tareas)
            db.evidenciaDao().insertarTodas(DatosPrueba.evidencias)
            db.validacionDao().insertarTodas(DatosPrueba.validaciones)
        }
    }

    // ---------- Usuarios ----------
    suspend fun buscarUsuario(usuario: String, clave: String): Usuario? =
        db.usuarioDao().buscarPorCredenciales(usuario, clave)

    fun obtenerEstudiantes(): Flow<List<Usuario>> = db.usuarioDao().obtenerEstudiantes()

    fun obtenerUsuarios(): Flow<List<Usuario>> = db.usuarioDao().obtenerTodos()

    suspend fun esEstudianteActivo(id: Int): Boolean = db.usuarioDao().contarEstudianteActivo(id) > 0

    // ---------- Sectores ----------
    fun obtenerSectores(): Flow<List<Sector>> = db.sectorDao().obtenerTodos()

    fun observarSector(id: Int): Flow<Sector?> = db.sectorDao().observarPorId(id)

    suspend fun obtenerSector(id: Int): Sector? = db.sectorDao().obtenerPorId(id)

    suspend fun existeCodigoSector(codigo: String, idActual: Int): Boolean =
        db.sectorDao().contarPorCodigo(codigo, idActual) > 0

    suspend fun guardarSector(sector: Sector) {
        if (sector.id == 0) db.sectorDao().insertar(sector) else db.sectorDao().actualizar(sector)
    }

    // Devuelve false si no se pudo (la clave foránea lo impide: tiene cultivos o tareas)
    suspend fun eliminarSector(sector: Sector): Boolean {
        return try {
            db.sectorDao().eliminar(sector)
            true
        } catch (e: Exception) {
            false
        }
    }

    // ---------- Cultivos ----------
    fun obtenerCultivos(): Flow<List<Cultivo>> = db.cultivoDao().obtenerTodos()

    fun obtenerCultivosDeSector(sectorId: Int): Flow<List<Cultivo>> =
        db.cultivoDao().obtenerPorSector(sectorId)

    suspend fun obtenerCultivo(id: Int): Cultivo? = db.cultivoDao().obtenerPorId(id)

    suspend fun guardarCultivo(cultivo: Cultivo) {
        if (cultivo.id == 0) db.cultivoDao().insertar(cultivo) else db.cultivoDao().actualizar(cultivo)
    }

    suspend fun eliminarCultivo(cultivo: Cultivo): Boolean {
        return try {
            db.cultivoDao().eliminar(cultivo)
            true
        } catch (e: Exception) {
            false
        }
    }

    // ---------- Tareas ----------
    fun obtenerTareas(): Flow<List<Tarea>> = db.tareaDao().obtenerTodas()

    fun obtenerTareasDeEstudiante(estudiante: Usuario): Flow<List<Tarea>> =
        db.tareaDao().obtenerDeEstudiante(estudiante.id, estudiante.grupo)

    fun observarTarea(id: Int): Flow<Tarea?> = db.tareaDao().observarPorId(id)

    suspend fun obtenerTarea(id: Int): Tarea? = db.tareaDao().obtenerPorId(id)

    suspend fun guardarTarea(tarea: Tarea) {
        if (tarea.id == 0) db.tareaDao().insertar(tarea) else db.tareaDao().actualizar(tarea)
    }

    suspend fun eliminarTarea(tarea: Tarea): Boolean {
        return try {
            db.tareaDao().eliminar(tarea)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun cambiarEstadoTarea(tarea: Tarea, nuevo: EstadoTarea) {
        db.tareaDao().actualizar(tarea.copy(estado = nuevo))
    }

    // ---------- Evidencias y validaciones ----------
    fun obtenerEvidenciasDeTarea(tareaId: Int): Flow<List<Evidencia>> =
        db.evidenciaDao().obtenerDeTarea(tareaId)

    fun obtenerValidacionesDeTarea(tareaId: Int): Flow<List<Validacion>> =
        db.validacionDao().obtenerDeTarea(tareaId)

    fun obtenerEvidencias(): Flow<List<Evidencia>> = db.evidenciaDao().obtenerTodas()

    fun obtenerValidaciones(): Flow<List<Validacion>> = db.validacionDao().obtenerTodas()

    // El estudiante entrega: se guarda la evidencia y la tarea pasa a "pendiente de validación".
    // withTransaction: se hacen las dos cosas o ninguna.
    suspend fun entregarTarea(tarea: Tarea, autorId: Int, comentario: String, fotoUri: String) {
        db.withTransaction {
            db.evidenciaDao().insertar(
                Evidencia(
                    tareaId = tarea.id,
                    autorId = autorId,
                    fechaHora = fechaHoraActual(),
                    comentario = comentario,
                    fotoUri = fotoUri
                )
            )
            db.tareaDao().actualizar(tarea.copy(estado = EstadoTarea.PENDIENTE_VALIDACION))
        }
    }

    // El docente revisa la última entrega: queda registrado quién, cuándo y el resultado
    suspend fun validarTarea(
        tarea: Tarea,
        validadorId: Int,
        resultado: ResultadoValidacion,
        comentario: String
    ): Boolean {
        return db.withTransaction {
            val evidencia = db.evidenciaDao().obtenerUltimaDeTarea(tarea.id) ?: return@withTransaction false
            db.validacionDao().insertar(
                Validacion(
                    tareaId = tarea.id,
                    evidenciaId = evidencia.id,
                    validadorId = validadorId,
                    fechaHora = fechaHoraActual(),
                    resultado = resultado,
                    comentario = comentario
                )
            )
            val nuevoEstado = if (resultado == ResultadoValidacion.APROBADA) EstadoTarea.VALIDADA
            else EstadoTarea.RECHAZADA
            db.tareaDao().actualizar(tarea.copy(estado = nuevoEstado))
            true
        }
    }
}
