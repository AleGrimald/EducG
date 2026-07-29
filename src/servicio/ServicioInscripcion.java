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
    public boolean inscribirCurso(String email, int cursoId) throws SQLException {
        return inscripcionDAO.altaInscripcion(email, cursoId) == 1;
    }

    public boolean estaInscripto(String email, int cursoId) throws SQLException {
        return inscripcionDAO.estaInscripto(email, cursoId);
    }

    public void darDeBajaCurso(String email, int cursoId) throws SQLException {
        inscripcionDAO.bajaInscripcion(email, cursoId);
    }

    public List<Inscripcion> obtenerCursosInscriptos(String email) throws SQLException {
        return inscripcionDAO.listarPorUsuario(email);
    }

    public int obtenerProgreso(String email, int cursoId) throws SQLException {
        return inscripcionDAO.obtenerProgreso(email, cursoId);
    }

    public void actualizarProgreso(String email, int cursoId, int leccionActual) throws SQLException {
        inscripcionDAO.actualizarProgreso(email, cursoId, leccionActual);
    }
}
