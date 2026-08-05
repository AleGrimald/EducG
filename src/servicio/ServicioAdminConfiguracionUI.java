package servicio;

import dao.AdminConfiguracionUIDAO;
import modelo.ConfiguracionUI;

import java.sql.SQLException;
import java.util.List;

/** Servicio para gestionar configuraciones de UI del sistema. */
public class ServicioAdminConfiguracionUI {

    private final AdminConfiguracionUIDAO dao;

    public ServicioAdminConfiguracionUI(AdminConfiguracionUIDAO dao) {
        this.dao = dao;
    }

    public List<ConfiguracionUI> listar() throws SQLException {
        return dao.listarTodas();
    }

    public ConfiguracionUI obtener(String clave) throws SQLException {
        return dao.obtener(clave);
    }

    public boolean actualizar(String clave, String valor) throws SQLException {
        return dao.actualizar(clave, valor);
    }
}
