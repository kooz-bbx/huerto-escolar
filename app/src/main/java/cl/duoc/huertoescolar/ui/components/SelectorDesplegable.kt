package cl.duoc.huertoescolar.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

// Lista desplegable: muestra solo lo elegido y, al tocarla, despliega todas las opciones.
// Se usa cuando las opciones pueden ser muchas (alumnos, sectores, cultivos, tipos),
// para que la pantalla no se llene de botones sueltos.
// Si se pasa "textoTodos", agrega una primera opción para quitar la selección (ej: "Todos").
@Composable
fun <T> SelectorDesplegable(
    titulo: String,
    opciones: List<T>,
    seleccionada: T?,
    textoOpcion: (T) -> String,
    onSeleccion: (T) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    textoTodos: String? = null,
    onQuitarSeleccion: () -> Unit = {}
) {
    var abierto by remember { mutableStateOf(false) }

    // La flecha gira cuando se abre la lista (animación)
    val giroFlecha by animateFloatAsState(
        targetValue = if (abierto) 180f else 0f,
        label = "giroFlecha"
    )

    val colorBorde = when {
        error != null -> MaterialTheme.colorScheme.error
        abierto -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    // Texto que se ve con la lista cerrada
    val textoVisible = when {
        seleccionada != null -> textoOpcion(seleccionada)
        textoTodos != null -> textoTodos
        else -> "Toca para elegir (${opciones.size} opciones)"
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelLarge,
            color = if (error != null) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.size(4.dp))

        Box {
            // La "caja" que se toca para abrir la lista
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, colorBorde, RoundedCornerShape(8.dp))
                    .clickable { abierto = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = textoVisible,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (seleccionada != null) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = if (abierto) "Cerrar lista" else "Abrir lista",
                    modifier = Modifier.rotate(giroFlecha)
                )
            }

            // La lista que se despliega. Con muchas opciones se puede deslizar.
            DropdownMenu(
                expanded = abierto,
                onDismissRequest = { abierto = false },
                modifier = Modifier.heightIn(max = 320.dp)
            ) {
                if (textoTodos != null) {
                    DropdownMenuItem(
                        text = { Text(textoTodos) },
                        onClick = {
                            onQuitarSeleccion()
                            abierto = false
                        },
                        leadingIcon = if (seleccionada == null) {
                            { Icon(Icons.Filled.Check, contentDescription = null) }
                        } else null
                    )
                    HorizontalDivider()
                }
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(textoOpcion(opcion)) },
                        onClick = {
                            onSeleccion(opcion)
                            abierto = false
                        },
                        leadingIcon = if (opcion == seleccionada) {
                            { Icon(Icons.Filled.Check, contentDescription = null) }
                        } else null
                    )
                }
            }
        }

        if (error != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    Icons.Filled.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
