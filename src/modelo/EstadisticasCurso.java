package modelo;

/** Métricas de un curso para la tabla de detalle de Estadísticas del administrador. */
public class EstadisticasCurso {

    private final int cursoId;
    private final String titulo;
    private final int inscriptos;
    private final double promedio;
    private final double tasaAprobacion;

    public EstadisticasCurso(int cursoId, String titulo, int inscriptos, double promedio, double tasaAprobacion) {
        this.cursoId        = cursoId;
        this.titulo         = titulo;
        this.inscriptos     = inscriptos;
        this.promedio       = promedio;
        this.tasaAprobacion = tasaAprobacion;
    }

    public int getCursoId()           { return cursoId; }
    public String getTitulo()          { return titulo; }
    public int getInscriptos()         { return inscriptos; }
    public double getPromedio()        { return promedio; }
    public double getTasaAprobacion()  { return tasaAprobacion; }
}
