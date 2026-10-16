package cl.duoc.huertoescolar.ui.componentes

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// "Chip" de color para mostrar estados. El color cambia con animación
// (animateColorAsState, igual que el botón de FotoMotion).
@Composable
fun ChipEstado(
    texto: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val colorAnimado by animateColorAsState(
        targetValue = color,
        animationSpec = tween(600),
        label = "colorChip"
    )
    Surface(
        color = colorAnimado.copy(alpha = 0.15f),
        contentColor = colorAnimado,
        shape = RoundedCornerShape(50),
        modifier = modifier
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
