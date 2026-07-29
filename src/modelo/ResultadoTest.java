package modelo;

/** Resultado de un test rendido por un usuario en un curso. */
public class ResultadoTest {

    private final String cursoTitulo;
    private final int    puntaje;
    private final String fecha;

    public ResultadoTest(String cursoTitulo, int puntaje, String fecha) {
        this.cursoTitulo = cursoTitulo;
        this.puntaje     = puntaje;
        this.fecha       = fecha;
    }

    public String getCursoTitulo() { return cursoTitulo; }
    public int    getPuntaje()     { return puntaje; }
    public String getFecha()       { return fecha; }
}
