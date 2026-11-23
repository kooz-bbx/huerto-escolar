package cl.duoc.huertoescolar.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolar.model.data.local.HuertoDatabase
import cl.duoc.huertoescolar.model.data.local.entity.Evidencia
import cl.duoc.huertoescolar.model.data.local.entity.Problema
import cl.duoc.huertoescolar.model.data.local.entity.Sector
import cl.duoc.huertoescolar.model.data.local.entity.Tarea
import cl.duoc.huertoescolar.model.data.local.entity.Usuario
import cl.duoc.huertoescolar.model.data.local.entity.Validacion
import cl.duoc.huertoescolar.model.repository.HuertoRepositorio
import cl.duoc.huertoescolar.model.rules.TipoEvento
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HistorialViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = HuertoRepositorio(HuertoDatabase.obtener(application))

    val tareas: StateFlow<List<Tarea>> = repositorio.obtenerTareas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val evidencias: StateFlow<List<Evidencia>> = repositorio.obtenerEvidencias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val validaciones: StateFlow<List<Validacion>> = repositorio.obtenerValidaciones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val problemas: StateFlow<List<Problema>> = repositorio.obtenerProblemas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val usuarios: StateFlow<List<Usuario>> = repositorio.obtenerUsuarios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val sectores: StateFlow<List<Sector>> = repositorio.obtenerSectores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // null = mostrar todo
    var filtro by mutableStateOf<TipoEvento?>(null)
        private set

    fun cambiarFiltro(nuevo: TipoEvento?) {
        filtro = nuevo
    }
}
