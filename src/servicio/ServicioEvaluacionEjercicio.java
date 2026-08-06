package servicio;

import chatbot.FabricaMotorChatbot;
import chatbot.MensajeChat;
import chatbot.MotorChatbot;
import chatbot.MotorChatbotException;
import modelo.ResultadoEvaluacionEjercicio;

import java.util.List;

/** Evalúa con IA el código de un alumno para un ejercicio, cuando no coincide textualmente
 * con la respuesta de referencia (ver ControladorCursos.evaluarEjercicio). */
public class ServicioEvaluacionEjercicio {

    private final MotorChatbot motor = FabricaMotorChatbot.obtenerMotor();

    public ResultadoEvaluacionEjercicio evaluar(String enunciado, String respuestaEsperada, String codigoAlumno)
            throws MotorChatbotException {
        String contextoSistema = construirContextoSistema();
        String mensajeUsuario = construirMensajeUsuario(enunciado, respuestaEsperada, codigoAlumno);
        String respuesta = motor.enviarMensaje(List.of(MensajeChat.deUsuario(mensajeUsuario)), contextoSistema);
        return interpretar(respuesta);
    }

    private String construirContextoSistema() {
        return "Sos un profesor de programación evaluando el ejercicio de un alumno en la " +
            "plataforma educativa Educ G. Respondé siempre en español.\n\n" +
            "Se te va a dar el enunciado del ejercicio, una respuesta de referencia (solo " +
            "orientativa: el alumno puede resolverlo de una forma distinta e igual de válida) " +
            "y el código o respuesta que escribió el alumno.\n\n" +
            "Evaluá si la respuesta del alumno resuelve correctamente el ejercicio, aceptando " +
            "cualquier solución válida aunque no coincida textualmente con la referencia. Sé " +
            "estricto con errores reales (sintaxis rota, lógica incorrecta, no responde lo que " +
            "se pide) pero flexible con diferencias de estilo, nombres o formas alternativas " +
            "de resolver lo mismo.\n\n" +
            "Respondé SIEMPRE con este formato exacto, sin texto antes:\n" +
            "- Primera línea: únicamente la palabra CORRECTO o INCORRECTO.\n" +
            "- Después, una explicación breve (2 a 4 líneas) dirigida directamente al alumno: " +
            "si es CORRECTO, reforzá brevemente por qué; si es INCORRECTO, explicá qué está " +
            "mal y qué debería corregir, sin dar la solución completa.";
    }

    private String construirMensajeUsuario(String enunciado, String respuestaEsperada, String codigoAlumno) {
        return "Enunciado del ejercicio:\n" + enunciado +
            "\n\nRespuesta de referencia (orientativa):\n" + respuestaEsperada +
            "\n\nRespuesta del alumno:\n" + codigoAlumno;
    }

    private ResultadoEvaluacionEjercicio interpretar(String respuesta) {
        String texto = respuesta == null ? "" : respuesta.trim();
        int salto = texto.indexOf('\n');
        String primeraLinea = (salto >= 0 ? texto.substring(0, salto) : texto).trim().toUpperCase();
        String resto = (salto >= 0 ? texto.substring(salto + 1).trim() : "");
        boolean correcto = primeraLinea.contains("CORRECTO") && !primeraLinea.contains("INCORRECTO");
        return new ResultadoEvaluacionEjercicio(correcto, resto.isEmpty() ? texto : resto);
    }
}
