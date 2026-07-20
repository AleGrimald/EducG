package controlador;

import dao.CursoDAOJdbc;
import dao.InscripcionDAOJdbc;
import dao.UsuarioDAOJdbc;
import modelo.Curso;
import modelo.Usuario;
import servicio.ServicioCursos;
import servicio.ServicioInscripcion;
import servicio.ServicioUsuario;

import java.sql.SQLException;
import java.util.List;

/** Orquesta el catálogo de cursos y las inscripciones para VentanaCursos. */
public class ControladorCursos {

    private final ServicioCursos servicioCursos = new ServicioCursos(new CursoDAOJdbc());
    private final ServicioInscripcion servicioInscripcion = new ServicioInscripcion(new InscripcionDAOJdbc());
    private final ServicioUsuario servicioUsuario = new ServicioUsuario(new UsuarioDAOJdbc());

    public List<Curso> obtenerCatalogo() throws SQLException {
        return servicioCursos.obtenerCatalogo();
    }

    /** @return datos del usuario, o null si no existe */
    public Usuario obtenerDatosUsuario(String email) throws SQLException {
        return servicioUsuario.obtenerDatos(email);
    }

    /** @return true si la inscripción fue nueva; false si ya estaba inscripto */
    public boolean inscribirCurso(String email, String cursoTitulo) throws SQLException {
        return servicioInscripcion.inscribirCurso(email, cursoTitulo);
    }

    public boolean estaInscripto(String email, String cursoTitulo) throws SQLException {
        return servicioInscripcion.estaInscripto(email, cursoTitulo);
    }

    public int obtenerProgreso(String email, String cursoTitulo) throws SQLException {
        return servicioInscripcion.obtenerProgreso(email, cursoTitulo);
    }

    public void actualizarProgreso(String email, String cursoTitulo, int leccionActual) throws SQLException {
        servicioInscripcion.actualizarProgreso(email, cursoTitulo, leccionActual);
    }
}
