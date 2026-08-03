# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Educ G** is a Java Swing desktop application for course management and learning. Users register, log in, browse programming courses, enroll in them, and track progress through test results and statistics.

**Stack:** Java SE 11+, Swing, MySQL 8.x, IntelliJ IDEA project

## Architecture

### Diseño en capas: MVC

El código sigue el patrón **Modelo-Vista-Controlador**, con nombres de paquetes y clases en español (los textos de UI/SQL ya eran en español; el código ahora es consistente con eso). Paquetes bajo `src/`:

1. **`modelo`** — Entidades de datos (POJOs con encapsulamiento, sin lógica de negocio)
   - `Usuario`, `Curso`, `Leccion`, `Inscripcion`, `ResultadoTest`, `EstadisticasUsuario`, `PreguntaTest`, `OpcionTest`

2. **`vista`** — Ventanas Swing (JFrame)
   - `VentanaLogin`, `VentanaRegistro`, `VentanaCursos`, `VentanaPanelUsuario`, `VentanaContenidoCurso`, `VentanaTest`, `VentanaCertificado`
   - `VentanaBase` (abstracta): factoriza título/cierre/maximizado común — **herencia**
   - `vista.componentes`: `TarjetaCurso`, `BarraProgreso` (indicador visual para el test), `DialogoPersonalizado`, `BotonRedondeado`, `FiltroCaracteres`
   - `vista.estilo`: `EstiloUI` (paleta/fuentes/tamaños), `FabricaUI` (fábrica de componentes)
   - Construcción de UI en métodos `construirUI()`; los manejadores de eventos SOLO llaman al controlador — nunca SQL ni validación embebida en la vista

3. **`controlador`** — Orquestan los casos de uso por ventana, sin `javax.swing`
   - `ControladorLogin`, `ControladorRegistro`, `ControladorCursos`, `ControladorPanelUsuario`, `ControladorTest`
   - Validan con `Validador` (lanzan `IllegalArgumentException` con el mensaje a mostrar) y delegan al `servicio` correspondiente

4. **`servicio`** — Lógica de negocio por dominio
   - `ServicioAuth` (login/registro), `ServicioUsuario` (perfil/contraseña), `ServicioInscripcion` (inscripciones), `ServicioEstadisticas` (stats/tests), `ServicioCursos` (catálogo + contenido de lecciones, desde la DB), `ServicioTest` (preguntas, corrección, aprobación), `HasheadorPassword` (SHA-256 + salt)
   - Dependen de las interfaces de `dao`, nunca de las implementaciones concretas — **abstracción/polimorfismo**

5. **`dao`** — Acceso a datos vía **stored procedures** (nunca SQL embebido)
   - Interfaces: `UsuarioDAO`, `InscripcionDAO`, `ResultadoTestDAO`, `CursoDAO`, `TestPreguntasDAO`
   - Implementaciones: `UsuarioDAOJdbc`, `InscripcionDAOJdbc`, `ResultadoTestDAOJdbc`, `CursoDAOJdbc`, `TestPreguntasDAOJdbc` — cada método arma un `CallableStatement` (`{call sp_...(...)}`) contra un stored procedure de MySQL, nunca un `PreparedStatement` con SQL embebido

6. **`bd`** — `ConexionBD`: conexión JDBC; lee `.env` para credenciales

7. **`util`** — `Validador`: validación de inputs (regex email/password/nombre) + chequeo de riesgo de inyección

8. **`chatbot`** — Motor de IA intercambiable para la burbuja de chat flotante ("Robotito")
   - `MotorChatbot` (interfaz) — `enviarMensaje(historial, contextoSistema)`; `MotorClaude` y `MotorGemini` (implementaciones reales, HTTP directo a sus APIs vía `java.net.http.HttpClient` + `org.json`); `MotorGPT`/`MotorKimi` (stubs, listos para completar)
   - `MensajeChat` (POJO rol/contenido), `MotorChatbotException` (falla del motor, mensaje ya en español)
   - `ConfiguracionChatbot` (lee `CHATBOT_PROVEEDOR`/`CHATBOT_API_KEY`/`CHATBOT_MODELO` de `.env`), `FabricaMotorChatbot` (único punto de switch entre proveedores)
   - `servicio.ServicioChatbot` arma el contexto (cursos inscriptos, progreso, aprobación) desde `ServicioInscripcion`/`ServicioCursos`/`ServicioTest` y llama al motor configurado; `controlador.ControladorChatbot` valida el texto libre (longitud + caracteres de control, no la regex agresiva de `Validador.tieneRiesgoInyeccion()`)
   - Vista: `vista.componentes.BurbujaRobotito` (botón circular flotante) + `VentanaChatFlotante` (ventana de chat anclada cerca de la burbuja); enganchadas vía `VentanaBase.activarBurbujaChatbot(...)`, opt-in, llamado solo desde `VentanaCursos`/`VentanaContenidoCurso`/`VentanaPanelUsuario`
   - Ver "Chatbot flotante" más abajo para el detalle completo

### Data Flow

```
Evento de UI (click de botón, submit de formulario)
  ↓
Vista.manejarX() llama al Controlador correspondiente
  ↓
Controlador valida con Validador (lanza IllegalArgumentException si falla)
  ↓
Controlador delega al Servicio de ese dominio
  ↓
Servicio usa el DAO (interfaz) para consultar/actualizar la base
  ↓
DAO llama a un stored procedure (sp_alta_*/sp_crear_*, sp_obtener_*, sp_modificar_*,
  sp_baja_*/sp_desactivar_*/sp_activar_*/sp_eliminar_*, sp_listar_*/sp_buscar_*)
  vía CallableStatement sobre ConexionBD
  ↓
Resultado (objeto de modelo/boolean) vuelve hasta la Vista; se muestra
  DialogoPersonalizado de éxito/error
```

### Database Schema (Normalized to 4NF)

Todo el esquema (tablas + los 52 stored procedures + datos semilla) vive en un **único
archivo**: `educg_db.sql`, en la raíz del repo. Es un dump completo (`mysqldump
--routines --databases`) de la base real — no hay `schema.sql` ni scripts incrementales
separados; para reconstruir la base desde cero alcanza con correr ese único archivo.

| Table | Purpose | Key Fields |
|-------|---------|-----------|
| **usuarios** | User accounts | id (PK), email (UNIQUE), password_hash (salt:sha256), nombre, apellido, dni (BIGINT), telefono (VARCHAR(20)), es_admin (TINYINT(1), default 0), activo (soft delete), fecha_creacion, fecha_modificacion |
| **cursos** | Course catalog | id, emoji (INT, FK → imagenes.id_imagen, nullable — el ícono del curso), titulo (UNIQUE), descripcion, duracion, activo (soft delete), fecha_creacion, fecha_modificacion |
| **imagenes** | Binarios de imágenes (LONGBLOB): logo de la app, ícono de ventana, e íconos de tecnología seleccionables para un curso | id_imagen (PK), datos (LONGBLOB), clave (VARCHAR(50) UNIQUE, nullable — `logo_app`/`icono_ventana`/`icono_python`/`icono_java`/`icono_github`/`icono_react`/`icono_sql`/`icono_algoritmo`; los íconos de curso se resuelven por esta clave, nunca por id fijo) |
| **curso_contenidos** | Course lessons (title + body, resolves 1NF) | curso_id (FK), orden, topico, contenido (LONGTEXT — texto completo de la lección), ejercicio_propuesto (TEXT NULL — enunciado opcional del wizard "Crear Curso"), respuesta_esperada (VARCHAR(255) NULL — obligatoria si hay ejercicio, se usa para verificarlo), activo |
| **inscripciones** | User-course enrollments (N:M) | usuario_id (FK), curso_id (FK), fecha_inscripcion, activo, leccion_actual (TINYINT, progreso guardado en qué lección paró) |
| **test_resultados** | Test attempt scores | usuario_id (FK), curso_id (FK), puntaje, aprobado (TINYINT(1), calculado por el procedure al insertar), fecha |
| **test_preguntas** | Quiz question bank (20 per course, 10 elegidas al azar por intento) | curso_id (FK), enunciado, orden, activo |
| **test_opciones** | Multiple-choice options per question | pregunta_id (FK), texto, es_correcta, orden |
| **test_respuestas_usuario** | Which option the user picked, per attempt | test_resultado_id (FK), pregunta_id (FK), opcion_elegida_id (FK), es_correcta |
| **certificados** | Certificado emitido (uno por usuario+curso, `INSERT IGNORE` desde `sp_alta_resultado_test` al aprobar) | usuario_id (FK), curso_id (FK), test_resultado_id (FK), puntaje, fecha_emision |
| **auditoria_cambios** | Log de cambios para futuras auditorías del panel admin (no usada por el código Java todavía) | usuario_admin_id (FK, SET NULL), tabla_afectada, registro_id, accion, datos_anteriores/datos_nuevos (JSON), fecha, ip_origen |

No hay columna `rol`: el rol de administrador es el booleano `usuarios.es_admin`
(`UsuarioDAOJdbc.esAdmin()` lo lee de `sp_obtener_usuario`). `dni`/`telefono` siguen
siendo obligatorios a nivel de `Validador`/`VentanaRegistro`, pero a diferencia de una
versión anterior de este archivo, la base actual **no** tiene un `UNIQUE` en `dni`.

Inicializar con:
```bash
mysql -u root -p < educg_db.sql
```
Eso alcanza para tener el sistema completo funcionando: las 10 tablas, los 52
procedures y datos semilla (6 cursos, 36 lecciones, 120 preguntas/480 opciones, y una
cuenta admin — ver más abajo). Es un dump completo (`DROP TABLE IF EXISTS` +
`CREATE TABLE` por tabla), así que correrlo sobre una base existente la reemplaza
por completo; no es un script incremental.

### Stored Procedures

Todo el acceso a datos pasa por stored procedures — **nunca** SQL embebido en Java.
La convención de nombres mezcla dos generaciones: los procedures del flujo de alumno
más viejos usan `sp_<accion>_<entidad>` con `alta`/`baja`/`modificar`/`obtener`/`listar`;
los del panel de administrador (más nuevos) usan `crear`/`activar`/`desactivar`/
`eliminar`/`buscar`/`estadisticas`. Ambas conviven; no vale la pena unificarlas.

| Procedure | Usado por |
|-----------|-----------|
| `sp_alta_usuario` | `UsuarioDAOJdbc.altaUsuario()` — OUT p_resultado (1/0 duplicado) + OUT p_usuario_id |
| `sp_obtener_usuario` | `UsuarioDAOJdbc.obtenerUsuario()` / `esAdmin()` — filtra `activo=1` |
| `sp_obtener_hash_password` | `UsuarioDAOJdbc.obtenerHashPassword()` — filtra `activo=1` (una cuenta dada de baja no puede loguearse) |
| `sp_modificar_usuario` | `UsuarioDAOJdbc.modificarDatosPersonales()` — incluye email (a diferencia de la versión con la que se creó la base, que no lo actualizaba) |
| `sp_modificar_password_usuario` | `UsuarioDAOJdbc.modificarPassword()` |
| `sp_alta_inscripcion` | `InscripcionDAOJdbc.altaInscripcion()` — por `curso_id`, OUT 1=nueva/reactivada, 0=ya activa, -1=no existe |
| `sp_baja_inscripcion` | `InscripcionDAOJdbc.bajaInscripcion()` — por email + `curso_id` |
| `sp_obtener_inscripcion` | `InscripcionDAOJdbc.estaInscripto()` |
| `sp_listar_inscripciones_usuario` | `InscripcionDAOJdbc.listarPorUsuario()` |
| `sp_listar_resultados_test_usuario` | `ResultadoTestDAOJdbc.listarPorUsuario()` |
| `sp_obtener_estadisticas_usuario` | `ResultadoTestDAOJdbc.obtenerEstadisticas()` — `cursos_completados`/`tests_realizados`/`promedio_puntaje` |
| `sp_listar_cursos_catalogo` | `CursoDAOJdbc.listarCatalogo()` — filtra `activo=1`; `LEFT JOIN imagenes` para traer el ícono (`emoji_datos`/`emoji_clave`) junto con el curso |
| `sp_obtener_imagen_por_clave` | `ImagenDAOJdbc.obtenerPorClave()` — devuelve `id_imagen, datos` por `imagenes.clave`; usado por `FabricaUI` (logo/ícono de ventana) y por `AdminCursoDAOJdbc` para resolver la clave elegida en el selector de ícono de curso al id que esperan `sp_crear_curso`/`sp_modificar_curso` |
| `sp_crear_imagen` | `AdminCursoDAOJdbc` (interno, vía `resolverIdImagen`) — inserta un PNG subido desde `SelectorIconoCurso` como fila nueva en `imagenes` con una clave generada (`custom_<uuid>`), para que una edición posterior que no toque el ícono re-resuelva la misma fila en vez de duplicarla |
| `sp_listar_contenidos_curso` | `CursoDAOJdbc.listarLecciones()` (privado) — por `curso_id`, no por título |
| `sp_listar_preguntas_curso` | `TestPreguntasDAOJdbc.listarPorCurso()` — por `curso_id`; 10 al azar de 20, JOIN con `test_opciones` |
| `sp_alta_resultado_test` | `ResultadoTestDAOJdbc.registrarResultadoTest()` — por `curso_id`, calcula `aprobado` y emite certificado si corresponde |
| `sp_alta_respuesta_test` | `ResultadoTestDAOJdbc.registrarRespuesta()` |
| `sp_obtener_mejor_puntaje_curso` | `ResultadoTestDAOJdbc.obtenerMejorPuntaje()` — mejor puntaje histórico (apruebe o no), -1 si nunca lo rindió |
| `sp_obtener_progreso_inscripcion` / `sp_modificar_progreso_inscripcion` | `InscripcionDAOJdbc` — por `curso_id` |
| `sp_crear_curso` / `sp_modificar_curso` / `sp_activar_curso` / `sp_desactivar_curso` / `sp_eliminar_curso` | `AdminCursoDAOJdbc` (admin) — `p_emoji` es un `id_imagen` (INT, nullable), no bytes; `AdminCursoDAOJdbc` lo resuelve desde la clave del selector vía `sp_obtener_imagen_por_clave` antes de llamar a estos dos |
| `sp_listar_todos_cursos` / `sp_buscar_todos_cursos` | `AdminCursoDAOJdbc.listarTodos()`/`buscarPorNombreLike()` — incluyen inactivos, a diferencia de `sp_listar_cursos_catalogo`; mismo `LEFT JOIN imagenes` para `emoji_datos`/`emoji_clave` |
| `sp_crear_leccion` | `AdminCursoDAOJdbc` — alta de un ítem del Plan de Estudio (wizard), incluye `ejercicio_propuesto` |
| `sp_modificar_orden_leccion` | `AdminCursoDAOJdbc.reordenarPlan()` — reordenar por drag & drop en el panel de administrador: renumera `curso_contenidos.orden` de un ítem a la vez, uno por cada ítem de la lista, en una única transacción |
| `sp_crear_pregunta` / `sp_crear_opcion_pregunta` | `AdminCursoDAOJdbc` — alta de preguntas/opciones (paso 4 del wizard) |
| `sp_listar_todos_usuarios` | `AdminAlumnoDAOJdbc.listarTodos()` — solo `es_admin=0` (alumnos) |
| `sp_buscar_usuario_por_dni` | `AdminAlumnoDAOJdbc.buscarPorDni()` — coincidencia exacta, incluye inactivos |
| `sp_activar_usuario` / `sp_desactivar_usuario` / `sp_eliminar_usuario` | `AdminAlumnoDAOJdbc` (admin) |
| `sp_estadisticas_generales` / `sp_estadisticas_por_curso` / `sp_estadisticas_registros_mensuales` | `AdminEstadisticasDAOJdbc` |

Hay además un puñado de procedures del esquema original (`sp_buscar_usuarios`,
`sp_buscar_cursos`, `sp_listar_usuarios`, `sp_contar_*`, `sp_promedio_calificaciones`,
`sp_listar_certificados_*`, `sp_obtener_curso`, `sp_obtener_leccion`,
`sp_obtener_usuario_por_id`, `sp_modificar_leccion`, `sp_desactivar_leccion`) que hoy
**no llama ningún código Java** — quedaron del diseño original de la base y son
candidatos naturales si se agrega, por ejemplo, gestión de certificados emitidos o
edición de lecciones ya creadas.

Todos los procedures que necesitan resolver "qué usuario" reciben el **email**
directamente (nunca el `id` numérico) y lo resuelven internamente; los que necesitan
"qué curso" reciben el **id** (no el título — cambio respecto de versiones anteriores
de esta base, donde algunos procedures tomaban el título).

## Build & Run

### Compile

```bash
# IntelliJ: Build > Build Project (Ctrl+F9) — output goes to out/production/EducG

# Command line (el código ahora vive en paquetes, hay que listar todos los .java):
find src -name "*.java" > sources.txt
javac -cp "lib/mysql-connector-j-8.3.0.jar;lib/json-20240303.jar" -d out/production/EducG @sources.txt
```

### Run

```bash
# IntelliJ: Right-click Main.java > Run (or Shift+F10)

# Command line:
java -cp "out/production/EducG;lib/mysql-connector-j-8.3.0.jar;lib/json-20240303.jar" Main
```

`lib/json-20240303.jar` (org.json) se agregó para el chatbot — arma/parsea el JSON de las
llamadas HTTP a la API de Claude. Sin dependencias transitivas, igual que
`mysql-connector-j-8.3.0.jar`; agregalo también en IntelliJ (Project Structure > Libraries).

### Configure Database Connection

Create `.env` in the project root:
```
DB_HOST=localhost
DB_PORT=3306
DB_DATABASE=educg_db
DB_USER=root
DB_PASSWORD=<your_password>

# Chatbot flotante ("Robotito") — opcional, ver sección "Chatbot flotante"
# Proveedores con implementación real hoy: claude | gemini (gpt/kimi son stubs)
CHATBOT_PROVEEDOR=gemini
CHATBOT_API_KEY=<tu_api_key_de_gemini_o_claude>
CHATBOT_MODELO=gemini-flash-latest
```

`ConexionBD` (paquete `bd`) y `chatbot.ConfiguracionChatbot` comparten el mismo loader
(`bd.CargadorEnv`), que busca `.env` en: working directory → project root → system
environment variables.

### Testing

No automated test suite. Validation is manual:
- Test UI workflows: login → courses → enroll → profile → logout
- Test error handling: invalid email, weak password, duplicate registration, database disconnection
- All DB access goes through stored procedures via `CallableStatement`; client-side `Validador` is defense-in-depth

## Key Design Details

### Separación estricta Vista/Controlador/Servicio/DAO

Nunca accedas a la base de datos ni valides input directamente en una clase de `vista`. El flujo correcto siempre es Vista → Controlador → Servicio → DAO. Esto asegura:
- Manejo de errores consistente (el Controlador lanza `IllegalArgumentException` para errores de validación y propaga `SQLException` para errores de base de datos; la Vista solo traduce eso a un `DialogoPersonalizado`)
- Lógica de hashing de contraseñas en un solo lugar (`HasheadorPassword`)
- Los DAO son intercambiables detrás de su interfaz (útil para tests o para cambiar de motor de persistencia)

Ejemplo:
```java
// La Vista le pide el dato al Controlador
Usuario usuario = controlador.obtenerDatosUsuario(email);

// El Controlador valida y delega al Servicio
public void actualizarDatosPersonales(String email, String nombre, String apellido) throws SQLException {
    if (!Validador.esNombreValido(nombre)) throw new IllegalArgumentException("...");
    servicioUsuario.actualizarDatosPersonales(email, nombre, apellido);
}
```

### Style Centralization: FabricaUI + EstiloUI

All Swing components created via `FabricaUI` static factories (which read colors/fonts from `EstiloUI`, the single source of truth for the palette). Do not construct `JButton`, `JTextField`, etc. directly — use `FabricaUI.crearBotonPrimario()`, `crearCampo()`, etc., or `BotonRedondeado` directly for custom accent colors (e.g. `DialogoPersonalizado`'s per-type button).

### Course Catalog & Content (DB-driven)

El catálogo ya NO está hardcodeado en Java: `servicio.ServicioCursos` (vía `dao.CursoDAO`) lee `cursos` + `curso_contenidos` de la base, y `modelo.Curso` ahora lleva su `id` (además de emoji/título/descripción/duración/lecciones) porque el resto de los procedures (contenidos, preguntas, inscripciones, resultados) identifican al curso por `id`, no por título — solo `ServicioCursos.buscarPorTitulo()` sigue resolviendo por título, filtrando en Java sobre `listarCatalogo()`. Para agregar un curso a mano (fuera del wizard admin):
1. `INSERT INTO cursos (emoji, titulo, descripcion, duracion) VALUES ((SELECT id_imagen FROM imagenes WHERE clave='icono_python'), ...);` — `emoji` es la FK a `imagenes.id_imagen` (NULL = sin ícono), no un valor literal
2. `INSERT INTO curso_contenidos (curso_id, orden, topico, contenido, ejercicio_propuesto) VALUES (...)` — una fila por lección (orden 0 = "Introducción", 1-N = clases), con el texto completo en `contenido`
3. Al menos 20 preguntas en `test_preguntas` + 4 opciones cada una en `test_opciones` — `sp_listar_preguntas_curso` elige 10 al azar del banco disponible en cada llamada

`modelo.Curso.getTopicos()` deriva los títulos de lección desde `getLecciones()` (no se duplica el dato). Desde el panel de administrador, todo esto se hace vía el wizard "Crear Curso" (`vista.admin.VentanaCrearCurso`) en vez de SQL a mano — ver "Panel de Administrador" más abajo.

### Circuito de clases con ejercicio propuesto

`VentanaContenidoCurso` no pagina solo lecciones: arma internamente una lista de **pasos**
(`Paso`, clase privada con `leccionIndex` + `esEjercicio`) intercalando, después de cada
lección que tenga `ejercicio_propuesto` no vacío (`Leccion.tieneEjercicio()`), un paso extra
de ejercicio para esa misma lección. El botón "Siguiente" avanza un paso a la vez; si el paso
actual es un ejercicio sin resolver (`ejerciciosResueltos`, un `Set<Integer>` en memoria, vive
solo mientras la ventana está abierta), el botón queda **deshabilitado** hasta que el alumno
escribe la respuesta correcta en el campo de texto y hace clic en "Verificar"
(`ControladorCursos.verificarRespuestaEjercicio()`, comparación case-insensitive e
ignorando espacios extra contra `Leccion.getRespuestaEsperada()`, sin acceso a la base — es
lógica pura). Si una lección no tiene ejercicio, el paso de ejercicio simplemente no existe en
la lista y "Siguiente" pasa directo a la próxima lección. El último paso (sea lección o
ejercicio) muestra "Hacer Test" en vez de "Siguiente →".

**Progreso persistido:** `inscripciones.leccion_actual` sigue significando índice de *lección*
(0-based), no de paso — al guardar progreso siempre se persiste `pasos.get(pasoActual)
.leccionIndex`, así que estar viendo el ejercicio de la lección 2 también guarda "lección 2"
(todavía no se pasó de ahí). Al reabrir el curso, `cargarProgreso()` siempre reanuda en el
paso de **lección** (nunca a mitad de un ejercicio de una sesión anterior) — si esa lección
tenía un ejercicio sin resolver, hay que volver a resolverlo. Esto también mantiene sin
cambios el significado que ya asumía `ServicioChatbot` al armar el contexto ("lección X de Y").

Para cargar un ejercicio a mano: `curso_contenidos.ejercicio_propuesto` (enunciado) y
`respuesta_esperada` (obligatoria si hay enunciado — sin ella, el alumno nunca podría
avanzar). El wizard admin (`VentanaCrearCurso`, paso 3) exige ambos campos juntos
(`ControladorCrearCurso.validarPaso3()`); si se borra el ejercicio, la respuesta esperada se
descarta también aunque haya quedado texto tipeado.

### Test final y certificado

Cada curso tiene un banco de 20 preguntas multiple-choice (`test_preguntas`/`test_opciones`); `sp_listar_preguntas_curso` elige 10 al azar en cada llamada, así el test no es siempre igual. `VentanaTest` las muestra con `JRadioButton` agrupados por pregunta; al finalizar, `ControladorTest`/`ServicioTest` corrigen contra `es_correcta`, guardan el intento en `test_resultados` (vía `sp_alta_resultado_test`, que calcula `aprobado` internamente — puntaje ≥ 60 — y devuelve el id creado) y cada respuesta elegida en `test_respuestas_usuario`. `ServicioTest.PUNTAJE_APROBACION` (60/100, del lado Java) tiene que mantenerse en sync con el `60` hardcodeado dentro de `sp_alta_resultado_test`/`sp_estadisticas_por_curso` — SQL no puede leer la constante de Java. Puntaje ≥ 60 ⇒ el curso queda "Aprobado" en `VentanaContenidoCurso`, habilitando el botón "Ver Certificado" (`VentanaCertificado`, una vista generada a partir de nombre/curso/fecha/puntaje).

`sp_alta_resultado_test` también hace `INSERT IGNORE INTO certificados (...)` cuando el intento aprueba — a diferencia de una versión anterior de este archivo, los certificados **sí se persisten** (tabla `certificados`, `UNIQUE(usuario_id, curso_id)` así solo queda registrado el primero); `VentanaCertificado` sigue sin leer esa tabla, la sigue generando on-the-fly, pero el dato ya existe en la base para quien quiera, por ejemplo, un futuro listado de "certificados emitidos" (`sp_listar_certificados_emitidos`/`sp_listar_certificados_usuario`, ya definidos y sin usar todavía).

### Chatbot flotante ("Robotito")

Botón circular flotante (`vista.componentes.BurbujaRobotito`) visible sobre `VentanaCursos`,
`VentanaContenidoCurso` y `VentanaPanelUsuario` (nunca en Login/Registro/Test/Certificado),
enganchado vía `VentanaBase.activarBurbujaChatbot(email[, cursoTitulo])` — opt-in, una línea
al final del constructor de cada ventana que lo quiera. Al hacer clic abre
`vista.componentes.VentanaChatFlotante`, anclada abajo a la derecha (no centrada), donde el
usuario puede preguntar sobre sus cursos.

`servicio.ServicioChatbot` arma el contexto en cada mensaje leyendo `ServicioInscripcion`
(cursos inscriptos + progreso), `ServicioCursos` (título/lecciones) y `ServicioTest`
(mejor puntaje/aprobación) — el bot solo conoce lo que estos servicios ya exponen, no accede
a la base directamente. `controlador.ControladorChatbot` valida el texto libre (no vacío,
máx. 2000 caracteres, sin caracteres de control) — deliberadamente **no** usa
`Validador.tieneRiesgoInyeccion()`, que bloquearía puntuación normal de una pregunta en
español.

**Motor intercambiable:** `chatbot.FabricaMotorChatbot.obtenerMotor()` es el único punto de
switch entre proveedores, según `CHATBOT_PROVEEDOR` en `.env` (`claude` | `gemini` | `gpt` |
`kimi`). `chatbot.MotorClaude` (Claude Messages API) y `chatbot.MotorGemini` (Google
Generative Language API, modelo por defecto `gemini-flash-latest` — alias que Google mantiene
apuntando al Flash vigente; los nombres de modelo fijos como `gemini-2.5-flash` van quedando
sin disponibilidad para cuentas nuevas con el tiempo, mejor usar el alias) llaman a una API
real, ambos vía `java.net.http.HttpClient` + `org.json`;
`MotorGPT`/`MotorKimi` siguen siendo stubs que implementan la misma interfaz `MotorChatbot`
y devuelven "todavía no implementado". Agregar un proveedor nuevo = completar el cuerpo de
su `Motor*` existente, sin tocar `ServicioChatbot`, `ControladorChatbot` ni la vista. Cambiar
de proveedor = cambiar `CHATBOT_PROVEEDOR` (+ su API key) en `.env` y reiniciar la app — es
una lectura estática, igual que las credenciales de DB en `ConexionBD`.

Nota sobre `MensajeChat`: internamente los roles son `"user"`/`"assistant"` (convención de
Claude); `MotorGemini` los traduce a `"user"`/`"model"` (convención de Gemini) al armar el
request — el resto del código (`ServicioChatbot`, `ControladorChatbot`) es agnóstico al
proveedor y no necesita saber de esta diferencia.

`VentanaChatFlotante` usa `SwingWorker` para la llamada al motor (única excepción a "sin
hilos en segundo plano" del resto de la app — necesaria porque acá hay una llamada HTTP
real a un servicio externo, y bloquear el EDT congelaría la ventana).

### Panel de Administrador

El rol de administrador es el booleano `usuarios.es_admin` (no una columna `rol`).
`VentanaLogin.manejarLogin()` llama a `ControladorLogin.esAdmin(email)` (→ `ServicioAuth`
→ `UsuarioDAO.esAdmin` → `sp_obtener_usuario`, leyendo la columna `es_admin`) **después**
de un login exitoso y bifurca a `vista.admin.VentanaAdminAlumnos` o a `VentanaCursos` según
el resultado — no hay pantalla "hub" intermedia, el admin entra directo al primer submódulo
(Alumnos). No hay pantalla de login separada; `educg_db.sql` ya trae una cuenta admin
sembrada (`admin@educg.com`) para arrancar.

Los tres submódulos (`VentanaAdminAlumnos`/`VentanaAdminCursos`/`VentanaAdminEstadisticas`)
comparten una barra de pestañas en el encabezado (Alumnos/Cursos/Estadísticas) para saltar
entre ellos, igual patrón que las pestañas del lado alumno (`VentanaMisDatos`/`VentanaMisCursos`/
`VentanaMisEstadisticas`) — no hay botón "Volver" en ninguno de los dos lados.

Todo el código del panel vive en paquetes propios que siguen las mismas capas Vista→Controlador→
Servicio→DAO→stored procedures que el resto de la app (`vista.admin`, más `Admin*` en `dao`/
`servicio`/`controlador`, y `AlumnoAdmin`/`CursoAdmin`/`ItemPlanEstudio`/`Estadisticas*` en
`modelo`) — no se reutilizan `Usuario`/`Curso`/`Leccion` porque esos modelos no tienen
id/estado, que el CRUD de administrador necesita.

**Alumnos** (`VentanaAdminAlumnos`): alta, búsqueda exacta por DNI (`sp_buscar_usuario_por_dni`,
incluye inactivos, excluye admins), listado completo activos+inactivos (`sp_listar_todos_usuarios`,
filtra `es_admin=0`) y, por fila, Modificar/Baja Lógica (o Reactivar)/Eliminar.
`sp_desactivar_usuario`/`sp_activar_usuario` togglean `activo` (que ya bloqueaba el login antes
de este panel, vía el filtro en `sp_obtener_usuario`/`sp_obtener_hash_password`);
`sp_eliminar_usuario` borra en cascada (inscripciones, resultados de tests, certificados).

**Cursos** (`VentanaAdminCursos`): alta solo vía el wizard "Crear Curso" (no hay alta rápida de
un curso sin plan de estudio), búsqueda por nombre con `LIKE` (`sp_buscar_todos_cursos`, incluye
inactivos — a diferencia de `sp_buscar_cursos`, que es del esquema original y filtra `activo=1`),
listado completo (`sp_listar_todos_cursos`) y, por fila, Modificar (`DialogoFormCurso`, solo
emoji/título/descripción/duración — no reabre el wizard de plan de estudio, usa `sp_modificar_curso`)/
Baja Lógica (o Reactivar, `sp_desactivar_curso`/`sp_activar_curso`)/Eliminar (`sp_eliminar_curso`).
`sp_listar_cursos_catalogo` (el que usa `VentanaCursos` del lado alumno) filtra `activo = 1`:
dar de baja un curso lo oculta del catálogo y, como `ServicioCursos.buscarPorTitulo` reutiliza
esa misma consulta, también corta el acceso al contenido para alumnos ya inscriptos.

**Wizard "Crear Curso"** (`VentanaCrearCurso`, estado en memoria en `ControladorCrearCurso`,
persistido recién al final): 4 pasos navegados con Atrás/Siguiente, sin `CardLayout` — cada
paso se reconstruye desde el estado del controlador al entrar:
1. Datos básicos (emoji/título/descripción/duración) + alta dinámica de ítems del Plan de
   Estudio (uno por tema/clase, sin contenido todavía).
2. Cada ítem en un acordeón (`vista.componentes.PanelDesplegable`, reutilizable) con un
   `JTextArea` para el contenido teórico — obligatorio para avanzar.
3. Mismos acordeones, `JTextArea` opcional para el enunciado del ejercicio propuesto + un
   campo de texto para la "respuesta esperada" (se puede dejar vacío el ejercicio por clase;
   pero si hay enunciado, la respuesta esperada es obligatoria — ver "Circuito de clases con
   ejercicio propuesto" más abajo, el alumno la necesita para poder avanzar).
4. Alta de preguntas multiple-choice una por una (enunciado + 2 a 6 opciones + radio button
   para marcar la correcta) contra una lista en memoria; "Guardar Curso" recién ahí persiste todo.

El guardado final (`ServicioAdminCursos.guardarCursoCompleto` → `AdminCursoDAOJdbc
.guardarCursoCompleto`) es la única excepción al patrón "una conexión por método" del resto
de los DAO: abre una única `Connection` con `setAutoCommit(false)`, hace el alta del curso
(`sp_crear_curso`; si el título ya existe, la `UNIQUE KEY` de `cursos.titulo` tira
`SQLIntegrityConstraintViolationException`, que el DAO atrapa y traduce a "devolver -1" en vez
de dejar propagar la excepción) + un `sp_crear_leccion` por ítem (incluye `ejercicio_propuesto`)
+ un `sp_crear_pregunta`/`sp_crear_opcion_pregunta` por pregunta/opción, y recién comitea al
final — si algo fallara a mitad de camino, hace `rollback()` y no queda un curso a medias.

**Estadísticas** (`VentanaAdminEstadisticas`, solo lectura): tarjetas KPI (alumnos activos/
inactivos, cursos activos, inscripciones activas, aprobaciones totales — `sp_estadisticas_generales`),
barras horizontales de inscriptos por curso y de altas de alumnos por mes (reutilizan
`vista.componentes.BarraProgreso`, el mismo componente del test) y una tabla de detalle por
curso (inscriptos/promedio/% de aprobación — `sp_estadisticas_por_curso`, `sp_estadisticas_registros_mensuales`).
"Aprobado" en estas métricas usa el mismo umbral que el resto de la app
(`ServicioTest.PUNTAJE_APROBACION`, hardcodeado como `>= 60` en los procedures ya que SQL no
puede leer esa constante de Java).

**Tabla con botones de acción**: tanto `VentanaAdminAlumnos` como `VentanaAdminCursos` usan
`vista.componentes.ColumnaAcciones` (renderer+editor de `JTable` reutilizable, patrón estándar
de Swing para botones dentro de una celda) en vez de duplicar esa lógica — cada fila define sus
acciones con una lista de `ColumnaAcciones.AccionBoton`, cuya etiqueta puede depender del estado
de esa fila (p. ej. "Baja Lógica" vs. "Reactivar" según `activo`).

### Navigation Flow

```
Main → VentanaLogin
  ├─ [Registrarse] → VentanaRegistro → vuelve a VentanaLogin al cerrar
  └─ [Login exitoso, rol='alumno'] → VentanaCursos
  │    ├─ [Mi Panel] → VentanaPanelUsuario (vuelve a VentanaCursos)
  │    └─ [Iniciar Curso / Ingresar] → VentanaContenidoCurso
  │         ├─ [Hacer Test] → VentanaTest → corrige y vuelve a VentanaContenidoCurso
  │         └─ [Ver Certificado] → VentanaCertificado (ventana secundaria, no reemplaza a la anterior)
  └─ [Login exitoso, rol='admin'] → vista.admin.VentanaAdminAlumnos (pestañas: Alumnos/Cursos/Estadísticas)
       ├─ [Alumnos] → alta/DialogoFormAlumno, baja, eliminar
       ├─ [Cursos] → VentanaAdminCursos
       │    ├─ [Modificar] → DialogoFormCurso
       │    └─ [+ Crear Curso] → VentanaCrearCurso (wizard de 4 pasos) → vuelve a VentanaAdminCursos
       └─ [Estadísticas] → VentanaAdminEstadisticas
```

Each window is a `JFrame` (via `VentanaBase`). Navigation uses `setVisible(true/false)` rather than hiding/showing a single frame; each `Ventana*` builds its own `Controlador*` internally (no contenedor de inyección de dependencias — no se justifica para el tamaño del proyecto).

## Common Tasks

### Add a user field (e.g., profile picture URL)

1. `ALTER TABLE usuarios ADD COLUMN ...` against the running database, update the relevant procedure definitions (`sp_alta_usuario`, `sp_obtener_usuario`, `sp_modificar_usuario` as needed), then re-export `educg_db.sql` so the single file reflects the change
2. Add getter/setter to `modelo.Usuario` and the corresponding method to `dao.UsuarioDAO`/`UsuarioDAOJdbc` (calls the updated stored procedure via `CallableStatement`)
3. Expose it via `servicio.ServicioUsuario` and `controlador.ControladorPanelUsuario`
4. Update `vista.VentanaPanelUsuario` UI to display/edit the field

### Debug authentication failures

1. Verify `.env` is in project root with correct credentials
2. Check MySQL is running: `mysql -u root -p -e "SELECT 1"`
3. In `VentanaLogin.manejarLogin()`, catch blocks show DB/validation errors
4. Check `HasheadorPassword.verificar()` — password hash format is `<saltHex>:<sha256Hex>`

### Modify UI look and feel

1. Adjust colors/fonts/sizes: edit constants in `vista.estilo.EstiloUI` (single source of truth)
2. Adjust component sizing/styling: edit `FabricaUI.crearTarjeta()`, `crearBotonPrimario()`, etc., or `BotonRedondeado`
3. No custom themes or LAF configuration; uses Nimbus set in `Main.java`

### Add a new AI provider to the chatbot

1. Fill in the corresponding stub in `chatbot` (`MotorGPT` or `MotorKimi`) — same shape as `MotorClaude`/`MotorGemini`: build the request, call the provider's HTTP API, parse the JSON reply, throw `MotorChatbotException` (message in Spanish) on failure
2. Add the provider's default model id to `ConfiguracionChatbot.modeloPorDefecto(...)` if it isn't already there
3. Nothing else changes — `FabricaMotorChatbot` already routes to it once `CHATBOT_PROVEEDOR` in `.env` matches the provider name; `ServicioChatbot`/`ControladorChatbot`/the vista layer are provider-agnostic

### Recover corrupted images in the DB

If the `imagenes` table gets corrupted (PNGs damaged or data loss), restore from `assets/`:

```bash
cd C:\Users\grima\Desktop\Ale\EducG
javac -cp "lib/mysql-connector-j-8.3.0.jar" util/CargarImagenes.java
java -cp ".;lib/mysql-connector-j-8.3.0.jar" util.CargarImagenes
```

**How to avoid:**
- Preset images (`icono_python`, `icono_java`, etc.) are stored in `assets/` — the app has a fallback to load them from there if DB fails (see `IconoCurso.java`)
- Only custom course images (user-uploaded) are stored in `imagenes` — presets should never be stored there
- If icons aren't loading, `IconoCurso.crearEtiqueta()` tries: BD bytes → assets fallback → initial placeholder
- Regular backups of `educg_db.sql` (via `mysqldump --routines --databases educg_db`) protect against data loss

## Security Notes

- Passwords: SHA-256 + random 16-byte salt, stored as `saltHex:hashHex` (`servicio.HasheadorPassword`)
- SQL injection: All DB access goes through stored procedures called via `CallableStatement` with bound parameters (no string-concatenated SQL anywhere in the DAO layer)
- Input validation: Client-side (`util.Validador` regex, called from `controlador`) + server-side (parameterized stored procedure calls)
- Password regex `^[a-zA-Z0-9]{6,20}$` restricts to alphanumeric; if symbols needed, expand regex and consider adding explicit SQL injection checks
- `.env` is in `.gitignore` — never commit database credentials, and this now also includes `CHATBOT_API_KEY`
- Chatbot free-text input is validated in `controlador.ControladorChatbot` with a length cap + control-character check — intentionally lighter than `Validador.tieneRiesgoInyeccion()`, which would reject normal punctuation in a chat question

## UI Style System

**CRITICAL:** All UI changes must use `vista.estilo.EstiloUI` class constants. Never hardcode colors, fonts, or dimensions. This ensures visual consistency across the entire application. (`FabricaUI` is the component factory built on top of `EstiloUI` — it no longer keeps its own separate palette.)

### Using EstiloUI

Always access styles through the `EstiloUI` class:

```java
// ✓ Correct - uses centralized style
JLabel label = new JLabel("Hello");
label.setFont(EstiloUI.FUENTE_ENCABEZADO);
label.setForeground(EstiloUI.TEXTO_PRIMARIO);

// ✗ Wrong - hardcoded color
label.setForeground(new Color(44, 62, 80));
```

### Color Palette

| Constant | Color | Usage |
|----------|-------|-------|
| `AZUL_OSCURO` | #142846 (Azul oscuro) | Panel izquierdo (login, registro) |
| `AZUL_CLARO` | #2980B9 (Azul claro) | Botones primarios, acciones |
| `MORADO_ACENTO` | #1E0550 (Morado) | **Fondo estándar de todas las ventanas** |
| `EXITO` | #27AE60 (Verde) | Diálogos exitosos; botón "Iniciar Curso" tras inscribirse |
| `ERROR` | #E74C3C (Rojo) | Diálogos de error |
| `INFO` | #2980B9 (Azul) | Diálogos informativos |
| `ADVERTENCIA` | #E6A23C (Ámbar) | Diálogos de confirmación (`TipoDialogo.CONFIRMACION`) |
| `TEXTO_PRIMARIO` | #2C3E50 (Gris oscuro) | Texto principal |
| `TEXTO_SECUNDARIO` | #7F8C8D (Gris medio) | Texto secundario |
| `FONDO_CAMPO` | #F8FAFC (Gris muy claro) | Fondo campos de entrada |

### Fonts

- `FUENTE_TITULO`: 48px bold (Segoe UI) — título "Educ G" grande (Login/Registro)
- `FUENTE_TITULO_COMPACTO`: 36px bold — título "Educ G" en encabezados (Cursos/Panel)
- `FUENTE_ENCABEZADO`: 28px bold — títulos de secciones
- `FUENTE_ETIQUETA`: 13px bold — etiquetas de campos
- `FUENTE_CUERPO`: 14px — texto del formulario
- `FUENTE_BOTON`: 14px bold — texto de botones

### Component Dimensions

- Buttons: `ALTO_BOTON` = 46px
- Text fields: `ALTO_CAMPO` = 42px
- Border radius (cards): `RADIO_BORDE_GRANDE` = 20px
- Button border radius: `RADIO_BORDE_MEDIANO` = 10px

### Window Background

Todas las ventanas del proyecto (**VentanaLogin**, **VentanaRegistro**, **VentanaCursos**, **VentanaPanelUsuario**) usan el mismo fondo estándar: `MORADO_ACENTO` (morado).

Para crear una nueva ventana con el fondo estándar:

```java
private void construirUI() {
    JPanel raiz = FabricaUI.crearFondoEstandar();  // ✓ Fondo estándar
    raiz.setLayout(new BorderLayout());
    setContentPane(raiz);
    // ... resto de la UI
}
```

**Nunca** hagas esto:
- No uses colores hardcodeados (`new Color(...)`)
- No uses `Color.WHITE` como fondo de la ventana principal

### Dialogs

Todos los diálogos (éxito, error, info, y confirmaciones sí/no) usan `vista.componentes.DialogoPersonalizado` — **no queda ningún `JOptionPane` en la app**:

```java
// ✓ Correct
DialogoPersonalizado.mostrarError(this, "Mensaje de error");
DialogoPersonalizado.mostrarExito(this, "Mensaje de éxito");
DialogoPersonalizado.mostrarConfirmacion(this, "Título", "¿Confirmás?", "Sí, continuar", () -> { /* acción */ });

// ✗ Wrong
JOptionPane.showMessageDialog(this, "Mensaje");
```

El ícono de cada diálogo (check/X/i/!) se **dibuja vectorialmente** (círculo de color + glifo con `Graphics2D`), no se renderiza con caracteres Unicode (✓✕ℹ) — esos símbolos no están garantizados en todas las fuentes del sistema y en algunos entornos no se veían. Ver `DialogoPersonalizado.IconoCirculo`.

Dialogs automatically animate in/out and success/info dialogs auto-close after 2 seconds (confirmaciones y errores no se autocierran).

## Notes for Onboarding

- **New developer checklist:** Run `mysql -u root -p < educg_db.sql` (single file — schema, all 52 procedures, and seed data), populate `.env`, ensure MySQL is on localhost:3306. `CHATBOT_API_KEY` is optional — without it the chatbot bubble still opens but shows a friendly error when you try to send a message. The dump already seeds an admin account (`admin@educg.com` — ask whoever ran the dump for the password, or update it directly: `UPDATE usuarios SET password_hash='<hash>' WHERE email='admin@educg.com';`, hash format is `HasheadorPassword`'s `saltHex:sha256Hex`).
- **Hardcoded strings:** All UI text is in Spanish; no localization mechanism exists
- **No background threads (except the chatbot):** Database calls are synchronous on the EDT; consider adding progress dialogs for slow queries in future. The one deliberate exception is `VentanaChatFlotante`, which uses `SwingWorker` for the HTTP call to the AI provider — that's a real external network call, not local MySQL, so blocking the EDT would freeze the window.
- **Test results:** `test_resultados` is now populated by `VentanaTest` (`sp_alta_resultado_test`, which also computes `aprobado` and emits a certificate row); `test_respuestas_usuario` records each individual answer per attempt.
- **UI Consistency:** When adding new features, **always use `EstiloUI` constants** for colors, fonts, and dimensions. Breaking this rule will require refactoring.
- **Stored procedures are mandatory:** the DAO layer (`dao.*Jdbc`) only calls stored procedures via `CallableStatement` — never add a `PreparedStatement` with inline SQL to a DAO. If you need a new query, add a `CREATE PROCEDURE` directly against the running database and re-export `educg_db.sql` (`mysqldump --routines --triggers --single-transaction --add-drop-table --databases educg_db > educg_db.sql`) so the single file stays the source of truth — there's no separate incremental-migration file to edit anymore.
- **`dni`/`telefono` are required:** at the app layer (`Validador`/`VentanaRegistro`); `sp_alta_usuario` will fail without them since they're `NOT NULL` columns, but unlike an earlier version of this schema, `dni` does **not** have a `UNIQUE` constraint in the database — duplicates aren't blocked at the DB level.
- **Certificates ARE persisted** (unlike an earlier version of this app): `sp_alta_resultado_test` does `INSERT IGNORE INTO certificados` when a test attempt approves (`UNIQUE(usuario_id, curso_id)` keeps only the first). `VentanaCertificado` still generates its view on the fly rather than reading that table, but the data exists for a future "certificados emitidos" screen (`sp_listar_certificados_emitidos` is already defined, unused by Java so far).
- **App logo/window icon now live in the `imagenes` table** (`FabricaUI.crearLogoEducG()`/`establecerIconoVentana()`, via `ImagenDAOJdbc`, keys `logo_app`/`icono_ventana`), not `assets/` — unlike before, `VentanaLogin` (the very first, pre-authentication window) now needs a live DB connection just to render its logo/icon. Both calls keep their original try/catch fallback (plain "Educ G" text label / default window icon), so a DB outage still degrades gracefully — just with the added latency of a failed connection attempt (JDBC driver default timeout) before falling back, instead of an instant local file-not-found.