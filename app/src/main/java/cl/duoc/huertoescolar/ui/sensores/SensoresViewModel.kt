package cl.duoc.huertoescolar.ui.sensores

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolar.data.local.HuertoDatabase
import cl.duoc.huertoescolar.data.modelo.LecturaSensor
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.repositorio.HuertoRepositorio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SensoresViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = HuertoRepositorio(HuertoDatabase.obtener(application))

    val lecturas: StateFlow<List<LecturaSensor>> = repositorio.obtenerLecturas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val sectores: StateFlow<List<Sector>> = repositorio.obtenerSectores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Genera datos de prueba nuevos (no se conecta a ningún sensor real)
    fun simularNuevaLectura(sectores: List<Sector>) {
        viewModelScope.launch {
            repositorio.generarLecturasSimuladas(sectores)
        }
    }
}
