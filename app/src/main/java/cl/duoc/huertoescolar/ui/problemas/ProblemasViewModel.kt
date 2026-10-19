package cl.duoc.huertoescolar.ui.problemas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolar.data.local.HuertoDatabase
import cl.duoc.huertoescolar.data.modelo.Problema
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.modelo.Usuario
import cl.duoc.huertoescolar.data.repositorio.HuertoRepositorio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProblemasViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = HuertoRepositorio(HuertoDatabase.obtener(application))

    val problemas: StateFlow<List<Problema>> = repositorio.obtenerProblemas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val sectores: StateFlow<List<Sector>> = repositorio.obtenerSectores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val usuarios: StateFlow<List<Usuario>> = repositorio.obtenerUsuarios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
