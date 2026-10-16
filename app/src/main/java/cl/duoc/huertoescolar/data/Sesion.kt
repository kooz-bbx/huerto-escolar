package cl.duoc.huertoescolar.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cl.duoc.huertoescolar.data.modelo.Perfil
import cl.duoc.huertoescolar.data.modelo.Usuario

// Guarda quién inició sesión, para que todas las pantallas lo puedan consultar
object Sesion {
    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    fun iniciar(usuario: Usuario) {
        usuarioActual = usuario
    }

    fun cerrar() {
        usuarioActual = null
    }

    fun esAdministrador(): Boolean = usuarioActual?.perfil == Perfil.ADMINISTRADOR
}
