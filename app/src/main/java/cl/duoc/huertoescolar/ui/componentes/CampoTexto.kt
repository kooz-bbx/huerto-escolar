package cl.duoc.huertoescolar.ui.componentes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

// Campo de texto reutilizable para TODOS los formularios.
// Si hay error: se pinta rojo, muestra un ícono de alerta y el mensaje debajo.
@Composable
fun CampoTexto(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    error: String?,
    modifier: Modifier = Modifier,
    esClave: Boolean = false,
    tecladoNumerico: Boolean = false,
    lineas: Int = 1
) {
    var mostrarClave by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = null) },
        trailingIcon = {
            if (error != null) {
                Icon(
                    Icons.Filled.ErrorOutline,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
            } else if (esClave) {
                IconButton(onClick = { mostrarClave = !mostrarClave }) {
                    Icon(
                        if (mostrarClave) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = "Mostrar u ocultar clave"
                    )
                }
            }
        },
        isError = error != null,
        supportingText = {
            if (error != null) Text(error)
        },
        visualTransformation =
            if (esClave && !mostrarClave) PasswordVisualTransformation()
            else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = when {
                esClave -> KeyboardType.Password
                tecladoNumerico -> KeyboardType.Number
                else -> KeyboardType.Text
            }
        ),
        singleLine = lineas == 1,
        minLines = lineas,
        modifier = modifier.fillMaxWidth()
    )
}
