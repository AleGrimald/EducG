package modelo;

/** Resultado de un test rendido por un usuario en un curso. */
public class ResultadoTest {

    private final String cursoTitulo;
    private final String nombreTest;
    private final int    puntaje;
    private final String fecha;

    public ResultadoTest(String cursoTitulo, String nombreTest, int puntaje, String fecha) {
        this.cursoTitulo = cursoTitulo;
        this.nombreTest  = nombreTest;
        this.puntaje     = puntaje;
        this.fecha       = fecha;
    }

    public String getCursoTitulo() { return cursoTitulo; }
    public String getNombreTest()  { return nombreTest; }
    public int    getPuntaje()     { return puntaje; }
    public String getFecha()       { return fecha; }
}
