package modelo;

/**
 * Lo que eligió el admin en {@code vista.componentes.SelectorIconoCurso} al crear/modificar
 * un curso: ninguno, uno de los presets conocidos (clave de {@code imagenes.clave}), o un PNG
 * recién subido desde el disco (sus bytes todavía no existen como fila en {@code imagenes}).
 */
public final class SeleccionIcono {

    private enum Tipo { NINGUNO, CLAVE, ARCHIVO_SUBIDO }

    private static final SeleccionIcono NINGUNO = new SeleccionIcono(Tipo.NINGUNO, null, null);

    private final Tipo tipo;
    private final String clave;
    private final byte[] datos;

    private SeleccionIcono(Tipo tipo, String clave, byte[] datos) {
        this.tipo = tipo;
        this.clave = clave;
        this.datos = datos;
    }

    public static SeleccionIcono ninguno()                     { return NINGUNO; }
    public static SeleccionIcono deClave(String clave)         { return new SeleccionIcono(Tipo.CLAVE, clave, null); }
    public static SeleccionIcono deArchivoSubido(byte[] datos) { return new SeleccionIcono(Tipo.ARCHIVO_SUBIDO, null, datos); }

    public boolean esNinguno()       { return tipo == Tipo.NINGUNO; }
    public boolean esClave()         { return tipo == Tipo.CLAVE; }
    public boolean esArchivoSubido() { return tipo == Tipo.ARCHIVO_SUBIDO; }

    public String getClave() { return clave; }
    public byte[] getDatos() { return datos; }
}
