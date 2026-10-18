package cl.duoc.huertoescolar.navegacion

// Nombres de las rutas (pantallas) de la app
object Rutas {
    const val LOGIN = "login"
    const val INICIO_ADMIN = "inicio_admin"
    const val INICIO_ESTUDIANTE = "inicio_estudiante"
    const val SECTORES = "sectores"
    const val SECTOR_DETALLE = "sector_detalle/{id}"
    const val SECTOR_FORM = "sector_form/{id}"
    const val CULTIVOS = "cultivos"
    const val CULTIVO_FORM = "cultivo_form/{id}"
    const val TAREAS = "tareas"
    const val TAREA_FORM = "tarea_form/{id}"
    const val TAREA_DETALLE = "tarea_detalle/{id}"

    // Funciones para armar la ruta con el id (0 = crear uno nuevo)
    fun sectorDetalle(id: Int) = "sector_detalle/$id"
    fun sectorForm(id: Int) = "sector_form/$id"
    fun cultivoForm(id: Int) = "cultivo_form/$id"
    fun tareaForm(id: Int) = "tarea_form/$id"
    fun tareaDetalle(id: Int) = "tarea_detalle/$id"
}
