package dao;

import modelo.Curso;

import java.sql.SQLException;
import java.util.List;

/** Contrato de persistencia para el catálogo de cursos y su contenido (lecciones). */
public interface CursoDAO {

    List<Curso> listarCatalogo() throws SQLException;

    /** @return el curso con ese título (con sus lecciones), o null si no existe */
    Curso buscarPorTitulo(String titulo) throws SQLException;
}
