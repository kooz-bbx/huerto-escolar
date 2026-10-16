package cl.duoc.huertoescolar.ui.componentes

import androidx.compose.ui.graphics.Color
import cl.duoc.huertoescolar.data.modelo.EstadoSector
import cl.duoc.huertoescolar.ui.theme.EstadoAviso
import cl.duoc.huertoescolar.ui.theme.EstadoError
import cl.duoc.huertoescolar.ui.theme.EstadoInfo
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
