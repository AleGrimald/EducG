package modelo;

/** Inscripción activa de un usuario a un curso. */
public class Inscripcion {

    private final int cursoId;
    private final String cursoTitulo;
    private final String fechaInscripcion;

    public Inscripcion(int cursoId, String cursoTitulo, String fechaInscripcion) {
        this.cursoId          = cursoId;
        this.cursoTitulo      = cursoTitulo;
        this.fechaInscripcion = fechaInscripcion;
    }

    public int    getCursoId()          { return cursoId; }
    public String getCursoTitulo()      { return cursoTitulo; }
    public String getFechaInscripcion() { return fechaInscripcion; }
}
