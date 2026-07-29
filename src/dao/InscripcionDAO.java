package dao;

import modelo.Inscripcion;
import java.sql.SQLException;
import java.util.List;

/** Contrato de persistencia para inscripciones usuario↔curso. Implementado sobre stored procedures. */
public interface InscripcionDAO {

    /** @return 1 = inscripto (nuevo o reactivado), 0 = ya estaba activo, -1 = usuario o curso no existe */
    int altaInscripcion(String email, int cursoId) throws SQLException;

    void bajaInscripcion(String email, int cursoId) throws SQLException;

    boolean estaInscripto(String email, int cursoId) throws SQLException;

    /** @return inscripciones activas del usuario, más recientes primero */
    List<Inscripcion> listarPorUsuario(String email) throws SQLException;

    /** @return la lección actual (progreso) en que quedó el usuario en este curso, o 0 si no hay registro */
    int obtenerProgreso(String email, int cursoId) throws SQLException;

    void actualizarProgreso(String email, int cursoId, int leccionActual) throws SQLException;
}
