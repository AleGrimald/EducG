package servicio;

import dao.ResultadoTestDAO;
import dao.TestPreguntasDAO;
import email.EmailException;
import email.EnviadorEmail;
import modelo.PreguntaTest;
import modelo.ResultadoTestGuardado;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Casos de uso del test final de un curso: obtener preguntas, corregir, verificar aprobación
 * y enviar el certificado por email cuando corresponde. */
public class ServicioTest {

    /** Puntaje mínimo (0-100) para considerar aprobado el curso. */
    public static final int PUNTAJE_APROBACION = 60;

    private final TestPreguntasDAO preguntasDAO;
    private final ResultadoTestDAO resultadoTestDAO;
    private final EnviadorEmail enviadorEmail;

    public ServicioTest(TestPreguntasDAO preguntasDAO, ResultadoTestDAO resultadoTestDAO, EnviadorEmail enviadorEmail) {
        this.preguntasDAO = preguntasDAO;
        this.resultadoTestDAO = resultadoTestDAO;
        this.enviadorEmail = enviadorEmail;
    }

    public List<PreguntaTest> obtenerPreguntas(int cursoId) throws SQLException {
        return preguntasDAO.listarPorCurso(cursoId);
    }

    /**
     * Corrige el test contra las opciones correctas, guarda el resultado y cada respuesta elegida.
     * @param respuestas mapa preguntaId -> opcionElegidaId
     * @return el puntaje obtenido y si esta aprobación generó un certificado nuevo (primera vez
     *         que se aprueba este curso — no dispara de nuevo en reintentos ya aprobados antes)
     */
    public ResultadoTestGuardado corregirYGuardar(String email, int cursoId, List<PreguntaTest> preguntas,
                                                   Map<Integer, Integer> respuestas) throws SQLException {
        int correctas = 0;
        for (PreguntaTest pregunta : preguntas) {
            Integer opcionElegidaId = respuestas.get(pregunta.getId());
            if (opcionElegidaId != null && esOpcionCorrecta(pregunta, opcionElegidaId)) correctas++;
        }
        int puntaje = preguntas.isEmpty() ? 0 : (int) Math.round(correctas * 100.0 / preguntas.size());

        ResultadoTestGuardado guardado = resultadoTestDAO.registrarResultadoTest(email, cursoId, puntaje);
        if (guardado.getResultadoId() != -1) {
            for (PreguntaTest pregunta : preguntas) {
                Integer opcionElegidaId = respuestas.get(pregunta.getId());
                if (opcionElegidaId != null) {
                    resultadoTestDAO.registrarRespuesta(guardado.getResultadoId(), pregunta.getId(), opcionElegidaId);
                }
            }
        }
        return guardado;
    }

    /** Envía el certificado (imagen PNG ya renderizada por la vista) por email. Pensado para
     * llamarse en segundo plano — es una llamada de red real, no debe correr en el EDT. */
    public void enviarCertificadoPorEmail(String email, String nombreUsuario, String cursoTitulo,
                                           int puntaje, byte[] certificadoPng) throws EmailException {
        String asunto = "¡Certificado disponible! – " + cursoTitulo;
        String cuerpo = "Hola " + nombreUsuario + ",\n\n" +
            "¡Felicitaciones! Aprobaste el curso \"" + cursoTitulo + "\" con un puntaje de " + puntaje + " / 100.\n\n" +
            "Te adjuntamos tu certificado de finalización.\n\n" +
            "Saludos,\nEduc G";
        enviadorEmail.enviarConAdjunto(email, asunto, cuerpo, "certificado.png", certificadoPng, "image/png");
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
