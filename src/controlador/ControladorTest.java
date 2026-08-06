package controlador;

import dao.ResultadoTestDAOJdbc;
import dao.TestPreguntasDAOJdbc;
import email.EmailException;
import email.EnviadorEmail;
import modelo.PreguntaTest;
import modelo.ResultadoTestGuardado;
import servicio.ServicioTest;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Orquesta el test final de un curso: preguntas, corrección, estado de aprobación y envío del certificado. */
public class ControladorTest {

    private final ServicioTest servicioTest =
        new ServicioTest(new TestPreguntasDAOJdbc(), new ResultadoTestDAOJdbc(), new EnviadorEmail());

    public List<PreguntaTest> obtenerPreguntas(int cursoId) throws SQLException {
        return servicioTest.obtenerPreguntas(cursoId);
    }

    /**
     * Corrige, guarda el resultado y las respuestas.
     * @param respuestas mapa preguntaId -> opcionElegidaId
     * @return puntaje obtenido y si esta aprobación generó un certificado nuevo
     */
    public ResultadoTestGuardado corregirYGuardar(String email, int cursoId, List<PreguntaTest> preguntas,
                                                   Map<Integer, Integer> respuestas) throws SQLException {
        return servicioTest.corregirYGuardar(email, cursoId, preguntas, respuestas);
    }

    /** Envía el certificado (imagen PNG ya renderizada por la vista) por email. */
    public void enviarCertificadoPorEmail(String email, String nombreUsuario, String cursoTitulo,
                                           int puntaje, byte[] certificadoPng) throws EmailException {
        servicioTest.enviarCertificadoPorEmail(email, nombreUsuario, cursoTitulo, puntaje, certificadoPng);
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
