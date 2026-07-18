package controlador;

import dao.ResultadoTestDAOJdbc;
import dao.TestPreguntasDAOJdbc;
import modelo.PreguntaTest;
import servicio.ServicioTest;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Orquesta el test final de un curso: preguntas, corrección y estado de aprobación. */
public class ControladorTest {

    private final ServicioTest servicioTest = new ServicioTest(new TestPreguntasDAOJdbc(), new ResultadoTestDAOJdbc());

    public List<PreguntaTest> obtenerPreguntas(String cursoTitulo) throws SQLException {
        return servicioTest.obtenerPreguntas(cursoTitulo);
    }

    /**
     * Corrige, guarda el resultado y las respuestas.
     * @param respuestas mapa preguntaId -> opcionElegidaId
     * @return puntaje obtenido (0-100)
     */
    public int corregirYGuardar(String email, String cursoTitulo, List<PreguntaTest> preguntas,
                                 Map<Integer, Integer> respuestas) throws SQLException {
        return servicioTest.corregirYGuardar(email, cursoTitulo, preguntas, respuestas);
    }

    public boolean estaAprobado(String email, String cursoTitulo) throws SQLException {
        return servicioTest.estaAprobado(email, cursoTitulo);
    }

    /** @return el mejor puntaje obtenido, o -1 si nunca rindió el test */
    public int obtenerMejorPuntaje(String email, String cursoTitulo) throws SQLException {
        return servicioTest.obtenerMejorPuntaje(email, cursoTitulo);
    }

    public static int puntajeAprobacion() {
        return ServicioTest.PUNTAJE_APROBACION;
    }
}
