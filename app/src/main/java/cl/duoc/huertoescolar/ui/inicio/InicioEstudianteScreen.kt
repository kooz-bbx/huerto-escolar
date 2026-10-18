package cl.duoc.huertoescolar.ui.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.huertoescolar.data.Sesion
import cl.duoc.huertoescolar.data.modelo.EstadoTarea
import cl.duoc.huertoescolar.domain.estadoMostrado
import cl.duoc.huertoescolar.domain.ordenarPorFecha
import cl.duoc.huertoescolar.ui.componentes.BarraSuperior
import cl.duoc.huertoescolar.ui.componentes.TarjetaSaludo
import cl.duoc.huertoescolar.ui.tareas.TarjetaTarea
import cl.duoc.huertoescolar.ui.tareas.textoResponsable

@Composable
fun InicioEstudianteScreen(
    onVerTarea: (Int) -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: InicioEstudianteViewModel = viewModel()
) {
    val usuario = Sesion.usuarioActual
    val tareas by viewModel.misTareas.collectAsState()
    val sectores by viewModel.sectores.collectAsState()
    val usuarios by viewModel.usuarios.collectAsState()

    val misTareas = ordenarPorFecha(tareas)
    val porHacer = misTareas.count {
        val estado = estadoMostrado(it)
        estado == EstadoTarea.PENDIENTE || estado == EstadoTarea.EN_DESARROLLO ||
                estado == EstadoTarea.ATRASADA || estado == EstadoTarea.RECHAZADA
    }

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TarjetaSaludo(
                    nombre = usuario?.nombre ?: "Estudiante",
                    detalle = "Estudiante · ${usuario?.grupo ?: ""}\nTienes $porHacer tarea(s) por hacer"
                )
            }
            item {
                Text("Mis tareas", style = MaterialTheme.typography.titleMedium)
            }
            if (misTareas.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.Spa, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "No tienes tareas asignadas por ahora.",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            items(misTareas, key = { it.id }) { tarea ->
                val sector = sectores.find { it.id == tarea.sectorId }
                TarjetaTarea(
                    tarea = tarea,
                    nombreSector = sector?.let { "${it.codigo} · ${it.nombre}" } ?: "",
                    nombreResponsable = textoResponsable(tarea, usuarios),
                    onClick = { onVerTarea(tarea.id) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}
