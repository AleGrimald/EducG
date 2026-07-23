package chatbot;

import bd.CargadorEnv;

import java.util.Properties;

/** Configuración del chatbot leída de .env: qué proveedor de IA usar y con qué credenciales. */
public final class ConfiguracionChatbot {

    private static final String PROVEEDOR;
    private static final String API_KEY;
    private static final String MODELO;

    static {
        Properties env = CargadorEnv.cargar("CHATBOT_PROVEEDOR", "CHATBOT_API_KEY", "CHATBOT_MODELO");
        PROVEEDOR = env.getProperty("CHATBOT_PROVEEDOR", "claude").trim().toLowerCase();
        API_KEY   = env.getProperty("CHATBOT_API_KEY", "");
        MODELO    = env.getProperty("CHATBOT_MODELO", modeloPorDefecto(PROVEEDOR));
    }

    private static String modeloPorDefecto(String proveedor) {
        switch (proveedor) {
            case "claude": return "claude-opus-4-8";
            case "gemini": return "gemini-flash-latest"; // alias siempre vigente; "gemini-2.5-flash" da 404 en cuentas nuevas
            case "gpt":    return "gpt-4o";          // sin efecto hasta implementar MotorGPT
            case "kimi":   return "kimi-k2";          // sin efecto hasta implementar MotorKimi
            default:       return "";
        }
    }

    public static String obtenerProveedor() { return PROVEEDOR; }
    public static String obtenerApiKey()    { return API_KEY; }
    public static String obtenerModelo()    { return MODELO; }

    private ConfiguracionChatbot() {}
}
