package cl.duoc.huertoescolar.ui.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.huertoescolar.data.Sesion
import cl.duoc.huertoescolar.ui.componentes.BarraSuperior
import cl.duoc.huertoescolar.ui.componentes.TarjetaModulo
import cl.duoc.huertoescolar.ui.componentes.TarjetaSaludo
import cl.duoc.huertoescolar.ui.theme.HuertoEscolarTheme

// Datos de cada opción del menú
data class OpcionMenu(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun InicioAdminScreen(
    onIrSectores: () -> Unit,
    onIrCultivos: () -> Unit,
    onIrTareas: () -> Unit,
    onIrProblemas: () -> Unit,
    onIrAlertas: () -> Unit,
    onIrSensores: () -> Unit,
    onIrHistorial: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val usuario = Sesion.usuarioActual

    val opciones = listOf(
        OpcionMenu("Sectores", "Bancales y zonas del huerto", Icons.Filled.Yard, onIrSectores),
        OpcionMenu("Cultivos", "Siembra, riego y cosecha", Icons.Filled.Eco, onIrCultivos),
        OpcionMenu("Tareas", "Asignar, revisar y validar", Icons.AutoMirrored.Filled.Assignment, onIrTareas),
        OpcionMenu("Problemas", "Reportes y seguimiento", Icons.Filled.ReportProblem, onIrProblemas),
        OpcionMenu("Alertas", "Riegos, atrasos y cosechas", Icons.Filled.NotificationsActive, onIrAlertas),
        OpcionMenu("Sensores", "Datos simulados", Icons.Filled.Sensors, onIrSensores),
        OpcionMenu("Historial", "Actividades y validaciones", Icons.Filled.History, onIrHistorial)
    )

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Huerto Escolar",
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
                nombre = usuario?.nombre ?: "Administrador",
                detalle = "${usuario?.cargo ?: "Docente"} · ${usuario?.grupo ?: ""}"
            )
            Text("¿Qué quieres revisar hoy?", style = MaterialTheme.typography.titleMedium)

            LazyVerticalGrid(
                // Adaptive: 2 columnas en celular, más en tablet
                columns = GridCells.Adaptive(minSize = 150.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(opciones) { opcion ->
                    TarjetaModulo(
                        titulo = opcion.titulo,
                        descripcion = opcion.descripcion,
                        icono = opcion.icono,
                        onClick = opcion.onClick
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInicioAdmin() {
    HuertoEscolarTheme {
        InicioAdminScreen(
            onIrSectores = {}, onIrCultivos = {}, onIrTareas = {}, onIrProblemas = {},
            onIrAlertas = {}, onIrSensores = {}, onIrHistorial = {}, onCerrarSesion = {}
        )
    }
}
