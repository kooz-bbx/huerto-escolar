# 🌱 Huerto Escolar · App Android

Aplicación móvil para la **gestión colaborativa del huerto escolar** de una escuela básica de San Bernardo (DSY1105 · Duoc UC).
El docente organiza sectores y cultivos, asigna tareas; el estudiante las realiza y entrega con foto; el docente las aprueba o rechaza.

> Proyecto académico · Evaluación Parcial 3/4 · Todos los datos son ficticios.
> Backend (microservicio Spring Boot): repositorio **backendHuerto**.

## 👥 Integrantes
- Benjamin Alegría
- Natalia Varela

## 🛠️ Tecnologías
| Tecnología | Uso |
|---|---|
| Kotlin + Jetpack Compose + Material 3 | Interfaz |
| MVVM (ViewModel + StateFlow) | Gestión de estado |
| Navigation Compose | Navegación con animaciones |
| Room | Almacenamiento local (SQLite) |
| Retrofit + Gson | Consumo del microservicio y de la API externa |
| Spring Boot + MySQL (XAMPP) | Microservicio de sectores y cultivos |
| Open-Meteo | API externa: clima real de San Bernardo |
| Cámara, galería, notificaciones | Recursos nativos |
| JUnit 4 | Pruebas unitarias |

## 🗂️ Estructura del proyecto (MVVM)
```
cl.duoc.huertoescolar
├── MainActivity.kt
├── model/                       → MODELO (datos y reglas)
│   ├── data/
│   │   ├── local/               → Room: HuertoDatabase
│   │   │   ├── entity/          → tablas: Usuario, Sector, Cultivo, Tarea, Evidencia, Validacion, Problema, LecturaSensor
│   │   │   └── dao/             → consultas SQL de cada tabla
│   │   └── remote/              → Retrofit: RetrofitClient, HuertoApiService, ClimaApiService, Mapeos
│   │       └── dto/             → objetos JSON (SectorDto, CultivoDto, ClimaRespuesta)
│   ├── repository/              → HuertoRepositorio: única puerta a los datos (Room + microservicio + API)
│   ├── rules/                   → reglas de negocio: Validaciones, ReglasTareas, Clima, Fechas, Alertas...
│   ├── DatosPrueba.kt           → datos ficticios iniciales
│   └── Sesion.kt                → usuario que inició sesión
├── viewmodel/                   → VIEWMODEL: un ViewModel por pantalla (estado + lógica)
├── ui/                          → VISTA
│   ├── screen/                  → pantallas por módulo (login, inicio, sectores, cultivos, tareas...)
│   ├── components/              → componentes reutilizables (CampoTexto, SelectorDesplegable, AvisoConexion...)
│   └── theme/                   → colores y tema Material 3
├── navigation/                  → Rutas y NavHost
└── utils/                       → cámara y notificaciones
```
**Flujo de datos:** `Pantalla → ViewModel → HuertoRepositorio → (Room | Microservicio | API clima)`

## 🔌 Conexión con el microservicio
- Sectores y cultivos se guardan en **Spring Boot + MySQL** y se copian en **Room**.
- Al abrir Sectores o Cultivos, la app sincroniza (`GET /api/sectores` y `GET /api/cultivos`) y guarda los datos con `@Upsert`.
- Crear/editar/eliminar llama al microservicio (`POST`, `PUT`, `DELETE`) y, si responde bien, actualiza Room.
- Si el backend está apagado, aparece el aviso **"Sin conexión con el servidor"** y la app sigue funcionando con los datos del teléfono.
- Dirección del backend: `RetrofitClient.URL_MICROSERVICIO = "http://10.0.2.2:8080/"` (emulador). En un celular real, usar la IP del PC.

## 🌤️ API externa
Pantalla **Sensores**: clima real de San Bernardo desde `https://api.open-meteo.com/v1/forecast`, con una recomendación de riego calculada en `model/rules/Clima.kt`.

## ▶️ Cómo ejecutar
1. Encender **MySQL** en XAMPP.
2. Ejecutar el backend (`HuertoApiApplication` en el repo backendHuerto). Queda en http://localhost:8080.
3. Abrir esta carpeta en Android Studio → esperar el Gradle sync → **Run ▶** en un emulador.

### Cuentas de prueba (clave `1234`)
| Usuario | Perfil |
|---|---|
| `docente`, `tallerista` | Administrador |
| `ana`, `benja`, `cami` | Estudiante |
| `lucas` | Estudiante con cuenta inactiva |

## 🧪 Pruebas unitarias
`app/src/test/java/cl/duoc/huertoescolar/` → clic derecho en la carpeta → **Run 'Tests in ...'** (19 pruebas).
| Archivo | Qué prueba |
|---|---|
| `ValidacionesTest` | Validaciones de formularios y flujo de estados de las tareas |
| `ClimaTest` | Recomendación de riego y conversión de la respuesta de la API del clima |
| `MapeosTest` | Conversión entre Room y el JSON del microservicio |

## 📦 APK firmado
**Build → Generate Signed App Bundle or APK → APK** → crear o elegir el keystore (`.jks`) → variante **release** → **Create**.
El APK queda en `app/release/app-release.apk`. El archivo `.jks` **no se sube** a GitHub (está en `.gitignore`).
