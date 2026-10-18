package cl.duoc.huertoescolar.ui.tareas

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.huertoescolar.data.modelo.EstadoTarea
import cl.duoc.huertoescolar.data.modelo.Evidencia
import cl.duoc.huertoescolar.data.modelo.ResultadoValidacion
import cl.duoc.huertoescolar.data.modelo.Validacion
import cl.duoc.huertoescolar.domain.estadoMostrado
import cl.duoc.huertoescolar.ui.componentes.BarraSuperior
import cl.duoc.huertoescolar.ui.componentes.CampoTexto
import cl.duoc.huertoescolar.ui.componentes.Cargando
import cl.duoc.huertoescolar.ui.componentes.ChipEstado
import cl.duoc.huertoescolar.ui.componentes.DatoConIcono
import cl.duoc.huertoescolar.ui.componentes.DialogoConfirmacion
import cl.duoc.huertoescolar.ui.componentes.SelectorFoto
import cl.duoc.huertoescolar.ui.componentes.colorEstadoTarea
import cl.duoc.huertoescolar.ui.theme.EstadoOk
import coil3.compose.AsyncImage

@Composable
fun TareaDetalleScreen(
    onVolver: () -> Unit,
    onEditarTarea: (Int) -> Unit,
    viewModel: TareaDetalleViewModel = viewModel()
) {
    val context = LocalContext.current
    val tarea by viewModel.tarea.collectAsState()
    val evidencias by viewModel.evidencias.collectAsState()
    val validaciones by viewModel.validaciones.collectAsState()
    val sectores by viewModel.sectores.collectAsState()
    val cultivos by viewModel.cultivos.collectAsState()
    val usuarios by viewModel.usuarios.collectAsState()
    var confirmarEliminar by remember { mutableStateOf(false) }

    val actual = tarea

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Detalle de la tarea",
                onVolver = onVolver,
                acciones = {
                    // Editar y eliminar: solo el docente y solo si la tarea sigue pendiente
                    if (actual != null && viewModel.puedeModificar(actual)) {
                        IconButton(onClick = { onEditarTarea(actual.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar tarea")
                        }
                        IconButton(onClick = { confirmarEliminar = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar tarea")
                        }
                    }
                }
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
            if (actual == null) {
                Cargando("Cargando tarea...")
                return@Column
            }
            if (!viewModel.tieneAcceso(actual)) {
                Text("No tienes acceso a esta tarea.", color = MaterialTheme.colorScheme.error)
                return@Column
            }

            val sector = sectores.find { it.id == actual.sectorId }
            val cultivo = cultivos.find { it.id == actual.cultivoId }
            val creador = usuarios.find { it.id == actual.creadorId }

            // ---------- Información general ----------
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row {
                        Text(
                            text = actual.tipo.etiqueta,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                        // El estado cambia con un fundido (Crossfade)
                        Crossfade(targetState = estadoMostrado(actual), label = "estado") { estado ->
                            ChipEstado(texto = estado.etiqueta, color = colorEstadoTarea(estado))
                        }
                    }
                    Text(
                        text = actual.instrucciones,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    DatoConIcono(Icons.Filled.Yard, sector?.let { "${it.codigo} · ${it.nombre}" } ?: "")
                    if (cultivo != null) DatoConIcono(Icons.Filled.Eco, "Cultivo: ${cultivo.nombre}")
                    DatoConIcono(Icons.Filled.CalendarMonth, "Fecha programada: ${actual.fechaProgramada}")
                    DatoConIcono(Icons.Filled.Group, "Responsable: ${textoResponsable(actual, usuarios)}")
                    DatoConIcono(Icons.Filled.Person, "Creada por ${creador?.nombre ?: "-"} el ${actual.fechaCreacion}")
                }
            }

            // ---------- Paso 1 (estudiante): iniciar ----------
            AnimatedVisibility(
                visible = viewModel.puedeComenzar(actual),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Para entregar esta tarea, primero iníciala.")
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.comenzarTarea() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Comenzar tarea")
                        }
                    }
                }
            }

            // ---------- Paso 2 (estudiante): entregar con evidencia ----------
            AnimatedVisibility(
                visible = viewModel.puedeEntregar(actual),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (actual.estado == EstadoTarea.RECHAZADA) "Corrige y vuelve a entregar"
                        else "Registrar mi trabajo",
                        style = MaterialTheme.typography.titleMedium
                    )
                    SelectorFoto(
                        fotoUri = viewModel.fotoUri,
                        onFotoSeleccionada = { viewModel.cambiarFoto(it) },
                        onQuitarFoto = { viewModel.quitarFoto() }
                    )
                    CampoTexto(
                        valor = viewModel.comentarioEstudiante,
                        onValorCambia = { viewModel.cambiarComentarioEstudiante(it) },
                        etiqueta = "¿Qué hiciste?",
                        icono = Icons.AutoMirrored.Filled.Comment,
                        error = viewModel.errorComentarioEstudiante,
                        lineas = 2
                    )
                    Button(
                        onClick = {
                            viewModel.entregar {
                                Toast.makeText(context, "Tarea enviada. Queda pendiente de validación.", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Marcar como realizada")
                    }
                }
            }

            // ---------- Paso 3 (docente): aprobar o rechazar ----------
            AnimatedVisibility(
                visible = viewModel.puedeRevisar(actual),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Revisar la última entrega", style = MaterialTheme.typography.titleMedium)
                    CampoTexto(
                        valor = viewModel.comentarioDocente,
                        onValorCambia = { viewModel.cambiarComentarioDocente(it) },
                        etiqueta = "Comentario (obligatorio si rechazas)",
                        icono = Icons.AutoMirrored.Filled.Comment,
                        error = viewModel.errorComentarioDocente,
                        lineas = 2
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                viewModel.aprobar {
                                    Toast.makeText(context, "Tarea validada", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EstadoOk),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Aprobar")
                        }
                        Button(
                            onClick = {
                                viewModel.rechazar {
                                    Toast.makeText(context, "Tarea rechazada. El estudiante deberá corregirla.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Cancel, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Rechazar")
                        }
                    }
                }
            }

            // ---------- Historial de entregas y revisiones ----------
            Text(
                text = "Entregas (${evidencias.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (evidencias.isEmpty()) {
                Text("Todavía no hay entregas.", style = MaterialTheme.typography.bodyMedium)
            }
            evidencias.forEach { evidencia ->
                TarjetaEntrega(
                    evidencia = evidencia,
                    validacion = validaciones.find { it.evidenciaId == evidencia.id },
                    nombreAutor = usuarios.find { it.id == evidencia.autorId }?.nombre ?: "-",
                    nombreValidador = { id -> usuarios.find { it.id == id }?.nombre ?: "-" }
                )
            }
        }
    }

    if (confirmarEliminar) {
        DialogoConfirmacion(
            titulo = "¿Eliminar tarea?",
            mensaje = "La tarea se borrará definitivamente.",
            textoConfirmar = "Eliminar",
            onConfirmar = {
                confirmarEliminar = false
                viewModel.eliminar { eliminada ->
                    val texto = if (eliminada) "Tarea eliminada" else "No se pudo eliminar la tarea"
                    Toast.makeText(context, texto, Toast.LENGTH_SHORT).show()
                    if (eliminada) onVolver()
                }
            },
            onCancelar = { confirmarEliminar = false }
        )
    }
}

// Una entrega (Evidencia) y, si existe, su revisión (Validacion)
@Composable
fun TarjetaEntrega(
    evidencia: Evidencia,
    validacion: Validacion?,
    nombreAutor: String,
    nombreValidador: (Int) -> String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            DatoConIcono(Icons.Filled.Person, "Entregada por $nombreAutor · ${evidencia.fechaHora}")
            DatoConIcono(Icons.AutoMirrored.Filled.Comment, "\"${evidencia.comentario}\"")
            if (evidencia.fotoUri.isNotEmpty()) {
                AsyncImage(
                    model = evidencia.fotoUri,
                    contentDescription = "Foto de evidencia",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            } else {
                Text(
                    text = "Sin fotografía adjunta",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (validacion == null) {
                Text(
                    text = "⏳ Pendiente de revisión",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                val aprobada = validacion.resultado == ResultadoValidacion.APROBADA
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (aprobada) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (aprobada) "✅ Aprobada" else "❌ Rechazada",
                            fontWeight = FontWeight.Bold
                        )
                        if (validacion.comentario.isNotEmpty()) {
                            Text("\"${validacion.comentario}\"", style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(
                            text = "Revisó ${nombreValidador(validacion.validadorId)} · ${validacion.fechaHora}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
