package cl.duoc.huertoescolar.model.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.huertoescolar.model.data.local.dao.CultivoDao
import cl.duoc.huertoescolar.model.data.local.dao.EvidenciaDao
import cl.duoc.huertoescolar.model.data.local.dao.LecturaSensorDao
import cl.duoc.huertoescolar.model.data.local.dao.ProblemaDao
import cl.duoc.huertoescolar.model.data.local.dao.SectorDao
import cl.duoc.huertoescolar.model.data.local.dao.TareaDao
import cl.duoc.huertoescolar.model.data.local.dao.UsuarioDao
import cl.duoc.huertoescolar.model.data.local.dao.ValidacionDao
import cl.duoc.huertoescolar.model.data.local.entity.Cultivo
import cl.duoc.huertoescolar.model.data.local.entity.Evidencia
import cl.duoc.huertoescolar.model.data.local.entity.LecturaSensor
import cl.duoc.huertoescolar.model.data.local.entity.Problema
import cl.duoc.huertoescolar.model.data.local.entity.Sector
import cl.duoc.huertoescolar.model.data.local.entity.Tarea
import cl.duoc.huertoescolar.model.data.local.entity.Usuario
import cl.duoc.huertoescolar.model.data.local.entity.Validacion

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
