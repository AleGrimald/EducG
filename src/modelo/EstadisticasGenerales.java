package modelo;

/** Totales generales para el dashboard de Estadísticas del administrador. */
public class EstadisticasGenerales {

    private final int alumnosActivos;
    private final int alumnosInactivos;
    private final int cursosActivos;
    private final int inscripcionesActivas;
    private final int aprobadosTotales;

    public EstadisticasGenerales(int alumnosActivos, int alumnosInactivos, int cursosActivos,
                                  int inscripcionesActivas, int aprobadosTotales) {
        this.alumnosActivos       = alumnosActivos;
        this.alumnosInactivos     = alumnosInactivos;
        this.cursosActivos        = cursosActivos;
        this.inscripcionesActivas = inscripcionesActivas;
        this.aprobadosTotales     = aprobadosTotales;
    }

    public int getAlumnosActivos()       { return alumnosActivos; }
    public int getAlumnosInactivos()     { return alumnosInactivos; }
    public int getCursosActivos()        { return cursosActivos; }
    public int getInscripcionesActivas() { return inscripcionesActivas; }
    public int getAprobadosTotales()     { return aprobadosTotales; }
}
