package chatbot;

import java.util.List;

/** Stub — listo para implementar cuando haya API key de OpenAI (GPT). */
public class MotorGPT implements MotorChatbot {
    @Override
    public String enviarMensaje(List<MensajeChat> historial, String contextoSistema) throws MotorChatbotException {
        throw new MotorChatbotException(
            "El proveedor 'gpt' todavía no está implementado. "
            + "Cambiá CHATBOT_PROVEEDOR a 'claude' en el archivo .env, o esperá una futura actualización.");
    }
}
