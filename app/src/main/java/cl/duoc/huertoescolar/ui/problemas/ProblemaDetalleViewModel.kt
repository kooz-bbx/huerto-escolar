package cl.duoc.huertoescolar.ui.problemas

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolar.data.local.HuertoDatabase
import cl.duoc.huertoescolar.data.modelo.Cultivo
import cl.duoc.huertoescolar.data.modelo.EstadoProblema
import cl.duoc.huertoescolar.data.modelo.Problema
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.modelo.Usuario
import cl.duoc.huertoescolar.data.repositorio.HuertoRepositorio
import cl.duoc.huertoescolar.domain.fechaHoraActual
import cl.duoc.huertoescolar.domain.tieneLargoMinimo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// El docente da seguimiento: cambia el estado y deja un comentario
class ProblemaDetalleViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = HuertoRepositorio(HuertoDatabase.obtener(application))
    private var yaCargado = false

    val sectores: StateFlow<List<Sector>> = repositorio.obtenerSectores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val cultivos: StateFlow<List<Cultivo>> = repositorio.obtenerCultivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val usuarios: StateFlow<List<Usuario>> = repositorio.obtenerUsuarios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var problema by mutableStateOf<Problema?>(null)
        private set
    var estado by mutableStateOf(EstadoProblema.INFORMADO)
        private set
    var comentario by mutableStateOf("")
        private set
    var errorComentario by mutableStateOf<String?>(null)
        private set

    fun cargar(id: Int) {
        if (yaCargado) return
        yaCargado = true
        viewModelScope.launch {
            val encontrado = repositorio.obtenerProblema(id) ?: return@launch
            problema = encontrado
            estado = encontrado.estado
            comentario = encontrado.comentarioDocente
        }
    }

    fun cambiarEstado(valor: EstadoProblema) { estado = valor; errorComentario = null }
    fun cambiarComentario(valor: String) { comentario = valor; errorComentario = null }

    fun guardar(onGuardado: () -> Unit) {
        val actual = problema ?: return
        val seCierra = estado == EstadoProblema.ATENDIDO || estado == EstadoProblema.CERRADO

        // Para dar por atendido o cerrado hay que explicar qué se hizo
        errorComentario = if (seCierra && !tieneLargoMinimo(comentario, 5))
            "Explica qué se hizo para solucionarlo (mínimo 5 caracteres)" else null
        if (errorComentario != null) return

        viewModelScope.launch {
            repositorio.guardarProblema(
                actual.copy(
                    estado = estado,
                    comentarioDocente = comentario.trim(),
                    fechaCierre = if (seCierra) fechaHoraActual() else ""
                )
            )
            onGuardado()
        }
    }
}
