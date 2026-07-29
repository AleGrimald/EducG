package dao;

import bd.ConexionBD;
import modelo.CursoAdmin;
import modelo.ItemPlanEstudio;
import modelo.OpcionTest;
import modelo.PreguntaTest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de {@link AdminCursoDAO} sobre stored procedures. {@link #guardarCursoCompleto}
 * es la única excepción al patrón "una conexión por método": mantiene una sola conexión abierta
 * a lo largo de todos los INSERT del wizard (curso + contenidos + preguntas + opciones) para que
 * el alta sea atómica (commit al final, rollback si cualquier paso falla).
 */
public class AdminCursoDAOJdbc implements AdminCursoDAO {

    @Override
    public List<CursoAdmin> listarTodos() throws SQLException {
        List<CursoAdmin> cursos = new ArrayList<>();
        final String sql = "{call sp_listar_todos_cursos()}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) cursos.add(mapearFila(rs));
            }
        }
        return cursos;
    }

    @Override
    public List<CursoAdmin> buscarPorNombreLike(String nombre) throws SQLException {
        List<CursoAdmin> cursos = new ArrayList<>();
        final String sql = "{call sp_buscar_todos_cursos(?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, nombre);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) cursos.add(mapearFila(rs));
            }
        }
        return cursos;
    }

    @Override
    public boolean modificarCurso(int id, String emoji, String titulo, String descripcion, String duracion) throws SQLException {
        final String sql = "{call sp_modificar_curso(?, ?, ?, ?, ?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, id);
            cs.setString(2, emoji);
            cs.setString(3, titulo);
            cs.setString(4, descripcion);
            cs.setString(5, duracion);
            cs.registerOutParameter(6, Types.TINYINT);
            cs.execute();
            return cs.getInt(6) > 0;
        }
    }

    @Override
    public boolean bajaLogica(int id) throws SQLException {
        return ejecutarCambioEstado("{call sp_desactivar_curso(?, ?)}", id);
    }

    @Override
    public boolean reactivar(int id) throws SQLException {
        return ejecutarCambioEstado("{call sp_activar_curso(?, ?)}", id);
    }

    @Override
    public boolean eliminar(int id) throws SQLException {
        return ejecutarCambioEstado("{call sp_eliminar_curso(?, ?)}", id);
    }

    @Override
    public int guardarCursoCompleto(String emoji, String titulo, String descripcion, String duracion,
                                     List<ItemPlanEstudio> items, List<PreguntaTest> preguntas) throws SQLException {
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try {
                int idCurso;
                try (CallableStatement cs = conn.prepareCall("{call sp_crear_curso(?, ?, ?, ?, ?)}")) {
                    cs.setString(1, emoji);
                    cs.setString(2, titulo);
                    cs.setString(3, descripcion);
                    cs.setString(4, duracion);
                    cs.registerOutParameter(5, Types.INTEGER);
                    cs.execute();
                    idCurso = cs.getInt(5);
                } catch (SQLIntegrityConstraintViolationException ex) {
                    conn.rollback();
                    return -1;
                }

                for (ItemPlanEstudio item : items) {
                    try (CallableStatement cs = conn.prepareCall("{call sp_crear_leccion(?, ?, ?, ?, ?, ?, ?)}")) {
                        cs.setInt(1, idCurso);
                        cs.setInt(2, item.getOrden());
                        cs.setString(3, item.getTopico());
                        cs.setString(4, item.getContenido());
                        String ejercicio = item.getEjercicioPropuesto();
                        if (ejercicio == null || ejercicio.isBlank()) {
                            cs.setNull(5, Types.LONGVARCHAR);
                            cs.setNull(6, Types.VARCHAR);
                        } else {
                            cs.setString(5, ejercicio);
                            String respuesta = item.getRespuestaEsperada();
                            if (respuesta == null || respuesta.isBlank()) cs.setNull(6, Types.VARCHAR);
                            else cs.setString(6, respuesta);
                        }
                        cs.registerOutParameter(7, Types.INTEGER);
                        cs.execute();
                    }
                }

                int ordenPregunta = 1;
                for (PreguntaTest pregunta : preguntas) {
                    int idPregunta;
                    try (CallableStatement cs = conn.prepareCall("{call sp_crear_pregunta(?, ?, ?, ?)}")) {
                        cs.setInt(1, idCurso);
                        cs.setString(2, pregunta.getEnunciado());
                        cs.setInt(3, ordenPregunta++);
                        cs.registerOutParameter(4, Types.INTEGER);
                        cs.execute();
                        idPregunta = cs.getInt(4);
                    }

                    int ordenOpcion = 1;
                    for (OpcionTest opcion : pregunta.getOpciones()) {
                        try (CallableStatement cs = conn.prepareCall("{call sp_crear_opcion_pregunta(?, ?, ?, ?)}")) {
                            cs.setInt(1, idPregunta);
                            cs.setString(2, opcion.getTexto());
                            cs.setBoolean(3, opcion.isCorrecta());
                            cs.setInt(4, ordenOpcion++);
                            cs.execute();
                        }
                    }
                }

                conn.commit();
                return idCurso;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
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

    private CursoAdmin mapearFila(ResultSet rs) throws SQLException {
        return new CursoAdmin(rs.getInt("id"), rs.getString("emoji"), rs.getString("titulo"),
            rs.getString("descripcion"), rs.getString("duracion"), rs.getBoolean("activo"));
    }
}
