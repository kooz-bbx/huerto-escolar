package cl.duoc.huertoescolar.ui.cultivos

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.WaterDrop
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
import cl.duoc.huertoescolar.data.modelo.Cultivo
import cl.duoc.huertoescolar.ui.componentes.BarraSuperior
import cl.duoc.huertoescolar.ui.componentes.ChipEstado
import cl.duoc.huertoescolar.ui.componentes.DatoConIcono
import cl.duoc.huertoescolar.ui.componentes.colorEstadoCultivo

@Composable
fun CultivosScreen(
    onVolver: () -> Unit,
    onNuevoCultivo: () -> Unit,
    onEditarCultivo: (Int) -> Unit,
    viewModel: CultivosViewModel = viewModel()
) {
    val cultivos by viewModel.cultivos.collectAsState()
    val sectores by viewModel.sectores.collectAsState()

    Scaffold(
        topBar = { BarraSuperior(titulo = "Cultivos", onVolver = onVolver) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevoCultivo,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nuevo cultivo") }
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
            items(cultivos, key = { it.id }) { cultivo ->
                val sector = sectores.find { it.id == cultivo.sectorId }
                TarjetaCultivo(
                    cultivo = cultivo,
                    nombreSector = sector?.let { "${it.codigo} · ${it.nombre}" } ?: "Sin sector",
                    onClick = { onEditarCultivo(cultivo.id) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
fun TarjetaCultivo(
    cultivo: Cultivo,
    nombreSector: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Eco, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = cultivo.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                ChipEstado(
                    texto = cultivo.estado.etiqueta,
                    color = colorEstadoCultivo(cultivo.estado)
                )
            }
            DatoConIcono(Icons.Filled.Yard, nombreSector)
            DatoConIcono(Icons.Filled.CalendarMonth, "Siembra: ${cultivo.fechaSiembra} · Cosecha est.: ${cultivo.fechaCosecha}")
            DatoConIcono(Icons.Filled.WaterDrop, "Riego cada ${cultivo.frecuenciaRiegoDias} día(s)")
            if (cultivo.observaciones.isNotBlank()) {
                Text(
                    text = "Obs: ${cultivo.observaciones}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
