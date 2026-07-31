package modelo;

/**
 * Un ítem del Plan de Estudio de un curso (una fila de {@code curso_contenidos}).
 * Mutable: se usa como estado en memoria a lo largo de los pasos del wizard
 * "Crear Curso" (el tópico se define en el paso 1, el contenido en el paso 2,
 * el ejercicio propuesto — opcional — en el paso 3).
 */
public class ItemPlanEstudio {

    /** -1 = todavía no persistido (ítem en construcción dentro del wizard). */
    private int id = -1;
    private int orden;
    private String topico;
    private String contenido = "";
    private String ejercicioPropuesto;
    private String respuestaEsperada;
    private boolean activo = true;

    public ItemPlanEstudio(int orden, String topico) {
        this.orden  = orden;
        this.topico = topico;
    }

    /** Ítem ya persistido, tal como se lista desde el panel de administrador. */
    public ItemPlanEstudio(int id, int orden, String topico, String contenido,
                            String ejercicioPropuesto, String respuestaEsperada, boolean activo) {
        this.id = id;
        this.orden = orden;
        this.topico = topico;
        this.contenido = contenido;
        this.ejercicioPropuesto = ejercicioPropuesto;
        this.respuestaEsperada = respuestaEsperada;
        this.activo = activo;
    }

    public int getId()                         { return id; }

    public boolean isActivo()                   { return activo; }

    public int getOrden()                     { return orden; }
    public void setOrden(int orden)            { this.orden = orden; }

    public String getTopico()                  { return topico; }
    public void setTopico(String topico)        { this.topico = topico; }

    public String getContenido()                { return contenido; }
    public void setContenido(String contenido)   { this.contenido = contenido; }

    public String getEjercicioPropuesto()               { return ejercicioPropuesto; }
    public void setEjercicioPropuesto(String ejercicio)  { this.ejercicioPropuesto = ejercicio; }

    public String getRespuestaEsperada()                { return respuestaEsperada; }
    public void setRespuestaEsperada(String respuesta)   { this.respuestaEsperada = respuesta; }
}
