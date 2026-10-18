package cl.duoc.huertoescolar.ui.sectores

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolar.data.local.HuertoDatabase
import cl.duoc.huertoescolar.data.modelo.Cultivo
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.modelo.Usuario
import cl.duoc.huertoescolar.data.repositorio.HuertoRepositorio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// SavedStateHandle trae el "id" que viene en la ruta (sector_detalle/{id})
class SectorDetalleViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repositorio = HuertoRepositorio(HuertoDatabase.obtener(application))
    private val idSector: Int = savedStateHandle.get<Int>("id") ?: 0

    val sector: StateFlow<Sector?> = repositorio.observarSector(idSector)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val cultivos: StateFlow<List<Cultivo>> = repositorio.obtenerCultivosDeSector(idSector)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val usuarios: StateFlow<List<Usuario>> = repositorio.obtenerUsuarios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // onResultado(true) si se eliminó, (false) si la base de datos lo impidió
    fun eliminar(onResultado: (Boolean) -> Unit) {
        val actual = sector.value ?: return
        viewModelScope.launch {
            onResultado(repositorio.eliminarSector(actual))
        }
    }
}
