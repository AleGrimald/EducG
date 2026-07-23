package chatbot;

/** Un turno de la conversación con el motor de IA. */
public class MensajeChat {

    private final String rol;       // "user" | "assistant" — coincide con los valores de la API de Claude
    private final String contenido;

    public MensajeChat(String rol, String contenido) {
        this.rol = rol;
        this.contenido = contenido;
    }

    public static MensajeChat deUsuario(String contenido)   { return new MensajeChat("user", contenido); }
    public static MensajeChat deAsistente(String contenido) { return new MensajeChat("assistant", contenido); }

    public String getRol()       { return rol; }
    public String getContenido() { return contenido; }
}
