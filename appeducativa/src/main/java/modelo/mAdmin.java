package modelo;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class mAdmin {
    private final mConexion conexion = new mConexion();
    
    public mAdmin(){
        
    }
    
    public boolean InsertAlumno(String ape, String nom, String user, String pass, String tel, String mail, String cursos){
        String sql = "{CALL InsertAlumno(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = conexion.Conectar();
            CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, ape);
            stmt.setString(2, nom);
            stmt.setString(3, user);
            stmt.setString(4, pass);
            stmt.setString(5, tel);
            stmt.setString(6, mail);
            stmt.setString(7, cursos);

            stmt.execute();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al ejecutar el procedimiento almacenado: " + e.getMessage());
            return false;
        }
    }
}
