package cl.duoc.huertoescolar.ui.tareas

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolar.data.Sesion
import cl.duoc.huertoescolar.data.local.HuertoDatabase
import cl.duoc.huertoescolar.data.modelo.Cultivo
import cl.duoc.huertoescolar.data.modelo.EstadoTarea
import cl.duoc.huertoescolar.data.modelo.Evidencia
import cl.duoc.huertoescolar.data.modelo.ResultadoValidacion
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.modelo.Tarea
import cl.duoc.huertoescolar.data.modelo.Usuario
import cl.duoc.huertoescolar.data.modelo.Validacion
import cl.duoc.huertoescolar.data.repositorio.HuertoRepositorio
import cl.duoc.huertoescolar.domain.perteneceAlEstudiante
import cl.duoc.huertoescolar.domain.puedeComenzarse
import cl.duoc.huertoescolar.domain.puedeEditarse
import cl.duoc.huertoescolar.domain.puedeEntregarse
import cl.duoc.huertoescolar.domain.puedeValidarse
import cl.duoc.huertoescolar.domain.tieneLargoMinimo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// SavedStateHandle trae el "id" de la ruta (tarea_detalle/{id})
class TareaDetalleViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repositorio = HuertoRepositorio(HuertoDatabase.obtener(application))
    private val idTarea: Int = savedStateHandle.get<Int>("id") ?: 0

    // ---------- Estado que observa la pantalla (StateFlow desde Room) ----------
    val tarea: StateFlow<Tarea?> = repositorio.observarTarea(idTarea)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val evidencias: StateFlow<List<Evidencia>> = repositorio.obtenerEvidenciasDeTarea(idTarea)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val validaciones: StateFlow<List<Validacion>> = repositorio.obtenerValidacionesDeTarea(idTarea)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val sectores: StateFlow<List<Sector>> = repositorio.obtenerSectores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val cultivos: StateFlow<List<Cultivo>> = repositorio.obtenerCultivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val usuarios: StateFlow<List<Usuario>> = repositorio.obtenerUsuarios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ---------- Formulario de entrega (estudiante) ----------
    var fotoUri by mutableStateOf("")
        private set
    var comentarioEstudiante by mutableStateOf("")
        private set
    var errorComentarioEstudiante by mutableStateOf<String?>(null)
        private set

    // ---------- Formulario de revisión (docente) ----------
    var comentarioDocente by mutableStateOf("")
        private set
    var errorComentarioDocente by mutableStateOf<String?>(null)
        private set

    // ---------- Permisos según perfil (privilegio mínimo) ----------
    fun esAdministrador(): Boolean = Sesion.esAdministrador()

    fun tieneAcceso(actual: Tarea): Boolean {
        val usuario = Sesion.usuarioActual ?: return false
        return esAdministrador() || perteneceAlEstudiante(actual, usuario)
    }

    fun puedeComenzar(actual: Tarea): Boolean =
        !esAdministrador() && tieneAcceso(actual) && puedeComenzarse(actual)

    fun puedeEntregar(actual: Tarea): Boolean =
        !esAdministrador() && tieneAcceso(actual) && puedeEntregarse(actual)

    // Un estudiante NUNCA ve los botones de validar (no puede aprobar su propia tarea)
    fun puedeRevisar(actual: Tarea): Boolean = esAdministrador() && puedeValidarse(actual)

    fun puedeModificar(actual: Tarea): Boolean = esAdministrador() && puedeEditarse(actual)

    // ---------- Acciones del estudiante ----------
    fun cambiarFoto(uri: String) { fotoUri = uri }
    fun quitarFoto() { fotoUri = "" }
    fun cambiarComentarioEstudiante(valor: String) {
        comentarioEstudiante = valor
        errorComentarioEstudiante = null
    }

    // PENDIENTE → EN_DESARROLLO
    fun comenzarTarea() {
        val actual = tarea.value ?: return
        if (!puedeComenzar(actual)) return
        viewModelScope.launch {
            repositorio.cambiarEstadoTarea(actual, EstadoTarea.EN_DESARROLLO)
        }
    }

    // EN_DESARROLLO (o RECHAZADA) → PENDIENTE_VALIDACION, guardando la evidencia
    fun entregar(onListo: () -> Unit) {
        val actual = tarea.value ?: return
        val autor = Sesion.usuarioActual ?: return
        if (!puedeEntregar(actual)) return

        errorComentarioEstudiante = when {
            !tieneLargoMinimo(comentarioEstudiante, 5) -> "Cuenta brevemente qué hiciste (mínimo 5 caracteres)"
            comentarioEstudiante.length > 300 -> "Máximo 300 caracteres"
            else -> null
        }
        if (errorComentarioEstudiante != null) return

        viewModelScope.launch {
            repositorio.entregarTarea(actual, autor.id, comentarioEstudiante.trim(), fotoUri)
            fotoUri = ""
            comentarioEstudiante = ""
            onListo()
        }
    }

    // ---------- Acciones del docente ----------
    fun cambiarComentarioDocente(valor: String) {
        comentarioDocente = valor
        errorComentarioDocente = null
    }

    fun aprobar(onListo: () -> Unit) {
        revisar(ResultadoValidacion.APROBADA, onListo)
    }

    // Para rechazar es obligatorio explicar el motivo
    fun rechazar(onListo: () -> Unit) {
        errorComentarioDocente = if (!tieneLargoMinimo(comentarioDocente, 5))
            "Indica el motivo del rechazo (mínimo 5 caracteres)" else null
        if (errorComentarioDocente != null) return
        revisar(ResultadoValidacion.RECHAZADA, onListo)
    }

    private fun revisar(resultado: ResultadoValidacion, onListo: () -> Unit) {
        val actual = tarea.value ?: return
        val validador = Sesion.usuarioActual ?: return
        if (!puedeRevisar(actual)) return
        viewModelScope.launch {
            val ok = repositorio.validarTarea(actual, validador.id, resultado, comentarioDocente.trim())
            if (ok) {
                comentarioDocente = ""
                onListo()
            }
        }
    }

    // Solo tareas PENDIENTES (nadie las ha empezado)
    fun eliminar(onResultado: (Boolean) -> Unit) {
        val actual = tarea.value ?: return
        if (!puedeModificar(actual)) return
        viewModelScope.launch {
            onResultado(repositorio.eliminarTarea(actual))
        }
    }
}
