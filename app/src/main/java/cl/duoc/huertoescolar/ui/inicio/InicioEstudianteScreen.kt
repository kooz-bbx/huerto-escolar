package cl.duoc.huertoescolar.ui.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.huertoescolar.data.Sesion
import cl.duoc.huertoescolar.ui.componentes.BarraSuperior
import cl.duoc.huertoescolar.ui.componentes.TarjetaSaludo
import cl.duoc.huertoescolar.ui.theme.HuertoEscolarTheme

@Composable
fun InicioEstudianteScreen(
    onCerrarSesion: () -> Unit
) {
    val usuario = Sesion.usuarioActual

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Mi huerto",
                acciones = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TarjetaSaludo(
                nombre = usuario?.nombre ?: "Estudiante",
                detalle = "Estudiante · ${usuario?.grupo ?: ""}"
            )
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.Spa, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "Aquí vas a ver las tareas que te asigne tu docente o tallerista.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInicioEstudiante() {
    HuertoEscolarTheme {
        InicioEstudianteScreen(onCerrarSesion = {})
    }
}
