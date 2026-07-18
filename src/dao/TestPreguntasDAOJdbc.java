package dao;

import bd.ConexionBD;
import modelo.OpcionTest;
import modelo.PreguntaTest;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Implementación de {@link TestPreguntasDAO} sobre el stored procedure sp_listar_preguntas_curso. */
public class TestPreguntasDAOJdbc implements TestPreguntasDAO {

    @Override
    public List<PreguntaTest> listarPorCurso(String cursoTitulo) throws SQLException {
        Map<Integer, String> enunciados = new LinkedHashMap<>();
        Map<Integer, List<OpcionTest>> opcionesPorPregunta = new LinkedHashMap<>();

        final String sql = "{call sp_listar_preguntas_curso(?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, cursoTitulo);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    int preguntaId = rs.getInt("pregunta_id");
                    enunciados.putIfAbsent(preguntaId, rs.getString("enunciado"));
                    opcionesPorPregunta.computeIfAbsent(preguntaId, k -> new ArrayList<>())
                        .add(new OpcionTest(rs.getInt("opcion_id"), rs.getString("opcion_texto"), rs.getBoolean("es_correcta")));
                }
            }
        }

        List<PreguntaTest> preguntas = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : enunciados.entrySet()) {
            preguntas.add(new PreguntaTest(entry.getKey(), entry.getValue(), opcionesPorPregunta.get(entry.getKey())));
        }
        return preguntas;
    }
}
