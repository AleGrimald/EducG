package controlador;

import dao.AdminConfiguracionUIJdbc;
import modelo.ConfiguracionUI;
import servicio.ServicioAdminConfiguracionUI;
import util.Validador;

import java.sql.SQLException;
import java.util.List;

/** Controlador para configuraciones de UI del sistema. */
public class ControladorAdminConfiguracionUI {

    private final ServicioAdminConfiguracionUI servicio =
        new ServicioAdminConfiguracionUI(new AdminConfiguracionUIJdbc());

    public List<ConfiguracionUI> listar() throws SQLException {
        return servicio.listar();
    }

    public boolean actualizar(String clave, String valorNuevo) throws SQLException {
        ConfiguracionUI actual = servicio.obtener(clave);
        if (actual == null)
            throw new IllegalArgumentException("No existe la configuración \"" + clave + "\".");
        validarValor(actual.getTipo(), valorNuevo);
        return servicio.actualizar(clave, valorNuevo);
    }

    private void validarValor(String tipo, String valor) {
        if (valor == null || valor.isBlank())
            throw new IllegalArgumentException("El valor no puede estar vacío.");
        switch (tipo) {
            case "color":
                if (!valor.matches("^#[0-9A-Fa-f]{6}$"))
                    throw new IllegalArgumentException("El color debe tener el formato hexadecimal #RRGGBB.");
                break;
            case "numero":
                if (!valor.matches("^[0-9]+$"))
                    throw new IllegalArgumentException("El valor debe ser un número entero positivo.");
                break;
            case "fuente":
            case "url":
                if (valor.length() > 255 || Validador.tieneRiesgoInyeccion(valor))
                    throw new IllegalArgumentException("Valor inválido.");
                break;
        }
    }
}
