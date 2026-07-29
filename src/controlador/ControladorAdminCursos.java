package controlador;

import dao.AdminCursoDAOJdbc;
import modelo.CursoAdmin;
import servicio.ServicioAdminCursos;

import java.sql.SQLException;
import java.util.List;

/** Orquesta los casos de uso del módulo Cursos del panel de administrador (alta de cursos: ver {@link ControladorCrearCurso}). */
public class ControladorAdminCursos {

    private final ServicioAdminCursos servicio = new ServicioAdminCursos(new AdminCursoDAOJdbc());

    public List<CursoAdmin> listar() throws SQLException {
        return servicio.listar();
    }

    public List<CursoAdmin> buscarPorNombre(String nombreLike) throws SQLException {
        return servicio.buscarPorNombre(nombreLike);
    }

    /** @return true si se modificó, false si no se encontró ese id */
    public boolean modificar(int id, String emoji, String titulo, String descripcion, String duracion) throws SQLException {
        validarDatosBasicos(emoji, titulo, descripcion, duracion);
        return servicio.modificar(id, emoji, titulo, descripcion, duracion);
    }

    public boolean bajaLogica(int id) throws SQLException {
        return servicio.bajaLogica(id);
    }

    public boolean reactivar(int id) throws SQLException {
        return servicio.reactivar(id);
    }

    public boolean eliminar(int id) throws SQLException {
        return servicio.eliminar(id);
    }

    static void validarDatosBasicos(String emoji, String titulo, String descripcion, String duracion) {
        if (emoji == null || emoji.isBlank() || emoji.length() > 10)
            throw new IllegalArgumentException("Ingresá un emoji para el curso (máx. 10 caracteres).");
        if (titulo == null || titulo.isBlank() || titulo.length() > 200)
            throw new IllegalArgumentException("El título debe tener entre 1 y 200 caracteres.");
        if (descripcion == null || descripcion.isBlank())
            throw new IllegalArgumentException("Ingresá una descripción para el curso.");
        if (duracion == null || duracion.isBlank() || duracion.length() > 50)
            throw new IllegalArgumentException("Ingresá la duración del curso (ej. \"8 semanas\", máx. 50 caracteres).");
    }
}
