package modelo;

/** Una opción de respuesta (multiple choice) de una pregunta de test. */
public class OpcionTest {

    private final int id;
    private final String texto;
    private final boolean correcta;

    public OpcionTest(int id, String texto, boolean correcta) {
        this.id = id;
        this.texto = texto;
        this.correcta = correcta;
    }

    public int getId()          { return id; }
    public String getTexto()    { return texto; }
    public boolean isCorrecta() { return correcta; }
}
