package dao;

import modelo.CursoAdmin;
import modelo.ItemPlanEstudio;
import modelo.PreguntaTest;

import java.sql.SQLException;
import java.util.List;

/** Contrato de persistencia para la gestión de cursos desde el panel de administrador. */
public interface AdminCursoDAO {

    List<CursoAdmin> listarTodos() throws SQLException;

    List<CursoAdmin> buscarPorNombreLike(String nombre) throws SQLException;

    boolean modificarCurso(int id, String emoji, String titulo, String descripcion, String duracion) throws SQLException;

    boolean bajaLogica(int id) throws SQLException;

    boolean reactivar(int id) throws SQLException;

    boolean eliminar(int id) throws SQLException;

    /**
     * Persiste un curso completo creado con el wizard (datos básicos + plan de estudio +
     * banco de preguntas) en una única transacción: si algo falla no queda ningún dato a medias.
     * @return el id del curso creado, o -1 si ya existía un curso con ese título (no se persiste nada)
     */
    int guardarCursoCompleto(String emoji, String titulo, String descripcion, String duracion,
                              List<ItemPlanEstudio> items, List<PreguntaTest> preguntas) throws SQLException;
}
