package cl.duoc.huertoescolar.ui.screen.sensores

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.huertoescolar.model.data.local.entity.LecturaSensor
import cl.duoc.huertoescolar.model.rules.ResumenClima
import cl.duoc.huertoescolar.ui.components.BarraSuperior
import cl.duoc.huertoescolar.ui.components.ChipEstado
import cl.duoc.huertoescolar.ui.components.SoloAdministrador
import cl.duoc.huertoescolar.ui.components.colorEstadoLectura
import cl.duoc.huertoescolar.ui.theme.Sol
import cl.duoc.huertoescolar.viewmodel.SensoresViewModel

@Composable
fun SensoresScreen(
    onVolver: () -> Unit,
    viewModel: SensoresViewModel = viewModel()
) {
    val lecturas by viewModel.lecturas.collectAsState()
    val sectores by viewModel.sectores.collectAsState()
    // La más reciente de cada sector
    val ultimas = lecturas.distinctBy { it.sectorId }

    SoloAdministrador(onVolver = onVolver) {
        Scaffold(
            topBar = { BarraSuperior(titulo = "Sensores (simulados)", onVolver = onVolver) },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.simularNuevaLectura(sectores) },
                    icon = { Icon(Icons.Filled.Refresh, contentDescription = null) },
                    text = { Text("Simular lectura") }
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
                // Clima real desde la API externa Open-Meteo
                item {
                    TarjetaClima(
                        clima = viewModel.clima,
                        cargando = viewModel.cargandoClima,
                        error = viewModel.errorClima,
                        onReintentar = { viewModel.cargarClima() }
                    )
                }
                // Aviso MUY visible: no son mediciones reales
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Sol.copy(alpha = 0.25f)),
                        border = BorderStroke(2.dp, Sol)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Science, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "SIMULACIÓN ACADÉMICA · Datos de prueba. No son mediciones reales " +
                                        "y la app no se conecta a sensores ni sistemas de riego.",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                // key = sectorId: así la misma tarjeta se actualiza y las barras se animan
                items(ultimas, key = { it.sectorId }) { lectura ->
                    val sector = sectores.find { it.id == lectura.sectorId }
                    TarjetaLectura(
                        lectura = lectura,
                        nombreSector = sector?.let { "${it.codigo} · ${it.nombre}" } ?: "",
                        modifier = Modifier.animateItem()
                    )
                }
                item {
                    Text(
                        text = "Total de lecturas simuladas guardadas: ${lecturas.size}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 72.dp)
                    )
                }
            }
        }
    }
}

// Estilo distinto (tarjeta con borde amarillo + origen en cursiva)
// para diferenciar estos datos de la información ingresada a mano
@Composable
fun TarjetaLectura(
    lectura: LecturaSensor,
    nombreSector: String,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        border = BorderStroke(1.dp, Sol),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = nombreSector,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                ChipEstado(texto = lectura.estado.etiqueta, color = colorEstadoLectura(lectura.estado))
            }
            BarraValor(Icons.Filled.WaterDrop, "Humedad del suelo", "${lectura.humedadSuelo} %", lectura.humedadSuelo / 100f)
            BarraValor(Icons.Filled.Thermostat, "Temperatura", "${lectura.temperatura} °C", (lectura.temperatura / 40.0).toFloat())
            BarraValor(Icons.Filled.Cloud, "Humedad ambiente", "${lectura.humedadAmbiente} %", lectura.humedadAmbiente / 100f)
            Text(
                text = "Origen: ${lectura.origen} · ${lectura.fechaHora}",
                style = MaterialTheme.typography.labelSmall,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// Barra que se "llena" con animación cuando cambia el valor
@Composable
fun BarraValor(icono: ImageVector, nombre: String, valorTexto: String, progreso: Float) {
    val progresoAnimado by animateFloatAsState(
        targetValue = progreso.coerceIn(0f, 1f),
        animationSpec = tween(900),
        label = "barra"
    )
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(nombre, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
            Text(valorTexto, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { progresoAnimado },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(top = 4.dp)
        )
    }
}

// Tarjeta con el clima real de San Bernardo (API externa) y la recomendación de riego
@Composable
fun TarjetaClima(
    clima: ResumenClima?,
    cargando: Boolean,
    error: String?,
    onReintentar: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.WbSunny, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Clima real · San Bernardo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Fuente: API Open-Meteo (datos reales)",
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(Modifier.height(8.dp))
            when {
                cargando -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                clima != null -> {
                    Text(
                        text = "${clima.temperatura} °C · ${clima.descripcion}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text("Humedad: ${clima.humedad}% · Probabilidad de lluvia hoy: ${clima.probabilidadLluvia}%")
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = clima.recomendacion,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                else -> {
                    Text(error ?: "Sin datos del clima", color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = onReintentar) { Text("Reintentar") }
                }
            }
        }
    }
}
