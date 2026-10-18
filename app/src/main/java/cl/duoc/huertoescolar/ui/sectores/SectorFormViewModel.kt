package cl.duoc.huertoescolar.ui.sectores

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolar.data.Sesion
import cl.duoc.huertoescolar.data.local.HuertoDatabase
import cl.duoc.huertoescolar.data.modelo.EstadoSector
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.repositorio.HuertoRepositorio
import cl.duoc.huertoescolar.domain.esCampoVacio
import cl.duoc.huertoescolar.domain.esCodigoSectorValido
import cl.duoc.huertoescolar.domain.fechaHoy
import cl.duoc.huertoescolar.domain.tieneLargoMinimo
import kotlinx.coroutines.launch

// Sirve tanto para CREAR (id = 0) como para MODIFICAR un sector
class SectorFormViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = HuertoRepositorio(HuertoDatabase.obtener(application))
    private var sectorOriginal: Sector? = null
    private var yaCargado = false

    var esEdicion by mutableStateOf(false)
        private set

    var codigo by mutableStateOf("")
        private set
    var nombre by mutableStateOf("")
        private set
    var descripcion by mutableStateOf("")
        private set
    var ubicacion by mutableStateOf("")
        private set
    var estado by mutableStateOf(EstadoSector.DISPONIBLE)
        private set

    var errorCodigo by mutableStateOf<String?>(null)
        private set
    var errorNombre by mutableStateOf<String?>(null)
        private set
    var errorDescripcion by mutableStateOf<String?>(null)
        private set
    var errorUbicacion by mutableStateOf<String?>(null)
        private set

    fun cargar(idSector: Int) {
        if (yaCargado) return
        yaCargado = true
        if (idSector == 0) return   // es un sector nuevo

        viewModelScope.launch {
            val sector = repositorio.obtenerSector(idSector) ?: return@launch
            sectorOriginal = sector
            esEdicion = true
            codigo = sector.codigo
            nombre = sector.nombre
            descripcion = sector.descripcion
            ubicacion = sector.ubicacion
            estado = sector.estado
        }
    }

    fun cambiarCodigo(valor: String) { codigo = valor.uppercase(); errorCodigo = null }
    fun cambiarNombre(valor: String) { nombre = valor; errorNombre = null }
    fun cambiarDescripcion(valor: String) { descripcion = valor; errorDescripcion = null }
    fun cambiarUbicacion(valor: String) { ubicacion = valor; errorUbicacion = null }
    fun cambiarEstado(valor: EstadoSector) { estado = valor }

    private fun validar(): Boolean {
        errorCodigo = when {
            esCampoVacio(codigo) -> "Ingresa el código del sector"
            !esCodigoSectorValido(codigo) -> "Formato: S- y dos números (ej: S-05)"
            else -> null
        }
        errorNombre = when {
            esCampoVacio(nombre) -> "Ingresa un nombre"
            !tieneLargoMinimo(nombre, 3) -> "Mínimo 3 caracteres"
            else -> null
        }
        errorDescripcion = if (!tieneLargoMinimo(descripcion, 5)) "Describe el sector (mínimo 5 caracteres)" else null
        errorUbicacion = if (esCampoVacio(ubicacion)) "Indica dónde está el sector" else null

        return errorCodigo == null && errorNombre == null &&
                errorDescripcion == null && errorUbicacion == null
    }

    fun guardar(onGuardado: () -> Unit) {
        if (!validar()) return

        viewModelScope.launch {
            val idActual = sectorOriginal?.id ?: 0
            if (repositorio.existeCodigoSector(codigo.trim(), idActual)) {
                errorCodigo = "Ya existe un sector con ese código"
                return@launch
            }
            val sector = Sector(
                id = idActual,
                codigo = codigo.trim(),
                nombre = nombre.trim(),
                descripcion = descripcion.trim(),
                ubicacion = ubicacion.trim(),
                estado = estado,
                responsableId = sectorOriginal?.responsableId ?: (Sesion.usuarioActual?.id ?: 0),
                fechaCreacion = sectorOriginal?.fechaCreacion ?: fechaHoy(),
                fechaActualizacion = fechaHoy()
            )
            repositorio.guardarSector(sector)
            onGuardado()
        }
    }
}
