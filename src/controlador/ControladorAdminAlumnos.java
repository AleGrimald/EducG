package controlador;

import dao.AdminAlumnoDAOJdbc;
import dao.UsuarioDAOJdbc;
import modelo.AlumnoAdmin;
import servicio.ServicioAdminAlumnos;
import util.Validador;

import java.sql.SQLException;
import java.util.List;

/** Orquesta los casos de uso del módulo Alumnos del panel de administrador. */
public class ControladorAdminAlumnos {

    private final ServicioAdminAlumnos servicio = new ServicioAdminAlumnos(new AdminAlumnoDAOJdbc(), new UsuarioDAOJdbc());

    public List<AlumnoAdmin> listar() throws SQLException {
        return servicio.listar();
    }

    /** @return el alumno con ese DNI, o null si no existe */
    public AlumnoAdmin buscarPorDni(String dni) throws SQLException {
        if (!Validador.esDniValido(dni))
            throw new IllegalArgumentException("El DNI debe tener entre 7 y 9 dígitos, sin puntos ni espacios.");
        return servicio.buscarPorDni(Long.parseLong(dni));
    }

    /** @return true si se creó, false si el email ya estaba registrado */
    public boolean alta(String nombre, String apellido, String dni, String telefono,
                         String email, String password) throws SQLException {
        validarDatosPersonales(nombre, apellido, dni, telefono, email);
        if (!Validador.esPasswordValida(password))
            throw new IllegalArgumentException("La contraseña debe tener entre 6 y 20 caracteres alfanuméricos\n"
                + "(solo letras y números, sin símbolos).");
        return servicio.alta(email, password, nombre, apellido, Long.parseLong(dni), telefono);
    }

    public boolean modificar(int id, String nombre, String apellido, String dni, String telefono, String email) throws SQLException {
        validarDatosPersonales(nombre, apellido, dni, telefono, email);
        return servicio.modificar(id, email, nombre, apellido, Long.parseLong(dni), telefono);
    }

    public boolean bajaLogica(int id) throws SQLException {
        return servicio.bajaLogica(id);
    }

    public boolean reactivar(int id) throws SQLException {
        return servicio.reactivar(id);
    }

    public boolean eliminar(int id) throws SQLException {
        return servicio.eliminar(id);
    }

    private void validarDatosPersonales(String nombre, String apellido, String dni, String telefono, String email) {
        if (!Validador.esNombreValido(nombre))
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 100 caracteres.");
        if (!Validador.esNombreValido(apellido))
            throw new IllegalArgumentException("El apellido debe tener entre 2 y 100 caracteres.");
        if (!Validador.esDniValido(dni))
            throw new IllegalArgumentException("El DNI debe tener entre 7 y 9 dígitos, sin puntos ni espacios.");
        if (!Validador.esTelefonoValido(telefono))
            throw new IllegalArgumentException("Ingresá un teléfono válido (6 a 20 dígitos).");
        if (!Validador.esEmailValido(email))
            throw new IllegalArgumentException("Ingresá un correo electrónico válido.\nEjemplo: usuario@dominio.com");
    }
}
