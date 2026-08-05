package dao;

import modelo.ConfiguracionUI;
import java.sql.SQLException;
import java.util.List;

/** DAO para gestionar configuraciones de UI del sistema. */
public interface AdminConfiguracionUIDAO {
    List<ConfiguracionUI> listarTodas() throws SQLException;
    ConfiguracionUI obtener(String clave) throws SQLException;
    boolean actualizar(String clave, String valor) throws SQLException;
}
