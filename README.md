#  Huerto Escolar

Aplicación móvil Android para la **gestión colaborativa del huerto escolar** de una escuela básica (caso DSY1105).
Permite organizar los sectores y cultivos del huerto, asignar tareas a estudiantes y llevar el registro de actividades, evidencias y problemas.


> Todos los datos y usuarios son **ficticios**.

##  Integrantes
- Benjamin alegriaa
- _Natalia Varela_

## 🛠 Tecnologías
- Kotlin + Jetpack compose
- Material Design 3
- Navigation compose
- Arquitectura MVVM (Viewmodel + estado con `mutableStateOf`)

##  Estado del avance
- [x] Proyecto base y tema visual (Material 3)
- [x] Inicio de sesión con validación de campos
- [x] Navegación según perfil (administrador / estudiante)
- [x] Listado de sectores  
- [ ] Persistencia local con Room
- [ ] Módulos de cultivos, tareas, problemas, alertas, sensores e historial
- [ ] Recursos nativos (cámara y notificaciones)

##  Cómo ejecutar
1. Clonar el repositorio: `git clone <url-del-repo>`
2. Abrir la carpeta en **Android Studio** y esperar que Gradle sincronice.
3. Ejecutar en un emulador (Pixel 8, API 36) o en un celular Android 7.0 o superior.

### Cuentas de prueba (clave `1234`)
| Usuario | Perfil                                                |
|---|-------------------------------------------------------|
| `docente` | Administrador (docente)                               |
| `tallerista` | Administrador (tallerista)                            |
| `ana`, `benja`, `cami` | Estudiante                                            |
| `lucas` | Estudiante con cuenta inactiva (para probar el error) |
