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
        HOST     = env.getProperty("DB_HOST", "");
        PORT     = Integer.parseInt(env.getProperty("DB_PORT", ""));
        DATABASE = env.getProperty("DB_DATABASE", "");
        USER     = env.getProperty("DB_USER", "");
        PASSWORD = env.getProperty("DB_PASSWORD", "");
    }

    private static java.util.Properties loadEnv() {
        return CargadorEnv.cargar("DB_HOST", "DB_PORT", "DB_DATABASE", "DB_USER", "DB_PASSWORD");
    }

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
                    + "?useSSL=false&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8"
                    + "&connectionCollation=utf8mb4_unicode_ci";

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
