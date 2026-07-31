package dao;

import bd.ConexionBD;
import modelo.Imagen;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Implementación de {@link ImagenDAO} sobre {@code sp_obtener_imagen_por_clave}. */
public class ImagenDAOJdbc implements ImagenDAO {

    @Override
    public Imagen obtenerPorClave(String clave) throws SQLException {
        final String sql = "{call sp_obtener_imagen_por_clave(?)}";
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, clave);
            try (ResultSet rs = cs.executeQuery()) {
                return rs.next() ? new Imagen(rs.getInt("id_imagen"), rs.getBytes("datos")) : null;
            }
        }
    }
}
