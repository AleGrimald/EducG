package controlador;

import dao.AdminCursoDAOJdbc;
import modelo.ItemPlanEstudio;
import modelo.OpcionTest;
import modelo.PreguntaTest;
import modelo.SeleccionIcono;
import servicio.ServicioAdminCursos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Mantiene el estado en memoria del wizard "Crear Curso" (4 pasos: datos + plan de estudio,
 * contenido teórico, ejercicio propuesto, preguntas) y lo persiste todo junto al finalizar.
 * Una instancia = un curso en construcción; se descarta al cerrar el wizard.
 */
public class ControladorCrearCurso {

    private final ServicioAdminCursos servicio = new ServicioAdminCursos(new AdminCursoDAOJdbc());

    private SeleccionIcono icono = SeleccionIcono.ninguno();
    private String titulo = "";
    private String descripcion = "";
    private String duracion = "";
    private final List<ItemPlanEstudio> items = new ArrayList<>();
    private final List<PreguntaTest> preguntas = new ArrayList<>();

    // ── Paso 1: datos básicos + plan de estudio ─────────────────────────────

    public void establecerDatosBasicos(SeleccionIcono icono, String titulo, String descripcion, String duracion) {
        ControladorAdminCursos.validarDatosBasicos(icono, titulo, descripcion, duracion);
        this.icono = icono;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.duracion = duracion;
    }

    public SeleccionIcono getIcono() { return icono; }
    public String getTitulo()      { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getDuracion()    { return duracion; }

    public void agregarItemPlan(String topico) {
        if (topico == null || topico.isBlank())
            throw new IllegalArgumentException("Ingresá un nombre para el tema.");
        if (topico.length() > 200)
            throw new IllegalArgumentException("El nombre del tema no puede superar los 200 caracteres.");
        items.add(new ItemPlanEstudio(items.size() + 1, topico.trim()));
    }

    public void eliminarItemPlan(int indice) {
        items.remove(indice);
        for (int i = 0; i < items.size(); i++) items.get(i).setOrden(i + 1);
    }

    /** Lista viva (mutable) de ítems: los pasos 2 y 3 escriben contenido/ejercicio directamente sobre ella. */
    public List<ItemPlanEstudio> obtenerItems() {
        return items;
    }

    public void validarPaso1() {
        if (titulo.isBlank())
            throw new IllegalArgumentException("Completá los datos básicos del curso antes de continuar.");
        if (items.isEmpty())
            throw new IllegalArgumentException("Agregá al menos un tema al Plan de Estudio.");
    }

    // ── Paso 2: contenido teórico (obligatorio por tema) ────────────────────

    public void validarPaso2() {
        for (ItemPlanEstudio item : items) {
            if (item.getContenido() == null || item.getContenido().isBlank())
                throw new IllegalArgumentException("Falta el contenido teórico del tema \"" + item.getTopico() + "\".");
        }
    }

    // ── Paso 3: ejercicio propuesto (opcional, pero si hay ejercicio necesita respuesta esperada) ──

    public void validarPaso3() {
        for (ItemPlanEstudio item : items) {
            if (item.getEjercicioPropuesto() != null && !item.getEjercicioPropuesto().isBlank()
                && (item.getRespuestaEsperada() == null || item.getRespuestaEsperada().isBlank())) {
                throw new IllegalArgumentException(
                    "El tema \"" + item.getTopico() + "\" tiene un ejercicio propuesto: completá también "
                    + "la respuesta esperada, o borrá el ejercicio.");
            }
        }
    }

    // ── Paso 4: preguntas multiple choice ────────────────────────────────────

    public void agregarPregunta(String enunciado, List<String> opciones, int indiceCorrecta) {
        if (enunciado == null || enunciado.isBlank())
            throw new IllegalArgumentException("Ingresá el enunciado de la pregunta.");
        List<String> textos = new ArrayList<>();
        for (String opcion : opciones) if (opcion != null && !opcion.isBlank()) textos.add(opcion.trim());
        if (textos.size() < 2)
            throw new IllegalArgumentException("Cada pregunta necesita al menos 2 opciones de respuesta.");
        if (indiceCorrecta < 0 || indiceCorrecta >= textos.size())
            throw new IllegalArgumentException("Marcá cuál opción es la correcta.");

        List<OpcionTest> opcionesTest = new ArrayList<>();
        for (int i = 0; i < textos.size(); i++) opcionesTest.add(new OpcionTest(0, textos.get(i), i == indiceCorrecta));
        preguntas.add(new PreguntaTest(0, enunciado.trim(), opcionesTest));
    }

    public void eliminarPregunta(int indice) {
        preguntas.remove(indice);
    }

    public List<PreguntaTest> obtenerPreguntas() {
        return preguntas;
    }

    public void validarPaso4() {
        if (preguntas.isEmpty())
            throw new IllegalArgumentException("Agregá al menos una pregunta antes de guardar el curso.");
    }

    // ── Guardado final ────────────────────────────────────────────────────

    /** @return el id del curso creado */
    public int guardarCurso() throws SQLException {
        validarPaso1();
        validarPaso2();
        validarPaso3();
        validarPaso4();
        int id = servicio.guardarCursoCompleto(icono, titulo, descripcion, duracion, items, preguntas);
        if (id == -1) throw new IllegalArgumentException("Ya existe un curso con el título \"" + titulo + "\".");
        return id;
    }
}
