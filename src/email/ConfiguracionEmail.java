package email;

import bd.CargadorEnv;

import java.util.Properties;

public final class ConfiguracionEmail {

    private static final String HOST;
    private static final int PUERTO;
    private static final String USUARIO;
    private static final String CONTRASENA;
    private static final String DESDE_NOMBRE;

    static {
        Properties env = CargadorEnv.cargar("SMTP_HOST", "SMTP_PORT", "SMTP_USER", "SMTP_PASSWORD", "SMTP_FROM_NOMBRE");
        HOST = env.getProperty("SMTP_HOST", "smtp.gmail.com").trim();
        PUERTO = Integer.parseInt(env.getProperty("SMTP_PORT", "587").trim());
        USUARIO = env.getProperty("SMTP_USER", "").trim();
        CONTRASENA = env.getProperty("SMTP_PASSWORD", "").trim();
        DESDE_NOMBRE = env.getProperty("SMTP_FROM_NOMBRE", "Educ G").trim();
    }

    public static String obtenerHost() { return HOST; }
    public static int obtenerPuerto() { return PUERTO; }
    public static String obtenerUsuario() { return USUARIO; }
    public static String obtenerContrasena() { return CONTRASENA; }
    public static String obtenerDesdeNombre() { return DESDE_NOMBRE; }

    private ConfiguracionEmail() {}
}
