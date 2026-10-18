package cl.duoc.huertoescolar.ui.cultivos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.huertoescolar.data.local.HuertoDatabase
import cl.duoc.huertoescolar.data.modelo.Cultivo
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.repositorio.HuertoRepositorio
import kotlinx.coroutines.flow.Flow

class CultivosViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = HuertoRepositorio(HuertoDatabase.obtener(application))

    val cultivos: Flow<List<Cultivo>> = repositorio.obtenerCultivos()
    val sectores: Flow<List<Sector>> = repositorio.obtenerSectores()
}
