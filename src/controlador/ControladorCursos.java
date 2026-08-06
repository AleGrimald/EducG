package controlador;

import chatbot.MotorChatbotException;
import dao.CursoDAOJdbc;
import dao.InscripcionDAOJdbc;
import dao.UsuarioDAOJdbc;
import modelo.Curso;
import modelo.ResultadoEvaluacionEjercicio;
import modelo.Usuario;
import servicio.ServicioCursos;
import servicio.ServicioEvaluacionEjercicio;
import servicio.ServicioInscripcion;
import servicio.ServicioUsuario;

import java.sql.SQLException;
import java.util.List;

/** Orquesta el catálogo de cursos y las inscripciones para VentanaCursos. */
public class ControladorCursos {

    private final ServicioCursos servicioCursos = new ServicioCursos(new CursoDAOJdbc());
    private final ServicioInscripcion servicioInscripcion = new ServicioInscripcion(new InscripcionDAOJdbc());
    private final ServicioUsuario servicioUsuario = new ServicioUsuario(new UsuarioDAOJdbc());
    private final ServicioEvaluacionEjercicio servicioEvaluacionEjercicio = new ServicioEvaluacionEjercicio();

    public List<Curso> obtenerCatalogo() throws SQLException {
        return servicioCursos.obtenerCatalogo();
    }

    /** @return datos del usuario, o null si no existe */
    public Usuario obtenerDatosUsuario(String email) throws SQLException {
        return servicioUsuario.obtenerDatos(email);
    }

    /** @return true si la inscripción fue nueva; false si ya estaba inscripto */
    public boolean inscribirCurso(String email, int cursoId) throws SQLException {
        return servicioInscripcion.inscribirCurso(email, cursoId);
    }

    public boolean estaInscripto(String email, int cursoId) throws SQLException {
        return servicioInscripcion.estaInscripto(email, cursoId);
    }

    public int obtenerProgreso(String email, int cursoId) throws SQLException {
        return servicioInscripcion.obtenerProgreso(email, cursoId);
    }

    public void actualizarProgreso(String email, int cursoId, int leccionActual) throws SQLException {
        servicioInscripcion.actualizarProgreso(email, cursoId, leccionActual);
    }

    /** Compara la respuesta del alumno contra la esperada, ignorando mayúsculas/minúsculas y espacios extra. */
    public boolean verificarRespuestaEjercicio(String respuestaAlumno, String respuestaEsperada) {
        if (respuestaAlumno == null || respuestaEsperada == null) return false;
        return normalizar(respuestaAlumno).equals(normalizar(respuestaEsperada));
    }

    /** Camino rápido (gratis) si coincide textualmente con la respuesta esperada; si no, la IA
     * juzga si la respuesta del alumno resuelve el ejercicio de todas formas. */
    public ResultadoEvaluacionEjercicio evaluarEjercicio(String codigoAlumno, String enunciado, String respuestaEsperada)
            throws MotorChatbotException {
        if (verificarRespuestaEjercicio(codigoAlumno, respuestaEsperada)) {
            return new ResultadoEvaluacionEjercicio(true, "¡Coincide con la respuesta esperada!");
        }
        return servicioEvaluacionEjercicio.evaluar(enunciado, respuestaEsperada, codigoAlumno);
    }

    private String normalizar(String texto) {
        return texto.trim().toLowerCase().replaceAll("\\s+", " ");
    }
}
