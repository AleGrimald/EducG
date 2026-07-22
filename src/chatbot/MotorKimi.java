package chatbot;

import java.util.List;

/** Stub — listo para implementar cuando haya API key de Kimi. */
public class MotorKimi implements MotorChatbot {
    @Override
    public String enviarMensaje(List<MensajeChat> historial, String contextoSistema) throws MotorChatbotException {
        throw new MotorChatbotException(
            "El proveedor 'kimi' todavía no está implementado. "
            + "Cambiá CHATBOT_PROVEEDOR a 'claude' en el archivo .env, o esperá una futura actualización.");
    }
}
