package cl.duoc.huertoescolar.ui.tareas

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolar.data.Sesion
import cl.duoc.huertoescolar.data.local.HuertoDatabase
import cl.duoc.huertoescolar.data.modelo.Cultivo
import cl.duoc.huertoescolar.data.modelo.EstadoTarea
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.modelo.Tarea
import cl.duoc.huertoescolar.data.modelo.TipoTarea
import cl.duoc.huertoescolar.data.modelo.Usuario
import cl.duoc.huertoescolar.data.repositorio.HuertoRepositorio
import cl.duoc.huertoescolar.domain.esCampoVacio
import cl.duoc.huertoescolar.domain.esFechaHoyOFutura
import cl.duoc.huertoescolar.domain.esFechaValida
import cl.duoc.huertoescolar.domain.fechaHoy
import cl.duoc.huertoescolar.domain.puedeEditarse
import cl.duoc.huertoescolar.domain.tieneLargoMinimo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// A quién se asigna la tarea
enum class TipoAsignacion(val etiqueta: String) {
    ESTUDIANTE("Un estudiante"),
    GRUPO("Un grupo")
}

// Crear (id = 0) o editar una tarea. Solo se editan tareas que siguen PENDIENTES.
class TareaFormViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = HuertoRepositorio(HuertoDatabase.obtener(application))
    private var tareaOriginal: Tarea? = null
    private var yaCargado = false

    val sectores: StateFlow<List<Sector>> = repositorio.obtenerSectores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val cultivos: StateFlow<List<Cultivo>> = repositorio.obtenerCultivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val estudiantes: StateFlow<List<Usuario>> = repositorio.obtenerEstudiantes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var esEdicion by mutableStateOf(false)
        private set
    var tipo by mutableStateOf<TipoTarea?>(null)
        private set
    var sectorId by mutableStateOf<Int?>(null)
        private set
    var cultivoId by mutableStateOf<Int?>(null)
        private set
    var asignacion by mutableStateOf(TipoAsignacion.ESTUDIANTE)
        private set
    var estudianteId by mutableStateOf<Int?>(null)
        private set
    var grupo by mutableStateOf<String?>(null)
        private set
    var fechaProgramada by mutableStateOf("")
        private set
    var instrucciones by mutableStateOf("")
        private set

    var errorTipo by mutableStateOf<String?>(null)
        private set
    var errorSector by mutableStateOf<String?>(null)
        private set
    var errorResponsable by mutableStateOf<String?>(null)
        private set
    var errorFecha by mutableStateOf<String?>(null)
        private set
    var errorInstrucciones by mutableStateOf<String?>(null)
        private set
    var mensajeError by mutableStateOf<String?>(null)
        private set

    fun cargar(id: Int) {
        if (yaCargado) return
        yaCargado = true
        if (id == 0) return

        viewModelScope.launch {
            val tarea = repositorio.obtenerTarea(id) ?: return@launch
            tareaOriginal = tarea
            esEdicion = true
            tipo = tarea.tipo
            sectorId = tarea.sectorId
            cultivoId = tarea.cultivoId
            asignacion = if (tarea.grupoAsignado != null) TipoAsignacion.GRUPO else TipoAsignacion.ESTUDIANTE
            estudianteId = tarea.responsableId
            grupo = tarea.grupoAsignado
            fechaProgramada = tarea.fechaProgramada
            instrucciones = tarea.instrucciones
            if (!puedeEditarse(tarea)) {
                mensajeError = "Esta tarea ya fue iniciada por el estudiante y no se puede editar."
            }
        }
    }

    fun cambiarTipo(valor: TipoTarea) { tipo = valor; errorTipo = null }

    fun cambiarSector(id: Int) {
        sectorId = id
        cultivoId = null   // al cambiar de sector, se borra el cultivo elegido
        errorSector = null
    }

    // Tocar el cultivo seleccionado otra vez lo deselecciona (es opcional)
    fun cambiarCultivo(id: Int) { cultivoId = if (cultivoId == id) null else id }

    fun cambiarAsignacion(valor: TipoAsignacion) {
        asignacion = valor
        estudianteId = null
        grupo = null
        errorResponsable = null
    }

    fun cambiarEstudiante(id: Int) { estudianteId = id; errorResponsable = null }
    fun cambiarGrupo(valor: String) { grupo = valor; errorResponsable = null }
    fun cambiarFecha(valor: String) { fechaProgramada = valor; errorFecha = null }
    fun cambiarInstrucciones(valor: String) { instrucciones = valor; errorInstrucciones = null }

    private fun validar(): Boolean {
        errorTipo = if (tipo == null) "Selecciona el tipo de tarea" else null
        errorSector = if (sectorId == null) "Selecciona el sector" else null
        errorResponsable = when {
            asignacion == TipoAsignacion.ESTUDIANTE && estudianteId == null -> "Selecciona un estudiante"
            asignacion == TipoAsignacion.GRUPO && grupo == null -> "Selecciona un grupo"
            else -> null
        }
        errorFecha = when {
            esCampoVacio(fechaProgramada) -> "Ingresa la fecha programada"
            !esFechaValida(fechaProgramada) -> "Formato dd/mm/aaaa (ej: 20/10/2026)"
            !esEdicion && !esFechaHoyOFutura(fechaProgramada) -> "La fecha no puede ser anterior a hoy"
            else -> null
        }
        errorInstrucciones = if (!tieneLargoMinimo(instrucciones, 10))
            "Escribe instrucciones claras (mínimo 10 caracteres)" else null

        return errorTipo == null && errorSector == null && errorResponsable == null &&
                errorFecha == null && errorInstrucciones == null
    }

    fun guardar(onGuardado: () -> Unit) {
        val original = tareaOriginal
        if (original != null && !puedeEditarse(original)) return
        if (!validar()) return

        viewModelScope.launch {
            // Seguridad extra: el responsable tiene que existir y ser un estudiante activo
            val idEstudiante = estudianteId
            if (asignacion == TipoAsignacion.ESTUDIANTE &&
                (idEstudiante == null || !repositorio.esEstudianteActivo(idEstudiante))
            ) {
                errorResponsable = "El estudiante no existe o su cuenta no está activa"
                return@launch
            }

            val datos = Tarea(
                id = original?.id ?: 0,
                tipo = tipo ?: TipoTarea.OTRA,
                instrucciones = instrucciones.trim(),
                sectorId = sectorId ?: 0,
                cultivoId = cultivoId,
                fechaCreacion = original?.fechaCreacion ?: fechaHoy(),
                fechaProgramada = fechaProgramada.trim(),
                responsableId = if (asignacion == TipoAsignacion.ESTUDIANTE) idEstudiante else null,
                grupoAsignado = if (asignacion == TipoAsignacion.GRUPO) grupo else null,
                creadorId = original?.creadorId ?: (Sesion.usuarioActual?.id ?: 0),
                estado = original?.estado ?: EstadoTarea.PENDIENTE
            )
            try {
                repositorio.guardarTarea(datos)
                onGuardado()
            } catch (e: Exception) {
                mensajeError = "No se pudo guardar la tarea. Revisa los datos e inténtalo de nuevo."
            }
        }
    }
}
