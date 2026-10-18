package cl.duoc.huertoescolar.ui.cultivos

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolar.data.local.HuertoDatabase
import cl.duoc.huertoescolar.data.modelo.Cultivo
import cl.duoc.huertoescolar.data.modelo.EstadoCultivo
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.repositorio.HuertoRepositorio
import cl.duoc.huertoescolar.domain.esCampoVacio
import cl.duoc.huertoescolar.domain.esFechaPosteriorOIgual
import cl.duoc.huertoescolar.domain.esFechaValida
import cl.duoc.huertoescolar.domain.esFrecuenciaRiegoValida
import cl.duoc.huertoescolar.domain.tieneLargoMinimo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Crear (id = 0) o modificar un cultivo, incluido su estado de desarrollo
class CultivoFormViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = HuertoRepositorio(HuertoDatabase.obtener(application))
    private var idCultivo = 0
    private var cultivoOriginal: Cultivo? = null
    private var yaCargado = false

    val sectores: StateFlow<List<Sector>> = repositorio.obtenerSectores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var esEdicion by mutableStateOf(false)
        private set

    var nombre by mutableStateOf("")
        private set
    var descripcion by mutableStateOf("")
        private set
    var sectorId by mutableStateOf<Int?>(null)
        private set
    var fechaSiembra by mutableStateOf("")
        private set
    var fechaTrasplante by mutableStateOf("")
        private set
    var fechaCosecha by mutableStateOf("")
        private set
    var frecuenciaRiego by mutableStateOf("")
        private set
    var estado by mutableStateOf(EstadoCultivo.PLANIFICADO)
        private set
    var observaciones by mutableStateOf("")
        private set

    var errorNombre by mutableStateOf<String?>(null)
        private set
    var errorSector by mutableStateOf<String?>(null)
        private set
    var errorFechaSiembra by mutableStateOf<String?>(null)
        private set
    var errorFechaTrasplante by mutableStateOf<String?>(null)
        private set
    var errorFechaCosecha by mutableStateOf<String?>(null)
        private set
    var errorRiego by mutableStateOf<String?>(null)
        private set

    fun cargar(id: Int) {
        if (yaCargado) return
        yaCargado = true
        if (id == 0) return

        viewModelScope.launch {
            val cultivo = repositorio.obtenerCultivo(id) ?: return@launch
            cultivoOriginal = cultivo
            idCultivo = cultivo.id
            esEdicion = true
            nombre = cultivo.nombre
            descripcion = cultivo.descripcion
            sectorId = cultivo.sectorId
            fechaSiembra = cultivo.fechaSiembra
            fechaTrasplante = cultivo.fechaTrasplante
            fechaCosecha = cultivo.fechaCosecha
            frecuenciaRiego = cultivo.frecuenciaRiegoDias.toString()
            estado = cultivo.estado
            observaciones = cultivo.observaciones
        }
    }

    fun cambiarNombre(valor: String) { nombre = valor; errorNombre = null }
    fun cambiarDescripcion(valor: String) { descripcion = valor }
    fun cambiarSector(id: Int) { sectorId = id; errorSector = null }
    fun cambiarFechaSiembra(valor: String) { fechaSiembra = valor; errorFechaSiembra = null }
    fun cambiarFechaTrasplante(valor: String) { fechaTrasplante = valor; errorFechaTrasplante = null }
    fun cambiarFechaCosecha(valor: String) { fechaCosecha = valor; errorFechaCosecha = null }
    fun cambiarFrecuenciaRiego(valor: String) { frecuenciaRiego = valor; errorRiego = null }
    fun cambiarEstado(valor: EstadoCultivo) { estado = valor }
    fun cambiarObservaciones(valor: String) { observaciones = valor }

    private fun validar(): Boolean {
        errorNombre = when {
            esCampoVacio(nombre) -> "Ingresa el nombre del cultivo"
            !tieneLargoMinimo(nombre, 3) -> "Mínimo 3 caracteres"
            else -> null
        }
        errorSector = if (sectorId == null) "Selecciona el sector donde está el cultivo" else null

        errorFechaSiembra = when {
            esCampoVacio(fechaSiembra) -> "Ingresa la fecha de siembra"
            !esFechaValida(fechaSiembra) -> "Formato dd/mm/aaaa (ej: 05/10/2026)"
            else -> null
        }
        // El trasplante es opcional, pero si se escribe debe ser válido y después de la siembra
        errorFechaTrasplante = when {
            esCampoVacio(fechaTrasplante) -> null
            !esFechaValida(fechaTrasplante) -> "Formato dd/mm/aaaa"
            errorFechaSiembra == null && !esFechaPosteriorOIgual(fechaSiembra, fechaTrasplante) ->
                "Debe ser igual o posterior a la siembra"
            else -> null
        }
        errorFechaCosecha = when {
            esCampoVacio(fechaCosecha) -> "Ingresa la fecha estimada de cosecha"
            !esFechaValida(fechaCosecha) -> "Formato dd/mm/aaaa"
            errorFechaSiembra == null && !esFechaPosteriorOIgual(fechaSiembra, fechaCosecha) ->
                "La cosecha debe ser después de la siembra"
            else -> null
        }
        errorRiego = if (!esFrecuenciaRiegoValida(frecuenciaRiego)) "Ingresa un número de días entre 1 y 30" else null

        return errorNombre == null && errorSector == null && errorFechaSiembra == null &&
                errorFechaTrasplante == null && errorFechaCosecha == null && errorRiego == null
    }

    fun guardar(onGuardado: () -> Unit) {
        if (!validar()) return

        viewModelScope.launch {
            val cultivo = Cultivo(
                id = idCultivo,
                nombre = nombre.trim(),
                descripcion = descripcion.trim(),
                sectorId = sectorId ?: 0,
                fechaSiembra = fechaSiembra.trim(),
                fechaTrasplante = fechaTrasplante.trim(),
                fechaCosecha = fechaCosecha.trim(),
                frecuenciaRiegoDias = frecuenciaRiego.trim().toInt(),
                estado = estado,
                observaciones = observaciones.trim()
            )
            repositorio.guardarCultivo(cultivo)
            onGuardado()
        }
    }

    // Eliminar (solo al editar). Las tareas de este cultivo quedan sin cultivo (SET_NULL)
    fun eliminar(onResultado: (Boolean) -> Unit) {
        val actual = cultivoOriginal ?: return
        viewModelScope.launch {
            onResultado(repositorio.eliminarCultivo(actual))
        }
    }
}
