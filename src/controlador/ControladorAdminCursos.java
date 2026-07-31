package controlador;

import dao.AdminCursoDAOJdbc;
import modelo.CursoAdmin;
import modelo.ItemPlanEstudio;
import modelo.SeleccionIcono;
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
    public boolean modificar(int id, SeleccionIcono icono, String titulo, String descripcion, String duracion) throws SQLException {
        validarDatosBasicos(icono, titulo, descripcion, duracion);
        return servicio.modificar(id, icono, titulo, descripcion, duracion);
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

    public List<ItemPlanEstudio> listarPlanEstudio(int cursoId) throws SQLException {
        return servicio.listarPlanEstudio(cursoId);
    }

    public void agregarItemPlan(int cursoId, int orden, String topico, String contenido,
                                 String ejercicioPropuesto, String respuestaEsperada) throws SQLException {
        validarTopicoContenido(topico, contenido);
        if (ejercicioPropuesto != null && !ejercicioPropuesto.isBlank()
            && (respuestaEsperada == null || respuestaEsperada.isBlank())) {
            throw new IllegalArgumentException(
                "Completá la respuesta esperada del ejercicio, o borrá el ejercicio propuesto.");
        }
        servicio.agregarItemPlan(cursoId, orden, topico, contenido,
            (ejercicioPropuesto == null || ejercicioPropuesto.isBlank()) ? null : ejercicioPropuesto,
            (respuestaEsperada == null || respuestaEsperada.isBlank()) ? null : respuestaEsperada);
    }

    /** @return true si se modificó, false si no se encontró ese ítem */
    public boolean modificarItemPlan(int leccionId, String topico, String contenido) throws SQLException {
        validarTopicoContenido(topico, contenido);
        return servicio.modificarItemPlan(leccionId, topico, contenido);
    }

    public boolean eliminarItemPlan(int leccionId) throws SQLException {
        return servicio.eliminarItemPlan(leccionId);
    }

    public void reordenarPlan(List<ItemPlanEstudio> itemsEnOrden) throws SQLException {
        servicio.reordenarPlan(itemsEnOrden);
    }

    private static void validarTopicoContenido(String topico, String contenido) {
        if (topico == null || topico.isBlank())
            throw new IllegalArgumentException("Ingresá un nombre para el tema.");
        if (topico.length() > 200)
            throw new IllegalArgumentException("El nombre del tema no puede superar los 200 caracteres.");
        if (contenido == null || contenido.isBlank())
            throw new IllegalArgumentException("Ingresá el contenido teórico del tema.");
    }

    private static final int TAMANO_MAXIMO_ICONO_SUBIDO = 5 * 1024 * 1024; // 5 MB, margen generoso para un PNG

    static void validarDatosBasicos(SeleccionIcono icono, String titulo, String descripcion, String duracion) {
        if (icono != null && icono.esArchivoSubido() && icono.getDatos().length > TAMANO_MAXIMO_ICONO_SUBIDO)
            throw new IllegalArgumentException("La imagen elegida es demasiado pesada (máx. 5 MB).");
        if (titulo == null || titulo.isBlank() || titulo.length() > 200)
            throw new IllegalArgumentException("El título debe tener entre 1 y 200 caracteres.");
        if (descripcion == null || descripcion.isBlank())
            throw new IllegalArgumentException("Ingresá una descripción para el curso.");
        if (duracion == null || duracion.isBlank() || duracion.length() > 50)
            throw new IllegalArgumentException("Ingresá la duración del curso (ej. \"8 semanas\", máx. 50 caracteres).");
    }
}
