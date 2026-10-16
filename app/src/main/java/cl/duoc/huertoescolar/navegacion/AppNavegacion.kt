package cl.duoc.huertoescolar.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.huertoescolar.data.Sesion
import cl.duoc.huertoescolar.ui.inicio.InicioAdminScreen
import cl.duoc.huertoescolar.ui.inicio.InicioEstudianteScreen
import cl.duoc.huertoescolar.ui.login.LoginScreen
import cl.duoc.huertoescolar.ui.sectores.SectoresScreen

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                onIngresoAdmin = { irAInicio(navController, Rutas.INICIO_ADMIN) },
                onIngresoEstudiante = { irAInicio(navController, Rutas.INICIO_ESTUDIANTE) }
            )
        }
        composable(Rutas.INICIO_ADMIN) {
            InicioAdminScreen(
                onIrSectores = { navController.navigate(Rutas.SECTORES) },
                onCerrarSesion = { cerrarSesion(navController) }
            )
        }
        composable(Rutas.INICIO_ESTUDIANTE) {
            InicioEstudianteScreen(
                onCerrarSesion = { cerrarSesion(navController) }
            )
        }
        composable(Rutas.SECTORES) {
            SectoresScreen(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}

// Después del login borramos la pantalla de login del historial,
// así el botón "atrás" no vuelve al login
private fun irAInicio(navController: NavHostController, ruta: String) {
    navController.navigate(ruta) {
        popUpTo(Rutas.LOGIN) { inclusive = true }
    }
}

private fun cerrarSesion(navController: NavHostController) {
    Sesion.cerrar()
    navController.navigate(Rutas.LOGIN) {
        popUpTo(navController.graph.id) { inclusive = true }
    }
}
