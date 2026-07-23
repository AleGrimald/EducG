package chatbot;

/** Falla del motor de IA: red, autenticación, parseo de respuesta o proveedor no implementado. */
public class MotorChatbotException extends Exception {
    public MotorChatbotException(String mensaje) { super(mensaje); }
    public MotorChatbotException(String mensaje, Throwable causa) { super(mensaje, causa); }
}
