package dao;

import bd.ConexionBD;
import modelo.Curso;
import modelo.Leccion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Implementación de {@link CursoDAO} sobre stored procedures (sp_listar_cursos_catalogo, sp_listar_contenidos_curso). */
public class CursoDAOJdbc implements CursoDAO {

    @Override
    public List<Curso> listarCatalogo() throws SQLException {
        // Primero se drena y cierra por completo el ResultSet/Statement de este SP
        // antes de abrir los de listarLecciones(): ConexionBD comparte una única
        // conexión, y MySQL invalida el cursor externo si se abre otro Statement
        // sobre la misma conexión mientras el primero sigue iterando.
        List<Object[]> filasBase = new ArrayList<>();
        final String sql = "{call sp_listar_cursos_catalogo()}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    filasBase.add(new Object[]{
                        rs.getInt("id"), rs.getBytes("emoji_datos"), rs.getString("titulo"),
                        rs.getString("descripcion"), rs.getString("duracion")
                    });
                }
            }
        }

        List<Curso> cursos = new ArrayList<>();
        for (Object[] fila : filasBase) {
            int id = (int) fila[0];
            cursos.add(new Curso(id, (byte[]) fila[1], (String) fila[2], (String) fila[3], (String) fila[4], listarLecciones(id)));
        }
        return cursos;
    }

    @Override
    public Curso buscarPorTitulo(String titulo) throws SQLException {
        return listarCatalogo().stream()
            .filter(c -> c.getTitulo().equals(titulo))
            .findFirst().orElse(null);
    }

    private List<Leccion> listarLecciones(int cursoId) throws SQLException {
        List<Leccion> lecciones = new ArrayList<>();
        final String sql = "{call sp_listar_contenidos_curso(?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, cursoId);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    lecciones.add(new Leccion(rs.getString("topico"), rs.getString("contenido"),
                        rs.getString("ejercicio_propuesto"), rs.getString("respuesta_esperada")));
                }
            }
        }
        return lecciones;
    }
}
