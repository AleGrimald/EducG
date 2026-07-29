package servicio;

import dao.AdminAlumnoDAO;
import dao.UsuarioDAO;
import modelo.AlumnoAdmin;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

/** Casos de uso de gestión de alumnos para el panel de administrador. */
public class ServicioAdminAlumnos {

    private final AdminAlumnoDAO adminAlumnoDAO;
    private final UsuarioDAO usuarioDAO;

    public ServicioAdminAlumnos(AdminAlumnoDAO adminAlumnoDAO, UsuarioDAO usuarioDAO) {
        this.adminAlumnoDAO = adminAlumnoDAO;
        this.usuarioDAO = usuarioDAO;
    }

    public List<AlumnoAdmin> listar() throws SQLException {
        return adminAlumnoDAO.listarTodos();
    }

    /** @return el alumno con ese DNI, o null si no existe */
    public AlumnoAdmin buscarPorDni(long dni) throws SQLException {
        return adminAlumnoDAO.buscarPorDni(dni);
    }

    /** @return true si se creó, false si el email ya estaba registrado */
    public boolean alta(String email, String password, String nombre, String apellido,
                         long dni, String telefono) throws SQLException {
        String hash;
        try {
            hash = HasheadorPassword.hashear(password);
        } catch (NoSuchAlgorithmException e) {
            throw new SQLException("Error al procesar la contraseña.", e);
        }
        return usuarioDAO.altaUsuario(email, hash, nombre, apellido, dni, telefono) == 1;
    }

    public boolean modificar(int id, String email, String nombre, String apellido,
                              long dni, String telefono) throws SQLException {
        return usuarioDAO.modificarDatosPersonales(id, email, nombre, apellido, dni, telefono);
    }

    public boolean bajaLogica(int id) throws SQLException {
        return adminAlumnoDAO.bajaLogica(id);
    }

    public boolean reactivar(int id) throws SQLException {
        return adminAlumnoDAO.reactivar(id);
    }

    public boolean eliminar(int id) throws SQLException {
        return adminAlumnoDAO.eliminar(id);
    }
}
