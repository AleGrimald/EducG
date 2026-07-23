package chatbot;

import java.util.List;

/** Abstracción del motor de IA que responde el chat — intercambiable vía {@link FabricaMotorChatbot}. */
public interface MotorChatbot {

    /**
     * Envía el historial de conversación + contexto del sistema al motor de IA
     * y devuelve la respuesta en texto plano.
     *
     * @param historial mensajes previos en orden cronológico, incluyendo el último mensaje del usuario
     * @param contextoSistema instrucciones + datos de contexto (cursos, progreso, aprobación)
     */
    String enviarMensaje(List<MensajeChat> historial, String contextoSistema) throws MotorChatbotException;
}
