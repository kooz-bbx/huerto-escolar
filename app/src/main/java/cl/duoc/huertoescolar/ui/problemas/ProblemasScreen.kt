package cl.duoc.huertoescolar.ui.problemas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.huertoescolar.ui.componentes.BarraSuperior
import cl.duoc.huertoescolar.ui.componentes.SoloAdministrador

@Composable
fun ProblemasScreen(
    onVolver: () -> Unit,
    onVerProblema: (Int) -> Unit,
    viewModel: ProblemasViewModel = viewModel()
) {
    val problemas by viewModel.problemas.collectAsState()
    val sectores by viewModel.sectores.collectAsState()
    val usuarios by viewModel.usuarios.collectAsState()

    SoloAdministrador(onVolver = onVolver) {
        Scaffold(
            topBar = { BarraSuperior(titulo = "Problemas reportados", onVolver = onVolver) }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (problemas.isEmpty()) {
                    item { Text("No hay problemas reportados 🌱") }
                }
                items(problemas, key = { it.id }) { problema ->
                    TarjetaProblema(
                        problema = problema,
                        nombreSector = sectores.find { it.id == problema.sectorId }?.nombre ?: "",
                        nombreReportante = usuarios.find { it.id == problema.reportadoPorId }?.nombre ?: "-",
                        onClick = { onVerProblema(problema.id) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}
