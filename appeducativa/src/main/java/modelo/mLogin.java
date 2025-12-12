package modelo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class mLogin {
    mConexion conexion = new mConexion();

    public mLogin() {}
    
    public List<String[]> SelectData() {
        List<String[]> lista = new ArrayList<>();

        try {
            try (CallableStatement stmt = conexion.Conectar().prepareCall("{CALL SelectLogin()}"); 
                 ResultSet rs = stmt.executeQuery()
            ) {
                
                while (rs.next()) {
                    String[] fila = new String[2];
                    fila[0] = rs.getString("user_name");
                    fila[1] = rs.getString("user_passw");
                    lista.add(fila);
                }
                
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                null, 
                "Error en la peticion a la base de datos: "+e, 
                "Error DB", 
                JOptionPane.INFORMATION_MESSAGE
            );
            //e.printStackTrace();
        }

        return lista;
    }
    
}
