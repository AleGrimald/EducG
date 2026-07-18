package modelo;

/** Una lección dentro del contenido de un curso (título + explicación). */
public class Leccion {

    private final String titulo;
    private final String contenido;

    public Leccion(String titulo, String contenido) {
        this.titulo = titulo;
        this.contenido = contenido;
    }

    public String getTitulo()    { return titulo; }
    public String getContenido() { return contenido; }
}
