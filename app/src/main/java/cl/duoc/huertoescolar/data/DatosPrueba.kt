package cl.duoc.huertoescolar.data

import cl.duoc.huertoescolar.data.modelo.Cultivo
import cl.duoc.huertoescolar.data.modelo.EstadoCuenta
import cl.duoc.huertoescolar.data.modelo.EstadoCultivo
import cl.duoc.huertoescolar.data.modelo.EstadoSector
import cl.duoc.huertoescolar.data.modelo.EstadoTarea
import cl.duoc.huertoescolar.data.modelo.Evidencia
import cl.duoc.huertoescolar.data.modelo.Perfil
import cl.duoc.huertoescolar.data.modelo.ResultadoValidacion
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.modelo.Tarea
import cl.duoc.huertoescolar.data.modelo.TipoTarea
import cl.duoc.huertoescolar.data.modelo.Usuario
import cl.duoc.huertoescolar.data.modelo.Validacion
import cl.duoc.huertoescolar.domain.fechaRelativa

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

    // Las fechas de las tareas son relativas a hoy (ver fechaRelativa)
    val tareas = listOf(
        Tarea(
            id = 1, tipo = TipoTarea.RIEGO, sectorId = 1, cultivoId = 1,
            instrucciones = "Regar las lechugas con la regadera chica, sin mojar las hojas.",
            fechaCreacion = fechaRelativa(-3), fechaProgramada = fechaRelativa(1),
            responsableId = 3, creadorId = 1
        ),
        Tarea(
            id = 2, tipo = TipoTarea.RETIRO_MALEZAS, sectorId = 1, cultivoId = 2,
            instrucciones = "Sacar las malezas alrededor de la acelga con la pala pequeña.",
            fechaCreacion = fechaRelativa(-6), fechaProgramada = fechaRelativa(-2),
            grupoAsignado = "Grupo Girasol", creadorId = 2
        ),
        Tarea(
            id = 3, tipo = TipoTarea.OBSERVACION, sectorId = 2, cultivoId = 3,
            instrucciones = "Observar los almácigos de tomate y contar cuántos brotaron.",
            fechaCreacion = fechaRelativa(-5), fechaProgramada = fechaRelativa(-1),
            responsableId = 3, creadorId = 1, estado = EstadoTarea.PENDIENTE_VALIDACION
        ),
        Tarea(
            id = 4, tipo = TipoTarea.COSECHA, sectorId = 3, cultivoId = 4,
            instrucciones = "Cosechar las hojas grandes de albahaca para el taller de cocina.",
            fechaCreacion = fechaRelativa(-8), fechaProgramada = fechaRelativa(-4),
            responsableId = 5, creadorId = 1, estado = EstadoTarea.VALIDADA
        ),
        Tarea(
            id = 5, tipo = TipoTarea.LIMPIEZA, sectorId = 4,
            instrucciones = "Ordenar las herramientas y barrer la zona de la compostera.",
            fechaCreacion = fechaRelativa(-4), fechaProgramada = fechaRelativa(2),
            responsableId = 4, creadorId = 2, estado = EstadoTarea.RECHAZADA
        ),
        Tarea(
            id = 6, tipo = TipoTarea.MANTENIMIENTO, sectorId = 3,
            instrucciones = "Revisar y reparar el borde de madera del jardín aromático.",
            fechaCreacion = fechaRelativa(-1), fechaProgramada = fechaRelativa(3),
            grupoAsignado = "Grupo Tomate", creadorId = 2
        ),
        Tarea(
            id = 7, tipo = TipoTarea.RIEGO, sectorId = 3, cultivoId = 5,
            instrucciones = "Regar la menta en la mañana, revisar si las hojas siguen amarillas.",
            fechaCreacion = fechaRelativa(0), fechaProgramada = fechaRelativa(0),
            responsableId = 5, creadorId = 1, estado = EstadoTarea.EN_DESARROLLO
        )
    )

    // Entregas de los estudiantes (evidencias)
    val evidencias = listOf(
        Evidencia(1, tareaId = 3, autorId = 3, fechaHora = fechaRelativa(-1) + " 11:30", comentario = "Brotaron 14 de 20 almácigos."),
        Evidencia(2, tareaId = 4, autorId = 5, fechaHora = fechaRelativa(-4) + " 10:15", comentario = "Cosechamos un canasto chico."),
        Evidencia(3, tareaId = 5, autorId = 4, fechaHora = fechaRelativa(-1) + " 12:00", comentario = "Listo.")
    )

    // Revisiones de los docentes (validaciones)
    val validaciones = listOf(
        Validacion(1, tareaId = 4, evidenciaId = 2, validadorId = 1, fechaHora = fechaRelativa(-3) + " 09:00",
            resultado = ResultadoValidacion.APROBADA, comentario = "¡Excelente trabajo!"),
        Validacion(2, tareaId = 5, evidenciaId = 3, validadorId = 2, fechaHora = fechaRelativa(0) + " 08:30",
            resultado = ResultadoValidacion.RECHAZADA, comentario = "Faltó guardar las palas en el estante. Repetir, por favor.")
    )
}
