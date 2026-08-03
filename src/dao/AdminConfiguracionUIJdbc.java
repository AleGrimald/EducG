package dao;

import bd.ConexionBD;
import modelo.ConfiguracionUI;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Implementación de AdminConfiguracionUIDAO sobre stored procedures. */
public class AdminConfiguracionUIJdbc implements AdminConfiguracionUIDAO {

    @Override
    public List<ConfiguracionUI> listarTodas() throws SQLException {
        List<ConfiguracionUI> configuraciones = new ArrayList<>();
        final String sql = "{call sp_listar_configuraciones_ui()}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    configuraciones.add(mapearFila(rs));
                }
            }
        }
        return configuraciones;
    }

    @Override
    public ConfiguracionUI obtener(String clave) throws SQLException {
        final String sql = "{call sp_obtener_configuracion_ui(?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, clave);
            try (ResultSet rs = cs.executeQuery()) {
                return rs.next() ? mapearFila(rs) : null;
            }
        }
    }

    @Override
    public boolean actualizar(String clave, String valor) throws SQLException {
        final String sql = "{call sp_modificar_configuracion_ui(?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, clave);
            cs.setString(2, valor);
            cs.execute();
            try (ResultSet rs = cs.getResultSet()) {
                if (rs.next()) {
                    return rs.getInt("filas_afectadas") > 0;
                }
            }
        }
        return false;
    }

    private ConfiguracionUI mapearFila(ResultSet rs) throws SQLException {
        LocalDateTime fecha = rs.getTimestamp("fecha_modificacion") != null
            ? rs.getTimestamp("fecha_modificacion").toLocalDateTime()
            : null;
        return new ConfiguracionUI(
            rs.getInt("id"),
            rs.getString("clave"),
            rs.getString("valor"),
            rs.getString("tipo"),
            rs.getString("descripcion"),
            rs.getString("modulo"),
            rs.getString("seccion"),
            fecha
        );
    }
}
