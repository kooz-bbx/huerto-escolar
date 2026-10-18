package cl.duoc.huertoescolar.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.huertoescolar.data.Sesion
import cl.duoc.huertoescolar.ui.cultivos.CultivoFormScreen
import cl.duoc.huertoescolar.ui.cultivos.CultivosScreen
import cl.duoc.huertoescolar.ui.inicio.InicioAdminScreen
import cl.duoc.huertoescolar.ui.inicio.InicioEstudianteScreen
import cl.duoc.huertoescolar.ui.login.LoginScreen
import cl.duoc.huertoescolar.ui.sectores.SectorFormScreen
import cl.duoc.huertoescolar.ui.sectores.SectoresScreen

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val volver: () -> Unit = { navController.popBackStack() }

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
                onIrCultivos = { navController.navigate(Rutas.CULTIVOS) },
                onCerrarSesion = { cerrarSesion(navController) }
            )
        }
        composable(Rutas.INICIO_ESTUDIANTE) {
            InicioEstudianteScreen(
                onCerrarSesion = { cerrarSesion(navController) }
            )
        }

        // ---------- Sectores ----------
        composable(Rutas.SECTORES) {
            SectoresScreen(
                onVolver = volver,
                onNuevoSector = { navController.navigate(Rutas.sectorForm(0)) },
                onEditarSector = { id -> navController.navigate(Rutas.sectorForm(id)) }
            )
        }
        composable(
            route = Rutas.SECTOR_FORM,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entrada ->
            SectorFormScreen(
                idSector = entrada.arguments?.getInt("id") ?: 0,
                onVolver = volver
            )
        }

        // ---------- Cultivos ----------
        composable(Rutas.CULTIVOS) {
            CultivosScreen(
                onVolver = volver,
                onNuevoCultivo = { navController.navigate(Rutas.cultivoForm(0)) },
                onEditarCultivo = { id -> navController.navigate(Rutas.cultivoForm(id)) }
            )
        }
        composable(
            route = Rutas.CULTIVO_FORM,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entrada ->
            CultivoFormScreen(
                idCultivo = entrada.arguments?.getInt("id") ?: 0,
                onVolver = volver
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
