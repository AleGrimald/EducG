package servicio;

import dao.UsuarioDAO;
import modelo.Usuario;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/** Casos de uso de gestión del perfil de usuario. */
public class ServicioUsuario {

    private final UsuarioDAO usuarioDAO;

    public ServicioUsuario(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /** @return datos del usuario, o null si no existe */
    public Usuario obtenerDatos(String email) throws SQLException {
        return usuarioDAO.obtenerUsuario(email);
    }

    public boolean actualizarDatosPersonales(String email, String nombre, String apellido) throws SQLException {
        return usuarioDAO.modificarDatosPersonales(email, nombre, apellido);
    }

    /** Verifica la contraseña actual antes de actualizar. @return false si la contraseña actual es incorrecta */
    public boolean cambiarPassword(String email, String actual, String nueva) throws SQLException {
        String hashAlmacenado = usuarioDAO.obtenerHashPassword(email);
        if (hashAlmacenado == null || !HasheadorPassword.verificar(actual, hashAlmacenado)) return false;

        String nuevoHash;
        try {
            nuevoHash = HasheadorPassword.hashear(nueva);
        } catch (NoSuchAlgorithmException e) {
            throw new SQLException("Error al procesar la contraseña.", e);
        }
        return usuarioDAO.modificarPassword(email, nuevoHash);
    }
}
