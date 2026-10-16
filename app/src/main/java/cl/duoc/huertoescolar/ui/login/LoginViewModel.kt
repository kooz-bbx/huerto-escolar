package cl.duoc.huertoescolar.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.duoc.huertoescolar.data.DatosPrueba
import cl.duoc.huertoescolar.data.Sesion
import cl.duoc.huertoescolar.data.modelo.EstadoCuenta
import cl.duoc.huertoescolar.data.modelo.Perfil
import cl.duoc.huertoescolar.domain.esClaveValida
import cl.duoc.huertoescolar.domain.esCampoVacio
import cl.duoc.huertoescolar.domain.esUsuarioValido

class LoginViewModel : ViewModel() {

    // Estado del formulario (la pantalla solo lo lee)
    var usuario by mutableStateOf("")
        private set
    var clave by mutableStateOf("")
        private set

    // Errores por campo (null = sin error)
    var errorUsuario by mutableStateOf<String?>(null)
        private set
    var errorClave by mutableStateOf<String?>(null)
        private set

    // Error general (ej: clave incorrecta)
    var mensajeError by mutableStateOf<String?>(null)
        private set

    fun cambiarUsuario(valor: String) {
        usuario = valor
        errorUsuario = null
        mensajeError = null
    }

    fun cambiarClave(valor: String) {
        clave = valor
        errorClave = null
        mensajeError = null
    }

    private fun validarFormulario(): Boolean {
        errorUsuario = when {
            esCampoVacio(usuario) -> "Ingresa tu usuario"
            !esUsuarioValido(usuario) -> "Mínimo 3 caracteres y sin espacios"
            else -> null
        }
        errorClave = when {
            esCampoVacio(clave) -> "Ingresa tu clave"
            !esClaveValida(clave) -> "La clave debe tener al menos 4 caracteres"
            else -> null
        }
        return errorUsuario == null && errorClave == null
    }

    // Si todo está bien, guarda la sesión y avisa el perfil para navegar
    fun iniciarSesion(onExito: (Perfil) -> Unit) {
        if (!validarFormulario()) return

        val encontrado = DatosPrueba.usuarios.find {
            it.usuario == usuario.trim().lowercase() && it.clave == clave
        }

        when {
            encontrado == null -> mensajeError = "Usuario o clave incorrectos"
            encontrado.estadoCuenta == EstadoCuenta.INACTIVA ->
                mensajeError = "Tu cuenta está inactiva. Habla con tu docente."
            else -> {
                Sesion.iniciar(encontrado)
                usuario = ""
                clave = ""
                onExito(encontrado.perfil)
            }
        }
    }
}
