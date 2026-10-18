package cl.duoc.huertoescolar.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.huertoescolar.data.Sesion

// Control de acceso: si quien entra NO es administrador, se muestra "acceso denegado"
// en vez de la pantalla. Así un estudiante nunca puede crear o editar sectores, cultivos o tareas.
@Composable
fun SoloAdministrador(
    onVolver: () -> Unit,
    contenido: @Composable () -> Unit
) {
    if (Sesion.esAdministrador()) {
        contenido()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Icon(
                Icons.Filled.Block,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "Esta sección es solo para docentes y talleristas.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Button(onClick = onVolver) { Text("Volver") }
        }
    }
}
