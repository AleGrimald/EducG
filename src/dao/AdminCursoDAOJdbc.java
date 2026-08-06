package dao;

import bd.ConexionBD;
import modelo.CursoAdmin;
import modelo.ItemPlanEstudio;
import modelo.OpcionTest;
import modelo.PreguntaTest;
import modelo.SeleccionIcono;

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
    public boolean modificarCurso(int id, SeleccionIcono icono, String titulo, String descripcion, String duracion) throws SQLException {
        final String sql = "{call sp_modificar_curso(?, ?, ?, ?, ?, ?)}";
        // La conexión se resuelve antes que el CallableStatement (no en el mismo try-with-resources)
        // porque resolverIdImagen() necesita esa misma conexión ya abierta: ConexionBD comparte una
        // única Connection estática, así que abrir un DAO aparte para eso la cerraría de golpe.
        try (Connection conn = ConexionBD.obtenerConexion()) {
            Integer idImagen = resolverIdImagen(conn, icono);
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setInt(1, id);
                if (idImagen == null) cs.setNull(2, Types.INTEGER); else cs.setInt(2, idImagen);
                cs.setString(3, titulo);
                cs.setString(4, descripcion);
                cs.setString(5, duracion);
                cs.registerOutParameter(6, Types.TINYINT);
                cs.execute();
                return cs.getInt(6) > 0;
            }
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
    public int guardarCursoCompleto(SeleccionIcono icono, String titulo, String descripcion, String duracion,
                                     List<ItemPlanEstudio> items, List<PreguntaTest> preguntas) throws SQLException {
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try {
                Integer idImagen = resolverIdImagen(conn, icono);
                int idCurso;
                try (CallableStatement cs = conn.prepareCall("{call sp_crear_curso(?, ?, ?, ?, ?)}")) {
                    if (idImagen == null) cs.setNull(1, Types.INTEGER); else cs.setInt(1, idImagen);
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

    @Override
    public List<ItemPlanEstudio> listarPlanEstudio(int cursoId) throws SQLException {
        List<ItemPlanEstudio> items = new ArrayList<>();
        final String sql = "{call sp_listar_contenidos_curso(?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, cursoId);
            try (ResultSet rs = cs.executeQuery()) {
                // El procedure no filtra por activo (lo usa también el lado alumno tal cual):
                // se filtra acá para que las bajas lógicas del admin desaparezcan de su propio listado.
                while (rs.next()) {
                    if (!rs.getBoolean("activo")) continue;
                    items.add(new ItemPlanEstudio(rs.getInt("id"), rs.getInt("orden"), rs.getString("topico"),
                        rs.getString("contenido"), rs.getString("ejercicio_propuesto"),
                        rs.getString("respuesta_esperada"), true));
                }
            }
        }
        return items;
    }

    @Override
    public int agregarItemPlan(int cursoId, int orden, String topico, String contenido,
                                String ejercicioPropuesto, String respuestaEsperada) throws SQLException {
        final String sql = "{call sp_crear_leccion(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, cursoId);
            cs.setInt(2, orden);
            cs.setString(3, topico);
            cs.setString(4, contenido);
            if (ejercicioPropuesto == null) {
                cs.setNull(5, Types.LONGVARCHAR);
                cs.setNull(6, Types.VARCHAR);
            } else {
                cs.setString(5, ejercicioPropuesto);
                cs.setString(6, respuestaEsperada);
            }
            cs.registerOutParameter(7, Types.INTEGER);
            cs.execute();
            return cs.getInt(7);
        }
    }

    @Override
    public boolean modificarItemPlan(int leccionId, String topico, String contenido) throws SQLException {
        final String sql = "{call sp_modificar_leccion(?, ?, ?, ?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, leccionId);
            cs.setString(2, topico);
            cs.setString(3, contenido);
            cs.registerOutParameter(4, Types.TINYINT);
            cs.execute();
            return cs.getInt(4) > 0;
        }
    }

    @Override
    public boolean eliminarItemPlan(int leccionId) throws SQLException {
        return ejecutarCambioEstado("{call sp_desactivar_leccion(?, ?)}", leccionId);
    }

    @Override
    public void reordenarPlan(List<ItemPlanEstudio> itemsEnOrden) throws SQLException {
        try (Connection conn = ConexionBD.obtenerConexion()) {
            conn.setAutoCommit(false);
            try {
                // curso_contenidos tiene UNIQUE(curso_id, orden): si dos ítems necesitan
                // intercambiar posición, actualizarlos directo a sus valores finales puede
                // chocar a mitad de camino (ambos con el mismo orden por un instante dentro de
                // la misma transacción). Por eso primero se pasan todos a negativos (que nunca
                // pueden repetir un orden real, siempre positivo) y recién después a los
                // definitivos — el clásico truco de dos fases para el "swap" con UNIQUE.
                for (int i = 0; i < itemsEnOrden.size(); i++) {
                    actualizarOrden(conn, itemsEnOrden.get(i).getId(), -(i + 1));
                }
                for (int i = 0; i < itemsEnOrden.size(); i++) {
                    actualizarOrden(conn, itemsEnOrden.get(i).getId(), i + 1);
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private static void actualizarOrden(Connection conn, int leccionId, int orden) throws SQLException {
        try (CallableStatement cs = conn.prepareCall("{call sp_modificar_orden_leccion(?, ?, ?)}")) {
            cs.setInt(1, leccionId);
            cs.setInt(2, orden);
            cs.registerOutParameter(3, Types.TINYINT);
            cs.execute();
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

    /**
     * Resuelve una {@link SeleccionIcono} al {@code id_imagen} que esperan
     * {@code sp_crear_curso}/{@code sp_modificar_curso}. Recibe la conexión ya abierta del
     * llamador en vez de abrir la propia — ver el comentario en {@link #modificarCurso}.
     * Si es un archivo recién subido, lo inserta en {@code imagenes} con la clave/etiqueta que
     * eligió el admin en {@code SelectorIconoCurso} — a partir de ahí queda disponible como
     * ícono reutilizable para cualquier curso (vía {@code sp_listar_iconos_curso}), no solo
     * para este. Una edición posterior de este mismo curso que no toque el selector vuelve a
     * resolver esa misma clave (rama {@code esClave()}) en vez de insertar una fila nueva.
     * @return null si no hay ícono elegido
     */
    private static Integer resolverIdImagen(Connection conn, SeleccionIcono icono) throws SQLException {
        if (icono == null || icono.esNinguno()) return null;

        if (icono.esArchivoSubido()) {
            try (CallableStatement cs = conn.prepareCall("{call sp_crear_imagen(?, ?, ?, ?)}")) {
                cs.setBytes(1, icono.getDatos());
                cs.setString(2, icono.getClave());
                cs.setString(3, icono.getEtiqueta());
                cs.registerOutParameter(4, Types.INTEGER);
                cs.execute();
                return cs.getInt(4);
            } catch (SQLIntegrityConstraintViolationException ex) {
                throw new SQLException("Ya existe un ícono con la clave '" + icono.getClave()
                    + "'. Elegí otra clave.", ex);
            }
        }

        try (CallableStatement cs = conn.prepareCall("{call sp_obtener_imagen_por_clave(?)}")) {
            cs.setString(1, icono.getClave());
            try (ResultSet rs = cs.executeQuery()) {
                if (!rs.next()) throw new SQLException("No existe una imagen con clave '" + icono.getClave() + "'.");
                return rs.getInt("id_imagen");
            }
        }
    }

    private CursoAdmin mapearFila(ResultSet rs) throws SQLException {
        return new CursoAdmin(rs.getInt("id"), rs.getBytes("emoji_datos"), rs.getString("emoji_clave"), rs.getString("titulo"),
            rs.getString("descripcion"), rs.getString("duracion"), rs.getBoolean("activo"));
    }
}
