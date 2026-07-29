package modelo;

/** Datos de un curso para el panel de administrador (incluye id/estado, a diferencia de {@link Curso}). */
public class CursoAdmin {

    private final int id;
    private final String emoji;
    private final String titulo;
    private final String descripcion;
    private final String duracion;
    private final boolean activo;

    public CursoAdmin(int id, String emoji, String titulo, String descripcion, String duracion, boolean activo) {
        this.id          = id;
        this.emoji       = emoji;
        this.titulo      = titulo;
        this.descripcion = descripcion;
        this.duracion    = duracion;
        this.activo      = activo;
    }

    public int getId()             { return id; }
    public String getEmoji()       { return emoji; }
    public String getTitulo()      { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getDuracion()    { return duracion; }
    public boolean isActivo()      { return activo; }
}
