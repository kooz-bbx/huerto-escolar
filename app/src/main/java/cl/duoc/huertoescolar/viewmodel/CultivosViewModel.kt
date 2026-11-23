package cl.duoc.huertoescolar.viewmodel

import android.app.Application
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolar.model.data.local.HuertoDatabase
import cl.duoc.huertoescolar.model.data.local.entity.Cultivo
import cl.duoc.huertoescolar.model.data.local.entity.Sector
import cl.duoc.huertoescolar.model.repository.HuertoRepositorio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CultivosViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = HuertoRepositorio(HuertoDatabase.obtener(application))

    val cultivos: StateFlow<List<Cultivo>> = repositorio.obtenerCultivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sectores: StateFlow<List<Sector>> = repositorio.obtenerSectores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ---------- Conexión con el microservicio ----------
    // Aviso que se muestra si el backend no responde (null = conectado)
    var avisoConexion by mutableStateOf<String?>(null)
        private set
    var sincronizando by mutableStateOf(false)
        private set

    init {
        sincronizar()
    }

    // Trae los datos del microservicio y los guarda en Room
    fun sincronizar() {
        viewModelScope.launch {
            sincronizando = true
            val conectado = repositorio.sincronizarSectoresYCultivos()
            sincronizando = false
            avisoConexion = if (conectado) null
            else "Sin conexión con el servidor: se muestran los datos guardados en el teléfono"
        }
    }
}
