package dao;

import modelo.Usuario;
import java.sql.SQLException;

/** Contrato de persistencia para la entidad Usuario. Implementado sobre stored procedures. */
public interface UsuarioDAO {

    /** @return 1 = usuario creado, 0 = el email ya estaba registrado */
    int altaUsuario(String email, String passwordHash, String nombre, String apellido,
                     long dni, String telefono) throws SQLException;

    /** @return Usuario con nombre/apellido/email, o null si no existe/está inactivo */
    Usuario obtenerUsuario(String email) throws SQLException;

    /** @return el hash almacenado (formato saltHex:hashHex), o null si no existe/está inactivo */
    String obtenerHashPassword(String email) throws SQLException;

    boolean modificarDatosPersonales(String email, String nombre, String apellido) throws SQLException;

    boolean modificarPassword(String email, String nuevoHash) throws SQLException;
}
