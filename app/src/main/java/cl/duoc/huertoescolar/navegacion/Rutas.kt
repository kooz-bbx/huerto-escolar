package cl.duoc.huertoescolar.navegacion

// Nombres de las rutas (pantallas) de la app
object Rutas {
    const val LOGIN = "login"
    const val INICIO_ADMIN = "inicio_admin"
    const val INICIO_ESTUDIANTE = "inicio_estudiante"
    const val SECTORES = "sectores"
    const val SECTOR_FORM = "sector_form/{id}"
    const val CULTIVOS = "cultivos"
    const val CULTIVO_FORM = "cultivo_form/{id}"

    // Funciones para armar la ruta con el id (0 = crear uno nuevo)
    fun sectorForm(id: Int) = "sector_form/$id"
    fun cultivoForm(id: Int) = "cultivo_form/$id"
}
