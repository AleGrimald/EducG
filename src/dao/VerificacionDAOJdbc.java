package dao;

import bd.ConexionBD;

import java.sql.*;

public class VerificacionDAOJdbc implements VerificacionDAO {

    @Override
    public void generarCodigo(long dni, String codigo, int minutosExpiracion) throws SQLException {
        final String sql = "{call sp_generar_codigo_verificacion(?, ?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setLong(1, dni);
            cs.setString(2, codigo);
            cs.setInt(3, minutosExpiracion);
            cs.execute();
        }
    }

    @Override
    public int verificarCodigo(long dni, String codigo) throws SQLException {
        final String sql = "{call sp_verificar_codigo_verificacion(?, ?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setLong(1, dni);
            cs.setString(2, codigo);
            cs.registerOutParameter(3, Types.TINYINT);
            cs.execute();
            return cs.getInt(3);
        }
    }
}
