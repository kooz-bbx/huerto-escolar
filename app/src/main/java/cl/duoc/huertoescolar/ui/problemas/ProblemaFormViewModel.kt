package cl.duoc.huertoescolar.ui.problemas

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolar.data.Sesion
import cl.duoc.huertoescolar.data.local.HuertoDatabase
import cl.duoc.huertoescolar.data.modelo.Cultivo
import cl.duoc.huertoescolar.data.modelo.Problema
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.modelo.TipoProblema
import cl.duoc.huertoescolar.data.repositorio.HuertoRepositorio
import cl.duoc.huertoescolar.domain.fechaHoraActual
import cl.duoc.huertoescolar.domain.tieneLargoMinimo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Reportar un problema detectado en el huerto (lo puede hacer el estudiante)
class ProblemaFormViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = HuertoRepositorio(HuertoDatabase.obtener(application))

    val sectores: StateFlow<List<Sector>> = repositorio.obtenerSectores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val cultivos: StateFlow<List<Cultivo>> = repositorio.obtenerCultivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var sectorId by mutableStateOf<Int?>(null)
        private set
    var cultivoId by mutableStateOf<Int?>(null)
        private set
    var tipo by mutableStateOf<TipoProblema?>(null)
        private set
    var descripcion by mutableStateOf("")
        private set
    var fotoUri by mutableStateOf("")
        private set

    var errorSector by mutableStateOf<String?>(null)
        private set
    var errorTipo by mutableStateOf<String?>(null)
        private set
    var errorDescripcion by mutableStateOf<String?>(null)
        private set

    fun cambiarSector(id: Int) { sectorId = id; cultivoId = null; errorSector = null }
    fun cambiarCultivo(id: Int) { cultivoId = if (cultivoId == id) null else id }
    fun cambiarTipo(valor: TipoProblema) { tipo = valor; errorTipo = null }
    fun cambiarDescripcion(valor: String) { descripcion = valor; errorDescripcion = null }
    fun cambiarFoto(uri: String) { fotoUri = uri }
    fun quitarFoto() { fotoUri = "" }

    private fun validar(): Boolean {
        errorSector = if (sectorId == null) "Selecciona dónde está el problema" else null
        errorTipo = if (tipo == null) "Selecciona el tipo de problema" else null
        errorDescripcion = when {
            !tieneLargoMinimo(descripcion, 10) -> "Describe el problema (mínimo 10 caracteres)"
            descripcion.length > 200 -> "Máximo 200 caracteres"
            else -> null
        }
        return errorSector == null && errorTipo == null && errorDescripcion == null
    }

    fun enviar(onEnviado: () -> Unit) {
        if (!validar()) return

        viewModelScope.launch {
            val problema = Problema(
                sectorId = sectorId ?: 0,
                cultivoId = cultivoId,
                tipo = tipo ?: TipoProblema.OTRO,
                descripcion = descripcion.trim(),
                fechaReporte = fechaHoraActual(),
                reportadoPorId = Sesion.usuarioActual?.id ?: 0,
                fotoUri = fotoUri
            )
            repositorio.guardarProblema(problema)
            onEnviado()
        }
    }
}
