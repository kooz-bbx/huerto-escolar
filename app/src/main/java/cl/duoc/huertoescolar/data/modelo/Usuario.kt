package cl.duoc.huertoescolar.data.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

// Perfiles de la app. El docente y el tallerista comparten el perfil ADMINISTRADOR
enum class Perfil(val etiqueta: String) {
    ADMINISTRADOR("Administrador"),
    ESTUDIANTE("Estudiante")
}

enum class EstadoCuenta(val etiqueta: String) {
    ACTIVA("Activa"),
    INACTIVA("Inactiva")
}

// Todos los usuarios son ficticios (regla del caso: nada de datos reales)
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val usuario: String,
    val clave: String,
    val perfil: Perfil,
    val cargo: String,          // "Docente", "Tallerista" o "Estudiante"
    val grupo: String,          // taller o grupo asignado
    val estadoCuenta: EstadoCuenta = EstadoCuenta.ACTIVA
)
