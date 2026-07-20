package servicio;

import dao.InscripcionDAO;
import modelo.Inscripcion;

import java.sql.SQLException;
import java.util.List;

/** Casos de uso de inscripción de usuarios a cursos. */
public class ServicioInscripcion {

    private final InscripcionDAO inscripcionDAO;

    public ServicioInscripcion(InscripcionDAO inscripcionDAO) {
        this.inscripcionDAO = inscripcionDAO;
    }

    /** @return true si la inscripción fue nueva o reactivada; false si ya estaba activa */
    public boolean inscribirCurso(String email, String cursoTitulo) throws SQLException {
        return inscripcionDAO.altaInscripcion(email, cursoTitulo) == 1;
    }

    public boolean estaInscripto(String email, String cursoTitulo) throws SQLException {
        return inscripcionDAO.estaInscripto(email, cursoTitulo);
    }

    public void darDeBajaCurso(String email, String cursoTitulo) throws SQLException {
        inscripcionDAO.bajaInscripcion(email, cursoTitulo);
    }

    public List<Inscripcion> obtenerCursosInscriptos(String email) throws SQLException {
        return inscripcionDAO.listarPorUsuario(email);
    }

    public int obtenerProgreso(String email, String cursoTitulo) throws SQLException {
        return inscripcionDAO.obtenerProgreso(email, cursoTitulo);
    }

    public void actualizarProgreso(String email, String cursoTitulo, int leccionActual) throws SQLException {
        inscripcionDAO.actualizarProgreso(email, cursoTitulo, leccionActual);
    }
}
