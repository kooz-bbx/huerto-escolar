package cl.duoc.huertoescolar.data

import cl.duoc.huertoescolar.data.modelo.EstadoCuenta
import cl.duoc.huertoescolar.data.modelo.EstadoSector
import cl.duoc.huertoescolar.data.modelo.Perfil
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.modelo.Usuario

// Datos ficticios para probar la app (cuentas de prueba)
object DatosPrueba {

    val usuarios = listOf(
        Usuario(1, "Prof. Rosa Tierra", "docente", "1234", Perfil.ADMINISTRADOR, "Docente", "Taller Huerto"),
        Usuario(2, "Pedro Semilla", "tallerista", "1234", Perfil.ADMINISTRADOR, "Tallerista", "Taller Huerto"),
        Usuario(3, "Ana Brote", "ana", "1234", Perfil.ESTUDIANTE, "Estudiante", "Grupo Girasol"),
        Usuario(4, "Benja Raíz", "benja", "1234", Perfil.ESTUDIANTE, "Estudiante", "Grupo Girasol"),
        Usuario(5, "Cami Hoja", "cami", "1234", Perfil.ESTUDIANTE, "Estudiante", "Grupo Tomate"),
        Usuario(6, "Lucas Tallo", "lucas", "1234", Perfil.ESTUDIANTE, "Estudiante", "Grupo Tomate", EstadoCuenta.INACTIVA)
    )

    val sectores = listOf(
        Sector(1, "S-01", "Bancal Norte", "Bancal elevado para hortalizas de hoja", "Junto a la sala de 5° básico", EstadoSector.EN_USO, 1, "02/09/2026", "02/09/2026"),
        Sector(2, "S-02", "Invernadero", "Invernadero pequeño para almácigos", "Patio trasero", EstadoSector.EN_USO, 2, "02/09/2026", "10/09/2026"),
        Sector(3, "S-03", "Jardín Aromático", "Hierbas aromáticas y medicinales", "Entrada del huerto", EstadoSector.EN_MANTENIMIENTO, 1, "05/09/2026", "20/09/2026"),
        Sector(4, "S-04", "Compostera", "Zona de compost y lombricultura", "Esquina sur", EstadoSector.DISPONIBLE, 2, "05/09/2026", "05/09/2026")
    )
}
