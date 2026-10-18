package cl.duoc.huertoescolar.domain

import cl.duoc.huertoescolar.data.modelo.EstadoTarea
import cl.duoc.huertoescolar.data.modelo.Tarea
import cl.duoc.huertoescolar.data.modelo.Usuario

// Reglas de negocio de las tareas (separadas de la UI).
// Flujo: PENDIENTE → EN_DESARROLLO → PENDIENTE_VALIDACION → VALIDADA / RECHAZADA
//        (si se rechaza, el estudiante la corrige y la vuelve a entregar)

// Todavía no se entrega (pendiente, en desarrollo o rechazada para corregir)
fun estaSinEntregar(tarea: Tarea): Boolean {
    return tarea.estado == EstadoTarea.PENDIENTE ||
            tarea.estado == EstadoTarea.EN_DESARROLLO ||
            tarea.estado == EstadoTarea.RECHAZADA
}

// Una tarea está atrasada si su fecha ya pasó y todavía no se entrega
fun estaAtrasada(tarea: Tarea): Boolean {
    val dias = diasHasta(tarea.fechaProgramada) ?: return false
    return estaSinEntregar(tarea) && dias < 0
}

// El estado que se muestra en pantalla (agrega "Atrasada" cuando corresponde)
fun estadoMostrado(tarea: Tarea): EstadoTarea {
    return if (estaAtrasada(tarea)) EstadoTarea.ATRASADA else tarea.estado
}

// ¿El estudiante puede trabajar en esta tarea? Solo si es suya o de su grupo
fun perteneceAlEstudiante(tarea: Tarea, estudiante: Usuario): Boolean {
    return tarea.responsableId == estudiante.id || tarea.grupoAsignado == estudiante.grupo
}

// Paso 1: el estudiante la inicia
fun puedeComenzarse(tarea: Tarea): Boolean {
    return tarea.estado == EstadoTarea.PENDIENTE
}

// Paso 2: solo se entrega si está en desarrollo o si fue rechazada (corregir y reenviar)
fun puedeEntregarse(tarea: Tarea): Boolean {
    return tarea.estado == EstadoTarea.EN_DESARROLLO || tarea.estado == EstadoTarea.RECHAZADA
}

// Paso 3: solo se valida o rechaza lo que el estudiante ya entregó
fun puedeValidarse(tarea: Tarea): Boolean {
    return tarea.estado == EstadoTarea.PENDIENTE_VALIDACION
}

// El docente solo edita o elimina tareas que nadie ha empezado
fun puedeEditarse(tarea: Tarea): Boolean {
    return tarea.estado == EstadoTarea.PENDIENTE
}

// Para ordenar por fecha (dd/MM/yyyy no se puede ordenar como texto)
fun ordenarPorFecha(tareas: List<Tarea>): List<Tarea> {
    return tareas.sortedBy { convertirFecha(it.fechaProgramada)?.time ?: Long.MAX_VALUE }
}
