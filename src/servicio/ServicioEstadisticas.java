package servicio;

import dao.ResultadoTestDAO;
import modelo.EstadisticasUsuario;
import modelo.ResultadoTest;

import java.sql.SQLException;
import java.util.List;

/** Casos de uso de estadísticas de progreso e historial de tests. */
public class ServicioEstadisticas {

    private final ResultadoTestDAO resultadoTestDAO;

    public ServicioEstadisticas(ResultadoTestDAO resultadoTestDAO) {
        this.resultadoTestDAO = resultadoTestDAO;
    }

    public EstadisticasUsuario obtenerEstadisticas(String email) throws SQLException {
        return resultadoTestDAO.obtenerEstadisticas(email);
    }

    public List<ResultadoTest> obtenerResultadosTests(String email) throws SQLException {
        return resultadoTestDAO.listarPorUsuario(email);
    }
}
