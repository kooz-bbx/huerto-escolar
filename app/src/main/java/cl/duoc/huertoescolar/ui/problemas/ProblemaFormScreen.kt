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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.huertoescolar.data.modelo.TipoProblema
import cl.duoc.huertoescolar.ui.componentes.BarraSuperior
import cl.duoc.huertoescolar.ui.componentes.CampoTexto
import cl.duoc.huertoescolar.ui.componentes.SelectorFoto
import cl.duoc.huertoescolar.ui.componentes.SelectorOpciones

@Composable
fun ProblemaFormScreen(
    onVolver: () -> Unit,
    viewModel: ProblemaFormViewModel = viewModel()
) {
    val context = LocalContext.current
    val sectores by viewModel.sectores.collectAsState()
    val cultivos by viewModel.cultivos.collectAsState()
    val cultivosDelSector = cultivos.filter { it.sectorId == viewModel.sectorId }

    Scaffold(
        topBar = { BarraSuperior(titulo = "Reportar un problema", onVolver = onVolver) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SelectorOpciones(
                titulo = "Sector",
                opciones = sectores,
                seleccionada = sectores.find { it.id == viewModel.sectorId },
                textoOpcion = { "${it.codigo} ${it.nombre}" },
                onSeleccion = { viewModel.cambiarSector(it.id) },
                error = viewModel.errorSector
            )
            if (cultivosDelSector.isNotEmpty()) {
                SelectorOpciones(
                    titulo = "Cultivo afectado (opcional)",
                    opciones = cultivosDelSector,
                    seleccionada = cultivosDelSector.find { it.id == viewModel.cultivoId },
                    textoOpcion = { it.nombre },
                    onSeleccion = { viewModel.cambiarCultivo(it.id) }
                )
            }
            SelectorOpciones(
                titulo = "Tipo de problema",
                opciones = TipoProblema.entries,
                seleccionada = viewModel.tipo,
                textoOpcion = { it.etiqueta },
                onSeleccion = { viewModel.cambiarTipo(it) },
                error = viewModel.errorTipo
            )
            CampoTexto(
                valor = viewModel.descripcion,
                onValorCambia = { viewModel.cambiarDescripcion(it) },
                etiqueta = "Describe brevemente el problema",
                icono = Icons.Filled.Description,
                error = viewModel.errorDescripcion,
                lineas = 3
            )
            SelectorFoto(
                fotoUri = viewModel.fotoUri,
                onFotoSeleccionada = { viewModel.cambiarFoto(it) },
                onQuitarFoto = { viewModel.quitarFoto() }
            )

            Spacer(Modifier.height(4.dp))
            Button(
                onClick = {
                    viewModel.enviar {
                        Toast.makeText(context, "Problema reportado. Tu docente lo revisará.", Toast.LENGTH_LONG).show()
                        onVolver()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Enviar reporte")
            }
        }
    }
}
