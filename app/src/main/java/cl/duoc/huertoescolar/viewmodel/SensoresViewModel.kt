package cl.duoc.huertoescolar.viewmodel

import android.app.Application
import cl.duoc.huertoescolar.model.rules.aResumen
import cl.duoc.huertoescolar.model.rules.ResumenClima
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolar.model.data.local.HuertoDatabase
import cl.duoc.huertoescolar.model.data.local.entity.LecturaSensor
import cl.duoc.huertoescolar.model.data.local.entity.Sector
import cl.duoc.huertoescolar.model.repository.HuertoRepositorio
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

    // ---------- API externa: clima real (Open-Meteo) ----------
    var clima by mutableStateOf<ResumenClima?>(null)
        private set
    var cargandoClima by mutableStateOf(false)
        private set
    var errorClima by mutableStateOf<String?>(null)
        private set

    init {
        cargarClima()
    }

    fun cargarClima() {
        viewModelScope.launch {
            cargandoClima = true
            errorClima = null
            val resumen = repositorio.obtenerClima()?.aResumen()
            cargandoClima = false
            if (resumen == null) errorClima = "No se pudo obtener el clima. Revisa la conexión a Internet."
            clima = resumen
        }
    }
}
