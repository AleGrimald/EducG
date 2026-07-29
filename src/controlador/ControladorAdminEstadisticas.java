package controlador;

import dao.AdminEstadisticasDAOJdbc;
import modelo.EstadisticasCurso;
import modelo.EstadisticasGenerales;
import servicio.ServicioAdminEstadisticas;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Orquesta el caso de uso del módulo Estadísticas del panel de administrador. */
public class ControladorAdminEstadisticas {

    private final ServicioAdminEstadisticas servicio = new ServicioAdminEstadisticas(new AdminEstadisticasDAOJdbc());

    public EstadisticasGenerales obtenerGenerales() throws SQLException {
        return servicio.obtenerGenerales();
    }

    public List<EstadisticasCurso> obtenerPorCurso() throws SQLException {
        return servicio.obtenerPorCurso();
    }

    public Map<String, Integer> obtenerRegistrosMensuales() throws SQLException {
        return servicio.obtenerRegistrosMensuales();
    }
}
