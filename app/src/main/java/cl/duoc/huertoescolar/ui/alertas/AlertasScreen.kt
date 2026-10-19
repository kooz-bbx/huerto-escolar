package cl.duoc.huertoescolar.ui.alertas

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
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.huertoescolar.domain.Alerta
import cl.duoc.huertoescolar.domain.TipoAlerta
import cl.duoc.huertoescolar.domain.generarAlertas
import cl.duoc.huertoescolar.ui.componentes.BarraSuperior
import cl.duoc.huertoescolar.ui.componentes.BotonNotificar
import cl.duoc.huertoescolar.ui.componentes.SoloAdministrador

@Composable
fun AlertasScreen(
    onVolver: () -> Unit,
    viewModel: AlertasViewModel = viewModel()
) {
    val tareas by viewModel.tareas.collectAsState()
    val cultivos by viewModel.cultivos.collectAsState()
    val problemas by viewModel.problemas.collectAsState()
    val lecturas by viewModel.lecturas.collectAsState()
    val sectores by viewModel.sectores.collectAsState()

    val alertas = generarAlertas(tareas, cultivos, problemas, lecturas, sectores)
    val porTipo = alertas.groupBy { it.tipo }

    // Texto para la notificación: un resumen de cuántas alertas hay de cada tipo
    val resumen = if (alertas.isEmpty()) "No hay alertas pendientes 🌱"
    else porTipo.entries.joinToString("\n") { "• ${it.key.etiqueta}: ${it.value.size}" }

    SoloAdministrador(onVolver = onVolver) {
        Scaffold(
            topBar = {
                BarraSuperior(
                    titulo = "Alertas y recordatorios",
                    onVolver = onVolver,
                    acciones = { BotonNotificar(titulo = "Huerto: ${alertas.size} alerta(s)", texto = resumen) }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Info, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Las alertas son informativas: la app no riega ni activa equipos. " +
                                        "Toca la campana para enviar el resumen a las notificaciones.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                if (alertas.isEmpty()) {
                    item { Text("No hay alertas pendientes 🌱", modifier = Modifier.padding(16.dp)) }
                }
                // Una sección por tipo de alerta, en el orden del enum
                TipoAlerta.entries.forEach { tipo ->
                    val delTipo = porTipo[tipo].orEmpty()
                    if (delTipo.isNotEmpty()) {
                        item(key = tipo.name) {
                            Text(
                                text = "${tipo.etiqueta} (${delTipo.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        items(delTipo) { alerta -> TarjetaAlerta(alerta) }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaAlerta(alerta: Alerta) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                iconoAlerta(alerta.tipo),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(alerta.titulo, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(alerta.detalle, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

fun iconoAlerta(tipo: TipoAlerta): ImageVector {
    return when (tipo) {
        TipoAlerta.PROXIMO_RIEGO -> Icons.Filled.WaterDrop
        TipoAlerta.RIEGO_ATRASADO -> Icons.Filled.Warning
        TipoAlerta.TAREA_ATRASADA -> Icons.Filled.HourglassTop
        TipoAlerta.PROXIMA_COSECHA -> Icons.Filled.EventAvailable
        TipoAlerta.MANTENIMIENTO -> Icons.Filled.Build
        TipoAlerta.POR_VALIDAR -> Icons.AutoMirrored.Filled.Assignment
        TipoAlerta.PROBLEMA_PENDIENTE -> Icons.Filled.ReportProblem
        TipoAlerta.LECTURA_FUERA_RANGO -> Icons.Filled.Sensors
    }
}
