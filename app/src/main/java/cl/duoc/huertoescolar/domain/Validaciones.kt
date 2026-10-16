package cl.duoc.huertoescolar.domain

// Funciones simples de validación (igual que en clases: devuelven true o false).
// Están separadas de la interfaz para que la lógica no quede pegada a los Composables.

fun esCampoVacio(texto: String): Boolean {
    return texto.trim().isEmpty()
}

fun esUsuarioValido(usuario: String): Boolean {
    // mínimo 3 caracteres y sin espacios
    return usuario.trim().length >= 3 && !usuario.trim().contains(" ")
}

fun esClaveValida(clave: String): Boolean {
    return clave.length >= 4
}
