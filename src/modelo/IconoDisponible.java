package modelo;

/**
 * Un ícono de curso reutilizable, tal como lo devuelve {@code sp_listar_iconos_curso}: una fila
 * de {@code imagenes} con clave y etiqueta (los presets originales como los que suba un admin
 * desde {@code vista.componentes.SelectorIconoCurso}). Solo incluye filas con ambos campos
 * cargados — sin etiqueta no aparece como opción seleccionable.
 */
public final class IconoDisponible {

    private final String clave;
    private final String etiqueta;
    private final byte[] datos;

    public IconoDisponible(String clave, String etiqueta, byte[] datos) {
        this.clave = clave;
        this.etiqueta = etiqueta;
        this.datos = datos;
    }

    public String getClave()    { return clave; }
    public String getEtiqueta() { return etiqueta; }
    public byte[] getDatos()    { return datos; }
}
