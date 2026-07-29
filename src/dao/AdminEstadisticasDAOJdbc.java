package dao;

import bd.ConexionBD;
import modelo.EstadisticasCurso;
import modelo.EstadisticasGenerales;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Implementación de {@link AdminEstadisticasDAO} sobre stored procedures de solo lectura. */
public class AdminEstadisticasDAOJdbc implements AdminEstadisticasDAO {

    @Override
    public EstadisticasGenerales obtenerGenerales() throws SQLException {
        final String sql = "{call sp_estadisticas_generales()}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return new EstadisticasGenerales(
                        rs.getInt("alumnos_activos"), rs.getInt("alumnos_inactivos"),
                        rs.getInt("cursos_activos"), rs.getInt("inscripciones_activas"),
                        rs.getInt("aprobados_totales"));
                }
            }
        }
        return new EstadisticasGenerales(0, 0, 0, 0, 0);
    }

    @Override
    public List<EstadisticasCurso> obtenerPorCurso() throws SQLException {
        List<EstadisticasCurso> resultado = new ArrayList<>();
        final String sql = "{call sp_estadisticas_por_curso()}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    resultado.add(new EstadisticasCurso(rs.getInt("curso_id"), rs.getString("titulo"),
                        rs.getInt("inscriptos"), rs.getDouble("promedio"), rs.getDouble("tasa_aprobacion")));
                }
            }
        }
        return resultado;
    }

    @Override
    public Map<String, Integer> obtenerRegistrosMensuales() throws SQLException {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        final String sql = "{call sp_estadisticas_registros_mensuales()}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) resultado.put(rs.getString("mes"), rs.getInt("cantidad"));
            }
        }
        return resultado;
    }
}
