package dao;

import bd.ConexionBD;
import modelo.Imagen;
import modelo.IconoPreset;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Implementación de {@link ImagenDAO} sobre {@code sp_obtener_imagen_por_clave} / {@code sp_listar_iconos_preset}. */
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

    @Override
    public List<IconoPreset> listarPresets() throws SQLException {
        final String sql = "{call sp_listar_iconos_preset()}";
        List<IconoPreset> presets = new ArrayList<>();
        try (Connection conn = ConexionBD.obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                presets.add(new IconoPreset(rs.getInt("id_imagen"), rs.getString("clave"),
                    rs.getString("etiqueta"), rs.getBytes("datos")));
            }
        }
        return presets;
    }
}
