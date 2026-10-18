package cl.duoc.huertoescolar.ui.sectores

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.huertoescolar.ui.componentes.BarraSuperior
import cl.duoc.huertoescolar.ui.componentes.Cargando
import cl.duoc.huertoescolar.ui.componentes.ChipEstado
import cl.duoc.huertoescolar.ui.componentes.DatoConIcono
import cl.duoc.huertoescolar.ui.componentes.DialogoConfirmacion
import cl.duoc.huertoescolar.ui.componentes.SoloAdministrador
import cl.duoc.huertoescolar.ui.componentes.colorEstadoSector
import cl.duoc.huertoescolar.ui.cultivos.TarjetaCultivo

@Composable
fun SectorDetalleScreen(
    onVolver: () -> Unit,
    onEditarSector: (Int) -> Unit,
    onVerCultivo: (Int) -> Unit,
    viewModel: SectorDetalleViewModel = viewModel()
) {
    val context = LocalContext.current
    val sector by viewModel.sector.collectAsState()
    val cultivos by viewModel.cultivos.collectAsState()
    val usuarios by viewModel.usuarios.collectAsState()
    var confirmarEliminar by remember { mutableStateOf(false) }

    SoloAdministrador(onVolver = onVolver) {
        Scaffold(
            topBar = {
                BarraSuperior(
                    titulo = "Detalle del sector",
                    onVolver = onVolver,
                    acciones = {
                        sector?.let { actual ->
                            IconButton(onClick = { onEditarSector(actual.id) }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Editar sector")
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            val actual = sector
            if (actual == null) {
                Column(modifier = Modifier.padding(innerPadding)) { Cargando() }
                return@Scaffold
            }
            val responsable = usuarios.find { it.id == actual.responsableId }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${actual.codigo} · ${actual.nombre}",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                ChipEstado(actual.estado.etiqueta, colorEstadoSector(actual.estado))
                            }
                            Text(actual.descripcion, modifier = Modifier.padding(vertical = 6.dp))
                            DatoConIcono(Icons.Filled.Place, actual.ubicacion)
                            DatoConIcono(Icons.Filled.Person, "Responsable: ${responsable?.nombre ?: "-"}")
                            DatoConIcono(Icons.Filled.CalendarMonth, "Creado: ${actual.fechaCreacion} · Actualizado: ${actual.fechaActualizacion}")
                        }
                    }
                }
                item {
                    Text(
                        text = "Cultivos de este sector (${cultivos.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (cultivos.isEmpty()) {
                    item { Text("Este sector todavía no tiene cultivos.") }
                }
                items(cultivos, key = { it.id }) { cultivo ->
                    TarjetaCultivo(
                        cultivo = cultivo,
                        nombreSector = actual.nombre,
                        onClick = { onVerCultivo(cultivo.id) },
                        modifier = Modifier.animateItem()
                    )
                }
                item {
                    OutlinedButton(
                        onClick = { confirmarEliminar = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Eliminar sector")
                    }
                }
            }
        }

        if (confirmarEliminar) {
            DialogoConfirmacion(
                titulo = "¿Eliminar sector?",
                mensaje = "Solo se puede eliminar un sector que no tenga cultivos ni tareas asociadas.",
                textoConfirmar = "Eliminar",
                onConfirmar = {
                    confirmarEliminar = false
                    viewModel.eliminar { eliminado ->
                        if (eliminado) {
                            Toast.makeText(context, "Sector eliminado", Toast.LENGTH_SHORT).show()
                            onVolver()
                        } else {
                            Toast.makeText(context, "No se puede eliminar: tiene cultivos o tareas asociadas", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                onCancelar = { confirmarEliminar = false }
            )
        }
    }
}
