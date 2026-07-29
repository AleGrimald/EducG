package dao;

import bd.ConexionBD;
import modelo.Usuario;

import java.sql.*;

/** Implementación de {@link UsuarioDAO} sobre stored procedures (sp_alta_usuario, sp_obtener_usuario, etc.). */
public class UsuarioDAOJdbc implements UsuarioDAO {

    @Override
    public int altaUsuario(String email, String passwordHash, String nombre, String apellido,
                            long dni, String telefono) throws SQLException {
        final String sql = "{call sp_alta_usuario(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            cs.setString(2, passwordHash);
            cs.setString(3, nombre);
            cs.setString(4, apellido);
            cs.setLong(5, dni);
            cs.setString(6, telefono);
            cs.registerOutParameter(7, Types.TINYINT);
            cs.registerOutParameter(8, Types.INTEGER);
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
                if (rs.next()) {
                    return new Usuario(rs.getInt("id"), rs.getString("nombre"), rs.getString("apellido"),
                        rs.getString("email"), rs.getLong("dni"), rs.getString("telefono"));
                }
            }
        }
        return null;
    }

    @Override
    public String obtenerHashPassword(String email) throws SQLException {
        final String sql = "{call sp_obtener_hash_password(?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.execute();
            return cs.getString(2);
        }
    }

    @Override
    public boolean modificarDatosPersonales(int id, String email, String nombre, String apellido,
                                             long dni, String telefono) throws SQLException {
        final String sql = "{call sp_modificar_usuario(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.setString(2, email);
            cs.setString(3, nombre);
            cs.setString(4, apellido);
            cs.setLong(5, dni);
            cs.setString(6, telefono);
            cs.registerOutParameter(7, Types.TINYINT);
            cs.execute();
            return cs.getInt(7) > 0;
        }
    }

    @Override
    public boolean modificarPassword(String email, String nuevoHash) throws SQLException {
        final String sql = "{call sp_modificar_password_usuario(?, ?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            cs.setString(2, nuevoHash);
            cs.registerOutParameter(3, Types.TINYINT);
            cs.execute();
            return cs.getInt(3) > 0;
        }
    }

    @Override
    public boolean esAdmin(String email) throws SQLException {
        final String sql = "{call sp_obtener_usuario(?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return rs.getBoolean("es_admin");
            }
        }
        return false;
    }
}
