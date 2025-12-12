package modelo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class mConexion {

    public mConexion() {
    }
    
    public Connection Conectar() {
        Connection conn =null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/educativa?useSSL=false&serverTimezone=UTC",
                "root",
                "78531015aA@"
            );
            return conn;

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error en la conexion a la base de datos");
            return conn;
        }
        
    }
}
