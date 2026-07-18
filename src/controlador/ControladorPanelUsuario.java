package controlador;

import dao.CursoDAOJdbc;
import dao.InscripcionDAOJdbc;
import dao.ResultadoTestDAOJdbc;
import dao.UsuarioDAOJdbc;
import modelo.Curso;
import modelo.EstadisticasUsuario;
import modelo.Inscripcion;
import modelo.ResultadoTest;
import modelo.Usuario;
import servicio.ServicioCursos;
import servicio.ServicioEstadisticas;
import servicio.ServicioInscripcion;
import servicio.ServicioUsuario;
import util.Validador;

import java.sql.SQLException;
import java.util.List;

/** Orquesta las tres pestañas de VentanaPanelUsuario: datos, cursos y estadísticas. */
public class ControladorPanelUsuario {

    private final ServicioUsuario servicioUsuario = new ServicioUsuario(new UsuarioDAOJdbc());
    private final ServicioInscripcion servicioInscripcion = new ServicioInscripcion(new InscripcionDAOJdbc());
    private final ServicioEstadisticas servicioEstadisticas = new ServicioEstadisticas(new ResultadoTestDAOJdbc());
    private final ServicioCursos servicioCursos = new ServicioCursos(new CursoDAOJdbc());

    // ── Mis Datos ────────────────────────────────────────────────────────────

    public Usuario obtenerDatosUsuario(String email) throws SQLException {
        return servicioUsuario.obtenerDatos(email);
    }

    public void actualizarDatosPersonales(String email, String nombre, String apellido) throws SQLException {
        if (!Validador.esNombreValido(nombre))
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 100 caracteres.");
        if (!Validador.esNombreValido(apellido))
            throw new IllegalArgumentException("El apellido debe tener entre 2 y 100 caracteres.");
        servicioUsuario.actualizarDatosPersonales(email, nombre, apellido);
    }

    /** @return false si la contraseña actual es incorrecta */
    public boolean cambiarPassword(String email, String actual, String nueva, String confirmar) throws SQLException {
        if (actual.isEmpty())
            throw new IllegalArgumentException("Ingresá tu contraseña actual.");
        if (!Validador.esPasswordValida(nueva))
            throw new IllegalArgumentException("La nueva contraseña debe tener 6–20 caracteres alfanuméricos.");
        if (!nueva.equals(confirmar))
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        return servicioUsuario.cambiarPassword(email, actual, nueva);
    }

    // ── Mis Cursos ───────────────────────────────────────────────────────────

    public List<Inscripcion> obtenerCursosInscriptos(String email) throws SQLException {
        return servicioInscripcion.obtenerCursosInscriptos(email);
    }

    public void darDeBajaCurso(String email, String cursoTitulo) throws SQLException {
        servicioInscripcion.darDeBajaCurso(email, cursoTitulo);
    }

    /** @return el curso del catálogo con ese título, o null si no está disponible */
    public Curso buscarCurso(String titulo) throws SQLException {
        return servicioCursos.buscarPorTitulo(titulo);
    }

    // ── Estadísticas ─────────────────────────────────────────────────────────

    public EstadisticasUsuario obtenerEstadisticas(String email) throws SQLException {
        return servicioEstadisticas.obtenerEstadisticas(email);
    }

    public List<ResultadoTest> obtenerResultadosTests(String email) throws SQLException {
        return servicioEstadisticas.obtenerResultadosTests(email);
    }
}
