package cl.duoc.huertoescolar.ui.componentes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.huertoescolar.util.copiarImagenALaApp
import cl.duoc.huertoescolar.util.crearUriFotografia
import coil3.compose.AsyncImage

// Componente para adjuntar una foto: CÁMARA (recurso nativo) o GALERÍA (PhotoPicker).
// Misma idea que FotoMotion: launcher TakePicture + uriPendiente + AsyncImage con animación.
@Composable
fun SelectorFoto(
    fotoUri: String,
    onFotoSeleccionada: (String) -> Unit,
    onQuitarFoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var uriPendiente by rememberSaveable { mutableStateOf<Uri?>(null) }

    // CÁMARA
    val lanzadorCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { fotoTomada ->
        if (fotoTomada) uriPendiente?.let { onFotoSeleccionada(it.toString()) }
        uriPendiente = null
    }

    // GALERÍA (PhotoPicker)
    val lanzadorGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            copiarImagenALaApp(context, uri)?.let { onFotoSeleccionada(it.toString()) }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Fotografía de evidencia (solo plantas o herramientas, sin rostros)",
            style = MaterialTheme.typography.labelLarge
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    try {
                        val nuevaUri = crearUriFotografia(context)
                        uriPendiente = nuevaUri
                        lanzadorCamara.launch(nuevaUri)
                    } catch (e: Exception) {
                        uriPendiente = null
                        e.printStackTrace()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.CameraAlt, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Cámara")
            }
            OutlinedButton(
                onClick = {
                    lanzadorGaleria.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Galería")
            }
        }

        // La foto aparece con animación (fadeIn + expandVertically)
        AnimatedVisibility(
            visible = fotoUri.isNotEmpty(),
            enter = fadeIn(tween(800)) + expandVertically(tween(800)),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                AsyncImage(
                    model = fotoUri,
                    contentDescription = "Foto de evidencia",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                TextButton(onClick = onQuitarFoto) {
                    Icon(Icons.Filled.Delete, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Quitar foto")
                }
            }
        }
    }
}
