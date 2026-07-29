package dao;

import bd.ConexionBD;
import modelo.AlumnoAdmin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Implementación de {@link AdminAlumnoDAO} sobre stored procedures (sp_listar_todos_usuarios, sp_buscar_usuario_por_dni, etc.). */
public class AdminAlumnoDAOJdbc implements AdminAlumnoDAO {

    @Override
    public List<AlumnoAdmin> listarTodos() throws SQLException {
        List<AlumnoAdmin> alumnos = new ArrayList<>();
        final String sql = "{call sp_listar_todos_usuarios()}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) alumnos.add(mapearFila(rs));
            }
        }
        return alumnos;
    }

    @Override
    public AlumnoAdmin buscarPorDni(long dni) throws SQLException {
        final String sql = "{call sp_buscar_usuario_por_dni(?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setLong(1, dni);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return mapearFila(rs);
            }
        }
        return null;
    }

    @Override
    public boolean bajaLogica(int id) throws SQLException {
        return ejecutarCambioEstado("{call sp_desactivar_usuario(?, ?)}", id);
    }

    @Override
    public boolean reactivar(int id) throws SQLException {
        return ejecutarCambioEstado("{call sp_activar_usuario(?, ?)}", id);
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        return ejecutarCambioEstado("{call sp_eliminar_usuario(?, ?)}", id);
    }

    private boolean ejecutarCambioEstado(String sql, int id) throws SQLException {
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.registerOutParameter(2, Types.TINYINT);
            cs.execute();
            return cs.getInt(2) > 0;
        }
    }

    private AlumnoAdmin mapearFila(ResultSet rs) throws SQLException {
        return new AlumnoAdmin(rs.getInt("id"), rs.getString("nombre"), rs.getString("apellido"),
            rs.getString("email"), rs.getLong("dni"), rs.getString("telefono"),
            rs.getString("fecha_creacion"), rs.getBoolean("activo"));
    }
}
