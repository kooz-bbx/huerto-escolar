package cl.duoc.huertoescolar.ui.historial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.huertoescolar.domain.EventoHistorial
import cl.duoc.huertoescolar.domain.TipoEvento
import cl.duoc.huertoescolar.domain.generarHistorial
import cl.duoc.huertoescolar.ui.componentes.BarraSuperior
import cl.duoc.huertoescolar.ui.componentes.SoloAdministrador
import coil3.compose.AsyncImage

@Composable
fun HistorialScreen(
    onVolver: () -> Unit,
    viewModel: HistorialViewModel = viewModel()
) {
    val tareas by viewModel.tareas.collectAsState()
    val evidencias by viewModel.evidencias.collectAsState()
    val validaciones by viewModel.validaciones.collectAsState()
    val problemas by viewModel.problemas.collectAsState()
    val usuarios by viewModel.usuarios.collectAsState()
    val sectores by viewModel.sectores.collectAsState()

    val eventos = generarHistorial(tareas, evidencias, validaciones, problemas, usuarios, sectores)
        .filter { viewModel.filtro == null || it.tipo == viewModel.filtro }

    SoloAdministrador(onVolver = onVolver) {
        Scaffold(
            topBar = { BarraSuperior(titulo = "Historial del huerto", onVolver = onVolver) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = viewModel.filtro == null,
                            onClick = { viewModel.cambiarFiltro(null) },
                            label = { Text("Todo") }
                        )
                    }
                    items(TipoEvento.entries) { tipo ->
                        FilterChip(
                            selected = viewModel.filtro == tipo,
                            onClick = { viewModel.cambiarFiltro(tipo) },
                            label = { Text(tipo.etiqueta) }
                        )
                    }
                }
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (eventos.isEmpty()) {
                        item { Text("Aún no hay registros.") }
                    }
                    items(eventos) { evento -> TarjetaEvento(evento) }
                }
            }
        }
    }
}

@Composable
fun TarjetaEvento(evento: EventoHistorial) {
    val icono = when (evento.tipo) {
        TipoEvento.TAREA_REALIZADA -> Icons.Filled.TaskAlt
        TipoEvento.VALIDACION -> Icons.Filled.CheckCircle
        TipoEvento.PROBLEMA -> Icons.Filled.ReportProblem
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(evento.fechaHora, style = MaterialTheme.typography.labelSmall)
                Text(evento.titulo, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                if (evento.detalle.isNotEmpty()) {
                    Text(evento.detalle, style = MaterialTheme.typography.bodySmall)
                }
            }
            // Miniatura de la foto, si la hay
            if (evento.fotoUri.isNotEmpty()) {
                Spacer(Modifier.width(8.dp))
                AsyncImage(
                    model = evento.fotoUri,
                    contentDescription = "Foto",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}
