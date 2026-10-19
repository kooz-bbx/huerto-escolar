package cl.duoc.huertoescolar.domain

import cl.duoc.huertoescolar.data.modelo.Cultivo
import cl.duoc.huertoescolar.data.modelo.EstadoCultivo
import cl.duoc.huertoescolar.data.modelo.EstadoLectura
import cl.duoc.huertoescolar.data.modelo.EstadoProblema
import cl.duoc.huertoescolar.data.modelo.EstadoTarea
import cl.duoc.huertoescolar.data.modelo.LecturaSensor
import cl.duoc.huertoescolar.data.modelo.Problema
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.modelo.Tarea
import cl.duoc.huertoescolar.data.modelo.TipoTarea

enum class TipoAlerta(val etiqueta: String) {
    PROXIMO_RIEGO("Próximos riegos"),
    RIEGO_ATRASADO("Riegos atrasados"),
    TAREA_ATRASADA("Tareas atrasadas"),
    PROXIMA_COSECHA("Próximas cosechas"),
    MANTENIMIENTO("Mantenimientos programados"),
    POR_VALIDAR("Pendientes de validación"),
    PROBLEMA_PENDIENTE("Problemas por revisar"),
    LECTURA_FUERA_RANGO("Lecturas simuladas fuera de rango")
}

data class Alerta(
    val tipo: TipoAlerta,
    val titulo: String,
    val detalle: String
)

// Las alertas son SOLO informativas: no activan riego ni ningún dispositivo
fun generarAlertas(
    tareas: List<Tarea>,
    cultivos: List<Cultivo>,
    problemas: List<Problema>,
    lecturas: List<LecturaSensor>,
    sectores: List<Sector>
): List<Alerta> {
    val alertas = mutableListOf<Alerta>()
    fun nombreSector(id: Int) = sectores.find { it.id == id }?.nombre ?: "Sector"

    for (tarea in ordenarPorFecha(tareas)) {
        val dias = diasHasta(tarea.fechaProgramada) ?: continue
        val pendiente = estaSinEntregar(tarea)
        val lugar = nombreSector(tarea.sectorId)

        when {
            tarea.tipo == TipoTarea.RIEGO && estaAtrasada(tarea) ->
                alertas.add(Alerta(TipoAlerta.RIEGO_ATRASADO, "Riego atrasado en $lugar", "Era para el ${tarea.fechaProgramada}"))
            tarea.tipo == TipoTarea.RIEGO && pendiente && dias in 0L..3L ->
                alertas.add(Alerta(TipoAlerta.PROXIMO_RIEGO, "Riego en $lugar", textoDias(dias)))
            estaAtrasada(tarea) ->
                alertas.add(Alerta(TipoAlerta.TAREA_ATRASADA, "${tarea.tipo.etiqueta} en $lugar", "Era para el ${tarea.fechaProgramada}"))
            tarea.tipo == TipoTarea.MANTENIMIENTO && pendiente ->
                alertas.add(Alerta(TipoAlerta.MANTENIMIENTO, "Mantenimiento en $lugar", textoDias(dias)))
        }
        if (tarea.estado == EstadoTarea.PENDIENTE_VALIDACION) {
            alertas.add(Alerta(TipoAlerta.POR_VALIDAR, "${tarea.tipo.etiqueta} en $lugar", "Programada para el ${tarea.fechaProgramada}"))
        }
    }

    // Cosechas: listas para cosechar o con fecha estimada en los próximos 14 días
    for (cultivo in cultivos) {
        if (cultivo.estado == EstadoCultivo.COSECHADO || cultivo.estado == EstadoCultivo.FINALIZADO) continue
        val dias = diasHasta(cultivo.fechaCosecha) ?: continue
        if (cultivo.estado == EstadoCultivo.LISTO_PARA_COSECHAR || dias in 0L..14L) {
            alertas.add(Alerta(TipoAlerta.PROXIMA_COSECHA, "${cultivo.nombre} (${nombreSector(cultivo.sectorId)})", "Cosecha estimada: ${cultivo.fechaCosecha}"))
        }
    }

    for (problema in problemas) {
        if (problema.estado == EstadoProblema.INFORMADO || problema.estado == EstadoProblema.EN_REVISION) {
            alertas.add(Alerta(TipoAlerta.PROBLEMA_PENDIENTE, "${problema.tipo.etiqueta} en ${nombreSector(problema.sectorId)}", "Reportado el ${problema.fechaReporte}"))
        }
    }

    // Solo la lectura más reciente de cada sector (la lista viene de la más nueva a la más vieja)
    val ultimasLecturas = lecturas.distinctBy { it.sectorId }
    for (lectura in ultimasLecturas) {
        if (lectura.estado == EstadoLectura.BAJO || lectura.estado == EstadoLectura.ALTO) {
            alertas.add(
                Alerta(
                    TipoAlerta.LECTURA_FUERA_RANGO,
                    "${nombreSector(lectura.sectorId)}: valor ${lectura.estado.etiqueta.lowercase()} (dato simulado)",
                    "Suelo ${lectura.humedadSuelo}% · ${lectura.temperatura} °C"
                )
            )
        }
    }
    return alertas
}

fun textoDias(dias: Long): String {
    return when (dias) {
        0L -> "Hoy"
        1L -> "Mañana"
        else -> "En $dias días"
    }
}
