package dao;

import modelo.Imagen;
import modelo.IconoPreset;

import java.sql.SQLException;
import java.util.List;

/** Contrato de persistencia para la tabla {@code imagenes} (logo de la app, ícono de ventana, íconos de curso). */
public interface ImagenDAO {

    /** @return la imagen con esa clave, o null si no existe ninguna. */
    Imagen obtenerPorClave(String clave) throws SQLException;

    /** @return los íconos de tecnología preseleccionables en {@code SelectorIconoCurso} (filas de
     * {@code imagenes} con {@code etiqueta} no nula), en el orden en que deben listarse. */
    List<IconoPreset> listarPresets() throws SQLException;
}
