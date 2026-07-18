package dao;

import modelo.Inscripcion;
import java.sql.SQLException;
import java.util.List;

/** Contrato de persistencia para inscripciones usuario↔curso. Implementado sobre stored procedures. */
public interface InscripcionDAO {

    /** @return 1 = inscripto (nuevo o reactivado), 0 = ya estaba activo, -1 = usuario o curso no existe */
    int altaInscripcion(String email, String cursoTitulo) throws SQLException;

    void bajaInscripcion(String email, String cursoTitulo) throws SQLException;

    boolean estaInscripto(String email, String cursoTitulo) throws SQLException;

    /** @return inscripciones activas del usuario, más recientes primero */
    List<Inscripcion> listarPorUsuario(String email) throws SQLException;
}
