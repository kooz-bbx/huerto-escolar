package cl.duoc.huertoescolar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolar.model.data.local.HuertoDatabase
import cl.duoc.huertoescolar.model.data.local.entity.Cultivo
import cl.duoc.huertoescolar.model.data.local.entity.LecturaSensor
import cl.duoc.huertoescolar.model.data.local.entity.Problema
import cl.duoc.huertoescolar.model.data.local.entity.Sector
import cl.duoc.huertoescolar.model.data.local.entity.Tarea
import cl.duoc.huertoescolar.model.repository.HuertoRepositorio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class AlertasViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = HuertoRepositorio(HuertoDatabase.obtener(application))

    val tareas: StateFlow<List<Tarea>> = repositorio.obtenerTareas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val cultivos: StateFlow<List<Cultivo>> = repositorio.obtenerCultivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val problemas: StateFlow<List<Problema>> = repositorio.obtenerProblemas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val lecturas: StateFlow<List<LecturaSensor>> = repositorio.obtenerLecturas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val sectores: StateFlow<List<Sector>> = repositorio.obtenerSectores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
