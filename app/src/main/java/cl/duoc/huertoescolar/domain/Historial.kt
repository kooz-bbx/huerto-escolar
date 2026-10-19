package cl.duoc.huertoescolar.domain

import cl.duoc.huertoescolar.data.modelo.Evidencia
import cl.duoc.huertoescolar.data.modelo.Problema
import cl.duoc.huertoescolar.data.modelo.ResultadoValidacion
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.modelo.Tarea
import cl.duoc.huertoescolar.data.modelo.Usuario
import cl.duoc.huertoescolar.data.modelo.Validacion
import java.text.SimpleDateFormat
import java.util.Locale

enum class TipoEvento(val etiqueta: String) {
    TAREA_REALIZADA("Entregas"),
    VALIDACION("Validaciones"),
    PROBLEMA("Problemas")
}

data class EventoHistorial(
    val fechaHora: String,
    val tipo: TipoEvento,
    val titulo: String,
    val detalle: String,
    val fotoUri: String = ""
)

// Arma el historial cronológico (lo más nuevo arriba) a partir de las
// evidencias, las validaciones y los problemas guardados en Room
fun generarHistorial(
    tareas: List<Tarea>,
    evidencias: List<Evidencia>,
    validaciones: List<Validacion>,
    problemas: List<Problema>,
    usuarios: List<Usuario>,
    sectores: List<Sector>
): List<EventoHistorial> {
    fun nombre(id: Int?) = usuarios.find { it.id == id }?.nombre ?: "-"
    fun sector(id: Int) = sectores.find { it.id == id }?.nombre ?: "-"
    fun tarea(id: Int) = tareas.find { it.id == id }
    val eventos = mutableListOf<EventoHistorial>()

    for (evidencia in evidencias) {
        val t = tarea(evidencia.tareaId)
        eventos.add(
            EventoHistorial(
                fechaHora = evidencia.fechaHora,
                tipo = TipoEvento.TAREA_REALIZADA,
                titulo = "${nombre(evidencia.autorId)} entregó: ${t?.tipo?.etiqueta ?: "Tarea"} · ${t?.let { sector(it.sectorId) } ?: ""}",
                detalle = evidencia.comentario,
                fotoUri = evidencia.fotoUri
            )
        )
    }
    for (validacion in validaciones) {
        val t = tarea(validacion.tareaId)
        val resultado = if (validacion.resultado == ResultadoValidacion.APROBADA) "aprobó" else "rechazó"
        eventos.add(
            EventoHistorial(
                fechaHora = validacion.fechaHora,
                tipo = TipoEvento.VALIDACION,
                titulo = "${nombre(validacion.validadorId)} $resultado: ${t?.tipo?.etiqueta ?: "Tarea"}",
                detalle = validacion.comentario
            )
        )
    }
    for (problema in problemas) {
        eventos.add(
            EventoHistorial(
                fechaHora = problema.fechaReporte,
                tipo = TipoEvento.PROBLEMA,
                titulo = "${problema.tipo.etiqueta} · ${sector(problema.sectorId)} (${problema.estado.etiqueta})",
                detalle = "Reportado por ${nombre(problema.reportadoPorId)}: ${problema.descripcion}",
                fotoUri = problema.fotoUri
            )
        )
    }

    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return eventos.sortedByDescending {
        try { formato.parse(it.fechaHora)?.time ?: 0L } catch (e: Exception) { 0L }
    }
}
