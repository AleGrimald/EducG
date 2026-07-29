package dao;

import modelo.EstadisticasUsuario;
import modelo.ResultadoTest;
import java.sql.SQLException;
import java.util.List;

/** Contrato de persistencia para resultados de tests y estadísticas. Implementado sobre stored procedures. */
public interface ResultadoTestDAO {

    /** @return resultados del usuario, más recientes primero */
    List<ResultadoTest> listarPorUsuario(String email) throws SQLException;

    EstadisticasUsuario obtenerEstadisticas(String email) throws SQLException;

    /** @return el id del resultado creado */
    int registrarResultadoTest(String email, int cursoId, int puntaje) throws SQLException;

    void registrarRespuesta(int testResultadoId, int preguntaId, int opcionElegidaId) throws SQLException;

    /** @return el mejor puntaje del usuario en ese curso, o -1 si nunca lo rindió */
    int obtenerMejorPuntaje(String email, int cursoId) throws SQLException;
}
