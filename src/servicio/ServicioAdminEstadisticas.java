package servicio;

import dao.AdminEstadisticasDAO;
import modelo.EstadisticasCurso;
import modelo.EstadisticasGenerales;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Casos de uso (solo lectura) del módulo de Estadísticas del panel de administrador. */
public class ServicioAdminEstadisticas {

    private final AdminEstadisticasDAO adminEstadisticasDAO;

    public ServicioAdminEstadisticas(AdminEstadisticasDAO adminEstadisticasDAO) {
        this.adminEstadisticasDAO = adminEstadisticasDAO;
    }

    public EstadisticasGenerales obtenerGenerales() throws SQLException {
        return adminEstadisticasDAO.obtenerGenerales();
    }

    public List<EstadisticasCurso> obtenerPorCurso() throws SQLException {
        return adminEstadisticasDAO.obtenerPorCurso();
    }

    public Map<String, Integer> obtenerRegistrosMensuales() throws SQLException {
        return adminEstadisticasDAO.obtenerRegistrosMensuales();
    }
}
