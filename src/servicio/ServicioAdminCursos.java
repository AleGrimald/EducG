package servicio;

import dao.AdminCursoDAO;
import modelo.CursoAdmin;
import modelo.ItemPlanEstudio;
import modelo.PreguntaTest;

import java.sql.SQLException;
import java.util.List;

/** Casos de uso de gestión de cursos para el panel de administrador, incluido el wizard "Crear Curso". */
public class ServicioAdminCursos {

    private final AdminCursoDAO adminCursoDAO;

    public ServicioAdminCursos(AdminCursoDAO adminCursoDAO) {
        this.adminCursoDAO = adminCursoDAO;
    }

    public List<CursoAdmin> listar() throws SQLException {
        return adminCursoDAO.listarTodos();
    }

    public List<CursoAdmin> buscarPorNombre(String nombreLike) throws SQLException {
        return adminCursoDAO.buscarPorNombreLike(nombreLike);
    }

    /** @return true si se modificó, false si no se encontró ese id */
    public boolean modificar(int id, String emoji, String titulo, String descripcion, String duracion) throws SQLException {
        return adminCursoDAO.modificarCurso(id, emoji, titulo, descripcion, duracion);
    }

    public boolean bajaLogica(int id) throws SQLException {
        return adminCursoDAO.bajaLogica(id);
    }

    public boolean reactivar(int id) throws SQLException {
        return adminCursoDAO.reactivar(id);
    }

    public boolean eliminar(int id) throws SQLException {
        return adminCursoDAO.eliminar(id);
    }

    /** @return el id del curso creado, o -1 si ya existía un curso con ese título */
    public int guardarCursoCompleto(String emoji, String titulo, String descripcion, String duracion,
                                     List<ItemPlanEstudio> items, List<PreguntaTest> preguntas) throws SQLException {
        return adminCursoDAO.guardarCursoCompleto(emoji, titulo, descripcion, duracion, items, preguntas);
    }
}
