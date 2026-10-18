package cl.duoc.huertoescolar.ui.tareas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.huertoescolar.ui.componentes.BarraSuperior
import cl.duoc.huertoescolar.ui.componentes.SoloAdministrador

@Composable
fun TareasScreen(
    onVolver: () -> Unit,
    onNuevaTarea: () -> Unit,
    onVerTarea: (Int) -> Unit,
    viewModel: TareasViewModel = viewModel()
) {
    val tareas by viewModel.tareas.collectAsState()
    val sectores by viewModel.sectores.collectAsState()
    val usuarios by viewModel.usuarios.collectAsState()
    val tareasFiltradas = viewModel.filtrar(tareas, usuarios)
    val responsables = tareas.map { textoResponsable(it, usuarios) }.distinct().sorted()

    SoloAdministrador(onVolver = onVolver) {
        Scaffold(
            topBar = { BarraSuperior(titulo = "Tareas del huerto", onVolver = onVolver) },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = onNuevaTarea,
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("Nueva tarea") }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Filtro por estado
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(FiltroTarea.entries) { filtro ->
                        FilterChip(
                            selected = viewModel.filtro == filtro,
                            onClick = { viewModel.cambiarFiltro(filtro) },
                            label = { Text(filtro.etiqueta) }
                        )
                    }
                }
                // Filtro por responsable (tocar de nuevo para quitarlo)
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(responsables) { nombre ->
                        FilterChip(
                            selected = viewModel.filtroResponsable == nombre,
                            onClick = {
                                viewModel.cambiarFiltroResponsable(
                                    if (viewModel.filtroResponsable == nombre) null else nombre
                                )
                            },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                            label = { Text(nombre) }
                        )
                    }
                }

                AnimatedVisibility(
                    visible = tareasFiltradas.isEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "No hay tareas con estos filtros",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    )
                }

                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tareasFiltradas, key = { it.id }) { tarea ->
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
    }
}
