package modelo;

/** Una fila de la tabla {@code imagenes}: el logo de la app, el ícono de ventana, o un ícono de curso. */
public final class Imagen {

    private final int id;
    private final byte[] datos;

    public Imagen(int id, byte[] datos) {
        this.id = id;
        this.datos = datos;
    }

    public int getId()      { return id; }
    public byte[] getDatos() { return datos; }
}
