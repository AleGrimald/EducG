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
        List<String[]> filasBase = new ArrayList<>();
        final String sql = "{call sp_listar_cursos_catalogo()}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    filasBase.add(new String[]{
                        rs.getString("emoji"), rs.getString("titulo"),
                        rs.getString("descripcion"), rs.getString("duracion")
                    });
                }
            }
        }

        List<Curso> cursos = new ArrayList<>();
        for (String[] fila : filasBase) {
            cursos.add(new Curso(fila[0], fila[1], fila[2], fila[3], listarLecciones(fila[1])));
        }
        return cursos;
    }

    @Override
    public Curso buscarPorTitulo(String titulo) throws SQLException {
        return listarCatalogo().stream()
            .filter(c -> c.getTitulo().equals(titulo))
            .findFirst().orElse(null);
    }

    private List<Leccion> listarLecciones(String cursoTitulo) throws SQLException {
        List<Leccion> lecciones = new ArrayList<>();
        final String sql = "{call sp_listar_contenidos_curso(?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, cursoTitulo);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    lecciones.add(new Leccion(rs.getString("topico"), rs.getString("contenido")));
                }
            }
        }
        return lecciones;
    }
}
