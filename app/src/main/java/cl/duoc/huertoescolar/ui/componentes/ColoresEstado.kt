package cl.duoc.huertoescolar.ui.componentes

import androidx.compose.ui.graphics.Color
import cl.duoc.huertoescolar.data.modelo.EstadoCultivo
import cl.duoc.huertoescolar.data.modelo.EstadoLectura
import cl.duoc.huertoescolar.data.modelo.EstadoProblema
import cl.duoc.huertoescolar.data.modelo.EstadoSector
import cl.duoc.huertoescolar.data.modelo.EstadoTarea
import cl.duoc.huertoescolar.ui.theme.EstadoAviso
import cl.duoc.huertoescolar.ui.theme.EstadoError
import cl.duoc.huertoescolar.ui.theme.EstadoInfo
import cl.duoc.huertoescolar.ui.theme.EstadoNeutro
import cl.duoc.huertoescolar.ui.theme.EstadoOk

// Un color para cada estado, así se reconocen de un vistazo
fun colorEstadoSector(estado: EstadoSector): Color {
    return when (estado) {
        EstadoSector.DISPONIBLE -> EstadoOk
        EstadoSector.EN_USO -> EstadoInfo
        EstadoSector.EN_MANTENIMIENTO -> EstadoAviso
        EstadoSector.FUERA_DE_USO -> EstadoError
    }
}

fun colorEstadoCultivo(estado: EstadoCultivo): Color {
    return when (estado) {
        EstadoCultivo.PLANIFICADO -> EstadoNeutro
        EstadoCultivo.SEMBRADO -> EstadoInfo
        EstadoCultivo.EN_CRECIMIENTO -> EstadoOk
        EstadoCultivo.LISTO_PARA_COSECHAR -> EstadoAviso
        EstadoCultivo.COSECHADO -> EstadoOk
        EstadoCultivo.CON_PROBLEMAS -> EstadoError
        EstadoCultivo.FINALIZADO -> EstadoNeutro
    }
}

fun colorEstadoTarea(estado: EstadoTarea): Color {
    return when (estado) {
        EstadoTarea.PENDIENTE -> EstadoNeutro
        EstadoTarea.EN_DESARROLLO -> EstadoInfo
        EstadoTarea.PENDIENTE_VALIDACION -> EstadoAviso
        EstadoTarea.VALIDADA -> EstadoOk
        EstadoTarea.RECHAZADA -> EstadoError
        EstadoTarea.ATRASADA -> EstadoError
    }
}

fun colorEstadoProblema(estado: EstadoProblema): Color {
    return when (estado) {
        EstadoProblema.INFORMADO -> EstadoError
        EstadoProblema.EN_REVISION -> EstadoAviso
        EstadoProblema.ATENDIDO -> EstadoInfo
        EstadoProblema.CERRADO -> EstadoOk
    }
}

fun colorEstadoLectura(estado: EstadoLectura): Color {
    return when (estado) {
        EstadoLectura.NORMAL -> EstadoOk
        EstadoLectura.BAJO -> EstadoAviso
        EstadoLectura.ALTO -> EstadoError
        EstadoLectura.PENDIENTE_REVISION -> EstadoNeutro
    }
}

