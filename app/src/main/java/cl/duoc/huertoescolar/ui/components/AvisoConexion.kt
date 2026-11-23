package cl.duoc.huertoescolar.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Muestra si la app está conectada con el microservicio Spring Boot.
// Mientras sincroniza: un círculo de carga. Si falla: aviso + botón Reintentar.
@Composable
fun AvisoConexion(
    mensaje: String?,
    sincronizando: Boolean,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = sincronizando || mensaje != null, modifier = modifier) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                if (sincronizando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Text("Sincronizando con el servidor...", modifier = Modifier.weight(1f))
                } else {
                    Icon(Icons.Filled.CloudOff, contentDescription = null)
                    Text(
                        text = mensaje ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onReintentar) { Text("Reintentar") }
                }
            }
        }
    }
}
