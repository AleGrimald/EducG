package servicio;

import chatbot.MensajeChat;
import chatbot.MotorChatbot;
import chatbot.MotorChatbotException;
import modelo.Curso;
import modelo.Inscripcion;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Arma el contexto del usuario (cursos, progreso, aprobación) y conversa con el motor de IA configurado. */
public class ServicioChatbot {

    private final ServicioInscripcion servicioInscripcion;
    private final ServicioCursos servicioCursos;
    private final ServicioTest servicioTest;
    private final MotorChatbot motor;

    /** Historial de la conversación actual — vive tanto como esta instancia (una por ventana de chat abierta). */
    private final List<MensajeChat> historial = new ArrayList<>();

    public ServicioChatbot(ServicioInscripcion servicioInscripcion, ServicioCursos servicioCursos,
                            ServicioTest servicioTest, MotorChatbot motor) {
        this.servicioInscripcion = servicioInscripcion;
        this.servicioCursos = servicioCursos;
        this.servicioTest = servicioTest;
        this.motor = motor;
    }

    public String responder(String email, String cursoTituloActual, String pregunta)
            throws SQLException, MotorChatbotException {
        String contexto = construirContexto(email, cursoTituloActual);
        historial.add(MensajeChat.deUsuario(pregunta));
        String respuesta = motor.enviarMensaje(historial, contexto);
        historial.add(MensajeChat.deAsistente(respuesta));
        return respuesta;
    }

    private String construirContexto(String email, String cursoTituloActual) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("Sos 'Robotito', el asistente virtual de la plataforma educativa Educ G. ")
          .append("Respondé siempre en español, de forma breve, cordial y concreta. ")
          .append("Usá únicamente la información provista abajo sobre el usuario; ")
          .append("no inventes contenido de lecciones que no se te haya dado.\n\n");

        List<Inscripcion> inscripciones = servicioInscripcion.obtenerCursosInscriptos(email);
        if (inscripciones.isEmpty()) {
            sb.append("El usuario todavía no está inscripto en ningún curso.\n");
        } else {
            sb.append("Cursos en los que el usuario está inscripto:\n");
            for (Inscripcion insc : inscripciones) {
                String titulo = insc.getCursoTitulo();
                sb.append("- ").append(titulo);
                try {
                    Curso curso = servicioCursos.buscarPorTitulo(titulo);
                    int progreso = servicioInscripcion.obtenerProgreso(email, insc.getCursoId());
                    int totalLecciones = (curso != null) ? curso.getLecciones().size() : 0;
                    int mejorPuntaje = servicioTest.obtenerMejorPuntaje(email, insc.getCursoId());
                    sb.append(" — lección ").append(progreso + 1).append(" de ").append(totalLecciones);
                    if (mejorPuntaje == -1) {
                        sb.append(", todavía no rindió el test final");
                    } else {
                        boolean aprobado = servicioTest.estaAprobado(email, insc.getCursoId());
                        sb.append(", test final: ").append(aprobado ? "APROBADO" : "no aprobado")
                          .append(" (mejor puntaje ").append(mejorPuntaje).append("/100)");
                    }
                } catch (SQLException e) {
                    sb.append(" (no se pudo cargar el detalle)");
                }
                sb.append("\n");
            }
        }

        if (cursoTituloActual != null && !cursoTituloActual.isBlank()) {
            sb.append("\nEl usuario está viendo actualmente el curso: ").append(cursoTituloActual)
              .append(". Priorizá tus respuestas sobre este curso salvo que se pregunte otra cosa.\n");
        }

        return sb.toString();
    }
}
