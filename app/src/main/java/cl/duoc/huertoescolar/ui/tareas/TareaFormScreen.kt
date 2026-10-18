package cl.duoc.huertoescolar.ui.tareas

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material.icons.filled.Description
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.huertoescolar.data.modelo.TipoTarea
import cl.duoc.huertoescolar.ui.componentes.BarraSuperior
import cl.duoc.huertoescolar.ui.componentes.CampoTexto
import cl.duoc.huertoescolar.ui.componentes.SelectorOpciones
import cl.duoc.huertoescolar.ui.componentes.SoloAdministrador

@Composable
fun TareaFormScreen(
    idTarea: Int,
    onVolver: () -> Unit,
    viewModel: TareaFormViewModel = viewModel()
) {
    val context = LocalContext.current
    val sectores by viewModel.sectores.collectAsState()
    val cultivos by viewModel.cultivos.collectAsState()
    val estudiantes by viewModel.estudiantes.collectAsState()
    LaunchedEffect(idTarea) { viewModel.cargar(idTarea) }

    // Solo se muestran los cultivos del sector elegido
    val cultivosDelSector = cultivos.filter { it.sectorId == viewModel.sectorId }
    val grupos = estudiantes.map { it.grupo }.distinct()

    SoloAdministrador(onVolver = onVolver) {
        Scaffold(
            topBar = {
                BarraSuperior(
                    titulo = if (viewModel.esEdicion) "Editar tarea" else "Nueva tarea",
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Mensaje general (ej: la tarea ya no se puede editar)
                viewModel.mensajeError?.let { mensaje ->
                    Text(mensaje, color = MaterialTheme.colorScheme.error)
                }
                SelectorOpciones(
                    titulo = "Tipo de tarea",
                    opciones = TipoTarea.entries,
                    seleccionada = viewModel.tipo,
                    textoOpcion = { it.etiqueta },
                    onSeleccion = { viewModel.cambiarTipo(it) },
                    error = viewModel.errorTipo
                )
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
                        titulo = "Cultivo relacionado (opcional)",
                        opciones = cultivosDelSector,
                        seleccionada = cultivosDelSector.find { it.id == viewModel.cultivoId },
                        textoOpcion = { it.nombre },
                        onSeleccion = { viewModel.cambiarCultivo(it.id) }
                    )
                }
                SelectorOpciones(
                    titulo = "Asignar a",
                    opciones = TipoAsignacion.entries,
                    seleccionada = viewModel.asignacion,
                    textoOpcion = { it.etiqueta },
                    onSeleccion = { viewModel.cambiarAsignacion(it) }
                )

                // Cambia con animación entre la lista de estudiantes y la de grupos
                AnimatedContent(
                    targetState = viewModel.asignacion,
                    label = "asignacion"
                ) { asignacion ->
                    if (asignacion == TipoAsignacion.ESTUDIANTE) {
                        SelectorOpciones(
                            titulo = "Estudiante responsable",
                            opciones = estudiantes,
                            seleccionada = estudiantes.find { it.id == viewModel.estudianteId },
                            textoOpcion = { it.nombre },
                            onSeleccion = { viewModel.cambiarEstudiante(it.id) },
                            error = viewModel.errorResponsable
                        )
                    } else {
                        SelectorOpciones(
                            titulo = "Grupo responsable",
                            opciones = grupos,
                            seleccionada = viewModel.grupo,
                            textoOpcion = { it },
                            onSeleccion = { viewModel.cambiarGrupo(it) },
                            error = viewModel.errorResponsable
                        )
                    }
                }

                CampoTexto(
                    valor = viewModel.fechaProgramada,
                    onValorCambia = { viewModel.cambiarFecha(it) },
                    etiqueta = "Fecha programada (dd/mm/aaaa)",
                    icono = Icons.Filled.CalendarMonth,
                    error = viewModel.errorFecha
                )
                CampoTexto(
                    valor = viewModel.instrucciones,
                    onValorCambia = { viewModel.cambiarInstrucciones(it) },
                    etiqueta = "Instrucciones para el estudiante",
                    icono = Icons.Filled.Description,
                    error = viewModel.errorInstrucciones,
                    lineas = 3
                )

                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = {
                        viewModel.guardar {
                            val texto = if (viewModel.esEdicion) "Tarea actualizada" else "Tarea creada y asignada"
                            Toast.makeText(context, texto, Toast.LENGTH_SHORT).show()
                            onVolver()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (viewModel.esEdicion) "Guardar cambios" else "Crear tarea")
                }
            }
        }
    }
}
