package cl.duoc.huertoescolar.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.huertoescolar.data.modelo.Perfil
import cl.duoc.huertoescolar.ui.componentes.CampoTexto
import cl.duoc.huertoescolar.ui.theme.HuertoEscolarTheme

@Composable
fun LoginScreen(
    onIngresoAdmin: () -> Unit,
    onIngresoEstudiante: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    // Para animar la entrada del logo al abrir la app
    var mostrarLogo by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { mostrarLogo = true }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            AnimatedVisibility(
                visible = mostrarLogo,
                enter = fadeIn(tween(1000)) + scaleIn(tween(1000))
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(110.dp)
                ) {
                    Icon(
                        Icons.Filled.Eco,
                        contentDescription = "Logo huerto",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(22.dp)
                    )
                }
            }

            Text(
                text = "Huerto Escolar",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Organiza sectores, cultivos y tareas del huerto",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))

            CampoTexto(
                valor = viewModel.usuario,
                onValorCambia = { viewModel.cambiarUsuario(it) },
                etiqueta = "Usuario",
                icono = Icons.Filled.Person,
                error = viewModel.errorUsuario
            )
            CampoTexto(
                valor = viewModel.clave,
                onValorCambia = { viewModel.cambiarClave(it) },
                etiqueta = "Clave",
                icono = Icons.Filled.Lock,
                error = viewModel.errorClave,
                esClave = true
            )

            // Mensaje de error general con animación
            AnimatedVisibility(
                visible = viewModel.mensajeError != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(8.dp))
                        Text(viewModel.mensajeError ?: "", color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.iniciarSesion { perfil ->
                        if (perfil == Perfil.ADMINISTRADOR) onIngresoAdmin() else onIngresoEstudiante()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Ingresar")
            }

            // Ayuda con las cuentas ficticias de prueba
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Icon(Icons.Filled.Info, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Cuentas de prueba (clave 1234):\n" +
                                "• Docente: docente\n" +
                                "• Tallerista: tallerista\n" +
                                "• Estudiantes: ana, benja, cami",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLogin() {
    HuertoEscolarTheme {
        LoginScreen(onIngresoAdmin = {}, onIngresoEstudiante = {})
    }
}
