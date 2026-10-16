package cl.duoc.huertoescolar.ui.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
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
    onCerrarSesion: () -> Unit
) {
    val usuario = Sesion.usuarioActual

    val opciones = listOf(
        OpcionMenu("Sectores", "Bancales y zonas del huerto", Icons.Filled.Yard, onIrSectores)
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
                columns = GridCells.Fixed(2),
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
        InicioAdminScreen(onIrSectores = {}, onCerrarSesion = {})
    }
}
