package cl.duoc.huertoescolar

import cl.duoc.huertoescolar.model.data.local.entity.Cultivo
import cl.duoc.huertoescolar.model.data.local.entity.EstadoCultivo
import cl.duoc.huertoescolar.model.data.local.entity.EstadoSector
import cl.duoc.huertoescolar.model.data.local.entity.Sector
import cl.duoc.huertoescolar.model.data.remote.aDto
import cl.duoc.huertoescolar.model.data.remote.aEntidad
import cl.duoc.huertoescolar.model.data.remote.dto.SectorDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// Pruebas de la conversión entre Room (local) y el microservicio (JSON).
// Si esto fallara, la app guardaría datos distintos a los del servidor.
class MapeosTest {

    private val sector = Sector(
        id = 0, codigo = "S-05", nombre = "Bancal Sur", descripcion = "Nuevo",
        ubicacion = "Patio", estado = EstadoSector.EN_USO, responsableId = 1,
        fechaCreacion = "20/11/2026", fechaActualizacion = "20/11/2026"
    )

    @Test
    fun sector_nuevo_se_envia_sin_id_para_que_lo_asigne_el_servidor() {
        val dto = sector.aDto()
        assertNull(dto.id)
        assertEquals("EN_USO", dto.estado)
    }

    @Test
    fun sector_ida_y_vuelta_mantiene_los_datos() {
        val deVuelta = sector.copy(id = 5).aDto().aEntidad()
        assertEquals(sector.copy(id = 5), deVuelta)
    }

    @Test
    fun estado_desconocido_del_servidor_queda_disponible() {
        val dto = SectorDto(1, "S-01", "Bancal", null, "Patio", "ESTADO_RARO", 1, null, null)
        val entidad = dto.aEntidad()
        assertEquals(EstadoSector.DISPONIBLE, entidad.estado)
        assertEquals("", entidad.descripcion)
    }

    @Test
    fun cultivo_ida_y_vuelta_mantiene_sector_y_riego() {
        val cultivo = Cultivo(
            id = 3, nombre = "Tomate", descripcion = "Cherry", sectorId = 2,
            fechaSiembra = "01/09/2026", fechaTrasplante = "", fechaCosecha = "15/12/2026",
            frecuenciaRiegoDias = 2, estado = EstadoCultivo.SEMBRADO, observaciones = ""
        )
        assertEquals(cultivo, cultivo.aDto().aEntidad())
    }
}
