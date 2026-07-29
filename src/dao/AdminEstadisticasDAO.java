package dao;

import modelo.EstadisticasCurso;
import modelo.EstadisticasGenerales;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Contrato de persistencia (solo lectura) para el módulo de Estadísticas del panel de administrador. */
public interface AdminEstadisticasDAO {

    EstadisticasGenerales obtenerGenerales() throws SQLException;

    List<EstadisticasCurso> obtenerPorCurso() throws SQLException;

    /** @return cantidad de alumnos registrados por mes (clave "yyyy-MM"), últimos 6 meses, en orden cronológico */
    Map<String, Integer> obtenerRegistrosMensuales() throws SQLException;
}
