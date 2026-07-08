# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Educ G** is a Java Swing desktop application for course management and learning. Users register, log in, browse programming courses, enroll in them, and track progress through test results and statistics.

**Stack:** Java SE 11+, Swing, MySQL 8.x, IntelliJ IDEA project

## Architecture

### Layered Design

The codebase follows a three-layer pattern:

1. **UI Layer** — Swing JFrame windows
   - `LoginFrame`, `RegisterFrame`, `CoursesFrame`, `UserPanelFrame`
   - Custom component: `CourseCard` for course display
   - All UI construction happens in `buildUI()` methods; event handlers call service layer

2. **Service Layer** — `AuthService`
   - Centralized access to all database operations
   - Methods grouped by domain: auth, user management, enrollments, test results, statistics
   - Password hashing (SHA-256 + salt) isolated here

3. **Support Layer**
   - `UIFactory`: Centralized colors, fonts, styled components (buttons, fields, backgrounds)
   - `DatabaseConnection`: JDBC connection pooling; reads `.env` for credentials
   - `Validator`: Input validation (email, password, name regex patterns) + injection risk checks

### Data Flow

```
UI Event (button click, form submit)
  ↓
Frame event handler validates input via Validator
  ↓
Calls AuthService static method with validated data
  ↓
AuthService queries database via DatabaseConnection
  ↓
Result returned to Frame; UI updated (show success/error dialog)
```

### Database Schema (Normalized to 4NF)

| Table | Purpose | Key Fields |
|-------|---------|-----------|
| **usuarios** | User accounts | id (PK), email (UNIQUE), password_hash (salt:sha256), nombre, apellido, activo (soft delete) |
| **cursos** | Course catalog | id, emoji, titulo, descripcion, duracion |
| **curso_contenidos** | Course topics (resolves 1NF) | curso_id (FK), orden, topico |
| **inscripciones** | User-course enrollments (N:M) | usuario_id (FK), curso_id (FK), fecha_inscripcion, activo |
| **test_resultados** | Test scores | usuario_id (FK), curso_id (FK), test_nombre, puntaje, fecha |

Initialize with:
```bash
mysql -u root -p < schema.sql
```

## Build & Run

### Compile

```bash
# IntelliJ: Build > Build Project (Ctrl+F9) — output goes to out/production/EducG

# Command line:
javac -cp "lib/mysql-connector-j-8.3.0.jar" -d out/production/EducG src/*.java
```

### Run

```bash
# IntelliJ: Right-click Main.java > Run (or Shift+F10)

# Command line:
java -cp "out/production/EducG:lib/mysql-connector-j-8.3.0.jar" Main
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

`DatabaseConnection` searches for `.env` in: working directory → project root → system environment variables.

### Testing

No automated test suite. Validation is manual:
- Test UI workflows: login → courses → enroll → profile → logout
- Test error handling: invalid email, weak password, duplicate registration, database disconnection
- All SQL uses `PreparedStatement`; client-side `Validator` is defense-in-depth

## Key Design Details

### Single Responsibility: AuthService

All database access goes through `AuthService` static methods. Never call `DatabaseConnection.getConnection()` and run SQL elsewhere. This ensures:
- Consistent error handling
- Password hashing logic in one place
- Easy to audit query patterns
- Methods are grouped by domain (auth, user, enrollments, stats)

Example:
```java
// Frame requests data from service
String[] userData = AuthService.getUserData(email);

// Service handles the query, error handling, null checks
public static String[] getUserData(String email) throws SQLException {
    // SQL here, never in Frame
}
```

### Style Centralization: UIFactory

All Swing components created via static factories. Do not construct `JButton`, `JTextField`, etc. directly. This keeps styling consistent and makes rebranding easy (change font sizes, color palette in one place).

### Coordinates System: Course Catalog

Courses are hardcoded in `CoursesFrame.COURSES` as Java objects. In a future iteration, load from the `cursos` table. For now, to add a course:
```java
new Course("emoji", "Title", "Description", "Duration weeks", "Topic 1", "Topic 2", ...),
```

### Navigation Flow

```
Main → LoginFrame
  ├─ [Register] → RegisterFrame → back to LoginFrame on close
  └─ [Login success] → CoursesFrame
       └─ [User menu] → UserPanelFrame (stays on top, returns to CoursesFrame)
```

Each window is a `JFrame`. Navigation uses `setVisible(true/false)` rather than hiding/showing a single frame.

## Common Tasks

### Add a user field (e.g., profile picture URL)

1. Add column to `usuarios` table in `schema.sql`
2. Add getter/setter method to `AuthService`
3. Update `UserPanelFrame` UI to display/edit the field
4. Wire action listener to call `AuthService.update*()` method

### Debug authentication failures

1. Verify `.env` is in project root with correct credentials
2. Check MySQL is running: `mysql -u root -p -e "SELECT 1"`
3. In `LoginFrame.handleLogin()`, catch block shows DB errors
4. Check `AuthService.verifyPassword()` — password hash format is `<saltHex>:<sha256Hex>`

### Modify UI look and feel

1. Adjust colors: edit `COLOR_*` constants in `UIFactory`
2. Adjust fonts: edit `FONT_*` constants
3. Adjust component sizing/styling: edit `createCard()`, `createPrimaryButton()`, etc.
4. No custom themes or LAF configuration; uses Nimbus set in `Main.java`

## Security Notes

- Passwords: SHA-256 + random 16-byte salt, stored as `saltHex:hashHex`
- SQL injection: All queries use `PreparedStatement` with bound parameters
- Input validation: Client-side (Validator regex) + server-side (PreparedStatement)
- Password regex `^[a-zA-Z0-9]{6,20}$` restricts to alphanumeric; if symbols needed, expand regex and consider adding explicit SQL injection checks
- `.env` is in `.gitignore` — never commit database credentials

## UI Style System

**CRITICAL:** All UI changes must use `UIStyle` class constants. Never hardcode colors, fonts, or dimensions. This ensures visual consistency across the entire application.

### Using UIStyle

Always access styles through the `UIStyle` class:

```java
// ✓ Correct - uses centralized style
JLabel label = new JLabel("Hello");
label.setFont(UIStyle.FONT_HEADING);
label.setForeground(UIStyle.TEXT_PRIMARY);

// ✗ Wrong - hardcoded color
label.setForeground(new Color(44, 62, 80));
```

### Color Palette

| Constant | Color | Usage |
|----------|-------|-------|
| `PRIMARY_DARK` | #142846 (Azul oscuro) | Panel izquierdo (login, registro) |
| `PRIMARY_LIGHT` | #2980B9 (Azul claro) | Botones primarios, acciones |
| `PRIMARY_ACCENT` | #1E0550 (Morado) | **Fondo estándar de todas las ventanas** |
| `SUCCESS` | #27AE60 (Verde) | Diálogos exitosos, confirmaciones |
| `ERROR` | #E74C3C (Rojo) | Diálogos de error, advertencias |
| `INFO` | #2980B9 (Azul) | Diálogos informativos |
| `TEXT_PRIMARY` | #2C3E50 (Gris oscuro) | Texto principal |
| `TEXT_SECONDARY` | #7F8C8D (Gris medio) | Texto secundario |
| `BG_FIELD` | #F8FAFC (Gris muy claro) | Fondo campos de entrada |

### Fonts

- `FONT_TITLE`: 48px bold (Segoe UI) — títulos principales
- `FONT_HEADING`: 28px bold — títulos de secciones
- `FONT_LABEL`: 13px bold — etiquetas de campos
- `FONT_BODY`: 14px — texto del formulario
- `FONT_BUTTON`: 14px bold — texto de botones

### Component Dimensions

- Buttons: `BUTTON_HEIGHT` = 46px
- Text fields: `FIELD_HEIGHT` = 42px
- Border radius (cards): `BORDER_RADIUS_LARGE` = 20px
- Button border radius: `BORDER_RADIUS_MEDIUM` = 10px

### Window Background

Todas las ventanas del proyecto (**LoginFrame**, **RegisterFrame**, **CoursesFrame**, **UserPanelFrame**) usan el mismo fondo estándar: `PRIMARY_ACCENT` (morado).

Para crear una nueva ventana con el fondo estándar:

```java
private void buildUI() {
    JPanel root = UIFactory.createDefaultBackground();  // ✓ Fondo estándar
    root.setLayout(new BorderLayout());
    setContentPane(root);
    // ... resto de la UI
}
```

**Nunca** hagas esto:
- No uses colores hardcodeados (`new Color(...)`)
- No uses `createGradientBackground()` (deprecated)
- No uses `Color.WHITE` como fondo de la ventana principal

### Dialogs

Error, success, and info dialogs use `CustomDialog` class — **never use `JOptionPane`**:

```java
// ✓ Correct
CustomDialog.showError(this, "Error message");
CustomDialog.showSuccess(this, "Success message");

// ✗ Wrong
JOptionPane.showMessageDialog(this, "Message");
```

Dialogs automatically animate in/out and success dialogs auto-close after 2 seconds.

## Notes for Onboarding

- **New developer checklist:** Run `mysql -u root -p < schema.sql`, populate `.env`, ensure MySQL is on localhost:3306
- **Deprecated code:** `DatabaseConnection` is marked `@Deprecated` and references a nonexistent `servidor.conexion.ConexionDB`. It still works; do not remove yet.
- **Hardcoded strings:** All UI text is in Spanish; no localization mechanism exists
- **No background threads:** Database calls are synchronous on the EDT; consider adding progress dialogs for slow queries in future
- **Test results:** The `test_resultados` table structure is in place, but the app does not yet populate it; scaffolding for test-taking UI is missing
- **UI Consistency:** When adding new features, **always use `UIStyle` constants** for colors, fonts, and dimensions. Breaking this rule will require refactoring.