package dao;

import modelo.PreguntaTest;

import java.sql.SQLException;
import java.util.List;

/** Contrato de persistencia para el banco de preguntas del test final de un curso. */
public interface TestPreguntasDAO {

    List<PreguntaTest> listarPorCurso(String cursoTitulo) throws SQLException;
}
