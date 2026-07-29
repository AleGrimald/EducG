package modelo;

/** Una lección dentro del contenido de un curso (título + explicación), con un ejercicio propuesto opcional. */
public class Leccion {

    private final String titulo;
    private final String contenido;
    private final String ejercicioPropuesto;
    private final String respuestaEsperada;

    public Leccion(String titulo, String contenido, String ejercicioPropuesto, String respuestaEsperada) {
        this.titulo             = titulo;
        this.contenido          = contenido;
        this.ejercicioPropuesto = ejercicioPropuesto;
        this.respuestaEsperada  = respuestaEsperada;
    }

    public String getTitulo()             { return titulo; }
    public String getContenido()          { return contenido; }
    public String getEjercicioPropuesto() { return ejercicioPropuesto; }
    public String getRespuestaEsperada()  { return respuestaEsperada; }

    /** @return true si esta lección tiene un ejercicio propuesto para resolver antes de avanzar */
    public boolean tieneEjercicio() {
        return ejercicioPropuesto != null && !ejercicioPropuesto.isBlank();
    }
}
