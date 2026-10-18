package cl.duoc.huertoescolar.ui.sectores

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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.huertoescolar.data.modelo.EstadoSector
import cl.duoc.huertoescolar.ui.componentes.BarraSuperior
import cl.duoc.huertoescolar.ui.componentes.CampoTexto
import cl.duoc.huertoescolar.ui.componentes.SelectorOpciones
import cl.duoc.huertoescolar.ui.componentes.SoloAdministrador

@Composable
fun SectorFormScreen(
    idSector: Int,
    onVolver: () -> Unit,
    viewModel: SectorFormViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(idSector) { viewModel.cargar(idSector) }

    SoloAdministrador(onVolver = onVolver) {
        Scaffold(
            topBar = {
                BarraSuperior(
                    titulo = if (viewModel.esEdicion) "Modificar sector" else "Nuevo sector",
                    onVolver = onVolver
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CampoTexto(
                    valor = viewModel.codigo,
                    onValorCambia = { viewModel.cambiarCodigo(it) },
                    etiqueta = "Código (ej: S-05)",
                    icono = Icons.Filled.Tag,
                    error = viewModel.errorCodigo
                )
                CampoTexto(
                    valor = viewModel.nombre,
                    onValorCambia = { viewModel.cambiarNombre(it) },
                    etiqueta = "Nombre del sector",
                    icono = Icons.Filled.Yard,
                    error = viewModel.errorNombre
                )
                CampoTexto(
                    valor = viewModel.descripcion,
                    onValorCambia = { viewModel.cambiarDescripcion(it) },
                    etiqueta = "Descripción",
                    icono = Icons.Filled.Description,
                    error = viewModel.errorDescripcion,
                    lineas = 3
                )
                CampoTexto(
                    valor = viewModel.ubicacion,
                    onValorCambia = { viewModel.cambiarUbicacion(it) },
                    etiqueta = "Ubicación dentro del huerto",
                    icono = Icons.Filled.Place,
                    error = viewModel.errorUbicacion
                )
                SelectorOpciones(
                    titulo = "Estado del sector",
                    opciones = EstadoSector.entries,
                    seleccionada = viewModel.estado,
                    textoOpcion = { it.etiqueta },
                    onSeleccion = { viewModel.cambiarEstado(it) }
                )

                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.guardar {
                            Toast.makeText(context, "Sector guardado correctamente", Toast.LENGTH_SHORT).show()
                            onVolver()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar sector")
                }
            }
        }
    }
}
