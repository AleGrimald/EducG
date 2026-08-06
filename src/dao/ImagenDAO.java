package dao;

import modelo.Imagen;

import java.sql.SQLException;

/** Contrato de persistencia para la tabla {@code imagenes} (logo de la app, ícono de ventana, íconos de curso). */
public interface ImagenDAO {

    /** @return la imagen con esa clave, o null si no existe ninguna. */
    Imagen obtenerPorClave(String clave) throws SQLException;
}
