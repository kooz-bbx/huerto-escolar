package cl.duoc.huertoescolar.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

// Basado en CamaraUtils.kt de FotoMotion (clase del profe).
// Cambio: guardamos en "filesDir" en vez de "cacheDir" para que la evidencia no se borre.

private fun carpetaFotos(context: Context): File {
    val carpeta = File(context.filesDir, "fotos")
    carpeta.mkdirs()
    return carpeta
}

// Crea un archivo vacío y devuelve su Uri para que la cámara guarde ahí la foto
fun crearUriFotografia(context: Context): Uri {
    val archivo = File.createTempFile("foto_", ".jpg", carpetaFotos(context))
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        archivo
    )
}

// La foto elegida de la galería solo se puede leer por un rato.
// Por eso la copiamos a nuestra carpeta y guardamos la Uri de la copia.
fun copiarImagenALaApp(context: Context, uriOrigen: Uri): Uri? {
    return try {
        val destino = File.createTempFile("galeria_", ".jpg", carpetaFotos(context))
        context.contentResolver.openInputStream(uriOrigen)?.use { entrada ->
            destino.outputStream().use { salida -> entrada.copyTo(salida) }
        }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", destino)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
