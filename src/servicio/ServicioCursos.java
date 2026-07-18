package servicio;

import dao.CursoDAO;
import modelo.Curso;

import java.sql.SQLException;
import java.util.List;

/** Casos de uso del catálogo de cursos (ahora persistido en la base de datos). */
public class ServicioCursos {

    private final CursoDAO cursoDAO;

    public ServicioCursos(CursoDAO cursoDAO) {
        this.cursoDAO = cursoDAO;
    }

    public List<Curso> obtenerCatalogo() throws SQLException {
        return cursoDAO.listarCatalogo();
    }

    /** @return el curso con ese título (con sus lecciones), o null si no existe */
    public Curso buscarPorTitulo(String titulo) throws SQLException {
        return cursoDAO.buscarPorTitulo(titulo);
    }
}
