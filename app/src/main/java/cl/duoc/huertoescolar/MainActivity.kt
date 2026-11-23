package cl.duoc.huertoescolar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.huertoescolar.navigation.AppNavegacion
import cl.duoc.huertoescolar.ui.theme.HuertoEscolarTheme
import cl.duoc.huertoescolar.utils.crearCanalNotificaciones

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        crearCanalNotificaciones(this)
        setContent {
            HuertoEscolarTheme {
                AppNavegacion()
            }
        }
    }
}
