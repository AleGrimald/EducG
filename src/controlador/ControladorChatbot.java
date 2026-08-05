package controlador;

import chatbot.FabricaMotorChatbot;
import chatbot.MotorChatbotException;
import dao.CursoDAOJdbc;
import dao.InscripcionDAOJdbc;
import dao.ResultadoTestDAOJdbc;
import dao.TestPreguntasDAOJdbc;
import email.EnviadorEmail;
import servicio.ServicioChatbot;
import servicio.ServicioCursos;
import servicio.ServicioInscripcion;
import servicio.ServicioTest;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class ControladorChatbot {

    private static final int LONGITUD_MAXIMA_MENSAJE = 2000;

    /** Bytes de control / null-byte — deliberadamente NO se usa Validador.tieneRiesgoInyeccion()
     *  porque bloquea comillas, guiones, '=', '<', '>', '&', '%', '#', lo que rompería
     *  cualquier pregunta en lenguaje natural normal. */
    private static final Pattern CARACTERES_NO_PERMITIDOS =
        Pattern.compile("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]");

    private final ServicioChatbot servicioChatbot = new ServicioChatbot(
        new ServicioInscripcion(new InscripcionDAOJdbc()),
        new ServicioCursos(new CursoDAOJdbc()),
        new ServicioTest(new TestPreguntasDAOJdbc(), new ResultadoTestDAOJdbc(), new EnviadorEmail()),
        FabricaMotorChatbot.obtenerMotor()
    );

    public String enviarPregunta(String email, String cursoTituloActual, String pregunta)
            throws SQLException, MotorChatbotException {
        if (pregunta == null || pregunta.trim().isEmpty())
            throw new IllegalArgumentException("Escribí una pregunta antes de enviar.");
        String texto = pregunta.trim();
        if (texto.length() > LONGITUD_MAXIMA_MENSAJE)
            throw new IllegalArgumentException(
                "El mensaje es demasiado largo (máx. " + LONGITUD_MAXIMA_MENSAJE + " caracteres).");
        if (CARACTERES_NO_PERMITIDOS.matcher(texto).find())
            throw new IllegalArgumentException("El mensaje contiene caracteres no permitidos.");
        return servicioChatbot.responder(email, cursoTituloActual, texto);
    }
}
