package modelo;

import java.time.LocalDateTime;

/** Configuración de UI del sistema (colores, fuentes, etc). */
public class ConfiguracionUI {

    private final int id;
    private final String clave;
    private final String valor;
    private final String tipo;
    private final String descripcion;
    private final String modulo;
    private final String seccion;
    private final LocalDateTime fechaModificacion;

    public ConfiguracionUI(int id, String clave, String valor, String tipo, String descripcion,
                          String modulo, String seccion, LocalDateTime fechaModificacion) {
        this.id = id;
        this.clave = clave;
        this.valor = valor;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.modulo = modulo;
        this.seccion = seccion;
        this.fechaModificacion = fechaModificacion;
    }

    public int getId()                      { return id; }
    public String getClave()                { return clave; }
    public String getValor()                { return valor; }
    public String getTipo()                 { return tipo; }
    public String getDescripcion()          { return descripcion; }
    public String getModulo()               { return modulo; }
    public String getSeccion()              { return seccion; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
}
