package cl.duoc.huertoescolar.ui.problemas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.huertoescolar.data.modelo.Problema
import cl.duoc.huertoescolar.ui.componentes.ChipEstado
import cl.duoc.huertoescolar.ui.componentes.DatoConIcono
import cl.duoc.huertoescolar.ui.componentes.colorEstadoProblema

@Composable
fun TarjetaProblema(
    problema: Problema,
    nombreSector: String,
    nombreReportante: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val contenido: @Composable () -> Unit = {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ReportProblem, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = problema.tipo.etiqueta,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                ChipEstado(texto = problema.estado.etiqueta, color = colorEstadoProblema(problema.estado))
            }
            Text(
                text = problema.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            DatoConIcono(Icons.Filled.Yard, nombreSector)
            DatoConIcono(Icons.Filled.CalendarMonth, problema.fechaReporte)
            DatoConIcono(Icons.Filled.Person, "Reportado por $nombreReportante")
            if (problema.comentarioDocente.isNotEmpty()) {
                DatoConIcono(Icons.AutoMirrored.Filled.Comment, "Docente: \"${problema.comentarioDocente}\"")
            }
        }
    }

    // Si tiene onClick (docente) la tarjeta se puede tocar; si no (estudiante), solo se muestra
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier.fillMaxWidth()) { contenido() }
    } else {
        Card(modifier = modifier.fillMaxWidth()) { contenido() }
    }
}
