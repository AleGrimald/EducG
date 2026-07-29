package servicio;

import dao.ResultadoTestDAO;
import dao.TestPreguntasDAO;
import modelo.PreguntaTest;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Casos de uso del test final de un curso: obtener preguntas, corregir y verificar aprobación. */
public class ServicioTest {

    /** Puntaje mínimo (0-100) para considerar aprobado el curso. */
    public static final int PUNTAJE_APROBACION = 60;

    private final TestPreguntasDAO preguntasDAO;
    private final ResultadoTestDAO resultadoTestDAO;

    public ServicioTest(TestPreguntasDAO preguntasDAO, ResultadoTestDAO resultadoTestDAO) {
        this.preguntasDAO = preguntasDAO;
        this.resultadoTestDAO = resultadoTestDAO;
    }

    public List<PreguntaTest> obtenerPreguntas(int cursoId) throws SQLException {
        return preguntasDAO.listarPorCurso(cursoId);
    }

    /**
     * Corrige el test contra las opciones correctas, guarda el resultado y cada respuesta elegida.
     * @param respuestas mapa preguntaId -> opcionElegidaId
     * @return puntaje obtenido (0-100)
     */
    public int corregirYGuardar(String email, int cursoId, List<PreguntaTest> preguntas,
                                 Map<Integer, Integer> respuestas) throws SQLException {
        int correctas = 0;
        for (PreguntaTest pregunta : preguntas) {
            Integer opcionElegidaId = respuestas.get(pregunta.getId());
            if (opcionElegidaId != null && esOpcionCorrecta(pregunta, opcionElegidaId)) correctas++;
        }
        int puntaje = preguntas.isEmpty() ? 0 : (int) Math.round(correctas * 100.0 / preguntas.size());

        int resultadoId = resultadoTestDAO.registrarResultadoTest(email, cursoId, puntaje);
        if (resultadoId != -1) {
            for (PreguntaTest pregunta : preguntas) {
                Integer opcionElegidaId = respuestas.get(pregunta.getId());
                if (opcionElegidaId != null) {
                    resultadoTestDAO.registrarRespuesta(resultadoId, pregunta.getId(), opcionElegidaId);
                }
            }
        }
        return puntaje;
    }

    private boolean esOpcionCorrecta(PreguntaTest pregunta, int opcionElegidaId) {
        return pregunta.getOpciones().stream()
            .anyMatch(o -> o.getId() == opcionElegidaId && o.isCorrecta());
    }

    public boolean estaAprobado(String email, int cursoId) throws SQLException {
        return resultadoTestDAO.obtenerMejorPuntaje(email, cursoId) >= PUNTAJE_APROBACION;
    }

    /** @return el mejor puntaje obtenido, o -1 si nunca rindió el test */
    public int obtenerMejorPuntaje(String email, int cursoId) throws SQLException {
        return resultadoTestDAO.obtenerMejorPuntaje(email, cursoId);
    }
}
