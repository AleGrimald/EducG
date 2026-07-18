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
   - `vista.componentes`: `TarjetaCurso`, `PanelDesplegable` (acordeón de lecciones), `DialogoPersonalizado`, `BotonRedondeado`, `FiltroCaracteres`
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
DAO llama a un stored procedure (sp_alta_*, sp_obtener_*, sp_modificar_*,
  sp_baja_*, sp_listar_*) vía CallableStatement sobre ConexionBD
  ↓
Resultado (objeto de modelo/boolean) vuelve hasta la Vista; se muestra
  DialogoPersonalizado de éxito/error
```

### Database Schema (Normalized to 4NF)

| Table | Purpose | Key Fields |
|-------|---------|-----------|
| **usuarios** | User accounts | id (PK), email (UNIQUE), password_hash (salt:sha256), nombre, apellido, dni (BIGINT, NOT NULL), telefono (VARCHAR(20), NOT NULL), activo (soft delete) |
| **cursos** | Course catalog | id, emoji, titulo, descripcion, duracion |
| **curso_contenidos** | Course lessons (title + body, resolves 1NF) | curso_id (FK), orden, topico, contenido (TEXT — texto completo de la lección) |
| **inscripciones** | User-course enrollments (N:M) | usuario_id (FK), curso_id (FK), fecha_inscripcion, activo |
| **test_resultados** | Test attempt scores | usuario_id (FK), curso_id (FK), test_nombre, puntaje, fecha |
| **test_preguntas** | Quiz question bank (10 per course) | curso_id (FK), enunciado, orden |
| **test_opciones** | Multiple-choice options per question | pregunta_id (FK), texto, es_correcta, orden |
| **test_respuestas_usuario** | Which option the user picked, per attempt | test_resultado_id (FK), pregunta_id (FK), opcion_elegida_id (FK) |

`dni` y `telefono` son **NOT NULL** — por eso `VentanaRegistro` los pide obligatoriamente
(validados por `Validador.esDniValido()`/`esTelefonoValido()`); no hay forma de
crear un usuario sin esos dos datos.

Initialize with:
```bash
mysql -u root -p < schema.sql
mysql -u root -p educg_db < stored_procedures_test.sql
mysql -u root -p educg_db < stored_procedures_test_v2.sql
```

`stored_procedures_test.sql` es un script **adicional** (no reemplaza a `schema.sql`):
agrega la columna `contenido` a `curso_contenidos`, las 3 tablas de test_preguntas/
test_opciones/test_respuestas_usuario, carga el contenido de las lecciones y las 10
preguntas por curso, y redefine `sp_alta_resultado_test` con un parámetro `OUT`
adicional (el id del resultado creado). Correlo siempre después de `schema.sql`. La
carga de preguntas/opciones **no es re-ejecutable** (tiene una `UNIQUE KEY` que
fallaría en un segundo run) — ver el comentario al inicio del archivo si hace falta
recargarlas.

`stored_procedures_test_v2.sql` es otro script adicional, a correr después de
`stored_procedures_test.sql`: reemplaza el contenido de las 36 lecciones por una
versión mucho más extensa ("que parezca un curso real"), agrega 10 preguntas más
por curso (orden 11-20, banco total = 20 preguntas/curso) y redefine
`sp_listar_preguntas_curso` para que elija **10 preguntas al azar de las 20**
disponibles en cada llamada (vía una tabla temporal con un `RAND()` fijado una sola
vez por llamada), en vez de devolver siempre las mismas 10. La sección de contenido
es re-ejecutable (`UPDATE`/`ON DUPLICATE KEY UPDATE`); la carga de preguntas 11-20
no lo es, por el mismo motivo que en `stored_procedures_test.sql`.

### Stored Procedures

Todo el acceso a datos pasa por stored procedures — **nunca** SQL embebido en Java.
Conveción de nombres: `sp_<accion>_<entidad>` (`alta` = crear, `modificar` = actualizar,
`baja` = baja lógica, `obtener` = traer un registro/valor puntual, `listar` = traer varias filas).

| Procedure | Usado por |
|-----------|-----------|
| `sp_alta_usuario` | `UsuarioDAOJdbc.altaUsuario()` — chequea email duplicado internamente (OUT 1/0) |
| `sp_obtener_usuario` | `UsuarioDAOJdbc.obtenerUsuario()` / `obtenerHashPassword()` |
| `sp_modificar_usuario` | `UsuarioDAOJdbc.modificarDatosPersonales()` |
| `sp_modificar_password_usuario` | `UsuarioDAOJdbc.modificarPassword()` |
| `sp_alta_inscripcion` | `InscripcionDAOJdbc.altaInscripcion()` — OUT 1=nueva/reactivada, 0=ya activa, -1=no existe |
| `sp_baja_inscripcion` | `InscripcionDAOJdbc.bajaInscripcion()` |
| `sp_obtener_inscripcion` | `InscripcionDAOJdbc.estaInscripto()` |
| `sp_listar_inscripciones_usuario` | `InscripcionDAOJdbc.listarPorUsuario()` |
| `sp_listar_resultados_test_usuario` | `ResultadoTestDAOJdbc.listarPorUsuario()` |
| `sp_obtener_estadisticas_usuario` | `ResultadoTestDAOJdbc.obtenerEstadisticas()` |
| `sp_listar_cursos_catalogo` | `CursoDAOJdbc.listarCatalogo()` |
| `sp_listar_contenidos_curso` | `CursoDAOJdbc.listarLecciones()` (privado) |
| `sp_listar_preguntas_curso` | `TestPreguntasDAOJdbc.listarPorCurso()` |
| `sp_alta_resultado_test` | `ResultadoTestDAOJdbc.registrarResultadoTest()` — OUT con el id del intento creado (usado para asociar las respuestas) |
| `sp_alta_respuesta_test` | `ResultadoTestDAOJdbc.registrarRespuesta()` |
| `sp_obtener_mejor_puntaje_curso` | `ResultadoTestDAOJdbc.obtenerMejorPuntaje()` — define si el curso aparece "Aprobado" |

Definidos en `stored_procedures.sql` + `schema.sql` (base) y en `stored_procedures_test.sql`
(catálogo DB-driven + test/certificado, ver más arriba). Si agregás o cambiás un
procedure, mantené los archivos correspondientes en sync.

Todos los procedures que necesitan resolver "qué usuario" reciben el **email**
directamente (nunca el `id` numérico) y lo resuelven internamente — el backend Java
no necesita hacer un round-trip previo para obtener el id.

## Build & Run

### Compile

```bash
# IntelliJ: Build > Build Project (Ctrl+F9) — output goes to out/production/EducG

# Command line (el código ahora vive en paquetes, hay que listar todos los .java):
find src -name "*.java" > sources.txt
javac -cp "lib/mysql-connector-j-8.3.0.jar" -d out/production/EducG @sources.txt
```

### Run

```bash
# IntelliJ: Right-click Main.java > Run (or Shift+F10)

# Command line:
java -cp "out/production/EducG;lib/mysql-connector-j-8.3.0.jar" Main
```

### Configure Database Connection

Create `.env` in the project root:
```
DB_HOST=localhost
DB_PORT=3306
DB_DATABASE=educg_db
DB_USER=root
DB_PASSWORD=<your_password>
```

`ConexionBD` (paquete `bd`) busca `.env` en: working directory → project root → system environment variables.

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

El catálogo ya NO está hardcodeado en Java: `servicio.ServicioCursos` (vía `dao.CursoDAO`) lee `cursos` + `curso_contenidos` de la base. Para agregar un curso:
1. `INSERT INTO cursos (emoji, titulo, descripcion, duracion) VALUES (...);`
2. `INSERT INTO curso_contenidos (curso_id, orden, topico, contenido) VALUES (...)` — una fila por lección (orden 0 = "Introducción", 1-N = clases), con el texto completo en `contenido`
3. Al menos 20 preguntas en `test_preguntas` + 4 opciones cada una en `test_opciones` (ver el patrón en `stored_procedures_test.sql`/`stored_procedures_test_v2.sql`) — `sp_listar_preguntas_curso` elige 10 al azar del banco disponible en cada llamada

`modelo.Curso.getTopicos()` deriva los títulos de lección desde `getLecciones()` (no se duplica el dato).

### Test final y certificado

Cada curso tiene un banco de 20 preguntas multiple-choice (`test_preguntas`/`test_opciones`); `sp_listar_preguntas_curso` elige 10 al azar en cada llamada, así el test no es siempre igual. `VentanaTest` las muestra con `JRadioButton` agrupados por pregunta; al finalizar, `ControladorTest`/`ServicioTest` corrigen contra `es_correcta`, guardan el intento en `test_resultados` (vía `sp_alta_resultado_test`, que ahora devuelve el id creado) y cada respuesta elegida en `test_respuestas_usuario`. Puntaje ≥ `ServicioTest.PUNTAJE_APROBACION` (60/100) ⇒ el curso queda "Aprobado" en `VentanaContenidoCurso`, habilitando el botón "Ver Certificado" (`VentanaCertificado`, una vista generada a partir de nombre/curso/fecha/puntaje — no se persiste como entidad separada).

### Navigation Flow

```
Main → VentanaLogin
  ├─ [Registrarse] → VentanaRegistro → vuelve a VentanaLogin al cerrar
  └─ [Login exitoso] → VentanaCursos
       ├─ [Mi Panel] → VentanaPanelUsuario (vuelve a VentanaCursos)
       └─ [Iniciar Curso / Ingresar] → VentanaContenidoCurso
            ├─ [Hacer Test] → VentanaTest → corrige y vuelve a VentanaContenidoCurso
            └─ [Ver Certificado] → VentanaCertificado (ventana secundaria, no reemplaza a la anterior)
```

Each window is a `JFrame` (via `VentanaBase`). Navigation uses `setVisible(true/false)` rather than hiding/showing a single frame; each `Ventana*` builds its own `Controlador*` internally (no contenedor de inyección de dependencias — no se justifica para el tamaño del proyecto).

## Common Tasks

### Add a user field (e.g., profile picture URL)

1. Add column to `usuarios` table in `schema.sql` **and** `stored_procedures.sql`/`schema.sql`'s procedure definitions (`sp_alta_usuario`, `sp_obtener_usuario`, `sp_modificar_usuario` as needed) — keep both SQL files in sync
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

## Security Notes

- Passwords: SHA-256 + random 16-byte salt, stored as `saltHex:hashHex` (`servicio.HasheadorPassword`)
- SQL injection: All DB access goes through stored procedures called via `CallableStatement` with bound parameters (no string-concatenated SQL anywhere in the DAO layer)
- Input validation: Client-side (`util.Validador` regex, called from `controlador`) + server-side (parameterized stored procedure calls)
- Password regex `^[a-zA-Z0-9]{6,20}$` restricts to alphanumeric; if symbols needed, expand regex and consider adding explicit SQL injection checks
- `.env` is in `.gitignore` — never commit database credentials

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

- **New developer checklist:** Run `mysql -u root -p < schema.sql`, then `mysql -u root -p educg_db < stored_procedures_test.sql`, populate `.env`, ensure MySQL is on localhost:3306
- **Hardcoded strings:** All UI text is in Spanish; no localization mechanism exists
- **No background threads:** Database calls are synchronous on the EDT; consider adding progress dialogs for slow queries in future
- **Test results:** `test_resultados` is now populated by `VentanaTest` (`sp_alta_resultado_test`); `test_respuestas_usuario` records each individual answer per attempt
- **UI Consistency:** When adding new features, **always use `EstiloUI` constants** for colors, fonts, and dimensions. Breaking this rule will require refactoring.
- **Stored procedures are mandatory:** the DAO layer (`dao.*Jdbc`) only calls stored procedures via `CallableStatement` — never add a `PreparedStatement` with inline SQL to a DAO. If you need a new query, add a procedure to both `stored_procedures.sql` and `schema.sql` (or to `stored_procedures_test.sql` if it belongs to the course-content/test feature), following the `sp_<accion>_<entidad>` naming convention.
- **`dni`/`telefono` are required:** added as `NOT NULL` columns on `usuarios`; `sp_alta_usuario` will fail without them. `VentanaRegistro` collects both.
- **Certificates are not persisted:** `VentanaCertificado` is generated on the fly from `nombre` + `curso` + `puntaje` + the current date — there is no `certificados` table. If a fixed issue date per approval is ever needed, it can be derived from the qualifying `test_resultados.fecha` row instead of adding new storage.