package cl.duoc.huertoescolar.ui.componentes

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import cl.duoc.huertoescolar.util.mostrarNotificacion
import cl.duoc.huertoescolar.util.tienePermisoNotificaciones

// Botón (campana) que envía un recordatorio como notificación del teléfono.
// Si falta el permiso (Android 13+), primero lo pide.
@Composable
fun BotonNotificar(
    titulo: String,
    texto: String
) {
    val context = LocalContext.current

    val pedirPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            mostrarNotificacion(context, titulo, texto)
        } else {
            Toast.makeText(context, "Sin permiso no se pueden mostrar recordatorios", Toast.LENGTH_LONG).show()
        }
    }

    IconButton(
        onClick = {
            if (tienePermisoNotificaciones(context)) {
                mostrarNotificacion(context, titulo, texto)
                Toast.makeText(context, "Recordatorio enviado a las notificaciones", Toast.LENGTH_SHORT).show()
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    ) {
        Icon(Icons.Filled.NotificationsActive, contentDescription = "Enviar recordatorio")
    }
}
