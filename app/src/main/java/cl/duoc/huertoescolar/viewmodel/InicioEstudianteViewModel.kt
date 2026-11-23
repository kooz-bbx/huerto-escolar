package cl.duoc.huertoescolar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolar.model.Sesion
import cl.duoc.huertoescolar.model.data.local.HuertoDatabase
import cl.duoc.huertoescolar.model.data.local.entity.Problema
import cl.duoc.huertoescolar.model.data.local.entity.Sector
import cl.duoc.huertoescolar.model.data.local.entity.Tarea
import cl.duoc.huertoescolar.model.data.local.entity.Usuario
import cl.duoc.huertoescolar.model.repository.HuertoRepositorio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn

class InicioEstudianteViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = HuertoRepositorio(HuertoDatabase.obtener(application))

    // Solo las tareas del estudiante que inició sesión (o de su grupo)
    val misTareas: StateFlow<List<Tarea>> =
        (Sesion.usuarioActual?.let { repositorio.obtenerTareasDeEstudiante(it) } ?: emptyFlow())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Problemas que reportó este estudiante (para ver el seguimiento del docente)
    val misProblemas: StateFlow<List<Problema>> =
        (Sesion.usuarioActual?.let { repositorio.obtenerProblemasDeUsuario(it.id) } ?: emptyFlow())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sectores: StateFlow<List<Sector>> = repositorio.obtenerSectores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val usuarios: StateFlow<List<Usuario>> = repositorio.obtenerUsuarios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
