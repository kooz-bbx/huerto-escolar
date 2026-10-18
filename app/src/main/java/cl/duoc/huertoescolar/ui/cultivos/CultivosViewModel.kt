package cl.duoc.huertoescolar.ui.cultivos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolar.data.local.HuertoDatabase
import cl.duoc.huertoescolar.data.modelo.Cultivo
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.repositorio.HuertoRepositorio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CultivosViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = HuertoRepositorio(HuertoDatabase.obtener(application))

    val cultivos: StateFlow<List<Cultivo>> = repositorio.obtenerCultivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sectores: StateFlow<List<Sector>> = repositorio.obtenerSectores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
