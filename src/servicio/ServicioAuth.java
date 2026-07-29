package servicio;

import dao.UsuarioDAO;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/** Casos de uso de autenticación: inicio de sesión y registro de cuentas. */
public class ServicioAuth {

    private final UsuarioDAO usuarioDAO;

    public ServicioAuth(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /** @return true si el email/password son correctos y la cuenta está activa */
    public boolean iniciarSesion(String email, String password) throws SQLException {
        String hashAlmacenado = usuarioDAO.obtenerHashPassword(email);
        return hashAlmacenado != null && HasheadorPassword.verificar(password, hashAlmacenado);
    }

    /** @return true si la cuenta es de administrador */
    public boolean esAdmin(String email) throws SQLException {
        return usuarioDAO.esAdmin(email);
    }

    /** @return true si el registro fue exitoso, false si el email ya existe */
    public boolean registrar(String email, String password, String nombre, String apellido,
                              long dni, String telefono) throws SQLException {
        String hash;
        try {
            hash = HasheadorPassword.hashear(password);
        } catch (NoSuchAlgorithmException e) {
            throw new SQLException("Error al procesar la contraseña.", e);
        }
        int resultado = usuarioDAO.altaUsuario(email, hash, nombre, apellido, dni, telefono);
        return resultado == 1;
    }
}
