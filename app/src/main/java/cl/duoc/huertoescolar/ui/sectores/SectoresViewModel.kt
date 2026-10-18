package cl.duoc.huertoescolar.ui.sectores

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.huertoescolar.data.local.HuertoDatabase
import cl.duoc.huertoescolar.data.modelo.Cultivo
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.repositorio.HuertoRepositorio
import kotlinx.coroutines.flow.Flow

class SectoresViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = HuertoRepositorio(HuertoDatabase.obtener(application))

    // Listas que vienen de Room (se actualizan solas)
    val sectores: Flow<List<Sector>> = repositorio.obtenerSectores()
    val cultivos: Flow<List<Cultivo>> = repositorio.obtenerCultivos()
}
