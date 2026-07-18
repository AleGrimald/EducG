package controlador;

import dao.UsuarioDAOJdbc;
import servicio.ServicioAuth;
import util.Validador;

import java.sql.SQLException;

/** Orquesta el caso de uso de registro de cuenta para VentanaRegistro. */
public class ControladorRegistro {

    private final ServicioAuth servicioAuth = new ServicioAuth(new UsuarioDAOJdbc());

    /** Valida los datos del paso 1 (Apellido, Nombre, DNI). Lanza IllegalArgumentException si algo falla. */
    public void validarPaso1(String nombre, String apellido, String dni) {
        if (!Validador.esNombreValido(nombre))
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 100 caracteres y no contener símbolos especiales.");
        if (!Validador.esNombreValido(apellido))
            throw new IllegalArgumentException("El apellido debe tener entre 2 y 100 caracteres y no contener símbolos especiales.");
        if (!Validador.esDniValido(dni))
            throw new IllegalArgumentException("El DNI debe tener entre 7 y 9 dígitos, sin puntos ni espacios.");
    }

    /** @return true si el registro fue exitoso, false si el email ya existe */
    public boolean registrar(String nombre, String apellido, String dni, String telefono,
                              String email, String password, String confirmar)
            throws SQLException {
        validarPaso1(nombre, apellido, dni);
        if (!Validador.esTelefonoValido(telefono))
            throw new IllegalArgumentException("Ingresá un teléfono válido (6 a 20 dígitos).");
        if (!Validador.esEmailValido(email))
            throw new IllegalArgumentException("Ingresá un correo electrónico válido.\nEjemplo: usuario@dominio.com");
        if (!Validador.esPasswordValida(password))
            throw new IllegalArgumentException("La contraseña debe tener entre 6 y 20 caracteres alfanuméricos\n"
                + "(solo letras y números, sin símbolos).");
        if (!password.equals(confirmar))
            throw new IllegalArgumentException("Las contraseñas no coinciden.");

        return servicioAuth.registrar(email, password, nombre, apellido, Long.parseLong(dni), telefono);
    }
}
