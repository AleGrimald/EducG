package vista;

import chatbot.MotorChatbotException;
import controlador.ControladorCursos;
import controlador.ControladorTest;
import modelo.Curso;
import modelo.Leccion;
import modelo.ResultadoEvaluacionEjercicio;
import vista.componentes.IconoCurso;
import vista.componentes.IconoVectorial;
import vista.componentes.PanelEditorCodigo;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Muestra el contenido de un curso al que el usuario ya está inscripto: alterna clases y,
 * cuando una clase tiene ejercicio propuesto, un paso de ejercicio antes de poder avanzar a la
 * siguiente clase (el botón "Siguiente" queda deshabilitado hasta resolverlo correctamente).
 */
public class VentanaContenidoCurso extends VentanaBase {

    private final ControladorCursos controladorCursos = new ControladorCursos();
    private final ControladorTest controladorTest = new ControladorTest();
    private final Curso curso;
    private final String emailUsuario;
    private final String nombreUsuario;
    private final Runnable alVolver;

    private final List<Paso> pasos;
    private final Set<Integer> ejerciciosResueltos = new HashSet<>();
    private int pasoActual = 0;
    private JPanel panelContenido;

    public VentanaContenidoCurso(Curso curso, String emailUsuario, String nombreUsuario, Runnable alVolver) {
        super("Educ G", EXIT_ON_CLOSE);
        this.curso = curso;
        this.emailUsuario = emailUsuario;
        this.nombreUsuario = nombreUsuario;
        this.alVolver = alVolver;
        this.pasos = construirPasos();
        cargarProgreso();
        construirUI();
        FabricaUI.establecerIconoVentana(this);
        activarBurbujaChatbot(emailUsuario, curso.getTitulo());
    }

    /** Un paso del circuito: la clase en sí, o (si la clase tiene ejercicio propuesto) su ejercicio, justo después. */
    private static class Paso {
        final int leccionIndex;
        final boolean esEjercicio;
        Paso(int leccionIndex, boolean esEjercicio) {
            this.leccionIndex = leccionIndex;
            this.esEjercicio = esEjercicio;
        }
    }

    private List<Paso> construirPasos() {
        List<Paso> resultado = new ArrayList<>();
        List<Leccion> lecciones = curso.getLecciones();
        for (int i = 0; i < lecciones.size(); i++) {
            resultado.add(new Paso(i, false));
            if (lecciones.get(i).tieneEjercicio()) resultado.add(new Paso(i, true));
        }
        return resultado;
    }

    private void cargarProgreso() {
        int leccionGuardada;
        try {
            leccionGuardada = controladorCursos.obtenerProgreso(emailUsuario, curso.getId());
        } catch (Exception ignored) {
            leccionGuardada = 0;
        }
        leccionGuardada = Math.max(0, Math.min(leccionGuardada, curso.getLecciones().size() - 1));

        // Reanuda siempre en la clase (nunca a mitad de un ejercicio de una sesión anterior).
        pasoActual = 0;
        for (int i = 0; i < pasos.size(); i++) {
            if (!pasos.get(i).esEjercicio && pasos.get(i).leccionIndex == leccionGuardada) {
                pasoActual = i;
                break;
            }
        }
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new BorderLayout());
        setContentPane(raiz);

        raiz.add(construirEncabezado(), BorderLayout.NORTH);
        panelContenido = new JPanel(new BorderLayout());
        raiz.add(panelContenido, BorderLayout.CENTER);
        reconstruirContenido();
    }

    private JPanel construirEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(true);
        encabezado.setBackground(EstiloUI.FONDO_SUAVE);
        encabezado.setBorder(new EmptyBorder(24, 32, 16, 32));

        JPanel bloqueTitulo = new JPanel();
        bloqueTitulo.setOpaque(false);
        bloqueTitulo.setLayout(new BoxLayout(bloqueTitulo, BoxLayout.Y_AXIS));

        JPanel filaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filaTitulo.setOpaque(false);
        filaTitulo.setAlignmentX(LEFT_ALIGNMENT);
        filaTitulo.add(IconoCurso.crearEtiqueta(curso, 36));
        JLabel tituloLbl = new JLabel(curso.getTitulo());
        tituloLbl.setFont(EstiloUI.FUENTE_TITULO_COMPACTO);
        tituloLbl.setForeground(EstiloUI.TEXTO_PRIMARIO);
        filaTitulo.add(tituloLbl);

        JLabel duracionLbl = new JLabel("⏱ " + curso.getDuracion() + "  ·  " + curso.getDescripcion());
        duracionLbl.setFont(EstiloUI.FUENTE_SUBTITULO_COMPACTO);
        duracionLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);

        bloqueTitulo.add(filaTitulo);
        bloqueTitulo.add(Box.createVerticalStrut(4));
        bloqueTitulo.add(duracionLbl);

        JButton botonVolver = FabricaUI.crearBotonSecundarioPequeno("Volver", IconoVectorial.Tipo.VOLVER);
        botonVolver.addActionListener(e -> {
            if (!iniciarTransicionUnica()) return;
            dispose();
            alVolver.run();
        });

        encabezado.add(bloqueTitulo, BorderLayout.WEST);
        encabezado.add(botonVolver, BorderLayout.EAST);
        return encabezado;
    }

    private Component construirContenido() {
        JPanel contenedorCentral = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contenedorCentral.setBackground(EstiloUI.FONDO_SUAVE);

        JPanel contenido = new JPanel();
        contenido.setBackground(EstiloUI.FONDO_SUAVE);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        contenido.add(construirBarraEvaluacion());
        contenido.add(Box.createVerticalStrut(24));

        Paso paso = pasos.get(pasoActual);
        Leccion leccion = curso.getLecciones().get(paso.leccionIndex);

        if (paso.esEjercicio) {
            contenido.add(construirCuerpoEjercicio(paso, leccion));
        } else {
            contenido.add(construirCuerpoLeccion(paso, leccion));
        }
        contenido.add(Box.createVerticalStrut(24));

        contenido.add(construirFilaBotones());

        contenedorCentral.add(contenido);

        JScrollPane scroll = new JScrollPane(contenedorCentral);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel construirCuerpoLeccion(Paso paso, Leccion leccion) {
        JPanel bloque = new JPanel();
        bloque.setOpaque(false);
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));

        int total = curso.getLecciones().size();
        JLabel numeroLbl = new JLabel("Lección " + (paso.leccionIndex + 1) + " de " + total);
        numeroLbl.setFont(EstiloUI.FUENTE_PEQUENA);
        numeroLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        numeroLbl.setAlignmentX(LEFT_ALIGNMENT);
        bloque.add(numeroLbl);
        bloque.add(Box.createVerticalStrut(8));

        JLabel tituloLeccion = new JLabel(leccion.getTitulo());
        tituloLeccion.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tituloLeccion.setForeground(EstiloUI.TEXTO_PRIMARIO);
        tituloLeccion.setAlignmentX(LEFT_ALIGNMENT);
        bloque.add(tituloLeccion);
        bloque.add(Box.createVerticalStrut(14));

        bloque.add(construirCajaTexto(leccion.getContenido()));
        return bloque;
    }

    private JPanel construirCuerpoEjercicio(Paso paso, Leccion leccion) {
        JPanel bloque = new JPanel();
        bloque.setOpaque(false);
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));

        int total = curso.getLecciones().size();
        JLabel numeroLbl = new JLabel("Ejercicio propuesto — Clase " + (paso.leccionIndex + 1) + " de " + total);
        numeroLbl.setFont(EstiloUI.FUENTE_PEQUENA);
        numeroLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        numeroLbl.setAlignmentX(LEFT_ALIGNMENT);
        bloque.add(numeroLbl);
        bloque.add(Box.createVerticalStrut(8));

        JLabel tituloEjercicio = new JLabel("✏️  Ejercicio: " + leccion.getTitulo());
        tituloEjercicio.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tituloEjercicio.setForeground(EstiloUI.TEXTO_PRIMARIO);
        tituloEjercicio.setAlignmentX(LEFT_ALIGNMENT);
        bloque.add(tituloEjercicio);
        bloque.add(Box.createVerticalStrut(14));

        boolean resuelto = ejerciciosResueltos.contains(paso.leccionIndex);
        if (resuelto) {
            bloque.add(construirCajaTexto(leccion.getEjercicioPropuesto()));
            bloque.add(Box.createVerticalStrut(20));
            JLabel resueltoLbl = new JLabel("✓  ¡Resuelto correctamente! Ya podés avanzar a la siguiente clase.");
            resueltoLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            resueltoLbl.setForeground(EstiloUI.EXITO);
            resueltoLbl.setAlignmentX(LEFT_ALIGNMENT);
            bloque.add(resueltoLbl);
        } else {
            bloque.add(construirFilaEjercicioYCodigo(paso, leccion));
        }

        return bloque;
    }

    /** Fila con el enunciado a la izquierda y el editor de código del alumno a la derecha. */
    private JPanel construirFilaEjercicioYCodigo(Paso paso, Leccion leccion) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);

        fila.add(construirCajaTexto(leccion.getEjercicioPropuesto(), EstiloUI.ANCHO_ENUNCIADO_EJERCICIO));
        fila.add(construirColumnaEditor(paso, leccion));

        return fila;
    }

    private JPanel construirColumnaEditor(Paso paso, Leccion leccion) {
        JPanel columna = new JPanel();
        columna.setOpaque(false);
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));

        JLabel etiqueta = FabricaUI.crearEtiqueta("Tu código");
        etiqueta.setAlignmentX(LEFT_ALIGNMENT);
        columna.add(etiqueta);
        columna.add(Box.createVerticalStrut(6));

        PanelEditorCodigo editor = new PanelEditorCodigo(EstiloUI.ANCHO_EDITOR_CODIGO, alturaCajaEstandar());
        columna.add(editor);
        columna.add(Box.createVerticalStrut(10));

        JPanel filaBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaBoton.setOpaque(false);
        filaBoton.setAlignmentX(LEFT_ALIGNMENT);
        JButton botonVerificar = FabricaUI.crearBotonPrimario("Verificar", IconoVectorial.Tipo.GUARDAR);
        filaBoton.add(botonVerificar);
        columna.add(filaBoton);

        columna.add(Box.createVerticalStrut(8));
        JLabel feedbackLbl = new JLabel(" ");
        feedbackLbl.setFont(EstiloUI.FUENTE_PEQUENA);
        feedbackLbl.setAlignmentX(LEFT_ALIGNMENT);
        columna.add(feedbackLbl);

        botonVerificar.addActionListener(e -> {
            botonVerificar.setEnabled(false);
            editor.setHabilitado(false);
            setFeedback(feedbackLbl, "Evaluando con IA...", EstiloUI.TEXTO_SECUNDARIO);

            new SwingWorker<ResultadoEvaluacionEjercicio, Void>() {
                @Override
                protected ResultadoEvaluacionEjercicio doInBackground() throws Exception {
                    return controladorCursos.evaluarEjercicio(
                        editor.getCodigo(), leccion.getEjercicioPropuesto(), leccion.getRespuestaEsperada());
                }

                @Override
                protected void done() {
                    botonVerificar.setEnabled(true);
                    editor.setHabilitado(true);
                    try {
                        ResultadoEvaluacionEjercicio resultado = get();
                        if (resultado.isCorrecto()) {
                            ejerciciosResueltos.add(paso.leccionIndex);
                            reconstruirContenido();
                        } else {
                            setFeedback(feedbackLbl, "✕  " + resultado.getFeedback(), EstiloUI.ERROR);
                        }
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    } catch (ExecutionException ee) {
                        Throwable causa = ee.getCause();
                        String mensaje = causa instanceof MotorChatbotException
                            ? causa.getMessage()
                            : "Ocurrió un error inesperado. Intentá de nuevo.";
                        setFeedback(feedbackLbl, mensaje, EstiloUI.ERROR);
                    }
                }
            }.execute();
        });

        return columna;
    }

    /** Feedback envuelto en HTML (puede ser varias líneas, viene de la IA) con escape básico
     * para que código mencionado en el texto (ej. "<Saludo ... />") no rompa el render del label. */
    private void setFeedback(JLabel label, String texto, Color color) {
        String escapado = texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        label.setText("<html><body style='width: " + EstiloUI.ANCHO_EDITOR_CODIGO + "px'>"
            + escapado.replace("\n", "<br>") + "</body></html>");
        label.setForeground(color);
    }

    private JScrollPane construirCajaTexto(String textoHtml) {
        return construirCajaTexto(textoHtml, 748);
    }

    private JScrollPane construirCajaTexto(String textoHtml, int ancho) {
        // -48px de margen (igual que el 748->700 original): deja lugar a la scrollbar vertical
        // y al padding del JScrollPane, si no el texto queda más ancho que el viewport y se corta.
        JLabel contenidoLbl = new JLabel("<html><body style='width: " + (ancho - 48) + "px'>" + textoHtml + "</body></html>");
        contenidoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contenidoLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        contenidoLbl.setVerticalAlignment(SwingConstants.TOP);

        JPanel envoltorioTexto = new JPanel(new BorderLayout());
        envoltorioTexto.setOpaque(false);
        envoltorioTexto.add(contenidoLbl, BorderLayout.NORTH);

        // Caja de tamaño fijo (60% del alto de pantalla): el texto scrollea adentro
        // en vez de cambiar el alto de la caja, así los botones no se mueven según la lección.
        JScrollPane cajaTexto = new JScrollPane(
                envoltorioTexto,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        cajaTexto.setBorder(BorderFactory.createEmptyBorder());
        cajaTexto.setViewportBorder(BorderFactory.createEmptyBorder());
        cajaTexto.getVerticalScrollBar().setUnitIncrement(16);
        cajaTexto.setOpaque(false);
        cajaTexto.getViewport().setOpaque(false);
        cajaTexto.setAlignmentX(LEFT_ALIGNMENT);

        Dimension tamanoCaja = new Dimension(ancho, alturaCajaEstandar());
        cajaTexto.setPreferredSize(tamanoCaja);
        cajaTexto.setMinimumSize(tamanoCaja);
        cajaTexto.setMaximumSize(tamanoCaja);

        return cajaTexto;
    }

    /** Alto fijo (40% de la pantalla) compartido por la caja de texto y el editor de código,
     * para que ambos queden alineados y los botones no se muevan según el contenido. */
    private int alturaCajaEstandar() {
        return (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.4);
    }

    private JPanel construirFilaBotones() {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);

        Paso paso = pasos.get(pasoActual);
        boolean esUltimoPaso = pasoActual == pasos.size() - 1;
        boolean esPrimero = pasoActual == 0;
        boolean bloqueadoPorEjercicio = paso.esEjercicio && !ejerciciosResueltos.contains(paso.leccionIndex);

        JButton botonAnterior = FabricaUI.crearBotonSecundario("Anterior", IconoVectorial.Tipo.ANTERIOR);
        botonAnterior.setEnabled(!esPrimero);
        botonAnterior.addActionListener(e -> {
            pasoActual = Math.max(0, pasoActual - 1);
            guardarProgreso();
            reconstruirContenido();
        });
        fila.add(botonAnterior);

        JButton botonSiguiente = FabricaUI.crearBotonPrimarioIconoAlFinal(esUltimoPaso ? "Hacer Test" : "Siguiente", IconoVectorial.Tipo.SIGUIENTE);
        botonSiguiente.setEnabled(!bloqueadoPorEjercicio);
        botonSiguiente.addActionListener(e -> {
            if (esUltimoPaso) {
                abrirTest();
            } else {
                pasoActual = Math.min(pasos.size() - 1, pasoActual + 1);
                guardarProgreso();
                reconstruirContenido();
            }
        });
        fila.add(botonSiguiente);

        return fila;
    }

    private void guardarProgreso() {
        try {
            controladorCursos.actualizarProgreso(emailUsuario, curso.getId(), pasos.get(pasoActual).leccionIndex);
        } catch (Exception ignored) {}
    }

    private void reconstruirContenido() {
        panelContenido.removeAll();
        panelContenido.add(construirContenido(), BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    /** Tarjeta con el estado del test final (aprobado/no rendido) y los botones Hacer Test / Ver Certificado. */
    private JPanel construirBarraEvaluacion() {
        int puntajeObtenido = -1;
        try {
            puntajeObtenido = controladorTest.obtenerMejorPuntaje(emailUsuario, curso.getId());
        } catch (Exception ignored) {}
        final int mejorPuntaje = puntajeObtenido;
        boolean aprobado = mejorPuntaje >= ControladorTest.puntajeAprobacion();

        JPanel tarjeta = new JPanel(new BorderLayout(16, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Float(2, 3, getWidth() - 3, getHeight() - 3, 12, 12));
                g2.setColor(EstiloUI.FONDO_SUAVE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 4, 12, 12));
                g2.dispose();
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(new EmptyBorder(16, 20, 16, 20));
        tarjeta.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel bloqueEstado = new JPanel();
        bloqueEstado.setOpaque(false);
        bloqueEstado.setLayout(new BoxLayout(bloqueEstado, BoxLayout.Y_AXIS));

        JLabel estadoLbl = new JLabel(textoEstado(mejorPuntaje, aprobado));
        estadoLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        estadoLbl.setForeground(aprobado ? EstiloUI.EXITO : EstiloUI.TEXTO_PRIMARIO);

        JLabel detalleLbl = new JLabel(mejorPuntaje >= 0
            ? "Mejor puntaje: " + mejorPuntaje + " / 100  (aprueba con " + ControladorTest.puntajeAprobacion() + ")"
            : "Rendí el test para evaluar lo que aprendiste en este curso.");
        detalleLbl.setFont(EstiloUI.FUENTE_PEQUENA);
        detalleLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);

        bloqueEstado.add(estadoLbl);
        bloqueEstado.add(Box.createVerticalStrut(3));
        bloqueEstado.add(detalleLbl);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);

        JButton botonTest = FabricaUI.crearBotonPrimario(mejorPuntaje >= 0 ? "Volver a hacer el test" : "Hacer Test");
        botonTest.addActionListener(e -> abrirTest());
        botones.add(botonTest);

        if (aprobado) {
            JButton botonCertificado = FabricaUI.crearBotonSecundario("Ver Certificado");
            botonCertificado.addActionListener(e -> abrirCertificado(mejorPuntaje));
            botones.add(botonCertificado);
        }

        tarjeta.add(bloqueEstado, BorderLayout.CENTER);
        tarjeta.add(botones, BorderLayout.EAST);
        return tarjeta;
    }

    private String textoEstado(int mejorPuntaje, boolean aprobado) {
        if (aprobado) return "✓ Curso aprobado";
        if (mejorPuntaje >= 0) return "Curso no aprobado todavía";
        return "Todavía no rendiste el test";
    }

    private void abrirTest() {
        if (!iniciarTransicionUnica()) return;
        dispose();
        new VentanaTest(curso, emailUsuario, () ->
            new VentanaContenidoCurso(curso, emailUsuario, nombreUsuario, alVolver).setVisible(true)
        ).setVisible(true);
    }

    private void abrirCertificado(int puntaje) {
        new VentanaCertificado(curso, nombreUsuario, puntaje).setVisible(true);
    }
}
