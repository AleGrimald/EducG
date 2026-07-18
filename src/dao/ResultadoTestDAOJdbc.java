package dao;

import bd.ConexionBD;
import modelo.EstadisticasUsuario;
import modelo.ResultadoTest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Implementación de {@link ResultadoTestDAO} sobre stored procedures (sp_listar_resultados_test_usuario, sp_obtener_estadisticas_usuario). */
public class ResultadoTestDAOJdbc implements ResultadoTestDAO {

    @Override
    public List<ResultadoTest> listarPorUsuario(String email) throws SQLException {
        List<ResultadoTest> resultados = new ArrayList<>();
        final String sql = "{call sp_listar_resultados_test_usuario(?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next())
                    resultados.add(new ResultadoTest(
                        rs.getString("curso_titulo"), rs.getString("test_nombre"),
                        rs.getInt("puntaje"), rs.getString("fecha")
                    ));
            }
        }
        return resultados;
    }

    @Override
    public EstadisticasUsuario obtenerEstadisticas(String email) throws SQLException {
        final String sql = "{call sp_obtener_estadisticas_usuario(?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return new EstadisticasUsuario(
                        rs.getInt("cursos_inscriptos"),
                        rs.getInt("tests_realizados"),
                        rs.getInt("promedio_puntaje")
                    );
                }
            }
        }
        return new EstadisticasUsuario(0, 0, 0);
    }

    @Override
    public int registrarResultadoTest(String email, String cursoTitulo, String testNombre, int puntaje) throws SQLException {
        final String sql = "{call sp_alta_resultado_test(?, ?, ?, ?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            cs.setString(2, cursoTitulo);
            cs.setString(3, testNombre);
            cs.setInt(4, puntaje);
            cs.registerOutParameter(5, Types.INTEGER);
            cs.execute();
            return cs.getInt(5);
        }
    }

    @Override
    public void registrarRespuesta(int testResultadoId, int preguntaId, int opcionElegidaId) throws SQLException {
        final String sql = "{call sp_alta_respuesta_test(?, ?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, testResultadoId);
            cs.setInt(2, preguntaId);
            cs.setInt(3, opcionElegidaId);
            cs.execute();
        }
    }

    @Override
    public int obtenerMejorPuntaje(String email, String cursoTitulo) throws SQLException {
        final String sql = "{call sp_obtener_mejor_puntaje_curso(?, ?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, email);
            cs.setString(2, cursoTitulo);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.execute();
            return cs.getInt(3);
        }
    }
}
