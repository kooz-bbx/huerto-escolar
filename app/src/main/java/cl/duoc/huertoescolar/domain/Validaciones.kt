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

fun tieneLargoMinimo(texto: String, minimo: Int): Boolean {
    return texto.trim().length >= minimo
}

// Código de sector: "S-" seguido de 2 números. Ej: S-01, S-12
fun esCodigoSectorValido(codigo: String): Boolean {
    return Regex("^S-\\d{2}$").matches(codigo.trim().uppercase())
}

fun esFechaValida(fecha: String): Boolean {
    return convertirFecha(fecha) != null
}

// true si "fechaFinal" es igual o posterior a "fechaInicial"
fun esFechaPosteriorOIgual(fechaInicial: String, fechaFinal: String): Boolean {
    val inicio = convertirFecha(fechaInicial) ?: return false
    val fin = convertirFecha(fechaFinal) ?: return false
    return !fin.before(inicio)
}

// Riego: un número entero entre 1 y 30 días
fun esFrecuenciaRiegoValida(texto: String): Boolean {
    val dias = texto.trim().toIntOrNull() ?: return false
    return dias in 1..30
}
