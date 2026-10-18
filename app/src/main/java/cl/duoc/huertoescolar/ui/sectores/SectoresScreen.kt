package cl.duoc.huertoescolar.ui.sectores

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.ui.componentes.BarraSuperior
import cl.duoc.huertoescolar.ui.componentes.ChipEstado
import cl.duoc.huertoescolar.ui.componentes.colorEstadoSector

@Composable
fun SectoresScreen(
    onVolver: () -> Unit,
    onNuevoSector: () -> Unit,
    onVerSector: (Int) -> Unit,
    viewModel: SectoresViewModel = viewModel()
) {
    val sectores by viewModel.sectores.collectAsState()
    val cultivos by viewModel.cultivos.collectAsState()

    Scaffold(
        topBar = { BarraSuperior(titulo = "Sectores del huerto", onVolver = onVolver) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevoSector,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nuevo sector") }
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
            items(sectores, key = { it.id }) { sector ->
                // Nombres de los cultivos asociados a este sector
                val nombresCultivos = cultivos
                    .filter { it.sectorId == sector.id }
                    .joinToString(", ") { it.nombre }

                TarjetaSector(
                    sector = sector,
                    cultivosAsociados = nombresCultivos,
                    onClick = { onVerSector(sector.id) },
                    modifier = Modifier.animateItem()   // animación al agregar/mover
                )
            }
        }
    }
}

@Composable
fun TarjetaSector(
    sector: Sector,
    cultivosAsociados: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Yard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${sector.codigo} · ${sector.nombre}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                ChipEstado(
                    texto = sector.estado.etiqueta,
                    color = colorEstadoSector(sector.estado)
                )
            }
            Text(
                text = sector.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(Icons.Filled.Place, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(text = sector.ubicacion, style = MaterialTheme.typography.bodySmall)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(Icons.Filled.Eco, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (cultivosAsociados.isEmpty()) "Sin cultivos" else cultivosAsociados,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "Actualizado: ${sector.fechaActualizacion}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
