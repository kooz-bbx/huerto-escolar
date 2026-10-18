# 🌱 Huerto Escolar

Aplicación móvil Android para la **gestión colaborativa del huerto escolar** de una escuela básica (caso DSY1105).
Permite organizar los sectores y cultivos del huerto, asignar tareas a estudiantes y llevar el registro de actividades, evidencias y problemas.

> Proyecto académico — Evaluación Parcial 2 · DSY1105 Desarrollo de Aplicaciones Móviles · Duoc UC.
> Todos los datos y usuarios son **ficticios**.

## 👥 Integrantes
- Benjamin Alegría
- Natalia Varela

## 🛠️ Tecnologías
- Kotlin + Jetpack Compose
- Material Design 3
- Navigation Compose
- Arquitectura MVVM (ViewModel + estado con `mutableStateOf`)
- Room (persistencia local) con KSP

## 📌 Estado del avance
- [x] Proyecto base y tema visual (Material 3)
- [x] Inicio de sesión con validación de campos
- [x] Navegación según perfil (administrador / estudiante)
- [x] Persistencia local con Room (usuarios, sectores y cultivos)
- [x] Sectores: listar, crear, modificar y cambiar estado (formulario validado)
- [x] Cultivos: registrar, asociar a sector, fechas, riego y estado (formulario validado)
- [x] Control de acceso: solo administradores crean o modifican sectores y cultivos
- [ ] Módulos de tareas, problemas, alertas, sensores e historial
- [ ] Recursos nativos (cámara y notificaciones)

## ▶️ Cómo ejecutar
1. Clonar el repositorio: `git clone <url-del-repo>`
2. Abrir la carpeta en **Android Studio** y esperar que Gradle sincronice.
3. Ejecutar en un emulador (Pixel 8, API 36) o en un celular Android 7.0 o superior.

### Cuentas de prueba (clave `1234`)
| Usuario | Perfil |
|---|---|
| `docente` | Administrador (docente) |
| `tallerista` | Administrador (tallerista) |
| `ana`, `benja`, `cami` | Estudiante |
| `lucas` | Estudiante con cuenta inactiva (para probar el error) |

## 🗂️ Estructura del proyecto (MVVM)
```
cl.duoc.huertoescolar
├── data/            → modelos (entidades Room), DAO, base de datos y repositorio
├── domain/          → validaciones y utilidades de fechas (lógica sin UI)
├── navegacion/      → rutas y NavHost
└── ui/              → pantallas (Screen) y sus ViewModel, separados por módulo
```

> Nota: si Android Studio reclama por la versión de KSP, se cambia en `gradle/libs.versions.toml` (línea `ksp = ...`).

