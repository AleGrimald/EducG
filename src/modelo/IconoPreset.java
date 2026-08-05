package modelo;

/** Un ícono de tecnología preseleccionable en {@link vista.componentes.SelectorIconoCurso},
 * leído de la tabla {@code imagenes} (filas con {@code etiqueta} no nula). */
public final class IconoPreset {

    private final int idImagen;
    private final String clave;
    private final String etiqueta;
    private final byte[] datos;

    public IconoPreset(int idImagen, String clave, String etiqueta, byte[] datos) {
        this.idImagen = idImagen;
        this.clave = clave;
        this.etiqueta = etiqueta;
        this.datos = datos;
    }

    public int getIdImagen()    { return idImagen; }
    public String getClave()    { return clave; }
    public String getEtiqueta() { return etiqueta; }
    public byte[] getDatos()    { return datos; }
}
