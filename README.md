# 🌱 Huerto Escolar

Aplicación móvil Android para la **gestión colaborativa del huerto escolar** de una escuela básica de San Bernardo (caso DSY1105).
Permite organizar los sectores y cultivos del huerto, asignar tareas a estudiantes, validar el trabajo realizado y llevar un registro de evidencias, problemas y alertas.

> Proyecto académico · Evaluación Parcial 2 · DSY1105 Desarrollo de Aplicaciones Móviles · Duoc UC.
> Todos los datos, usuarios y fotos son **ficticios**. Las lecturas de sensores son **simuladas**.

## 👥 Integrantes
- Benjamin Alegría
- Natalia Varela

## 🛠️ Tecnologías
| Tecnología | Uso |
|---|---|
| Kotlin + Jetpack Compose | Interfaz declarativa |
| Material Design 3 | Tema verde propio, tarjetas, chips, botones flotantes |
| Navigation Compose | Navegación entre pantallas con transiciones animadas |
| MVVM (ViewModel + StateFlow) | UI → ViewModel → Repositorio → Room; estado observable con StateFlow |
| Room + KSP | Persistencia local (SQLite) con claves foráneas y transacciones |
| Coil + FileProvider | Fotos tomadas con la cámara o elegidas de la galería |
| NotificationCompat | Recordatorios como notificaciones del teléfono |
| JUnit 4 | Pruebas unitarias de validaciones y reglas |

## ✅ Funcionalidades implementadas

### Perfil administrador (docente / tallerista)
- Inicio de sesión con validación de campos y cuentas ficticias.
- **Sectores:** listar, ver detalle con sus cultivos, crear, modificar, cambiar estado y eliminar (si no tiene cultivos ni tareas).
- **Cultivos:** registrar, asociar a sector, fechas de siembra/trasplante/cosecha, frecuencia de riego, estado de desarrollo y eliminar.
- **Tareas:** crear, editar, eliminar y asignar a un estudiante o a un grupo, con sector, cultivo, fecha e instrucciones. Filtros por estado y por responsable.
- **Validación:** revisar cada entrega (foto y comentario) y aprobarla o rechazarla, con motivo obligatorio al rechazar. Queda registrado quién validó, cuándo y el resultado.
- **Problemas:** ver reportes, cambiar su estado (informado → en revisión → atendido → cerrado) y registrar el seguimiento.
- **Alertas:** próximos riegos, riegos y tareas atrasadas, próximas cosechas, mantenimientos, tareas por validar, problemas pendientes y lecturas fuera de rango. Se pueden enviar como notificación.
- **Sensores simulados:** humedad del suelo, temperatura y humedad ambiental, claramente marcados como "Simulación académica".
- **Historial** cronológico de tareas realizadas, validaciones y problemas, con fotos.

### Perfil estudiante
- Ver sus tareas (individuales y de su grupo) con fecha, sector, cultivo e instrucciones.
- Iniciar una tarea (**En desarrollo**) y entregarla con foto y comentario → queda **pendiente de validación**.
- Ver si su trabajo fue validado o rechazado y el comentario del docente. Si fue rechazada, puede repetirla.
- Reportar problemas con foto y ver el seguimiento.
- Recibir un recordatorio de sus tareas como notificación.

### Reglas de negocio y seguridad
- Control de acceso por perfil: el estudiante **no** puede crear ni modificar sectores, cultivos o tareas, ni validar su propio trabajo (pantallas protegidas con `SoloAdministrador`).
- El estudiante solo ve y trabaja en sus propias tareas o en las de su grupo.
- Las alertas son informativas: la app no riega ni activa ningún equipo.
- No se usa GPS, micrófono ni datos personales reales.

### Flujo de una tarea
`Pendiente → En desarrollo → Pendiente de validación → Validada / Rechazada` (si se rechaza, el estudiante la corrige y la vuelve a entregar).

## 🗄️ Modelo de datos (Room, normalizado)
| Entidad | Relaciones (claves foráneas) |
|---|---|
| `Usuario` | — |
| `Sector` | `responsableId → Usuario` |
| `Cultivo` | `sectorId → Sector` |
| `Tarea` | `sectorId → Sector`, `cultivoId → Cultivo`, `responsableId → Usuario`, `creadorId → Usuario` |
| `Evidencia` | `tareaId → Tarea`, `autorId → Usuario` |
| `Validacion` | `tareaId → Tarea`, `evidenciaId → Evidencia`, `validadorId → Usuario` |
| `Problema` | `sectorId → Sector`, `cultivoId → Cultivo`, `reportadoPorId → Usuario` |
| `LecturaSensor` | `sectorId → Sector` |

## 📱 Recursos nativos
1. **Cámara** (`ActivityResultContracts.TakePicture` + `FileProvider`) y **galería** (`PhotoPicker`) para las fotos de evidencia y de problemas. Las fotos se guardan en la carpeta interna de la app (`filesDir`) para que no se pierdan.
2. **Notificaciones locales** (con permiso `POST_NOTIFICATIONS` en Android 13+) para los recordatorios de riegos, atrasos, cosechas y tareas.

## ✨ Animaciones
- Transiciones entre pantallas (deslizar + fundido) en el `NavHost`.
- Logo del login con `fadeIn + scaleIn` y mensajes de error con `AnimatedVisibility`.
- Chips de estado con `animateColorAsState` y cambio de estado con `Crossfade`.
- Fotos que aparecen con `fadeIn + expandVertically`.
- Listas con `animateItem()` y cambio estudiante/grupo con `AnimatedContent`.
- Barras de sensores que se llenan con `animateFloatAsState`.

## 🗂️ Estructura del proyecto (MVVM)
```
cl.duoc.huertoescolar
├── data/
│   ├── modelo/        → entidades Room (Usuario, Sector, Cultivo, Tarea, Evidencia, Validacion, Problema, LecturaSensor)
│   ├── dao/           → consultas a la base de datos
│   ├── local/         → HuertoDatabase (Room)
│   ├── repositorio/   → HuertoRepositorio (única puerta a los datos)
│   ├── DatosPrueba.kt → datos ficticios iniciales
│   └── Sesion.kt      → usuario que inició sesión
├── domain/            → validaciones, fechas, reglas de tareas, alertas, historial y simulador (sin UI)
├── navegacion/        → rutas y NavHost
├── util/              → cámara y notificaciones
└── ui/                → pantallas (Screen) + ViewModel por módulo, componentes reutilizables y tema
```

## ▶️ Cómo ejecutar
1. Clonar el repositorio: `git clone <url-del-repo>`
2. Abrir la carpeta en **Android Studio** y esperar que Gradle sincronice.
3. Ejecutar en un emulador (Pixel 8, API 36) o en un celular con Android 7.0 o superior.
4. Pruebas unitarias: clic derecho en `app/src/test/.../ValidacionesTest.kt` → **Run**.

> Si Android Studio reclama por la versión de KSP, se cambia en `gradle/libs.versions.toml` (línea `ksp = ...`).

### Cuentas de prueba (clave `1234`)
| Usuario | Perfil |
|---|---|
| `docente` | Administrador (docente) |
| `tallerista` | Administrador (tallerista) |
| `ana`, `benja` | Estudiantes del Grupo Girasol |
| `cami` | Estudiante del Grupo Tomate |
| `lucas` | Estudiante con cuenta inactiva (para probar el error) |

### Flujo sugerido para la demo
1. Entrar como `ana` → abrir "Riego" → **Comenzar tarea** → sacar foto → comentar → **Marcar como realizada**.
2. Cerrar sesión, entrar como `docente` → **Tareas** → filtro "Por validar" → aprobar o rechazar con comentario.
3. Volver a entrar como `ana` y ver el resultado de la revisión.
4. Como `docente`: revisar **Alertas** (tocar la campana 🔔), **Sensores** (simular lectura) e **Historial**.
