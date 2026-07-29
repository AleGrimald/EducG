package dao;

import modelo.AlumnoAdmin;

import java.sql.SQLException;
import java.util.List;

/** Contrato de persistencia para la gestión de alumnos desde el panel de administrador. */
public interface AdminAlumnoDAO {

    List<AlumnoAdmin> listarTodos() throws SQLException;

    /** @return el alumno con ese DNI, o null si no existe */
    AlumnoAdmin buscarPorDni(long dni) throws SQLException;

    boolean bajaLogica(int id) throws SQLException;

    boolean reactivar(int id) throws SQLException;

    boolean eliminar(int id) throws SQLException;
}
