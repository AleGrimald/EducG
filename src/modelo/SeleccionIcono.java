package modelo;

/**
 * Lo que eligió el admin en {@code vista.componentes.SelectorIconoCurso} al crear/modificar
 * un curso: ninguno, uno de los íconos reutilizables ya existentes (clave de
 * {@code imagenes.clave}), o un PNG recién subido desde el disco con su propia clave/etiqueta
 * (sus bytes todavía no existen como fila en {@code imagenes} — el DAO los inserta al guardar,
 * y a partir de ahí ese ícono queda disponible para cualquier otro curso).
 */
public final class SeleccionIcono {

    private enum Tipo { NINGUNO, CLAVE, ARCHIVO_SUBIDO }

    private static final SeleccionIcono NINGUNO = new SeleccionIcono(Tipo.NINGUNO, null, null, null);

    private final Tipo tipo;
    private final String clave;
    private final String etiqueta;
    private final byte[] datos;

    private SeleccionIcono(Tipo tipo, String clave, String etiqueta, byte[] datos) {
        this.tipo = tipo;
        this.clave = clave;
        this.etiqueta = etiqueta;
        this.datos = datos;
    }

    public static SeleccionIcono ninguno()             { return NINGUNO; }
    public static SeleccionIcono deClave(String clave) { return new SeleccionIcono(Tipo.CLAVE, clave, null, null); }

    /** {@code clave}/{@code etiqueta} son las que tipeó el admin para el nuevo ícono reutilizable. */
    public static SeleccionIcono deArchivoSubido(byte[] datos, String clave, String etiqueta) {
        return new SeleccionIcono(Tipo.ARCHIVO_SUBIDO, clave, etiqueta, datos);
    }

    public boolean esNinguno()       { return tipo == Tipo.NINGUNO; }
    public boolean esClave()         { return tipo == Tipo.CLAVE; }
    public boolean esArchivoSubido() { return tipo == Tipo.ARCHIVO_SUBIDO; }

    public String getClave()    { return clave; }
    public String getEtiqueta() { return etiqueta; }
    public byte[] getDatos()    { return datos; }
}
