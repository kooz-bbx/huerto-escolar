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
- Arquitectura MVVM: UI (Compose) → ViewModel (StateFlow) → Repositorio → Room
- Room (persistencia local) con KSP
- Coil (carga de imágenes) y FileProvider (cámara)

## 📌 Estado del avance
- [x] Proyecto base y tema visual (Material 3)
- [x] Inicio de sesión con validación de campos
- [x] Navegación según perfil (administrador / estudiante)
- [x] Persistencia local con Room (usuarios, sectores y cultivos)
- [x] Sectores: listar, ver detalle con sus cultivos, crear, modificar, cambiar estado y eliminar
- [x] Cultivos: registrar, asociar a sector, fechas, riego, estado y eliminar
- [x] Control de acceso: solo administradores crean o modifican sectores y cultivos
- [x] Tareas: crear, editar, eliminar y asignar a estudiante o grupo; filtros por estado y responsable
- [x] Flujo: Pendiente → En desarrollo → Pendiente de validación → Validada / Rechazada
- [x] Estudiante: inicia la tarea y la entrega con foto y comentario (tabla Evidencia)
- [x] Docente: aprueba o rechaza con motivo obligatorio (tabla Validacion: quién, cuándo y resultado)
- [x] Modelo relacional con claves foráneas: Usuario, Sector, Cultivo, Tarea, Evidencia, Validacion
- [x] Recurso nativo 1: cámara (TakePicture + FileProvider) y galería (PhotoPicker)
- [x] Animaciones: transiciones entre pantallas, chips de estado, foto y listas
- [ ] Problemas, alertas, sensores simulados e historial
- [ ] Recurso nativo 2: notificaciones locales

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

