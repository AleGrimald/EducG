package bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Gestiona la conexión JDBC con MySQL. Lee credenciales desde .env. */
public class ConexionBD {

    private static final String HOST;
    private static final int    PORT;
    private static final String DATABASE;
    private static final String USER;
    private static final String PASSWORD;

    static {
        java.util.Properties env = loadEnv();
        HOST     = env.getProperty("DB_HOST", "localhost");
        PORT     = Integer.parseInt(env.getProperty("DB_PORT", "3306"));
        DATABASE = env.getProperty("DB_DATABASE", "educg_db");
        USER     = env.getProperty("DB_USER", "root");
        PASSWORD = env.getProperty("DB_PASSWORD", "78531015aA@");
    }

    private static java.util.Properties loadEnv() {
        // Busca .env en: working dir, directorio del .class, raíz del proyecto
        String[] candidates = {
            ".env",
            System.getProperty("user.dir") + java.io.File.separator + ".env",
            new java.io.File(ConexionBD.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath())
                .getParentFile().getParent() + java.io.File.separator + ".env"
        };
        java.util.Properties props = new java.util.Properties();
        for (String path : candidates) {
            java.io.File f = new java.io.File(path);
            if (f.exists()) {
                try (java.io.InputStream in = new java.io.FileInputStream(f)) {
                    props.load(in);
                    return props;
                } catch (java.io.IOException ignored) {}
            }
        }
        // Fallback: variables de entorno del sistema operativo
        for (String key : new String[]{"DB_HOST","DB_PORT","DB_DATABASE","DB_USER","DB_PASSWORD"}) {
            String val = System.getenv(key);
            if (val != null) props.setProperty(key, val);
        }
        return props;
    }

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC&characterEncoding=UTF-8";

    private static Connection connection;

    private ConexionBD() {}

    public static Connection obtenerConexion() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "Driver MySQL no encontrado.\n"
                + "Agregue mysql-connector-java al proyecto (File > Project Structure > Libraries).", e);
        }
        return connection;
    }

    public static void cerrarConexion() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException ignored) {}
            connection = null;
        }
    }
}
