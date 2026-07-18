package dao;

import bd.ConexionBD;
import modelo.Usuario;

import java.sql.*;

/** Implementación de {@link UsuarioDAO} sobre stored procedures (sp_alta_usuario, sp_obtener_usuario, etc.). */
public class UsuarioDAOJdbc implements UsuarioDAO {

    @Override
    public int altaUsuario(String email, String passwordHash, String nombre, String apellido,
                            long dni, String telefono) throws SQLException {
        final String sql = "{call sp_alta_usuario(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            cs.setString(2, passwordHash);
            cs.setString(3, nombre);
            cs.setString(4, apellido);
            cs.setLong(5, dni);
            cs.setString(6, telefono);
            cs.registerOutParameter(7, Types.TINYINT);
            cs.execute();
            return cs.getInt(7);
        }
    }

    @Override
    public Usuario obtenerUsuario(String email) throws SQLException {
        final String sql = "{call sp_obtener_usuario(?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return new Usuario(rs.getString("nombre"), rs.getString("apellido"), email);
            }
        }
        return null;
    }

    @Override
    public String obtenerHashPassword(String email) throws SQLException {
        final String sql = "{call sp_obtener_usuario(?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return rs.getString("password_hash");
            }
        }
        return null;
    }

    @Override
    public boolean modificarDatosPersonales(String email, String nombre, String apellido) throws SQLException {
        final String sql = "{call sp_modificar_usuario(?, ?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            cs.setString(2, nombre);
            cs.setString(3, apellido);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return rs.getInt("filas_afectadas") > 0;
            }
        }
        return false;
    }

    @Override
    public boolean modificarPassword(String email, String nuevoHash) throws SQLException {
        final String sql = "{call sp_modificar_password_usuario(?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            cs.setString(2, nuevoHash);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return rs.getInt("filas_afectadas") > 0;
            }
        }
        return false;
    }
}
