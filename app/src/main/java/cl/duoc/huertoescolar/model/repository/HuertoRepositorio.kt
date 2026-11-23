package cl.duoc.huertoescolar.model.repository

import androidx.room.withTransaction
import cl.duoc.huertoescolar.model.DatosPrueba
import cl.duoc.huertoescolar.model.data.local.HuertoDatabase
import cl.duoc.huertoescolar.model.data.local.entity.Cultivo
import cl.duoc.huertoescolar.model.data.local.entity.EstadoTarea
import cl.duoc.huertoescolar.model.data.local.entity.Evidencia
import cl.duoc.huertoescolar.model.data.local.entity.LecturaSensor
import cl.duoc.huertoescolar.model.data.local.entity.Problema
import cl.duoc.huertoescolar.model.data.local.entity.ResultadoValidacion
import cl.duoc.huertoescolar.model.data.local.entity.Sector
import cl.duoc.huertoescolar.model.data.local.entity.Tarea
import cl.duoc.huertoescolar.model.data.local.entity.Usuario
import cl.duoc.huertoescolar.model.data.local.entity.Validacion
import cl.duoc.huertoescolar.model.rules.fechaHoraActual
import cl.duoc.huertoescolar.model.rules.generarLecturaSimulada
import cl.duoc.huertoescolar.model.data.remote.ClimaApiService
import cl.duoc.huertoescolar.model.data.remote.HuertoApiService
import cl.duoc.huertoescolar.model.data.remote.RetrofitClient
import cl.duoc.huertoescolar.model.data.remote.aDto
import cl.duoc.huertoescolar.model.data.remote.aEntidad
import cl.duoc.huertoescolar.model.data.remote.dto.ClimaRespuesta
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

// El repositorio es el único que habla con la base de datos.
// Los ViewModel le piden datos a él y no a los DAO directamente.
// db = almacenamiento local (Room) · api = microservicio Spring Boot · climaApi = API externa
class HuertoRepositorio(
    private val db: HuertoDatabase,
    private val api: HuertoApiService = RetrofitClient.huertoApi,
    private val climaApi: ClimaApiService = RetrofitClient.climaApi
) {

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
            db.problemaDao().insertarTodos(DatosPrueba.problemas)
            db.lecturaSensorDao().insertarTodas(DatosPrueba.lecturas)
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

    // ---------- Sincronización con el microservicio ----------
    // Trae sectores y cultivos desde Spring Boot y los guarda en Room (copia local).
    // Si el servidor no responde, la app sigue funcionando con los datos del teléfono.
    suspend fun sincronizarSectoresYCultivos(): Boolean {
        return try {
            val sectores = api.listarSectores().map { it.aEntidad() }
            val cultivos = api.listarCultivos().map { it.aEntidad() }
            db.withTransaction {
                db.sectorDao().upsertTodos(sectores)   // primero los padres (clave foránea)
                db.cultivoDao().upsertTodos(cultivos)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    // Guarda en el microservicio (POST si es nuevo, PUT si ya existe) y después en Room.
    // Devuelve null si salió bien, o el mensaje de error para mostrar en pantalla.
    suspend fun guardarSector(sector: Sector): String? {
        return try {
            val guardado = if (sector.id == 0) api.crearSector(sector.aDto())
            else api.actualizarSector(sector.id, sector.aDto())
            db.sectorDao().upsert(guardado.aEntidad())
            null
        } catch (e: Exception) {
            mensajeDeError(e)
        }
    }

    // Primero se borra en Room (revisa que no tenga cultivos ni tareas) y después en el servidor.
    // Si el servidor falla, se vuelve a insertar el sector para no quedar desincronizados.
    suspend fun eliminarSector(sector: Sector): String? {
        try {
            db.sectorDao().eliminar(sector)
        } catch (e: Exception) {
            return "No se puede eliminar: tiene cultivos o tareas asociadas"
        }
        return try {
            val respuesta = api.eliminarSector(sector.id)
            if (respuesta.isSuccessful || respuesta.code() == 404) {
                null
            } else {
                db.sectorDao().upsert(sector)
                "El servidor no permitió eliminar el sector"
            }
        } catch (e: Exception) {
            db.sectorDao().upsert(sector)
            mensajeDeError(e)
        }
    }

    // ---------- Cultivos ----------
    fun obtenerCultivos(): Flow<List<Cultivo>> = db.cultivoDao().obtenerTodos()

    fun obtenerCultivosDeSector(sectorId: Int): Flow<List<Cultivo>> =
        db.cultivoDao().obtenerPorSector(sectorId)

    suspend fun obtenerCultivo(id: Int): Cultivo? = db.cultivoDao().obtenerPorId(id)

    suspend fun guardarCultivo(cultivo: Cultivo): String? {
        return try {
            val guardado = if (cultivo.id == 0) api.crearCultivo(cultivo.aDto())
            else api.actualizarCultivo(cultivo.id, cultivo.aDto())
            db.cultivoDao().upsert(guardado.aEntidad())
            null
        } catch (e: Exception) {
            mensajeDeError(e)
        }
    }

    suspend fun eliminarCultivo(cultivo: Cultivo): String? {
        return try {
            val respuesta = api.eliminarCultivo(cultivo.id)
            if (respuesta.isSuccessful || respuesta.code() == 404) {
                db.cultivoDao().eliminar(cultivo)   // las tareas de este cultivo quedan sin cultivo (SET_NULL)
                null
            } else {
                "El servidor no permitió eliminar el cultivo"
            }
        } catch (e: Exception) {
            mensajeDeError(e)
        }
    }

    // ---------- API externa: clima ----------
    // Coordenadas de San Bernardo (comuna del caso del huerto)
    suspend fun obtenerClima(): ClimaRespuesta? {
        return try {
            climaApi.obtenerClima(latitud = -33.59, longitud = -70.70)
        } catch (e: Exception) {
            null
        }
    }

    // Traduce los errores de red o del servidor a un mensaje entendible
    private fun mensajeDeError(e: Exception): String = when (e) {
        is HttpException -> {
            // El backend responde {"estado": 409, "mensaje": "Ya existe un sector con el código S-01"}
            // o, si falla una validación: {"mensaje": "Datos inválidos", "campos": {"nombre": "El nombre es obligatorio"}}
            val cuerpo = try { e.response()?.errorBody()?.string() } catch (x: Exception) { null }
            val mensaje = try {
                cuerpo?.let {
                    val json = JSONObject(it)
                    val campos = json.optJSONObject("campos")
                    if (campos != null && campos.length() > 0) campos.getString(campos.keys().next())
                    else json.optString("mensaje")
                }
            } catch (x: Exception) { null }
            if (mensaje.isNullOrBlank()) "Error del servidor (${e.code()})" else mensaje
        }
        is IOException -> "No se pudo conectar con el servidor. Revisa que el backend esté encendido."
        else -> "Ocurrió un error: ${e.message}"
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

    // ---------- Problemas ----------
    fun obtenerProblemas(): Flow<List<Problema>> = db.problemaDao().obtenerTodos()

    fun obtenerProblemasDeUsuario(idUsuario: Int): Flow<List<Problema>> =
        db.problemaDao().obtenerDeUsuario(idUsuario)

    suspend fun obtenerProblema(id: Int): Problema? = db.problemaDao().obtenerPorId(id)

    suspend fun guardarProblema(problema: Problema) {
        if (problema.id == 0) db.problemaDao().insertar(problema) else db.problemaDao().actualizar(problema)
    }

    // ---------- Sensores (simulados) ----------
    fun obtenerLecturas(): Flow<List<LecturaSensor>> = db.lecturaSensorDao().obtenerTodas()

    // Crea una lectura ficticia nueva para cada sector (no hay sensores reales)
    suspend fun generarLecturasSimuladas(sectores: List<Sector>) {
        db.lecturaSensorDao().insertarTodas(sectores.map { generarLecturaSimulada(it.id) })
    }
}
