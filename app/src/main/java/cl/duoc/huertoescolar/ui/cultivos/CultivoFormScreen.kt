package cl.duoc.huertoescolar.ui.cultivos

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.huertoescolar.data.modelo.EstadoCultivo
import cl.duoc.huertoescolar.ui.componentes.BarraSuperior
import cl.duoc.huertoescolar.ui.componentes.CampoTexto
import cl.duoc.huertoescolar.ui.componentes.DialogoConfirmacion
import cl.duoc.huertoescolar.ui.componentes.SelectorOpciones
import cl.duoc.huertoescolar.ui.componentes.SoloAdministrador

@Composable
fun CultivoFormScreen(
    idCultivo: Int,
    onVolver: () -> Unit,
    viewModel: CultivoFormViewModel = viewModel()
) {
    val context = LocalContext.current
    val sectores by viewModel.sectores.collectAsState()
    var confirmarEliminar by remember { mutableStateOf(false) }
    LaunchedEffect(idCultivo) { viewModel.cargar(idCultivo) }

    SoloAdministrador(onVolver = onVolver) {
        Scaffold(
            topBar = {
                BarraSuperior(
                    titulo = if (viewModel.esEdicion) "Modificar cultivo" else "Nuevo cultivo",
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
                    valor = viewModel.nombre,
                    onValorCambia = { viewModel.cambiarNombre(it) },
                    etiqueta = "Nombre del cultivo",
                    icono = Icons.Filled.Eco,
                    error = viewModel.errorNombre
                )
                CampoTexto(
                    valor = viewModel.descripcion,
                    onValorCambia = { viewModel.cambiarDescripcion(it) },
                    etiqueta = "Descripción (opcional)",
                    icono = Icons.Filled.Description,
                    error = null
                )
                SelectorOpciones(
                    titulo = "Sector",
                    opciones = sectores,
                    seleccionada = sectores.find { it.id == viewModel.sectorId },
                    textoOpcion = { "${it.codigo} ${it.nombre}" },
                    onSeleccion = { viewModel.cambiarSector(it.id) },
                    error = viewModel.errorSector
                )
                CampoTexto(
                    valor = viewModel.fechaSiembra,
                    onValorCambia = { viewModel.cambiarFechaSiembra(it) },
                    etiqueta = "Fecha de siembra (dd/mm/aaaa)",
                    icono = Icons.Filled.CalendarMonth,
                    error = viewModel.errorFechaSiembra
                )
                CampoTexto(
                    valor = viewModel.fechaTrasplante,
                    onValorCambia = { viewModel.cambiarFechaTrasplante(it) },
                    etiqueta = "Fecha de trasplante (opcional)",
                    icono = Icons.Filled.SwapHoriz,
                    error = viewModel.errorFechaTrasplante
                )
                CampoTexto(
                    valor = viewModel.fechaCosecha,
                    onValorCambia = { viewModel.cambiarFechaCosecha(it) },
                    etiqueta = "Cosecha estimada (dd/mm/aaaa)",
                    icono = Icons.Filled.EventAvailable,
                    error = viewModel.errorFechaCosecha
                )
                CampoTexto(
                    valor = viewModel.frecuenciaRiego,
                    onValorCambia = { viewModel.cambiarFrecuenciaRiego(it) },
                    etiqueta = "Regar cada (días)",
                    icono = Icons.Filled.WaterDrop,
                    error = viewModel.errorRiego,
                    tecladoNumerico = true
                )
                SelectorOpciones(
                    titulo = "Estado de desarrollo",
                    opciones = EstadoCultivo.entries,
                    seleccionada = viewModel.estado,
                    textoOpcion = { it.etiqueta },
                    onSeleccion = { viewModel.cambiarEstado(it) }
                )
                CampoTexto(
                    valor = viewModel.observaciones,
                    onValorCambia = { viewModel.cambiarObservaciones(it) },
                    etiqueta = "Observaciones (opcional)",
                    icono = Icons.Filled.EditNote,
                    error = null,
                    lineas = 2
                )

                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.guardar {
                            Toast.makeText(context, "Cultivo guardado correctamente", Toast.LENGTH_SHORT).show()
                            onVolver()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar cultivo")
                }

                // Solo se puede eliminar un cultivo que ya existe
                if (viewModel.esEdicion) {
                    OutlinedButton(
                        onClick = { confirmarEliminar = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Eliminar cultivo")
                    }
                }
            }
        }

        if (confirmarEliminar) {
            DialogoConfirmacion(
                titulo = "¿Eliminar cultivo?",
                mensaje = "Las tareas de este cultivo se mantienen, pero quedarán sin cultivo asociado.",
                textoConfirmar = "Eliminar",
                onConfirmar = {
                    confirmarEliminar = false
                    viewModel.eliminar { eliminado ->
                        val texto = if (eliminado) "Cultivo eliminado" else "No se pudo eliminar el cultivo"
                        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show()
                        if (eliminado) onVolver()
                    }
                },
                onCancelar = { confirmarEliminar = false }
            )
        }
    }
}
