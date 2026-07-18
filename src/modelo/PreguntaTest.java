package modelo;

import java.util.List;

/** Una pregunta de multiple choice del test final de un curso. */
public class PreguntaTest {

    private final int id;
    private final String enunciado;
    private final List<OpcionTest> opciones;

    public PreguntaTest(int id, String enunciado, List<OpcionTest> opciones) {
        this.id = id;
        this.enunciado = enunciado;
        this.opciones = opciones;
    }

    public int getId()                    { return id; }
    public String getEnunciado()          { return enunciado; }
    public List<OpcionTest> getOpciones() { return opciones; }
}
