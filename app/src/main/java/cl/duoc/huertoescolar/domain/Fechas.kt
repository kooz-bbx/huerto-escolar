package cl.duoc.huertoescolar.domain

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// Ayudas para trabajar con fechas en formato dd/MM/yyyy
// (usamos SimpleDateFormat porque funciona desde Android 7, nuestro minSdk 24)

private const val FORMATO_FECHA = "dd/MM/yyyy"
private const val FORMATO_FECHA_HORA = "dd/MM/yyyy HH:mm"

fun convertirFecha(texto: String): Date? {
    return try {
        val formato = SimpleDateFormat(FORMATO_FECHA, Locale.getDefault())
        formato.isLenient = false   // así "31/02/2026" se considera inválida
        formato.parse(texto.trim())
    } catch (e: Exception) {
        null
    }
}

fun fechaHoy(): String {
    return SimpleDateFormat(FORMATO_FECHA, Locale.getDefault()).format(Date())
}

fun fechaHoraActual(): String {
    return SimpleDateFormat(FORMATO_FECHA_HORA, Locale.getDefault()).format(Date())
}

// Días que faltan desde hoy hasta la fecha (negativo = ya pasó). null si la fecha es inválida
fun diasHasta(texto: String): Long? {
    val fecha = convertirFecha(texto) ?: return null
    val hoy = convertirFecha(fechaHoy()) ?: return null
    return TimeUnit.MILLISECONDS.toDays(fecha.time - hoy.time)
}

// Fecha de hoy + N días (negativo = días atrás). Sirve para los datos de prueba,
// así siempre hay tareas "próximas" y "atrasadas" sin importar el día en que se pruebe la app
fun fechaRelativa(dias: Int): String {
    val calendario = Calendar.getInstance()
    calendario.add(Calendar.DAY_OF_YEAR, dias)
    return SimpleDateFormat(FORMATO_FECHA, Locale.getDefault()).format(calendario.time)
}
