package cl.duoc.huertoescolar.data.repositorio

import cl.duoc.huertoescolar.data.DatosPrueba
import cl.duoc.huertoescolar.data.local.HuertoDatabase
import cl.duoc.huertoescolar.data.modelo.Cultivo
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.modelo.Usuario
import kotlinx.coroutines.flow.Flow

// El repositorio es el único que habla con la base de datos.
// Los ViewModel le piden datos a él y no a los DAO directamente.
class HuertoRepositorio(private val db: HuertoDatabase) {

    // ---------- Datos iniciales ----------
    suspend fun cargarDatosIniciales() {
        if (db.usuarioDao().contar() == 0) {
            db.usuarioDao().insertarTodos(DatosPrueba.usuarios)
            db.sectorDao().insertarTodos(DatosPrueba.sectores)
            db.cultivoDao().insertarTodos(DatosPrueba.cultivos)
        }
    }

    // ---------- Usuarios ----------
    suspend fun buscarUsuario(usuario: String, clave: String): Usuario? =
        db.usuarioDao().buscarPorCredenciales(usuario, clave)

    fun obtenerEstudiantes(): Flow<List<Usuario>> = db.usuarioDao().obtenerEstudiantes()

    fun obtenerUsuarios(): Flow<List<Usuario>> = db.usuarioDao().obtenerTodos()

    // ---------- Sectores ----------
    fun obtenerSectores(): Flow<List<Sector>> = db.sectorDao().obtenerTodos()

    suspend fun obtenerSector(id: Int): Sector? = db.sectorDao().obtenerPorId(id)

    suspend fun existeCodigoSector(codigo: String, idActual: Int): Boolean =
        db.sectorDao().contarPorCodigo(codigo, idActual) > 0

    suspend fun guardarSector(sector: Sector) {
        if (sector.id == 0) db.sectorDao().insertar(sector) else db.sectorDao().actualizar(sector)
    }

    // ---------- Cultivos ----------
    fun obtenerCultivos(): Flow<List<Cultivo>> = db.cultivoDao().obtenerTodos()

    suspend fun obtenerCultivo(id: Int): Cultivo? = db.cultivoDao().obtenerPorId(id)

    suspend fun guardarCultivo(cultivo: Cultivo) {
        if (cultivo.id == 0) db.cultivoDao().insertar(cultivo) else db.cultivoDao().actualizar(cultivo)
    }
}
