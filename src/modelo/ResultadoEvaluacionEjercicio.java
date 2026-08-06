package modelo;

/** Resultado de evaluar la respuesta de un alumno a un ejercicio (match exacto o juicio de IA). */
public class ResultadoEvaluacionEjercicio {

    private final boolean correcto;
    private final String feedback;

    public ResultadoEvaluacionEjercicio(boolean correcto, String feedback) {
        this.correcto = correcto;
        this.feedback = feedback;
    }

    public boolean isCorrecto() {
        return correcto;
    }

    public String getFeedback() {
        return feedback;
    }
}
