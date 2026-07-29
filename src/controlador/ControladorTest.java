package controlador;

import dao.ResultadoTestDAOJdbc;
import dao.TestPreguntasDAOJdbc;
import modelo.PreguntaTest;
import servicio.ServicioTest;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Orquesta el test final de un curso: preguntas, corrección y estado de aprobación. */
public class ControladorTest {

    private final ServicioTest servicioTest = new ServicioTest(new TestPreguntasDAOJdbc(), new ResultadoTestDAOJdbc());

    public List<PreguntaTest> obtenerPreguntas(int cursoId) throws SQLException {
        return servicioTest.obtenerPreguntas(cursoId);
    }

    /**
     * Corrige, guarda el resultado y las respuestas.
     * @param respuestas mapa preguntaId -> opcionElegidaId
     * @return puntaje obtenido (0-100)
     */
    public int corregirYGuardar(String email, int cursoId, List<PreguntaTest> preguntas,
                                 Map<Integer, Integer> respuestas) throws SQLException {
        return servicioTest.corregirYGuardar(email, cursoId, preguntas, respuestas);
    }

    public boolean estaAprobado(String email, int cursoId) throws SQLException {
        return servicioTest.estaAprobado(email, cursoId);
    }

    /** @return el mejor puntaje obtenido, o -1 si nunca rindió el test */
    public int obtenerMejorPuntaje(String email, int cursoId) throws SQLException {
        return servicioTest.obtenerMejorPuntaje(email, cursoId);
    }

    public static int puntajeAprobacion() {
        return ServicioTest.PUNTAJE_APROBACION;
    }
}
