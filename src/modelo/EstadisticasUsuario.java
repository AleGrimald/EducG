package modelo;

/** Resumen estadístico del progreso de un usuario. */
public class EstadisticasUsuario {

    private final int cursosInscriptos;
    private final int testsRealizados;
    private final int promedio;

    public EstadisticasUsuario(int cursosInscriptos, int testsRealizados, int promedio) {
        this.cursosInscriptos = cursosInscriptos;
        this.testsRealizados  = testsRealizados;
        this.promedio         = promedio;
    }

    public int getCursosInscriptos() { return cursosInscriptos; }
    public int getTestsRealizados()  { return testsRealizados; }
    public int getPromedio()         { return promedio; }
}
