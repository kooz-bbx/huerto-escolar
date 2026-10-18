package cl.duoc.huertoescolar.data

import cl.duoc.huertoescolar.data.modelo.Cultivo
import cl.duoc.huertoescolar.data.modelo.EstadoCuenta
import cl.duoc.huertoescolar.data.modelo.EstadoCultivo
import cl.duoc.huertoescolar.data.modelo.EstadoSector
import cl.duoc.huertoescolar.data.modelo.Perfil
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.modelo.Usuario

// Datos ficticios para probar la app.
// Se cargan en Room la primera vez que se abre la app.
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

    val cultivos = listOf(
        Cultivo(1, "Lechuga", "Lechuga costina para ensaladas", 1, "10/09/2026", "25/09/2026", "05/11/2026", 2, EstadoCultivo.EN_CRECIMIENTO, "Crece bien, vigilar caracoles"),
        Cultivo(2, "Acelga", "Acelga verde", 1, "12/09/2026", "", "20/11/2026", 3, EstadoCultivo.EN_CRECIMIENTO),
        Cultivo(3, "Tomate", "Almácigos de tomate cherry", 2, "01/09/2026", "", "15/12/2026", 2, EstadoCultivo.SEMBRADO),
        Cultivo(4, "Albahaca", "Albahaca para el taller de cocina", 3, "15/09/2026", "", "30/10/2026", 2, EstadoCultivo.LISTO_PARA_COSECHAR),
        Cultivo(5, "Menta", "Menta y hierbabuena", 3, "05/09/2026", "", "25/10/2026", 3, EstadoCultivo.CON_PROBLEMAS, "Hojas amarillas en un sector"),
        Cultivo(6, "Zanahoria", "Zanahoria para 3° básico", 1, "20/10/2026", "", "20/01/2027", 4, EstadoCultivo.PLANIFICADO)
    )
}
