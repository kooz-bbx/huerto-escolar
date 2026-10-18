package cl.duoc.huertoescolar.ui.tareas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duoc.huertoescolar.data.modelo.Tarea
import cl.duoc.huertoescolar.domain.estadoMostrado
import cl.duoc.huertoescolar.ui.componentes.ChipEstado
import cl.duoc.huertoescolar.ui.componentes.DatoConIcono
import cl.duoc.huertoescolar.ui.componentes.colorEstadoTarea

// Tarjeta de tarea: la usan la lista del docente y la del estudiante
@Composable
fun TarjetaTarea(
    tarea: Tarea,
    nombreSector: String,
    nombreResponsable: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado = estadoMostrado(tarea)

    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.AutoMirrored.Filled.Assignment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = tarea.tipo.etiqueta,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                ChipEstado(texto = estado.etiqueta, color = colorEstadoTarea(estado))
            }
            Text(
                text = tarea.instrucciones,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
            DatoConIcono(Icons.Filled.Yard, nombreSector)
            DatoConIcono(Icons.Filled.CalendarMonth, "Programada: ${tarea.fechaProgramada}")
            DatoConIcono(Icons.Filled.Group, nombreResponsable)
        }
    }
}
