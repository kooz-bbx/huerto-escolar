package cl.duoc.huertoescolar.ui.tareas

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolar.data.local.HuertoDatabase
import cl.duoc.huertoescolar.data.modelo.EstadoTarea
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.modelo.Tarea
import cl.duoc.huertoescolar.data.modelo.Usuario
import cl.duoc.huertoescolar.data.repositorio.HuertoRepositorio
import cl.duoc.huertoescolar.domain.estadoMostrado
import cl.duoc.huertoescolar.domain.ordenarPorFecha
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

enum class FiltroTarea(val etiqueta: String) {
    TODAS("Todas"),
    PENDIENTES("Pendientes"),
    EN_DESARROLLO("En desarrollo"),
    POR_VALIDAR("Por validar"),
    ATRASADAS("Atrasadas"),
    VALIDADAS("Validadas"),
    RECHAZADAS("Rechazadas")
}

class TareasViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = HuertoRepositorio(HuertoDatabase.obtener(application))

    val tareas: StateFlow<List<Tarea>> = repositorio.obtenerTareas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val sectores: StateFlow<List<Sector>> = repositorio.obtenerSectores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val usuarios: StateFlow<List<Usuario>> = repositorio.obtenerUsuarios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var filtro by mutableStateOf(FiltroTarea.TODAS)
        private set

    // null = todos los responsables
    var filtroResponsable by mutableStateOf<String?>(null)
        private set

    fun cambiarFiltro(nuevo: FiltroTarea) { filtro = nuevo }

    fun cambiarFiltroResponsable(nombre: String?) { filtroResponsable = nombre }

    // Aplica los filtros (estado y responsable) y ordena por fecha programada
    fun filtrar(lista: List<Tarea>, usuarios: List<Usuario>): List<Tarea> {
        val filtradas = lista.filter { tarea ->
            val estado = estadoMostrado(tarea)
            val cumpleEstado = when (filtro) {
                FiltroTarea.TODAS -> true
                FiltroTarea.PENDIENTES -> estado == EstadoTarea.PENDIENTE
                FiltroTarea.EN_DESARROLLO -> estado == EstadoTarea.EN_DESARROLLO
                FiltroTarea.POR_VALIDAR -> estado == EstadoTarea.PENDIENTE_VALIDACION
                FiltroTarea.ATRASADAS -> estado == EstadoTarea.ATRASADA
                FiltroTarea.VALIDADAS -> estado == EstadoTarea.VALIDADA
                FiltroTarea.RECHAZADAS -> estado == EstadoTarea.RECHAZADA
            }
            val cumpleResponsable = filtroResponsable == null ||
                    textoResponsable(tarea, usuarios) == filtroResponsable
            cumpleEstado && cumpleResponsable
        }
        return ordenarPorFecha(filtradas)
    }
}

// Texto del responsable: nombre del estudiante o del grupo
fun textoResponsable(tarea: Tarea, usuarios: List<Usuario>): String {
    return when {
        tarea.grupoAsignado != null -> "Grupo: ${tarea.grupoAsignado}"
        else -> usuarios.find { it.id == tarea.responsableId }?.nombre ?: "Sin responsable"
    }
}
