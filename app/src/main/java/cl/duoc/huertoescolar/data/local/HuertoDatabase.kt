package cl.duoc.huertoescolar.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.huertoescolar.data.dao.CultivoDao
import cl.duoc.huertoescolar.data.dao.SectorDao
import cl.duoc.huertoescolar.data.dao.UsuarioDao
import cl.duoc.huertoescolar.data.modelo.Cultivo
import cl.duoc.huertoescolar.data.modelo.Sector
import cl.duoc.huertoescolar.data.modelo.Usuario

@Database(
    entities = [Usuario::class, Sector::class, Cultivo::class],
    version = 1,
    exportSchema = false
)
abstract class HuertoDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun sectorDao(): SectorDao
    abstract fun cultivoDao(): CultivoDao

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
