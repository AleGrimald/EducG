package modelo;

import java.util.List;

/** Modelo de datos para un curso de programación. */
public class Curso {

    private final int id;
    private final String emoji;
    private final String titulo;
    private final String descripcion;
    private final String duracion;
    private final List<Leccion> lecciones;

    public Curso(int id, String emoji, String titulo, String descripcion,
                 String duracion, List<Leccion> lecciones) {
        this.id          = id;
        this.emoji       = emoji;
        this.titulo      = titulo;
        this.descripcion = descripcion;
        this.duracion    = duracion;
        this.lecciones   = lecciones;
    }

    public int          getId()          { return id; }
    public String       getEmoji()       { return emoji; }
    public String       getTitulo()      { return titulo; }
    public String       getDescripcion() { return descripcion; }
    public String       getDuracion()    { return duracion; }
    public List<Leccion> getLecciones()  { return lecciones; }

    /** Títulos de las lecciones, para el resumen compacto de la tarjeta del catálogo. */
    public String[] getTopicos() {
        return lecciones.stream().map(Leccion::getTitulo).toArray(String[]::new);
    }
}
