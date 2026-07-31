package dao;

import modelo.CursoAdmin;
import modelo.ItemPlanEstudio;
import modelo.PreguntaTest;
import modelo.SeleccionIcono;

import java.sql.SQLException;
import java.util.List;

/** Contrato de persistencia para la gestión de cursos desde el panel de administrador. */
public interface AdminCursoDAO {

    List<CursoAdmin> listarTodos() throws SQLException;

    List<CursoAdmin> buscarPorNombreLike(String nombre) throws SQLException;

    boolean modificarCurso(int id, SeleccionIcono icono, String titulo, String descripcion, String duracion) throws SQLException;

    boolean bajaLogica(int id) throws SQLException;

    boolean reactivar(int id) throws SQLException;

    boolean eliminar(int id) throws SQLException;

    /**
     * Persiste un curso completo creado con el wizard (datos básicos + plan de estudio +
     * banco de preguntas) en una única transacción: si algo falla no queda ningún dato a medias.
     * @return el id del curso creado, o -1 si ya existía un curso con ese título (no se persiste nada)
     */
    int guardarCursoCompleto(SeleccionIcono icono, String titulo, String descripcion, String duracion,
                              List<ItemPlanEstudio> items, List<PreguntaTest> preguntas) throws SQLException;

    /** Ítems activos del Plan de Estudio de un curso, ordenados por {@code orden}. */
    List<ItemPlanEstudio> listarPlanEstudio(int cursoId) throws SQLException;

    /** @return el id del ítem creado */
    int agregarItemPlan(int cursoId, int orden, String topico, String contenido,
                         String ejercicioPropuesto, String respuestaEsperada) throws SQLException;

    /** Modifica tópico/contenido de un ítem ya existente (el ejercicio propuesto no se toca). */
    boolean modificarItemPlan(int leccionId, String topico, String contenido) throws SQLException;

    /** Baja lógica del ítem (lo oculta del plan de estudio del admin). */
    boolean eliminarItemPlan(int leccionId) throws SQLException;

    /** Renumera {@code orden} de cada ítem según su posición en la lista (índice 0 = orden 1, etc.). */
    void reordenarPlan(List<ItemPlanEstudio> itemsEnOrden) throws SQLException;
}
