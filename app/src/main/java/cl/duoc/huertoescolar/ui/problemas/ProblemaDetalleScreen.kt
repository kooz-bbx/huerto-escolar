package cl.duoc.huertoescolar.ui.problemas

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.huertoescolar.data.modelo.EstadoProblema
import cl.duoc.huertoescolar.ui.componentes.BarraSuperior
import cl.duoc.huertoescolar.ui.componentes.CampoTexto
import cl.duoc.huertoescolar.ui.componentes.DatoConIcono
import cl.duoc.huertoescolar.ui.componentes.SelectorOpciones
import cl.duoc.huertoescolar.ui.componentes.SoloAdministrador
import coil3.compose.AsyncImage

@Composable
fun ProblemaDetalleScreen(
    idProblema: Int,
    onVolver: () -> Unit,
    viewModel: ProblemaDetalleViewModel = viewModel()
) {
    val context = LocalContext.current
    val sectores by viewModel.sectores.collectAsState()
    val cultivos by viewModel.cultivos.collectAsState()
    val usuarios by viewModel.usuarios.collectAsState()
    LaunchedEffect(idProblema) { viewModel.cargar(idProblema) }

    SoloAdministrador(onVolver = onVolver) {
        Scaffold(
            topBar = { BarraSuperior(titulo = "Seguimiento del problema", onVolver = onVolver) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val problema = viewModel.problema
                if (problema == null) {
                    Text("Cargando...")
                    return@Column
                }

                TarjetaProblema(
                    problema = problema,
                    nombreSector = sectores.find { it.id == problema.sectorId }?.nombre ?: "",
                    nombreReportante = usuarios.find { it.id == problema.reportadoPorId }?.nombre ?: "-",
                    onClick = null
                )
                cultivos.find { it.id == problema.cultivoId }?.let {
                    DatoConIcono(Icons.Filled.Eco, "Cultivo afectado: ${it.nombre}")
                }
                if (problema.fotoUri.isNotEmpty()) {
                    AsyncImage(
                        model = problema.fotoUri,
                        contentDescription = "Foto del problema",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }

                Text("Actualizar seguimiento", style = MaterialTheme.typography.titleMedium)
                SelectorOpciones(
                    titulo = "Estado del reporte",
                    opciones = EstadoProblema.entries,
                    seleccionada = viewModel.estado,
                    textoOpcion = { it.etiqueta },
                    onSeleccion = { viewModel.cambiarEstado(it) }
                )
                CampoTexto(
                    valor = viewModel.comentario,
                    onValorCambia = { viewModel.cambiarComentario(it) },
                    etiqueta = "Comentario de seguimiento",
                    icono = Icons.AutoMirrored.Filled.Comment,
                    error = viewModel.errorComentario,
                    lineas = 2
                )
                Button(
                    onClick = {
                        viewModel.guardar {
                            Toast.makeText(context, "Seguimiento guardado", Toast.LENGTH_SHORT).show()
                            onVolver()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar")
                }
            }
        }
    }
}
