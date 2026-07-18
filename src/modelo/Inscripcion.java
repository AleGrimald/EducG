package modelo;

/** Inscripción activa de un usuario a un curso. */
public class Inscripcion {

    private final String cursoTitulo;
    private final String fechaInscripcion;

    public Inscripcion(String cursoTitulo, String fechaInscripcion) {
        this.cursoTitulo      = cursoTitulo;
        this.fechaInscripcion = fechaInscripcion;
    }

    public String getCursoTitulo()      { return cursoTitulo; }
    public String getFechaInscripcion() { return fechaInscripcion; }
}
