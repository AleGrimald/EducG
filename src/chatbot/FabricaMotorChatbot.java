package chatbot;

/** Único punto de switch entre proveedores de IA — decide según CHATBOT_PROVEEDOR en .env. */
public final class FabricaMotorChatbot {

    private FabricaMotorChatbot() {}

    public static MotorChatbot obtenerMotor() {
        switch (ConfiguracionChatbot.obtenerProveedor()) {
            case "claude": return new MotorClaude(ConfiguracionChatbot.obtenerApiKey(), ConfiguracionChatbot.obtenerModelo());
            case "gemini": return new MotorGemini(ConfiguracionChatbot.obtenerApiKey(), ConfiguracionChatbot.obtenerModelo());
            case "gpt":    return new MotorGPT();
            case "kimi":   return new MotorKimi();
            default:
                throw new IllegalStateException(
                    "CHATBOT_PROVEEDOR desconocido: '" + ConfiguracionChatbot.obtenerProveedor()
                    + "'. Valores válidos: claude, gemini, gpt, kimi.");
        }
    }
}
