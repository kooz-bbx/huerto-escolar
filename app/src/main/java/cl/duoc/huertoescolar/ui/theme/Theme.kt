package cl.duoc.huertoescolar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = VerdeHoja,
    onPrimary = Color.White,
    primaryContainer = VerdeClaro,
    onPrimaryContainer = VerdeOscuro,
    secondary = Tierra,
    onSecondary = Color.White,
    secondaryContainer = TierraClara,
    tertiary = Sol,
    tertiaryContainer = SolClaro,
    background = FondoCrema,
    surface = FondoCrema
)

private val DarkColorScheme = darkColorScheme(
    primary = VerdeClaro,
    onPrimary = VerdeOscuro,
    primaryContainer = VerdeOscuro,
    secondary = TierraClara,
    tertiary = SolClaro
)

@Composable
fun HuertoEscolarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // No usamos "dynamic color" para que la app siempre se vea verde (identidad del huerto)
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
