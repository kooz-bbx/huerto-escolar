package cl.duoc.huertoescolar.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.huertoescolar.data.dao.CultivoDao
import cl.duoc.huertoescolar.data.dao.EvidenciaDao
import cl.duoc.huertoescolar.data.dao.LecturaSensorDao
import cl.duoc.huertoescolar.data.dao.ProblemaDao
import cl.duoc.huertoescolar.data.dao.SectorDao
import cl.duoc.huertoescolar.data.dao.TareaDao
import cl.duoc.huertoescolar.data.dao.UsuarioDao
import cl.duoc.huertoescolar.data.dao.ValidacionDao
import cl.duoc.huertoescolar.data.modelo.Cultivo
import cl.duoc.huertoescolar.data.modelo.Evidencia
import cl.duoc.huertoescolar.data.modelo.LecturaSensor
import cl.duoc.huertoescolar.data.modelo.Problema
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.modelo.Tarea
import cl.duoc.huertoescolar.data.modelo.Usuario
import cl.duoc.huertoescolar.data.modelo.Validacion

@Database(
    entities = [
        Usuario::class, Sector::class, Cultivo::class,
        Tarea::class, Evidencia::class, Validacion::class,
        Problema::class, LecturaSensor::class
    ],
    version = 3,   // v2: tareas, evidencias y validaciones · v3: problemas y lecturas simuladas
    exportSchema = false
)
abstract class HuertoDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun sectorDao(): SectorDao
    abstract fun cultivoDao(): CultivoDao
    abstract fun tareaDao(): TareaDao
    abstract fun evidenciaDao(): EvidenciaDao
    abstract fun validacionDao(): ValidacionDao
    abstract fun problemaDao(): ProblemaDao
    abstract fun lecturaSensorDao(): LecturaSensorDao

    companion object {
        // Una sola instancia de la base de datos para toda la app (patrón Singleton)
        @Volatile
        private var INSTANCIA: HuertoDatabase? = null

        fun obtener(context: Context): HuertoDatabase {
            return INSTANCIA ?: synchronized(this) {
                val nueva = Room.databaseBuilder(
                    context.applicationContext,
                    HuertoDatabase::class.java,
                    "huerto_db"
                )
                    // Si cambiamos la versión, borra y recrea la BD (son datos de prueba)
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCIA = nueva
                nueva
            }
        }
    }
}
