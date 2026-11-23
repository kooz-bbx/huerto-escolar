package cl.duoc.huertoescolar.model.data.remote

import cl.duoc.huertoescolar.model.data.local.entity.Cultivo
import cl.duoc.huertoescolar.model.data.local.entity.EstadoCultivo
import cl.duoc.huertoescolar.model.data.local.entity.EstadoSector
import cl.duoc.huertoescolar.model.data.local.entity.Sector
import cl.duoc.huertoescolar.model.data.remote.dto.CultivoDto
import cl.duoc.huertoescolar.model.data.remote.dto.SectorDto

// Convierte entre las entidades de Room (local) y los DTO del microservicio (remoto)

fun Sector.aDto(): SectorDto = SectorDto(
    id = if (id == 0) null else id,
    codigo = codigo,
    nombre = nombre,
    descripcion = descripcion,
    ubicacion = ubicacion,
    estado = estado.name,
    responsableId = responsableId,
    fechaCreacion = fechaCreacion,
    fechaActualizacion = fechaActualizacion
)

fun SectorDto.aEntidad(): Sector = Sector(
    id = id ?: 0,
    codigo = codigo,
    nombre = nombre,
    descripcion = descripcion ?: "",
    ubicacion = ubicacion,
    estado = EstadoSector.entries.find { it.name == estado } ?: EstadoSector.DISPONIBLE,
    responsableId = responsableId,
    fechaCreacion = fechaCreacion ?: "",
    fechaActualizacion = fechaActualizacion ?: ""
)

fun Cultivo.aDto(): CultivoDto = CultivoDto(
    id = if (id == 0) null else id,
    nombre = nombre,
    descripcion = descripcion,
    sectorId = sectorId,
    fechaSiembra = fechaSiembra,
    fechaTrasplante = fechaTrasplante,
    fechaCosecha = fechaCosecha,
    frecuenciaRiegoDias = frecuenciaRiegoDias,
    estado = estado.name,
    observaciones = observaciones
)

fun CultivoDto.aEntidad(): Cultivo = Cultivo(
    id = id ?: 0,
    nombre = nombre,
    descripcion = descripcion ?: "",
    sectorId = sectorId,
    fechaSiembra = fechaSiembra,
    fechaTrasplante = fechaTrasplante ?: "",
    fechaCosecha = fechaCosecha,
    frecuenciaRiegoDias = frecuenciaRiegoDias,
    estado = EstadoCultivo.entries.find { it.name == estado } ?: EstadoCultivo.PLANIFICADO,
    observaciones = observaciones ?: ""
)
