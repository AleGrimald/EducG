package controlador;

import dao.UsuarioDAOJdbc;
import servicio.ServicioAuth;
import util.Validador;

import java.sql.SQLException;

/** Orquesta el caso de uso de inicio de sesión para VentanaLogin. */
public class ControladorLogin {

    private final ServicioAuth servicioAuth = new ServicioAuth(new UsuarioDAOJdbc());

    /** @return true si las credenciales son correctas */
    public boolean iniciarSesion(String email, String password) throws SQLException {
        if (!Validador.esEmailValido(email))
            throw new IllegalArgumentException("Ingresá un correo electrónico válido.\nEjemplo: usuario@dominio.com");
        if (!Validador.esPasswordValida(password))
            throw new IllegalArgumentException("La contraseña debe tener entre 6 y 20 caracteres alfanuméricos\n"
                + "(solo letras y números, sin símbolos).");

        return servicioAuth.iniciarSesion(email, password);
    }

    /** @return true si la cuenta es de administrador, para decidir a qué ventana ir tras un login exitoso */
    public boolean esAdmin(String email) throws SQLException {
        return servicioAuth.esAdmin(email);
    }
}
