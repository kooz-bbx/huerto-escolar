package cl.duoc.huertoescolar.navegacion

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import cl.duoc.huertoescolar.ui.sectores.SectorDetalleScreen
import cl.duoc.huertoescolar.ui.sectores.SectorFormScreen
import cl.duoc.huertoescolar.ui.sectores.SectoresScreen
import cl.duoc.huertoescolar.ui.tareas.TareaDetalleScreen
import cl.duoc.huertoescolar.ui.tareas.TareaFormScreen
import cl.duoc.huertoescolar.ui.tareas.TareasScreen

// Argumento "id" (número) que usan las rutas de detalle y formularios
private val argumentoId = listOf(navArgument("id") { type = NavType.IntType })

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val volver: () -> Unit = { navController.popBackStack() }

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN,
        // Animaciones al cambiar de pantalla: la nueva entra deslizándose desde la derecha
        enterTransition = { slideInHorizontally(tween(350)) { ancho -> ancho } + fadeIn(tween(350)) },
        exitTransition = { fadeOut(tween(250)) },
        popEnterTransition = { fadeIn(tween(250)) },
        popExitTransition = { slideOutHorizontally(tween(350)) { ancho -> ancho } + fadeOut(tween(350)) }
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
                onIrTareas = { navController.navigate(Rutas.TAREAS) },
                onCerrarSesion = { cerrarSesion(navController) }
            )
        }
        composable(Rutas.INICIO_ESTUDIANTE) {
            InicioEstudianteScreen(
                onVerTarea = { id -> navController.navigate(Rutas.tareaDetalle(id)) },
                onCerrarSesion = { cerrarSesion(navController) }
            )
        }

        // ---------- Sectores ----------
        composable(Rutas.SECTORES) {
            SectoresScreen(
                onVolver = volver,
                onNuevoSector = { navController.navigate(Rutas.sectorForm(0)) },
                onVerSector = { id -> navController.navigate(Rutas.sectorDetalle(id)) }
            )
        }
        composable(Rutas.SECTOR_DETALLE, arguments = argumentoId) {
            SectorDetalleScreen(
                onVolver = volver,
                onEditarSector = { id -> navController.navigate(Rutas.sectorForm(id)) },
                onVerCultivo = { id -> navController.navigate(Rutas.cultivoForm(id)) }
            )
        }
        composable(Rutas.SECTOR_FORM, arguments = argumentoId) { entrada ->
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
        composable(Rutas.CULTIVO_FORM, arguments = argumentoId) { entrada ->
            CultivoFormScreen(
                idCultivo = entrada.arguments?.getInt("id") ?: 0,
                onVolver = volver
            )
        }

        // ---------- Tareas ----------
        composable(Rutas.TAREAS) {
            TareasScreen(
                onVolver = volver,
                onNuevaTarea = { navController.navigate(Rutas.tareaForm(0)) },
                onVerTarea = { id -> navController.navigate(Rutas.tareaDetalle(id)) }
            )
        }
        composable(Rutas.TAREA_FORM, arguments = argumentoId) { entrada ->
            TareaFormScreen(
                idTarea = entrada.arguments?.getInt("id") ?: 0,
                onVolver = volver
            )
        }
        composable(Rutas.TAREA_DETALLE, arguments = argumentoId) {
            TareaDetalleScreen(
                onVolver = volver,
                onEditarTarea = { id -> navController.navigate(Rutas.tareaForm(id)) }
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
