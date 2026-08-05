package vista;

import controlador.ControladorTest;
import modelo.Curso;
import modelo.OpcionTest;
import modelo.PreguntaTest;
import vista.componentes.BarraProgreso;
import vista.componentes.DialogoPersonalizado;
import vista.componentes.IconoVectorial;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Test final (multiple choice) de un curso: 10 preguntas, corrección y guardado del resultado. */
public class VentanaTest extends VentanaBase {

    private final ControladorTest controlador = new ControladorTest();
    private final Curso curso;
    private final String emailUsuario;
    private final Runnable alVolver;

    private List<PreguntaTest> preguntas = new ArrayList<>();
    private String errorCarga;
    private int preguntaIndexActual = 0;
    private final Map<Integer, Integer> respuestasSeleccionadas = new HashMap<>();
    private BarraProgreso barraProgreso;
    private JLabel textoPreguntaLbl;
    private JButton botonVolverEncabezado;
    private JPanel panelPreguntas;

    public VentanaTest(Curso curso, String emailUsuario, Runnable alVolver) {
        super("Educ G", EXIT_ON_CLOSE);
        this.curso = curso;
        this.emailUsuario = emailUsuario;
        this.alVolver = alVolver;
        cargarPreguntas();
        construirUI();
        FabricaUI.establecerIconoVentana(this);
    }

    private void cargarPreguntas() {
        try {
            preguntas = controlador.obtenerPreguntas(curso.getId());
        } catch (SQLException ex) {
            preguntas = new ArrayList<>();
            errorCarga = ex.getMessage();
        }
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new BorderLayout());
        setContentPane(raiz);

        raiz.add(construirEncabezado(), BorderLayout.NORTH);
        panelPreguntas = new JPanel(new BorderLayout());
        raiz.add(panelPreguntas, BorderLayout.CENTER);
        reconstruirPreguntas();
    }

    private JPanel construirEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(true);
        encabezado.setBackground(EstiloUI.FONDO_SUAVE);
        encabezado.setBorder(new EmptyBorder(24, 32, 12, 32));
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));

        JPanel fila1 = new JPanel(new BorderLayout());
        fila1.setOpaque(false);

        JPanel bloqueTitulo = new JPanel();
        bloqueTitulo.setOpaque(false);
        bloqueTitulo.setLayout(new BoxLayout(bloqueTitulo, BoxLayout.Y_AXIS));

        JLabel tituloLbl = new JLabel("📝  Test: " + curso.getTitulo());
        tituloLbl.setFont(EstiloUI.FUENTE_TITULO_COMPACTO);
        tituloLbl.setForeground(EstiloUI.TEXTO_PRIMARIO);

        textoPreguntaLbl = new JLabel("Pregunta 1 de " + preguntas.size());
        textoPreguntaLbl.setFont(EstiloUI.FUENTE_SUBTITULO_COMPACTO);
        textoPreguntaLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);

        bloqueTitulo.add(tituloLbl);
        bloqueTitulo.add(Box.createVerticalStrut(4));
        bloqueTitulo.add(textoPreguntaLbl);

        botonVolverEncabezado = FabricaUI.crearBotonSecundarioPequeno("Cancelar", IconoVectorial.Tipo.VOLVER);
        botonVolverEncabezado.addActionListener(e -> {
            if (!iniciarTransicionUnica()) return;
            dispose();
            alVolver.run();
        });

        fila1.add(bloqueTitulo, BorderLayout.WEST);
        fila1.add(botonVolverEncabezado, BorderLayout.EAST);

        encabezado.add(fila1);
        encabezado.add(Box.createVerticalStrut(12));

        barraProgreso = new BarraProgreso();
        barraProgreso.setProgreso(1, preguntas.size());
        encabezado.add(barraProgreso);

        return encabezado;
    }

    private Component construirPreguntas() {
        JPanel contenedorCentral = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contenedorCentral.setBackground(EstiloUI.FONDO_SUAVE);

        JPanel contenido = new JPanel();
        contenido.setBackground(EstiloUI.FONDO_SUAVE);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(20, 32, 20, 32));

        if (errorCarga != null) {
            JLabel errLbl = new JLabel("No se pudieron cargar las preguntas: " + errorCarga);
            errLbl.setForeground(EstiloUI.ERROR);
            errLbl.setAlignmentX(LEFT_ALIGNMENT);
            contenido.add(errLbl);
        } else if (!preguntas.isEmpty()) {
            contenido.add(construirTarjetaPregunta(preguntaIndexActual + 1, preguntas.get(preguntaIndexActual)));
            contenido.add(Box.createVerticalStrut(24));
            contenido.add(construirFilaBotonesNavegacion());
        }

        contenido.add(Box.createVerticalGlue());

        contenedorCentral.add(contenido);

        JScrollPane scroll = new JScrollPane(contenedorCentral);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel construirFilaBotonesNavegacion() {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);

        int total = preguntas.size();
        boolean esUltima = preguntaIndexActual == total - 1;
        boolean esPrimera = preguntaIndexActual == 0;

        JButton botonAnterior = FabricaUI.crearBotonSecundario("Anterior", IconoVectorial.Tipo.ANTERIOR);
        botonAnterior.setEnabled(!esPrimera);
        botonAnterior.addActionListener(e -> {
            preguntaIndexActual = Math.max(0, preguntaIndexActual - 1);
            reconstruirPreguntas();
        });
        fila.add(botonAnterior);

        JButton botonSiguiente = FabricaUI.crearBotonPrimarioIconoAlFinal(esUltima ? "Finalizar Test" : "Siguiente", IconoVectorial.Tipo.SIGUIENTE);
        botonSiguiente.addActionListener(e -> {
            if (esUltima) {
                manejarFinalizar();
            } else {
                preguntaIndexActual = Math.min(total - 1, preguntaIndexActual + 1);
                reconstruirPreguntas();
            }
        });
        fila.add(botonSiguiente);

        return fila;
    }

    private void reconstruirPreguntas() {
        textoPreguntaLbl.setText("Pregunta " + (preguntaIndexActual + 1) + " de " + preguntas.size());
        barraProgreso.setProgreso(preguntaIndexActual + 1, preguntas.size());
        panelPreguntas.removeAll();
        panelPreguntas.add(construirPreguntas(), BorderLayout.CENTER);
        panelPreguntas.revalidate();
        panelPreguntas.repaint();
    }

    private JPanel construirTarjetaPregunta(int numero, PreguntaTest pregunta) {
        JPanel tarjeta = new JPanel() {
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
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(16, 20, 16, 20));
        tarjeta.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel enunciadoLbl = new JLabel("<html><body style='width: 650px'>" + numero + ". " + pregunta.getEnunciado() + "</body></html>");
        enunciadoLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        enunciadoLbl.setForeground(EstiloUI.TEXTO_PRIMARIO);
        enunciadoLbl.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.add(enunciadoLbl);
        tarjeta.add(Box.createVerticalStrut(10));

        Integer opcionGuardada = respuestasSeleccionadas.get(pregunta.getId());

        for (OpcionTest opcion : pregunta.getOpciones()) {
            JRadioButton radio = new JRadioButton(opcion.getTexto());
            radio.setActionCommand(String.valueOf(opcion.getId()));
            radio.setFont(EstiloUI.FUENTE_CUERPO);
            radio.setForeground(EstiloUI.TEXTO_PRIMARIO);
            radio.setOpaque(false);
            radio.setAlignmentX(LEFT_ALIGNMENT);
            radio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (opcionGuardada != null && opcionGuardada == opcion.getId()) {
                radio.setSelected(true);
            }
            radio.addActionListener(e -> respuestasSeleccionadas.put(pregunta.getId(), opcion.getId()));
            tarjeta.add(radio);
        }

        return tarjeta;
    }

    private void manejarFinalizar() {
        if (respuestasSeleccionadas.size() < preguntas.size()) {
            DialogoPersonalizado.mostrarError(this, "Respondé todas las preguntas antes de finalizar.");
            return;
        }

        try {
            int puntaje = controlador.corregirYGuardar(emailUsuario, curso.getId(), preguntas, respuestasSeleccionadas);
            boolean aprobado = puntaje >= ControladorTest.puntajeAprobacion();
            mostrarResultados(puntaje, aprobado);
        } catch (SQLException ex) {
            DialogoPersonalizado.mostrarError(this, "No se pudo guardar el resultado:\n" + ex.getMessage());
        }
    }

    /** Reemplaza el cuerpo de la ventana por el resumen del puntaje y la revisión pregunta por pregunta. */
    private void mostrarResultados(int puntaje, boolean aprobado) {
        textoPreguntaLbl.setText(aprobado
            ? "¡Aprobaste con " + puntaje + " / 100!"
            : "No aprobaste  ·  " + puntaje + " / 100");
        barraProgreso.setProgreso(preguntas.size(), preguntas.size());
        botonVolverEncabezado.setText("Volver");

        panelPreguntas.removeAll();
        panelPreguntas.add(construirResultados(puntaje, aprobado), BorderLayout.CENTER);
        panelPreguntas.revalidate();
        panelPreguntas.repaint();
    }

    private Component construirResultados(int puntaje, boolean aprobado) {
        JPanel contenedorCentral = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contenedorCentral.setBackground(EstiloUI.FONDO_SUAVE);

        JPanel contenido = new JPanel();
        contenido.setBackground(EstiloUI.FONDO_SUAVE);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(20, 32, 20, 32));

        contenido.add(construirBannerResultado(puntaje, aprobado));
        contenido.add(Box.createVerticalStrut(20));

        for (int i = 0; i < preguntas.size(); i++) {
            contenido.add(construirTarjetaRevision(i + 1, preguntas.get(i)));
            contenido.add(Box.createVerticalStrut(12));
        }

        JButton botonVolver = FabricaUI.crearBotonPrimario("Volver al curso", IconoVectorial.Tipo.VOLVER);
        botonVolver.setAlignmentX(LEFT_ALIGNMENT);
        botonVolver.addActionListener(e -> {
            if (!iniciarTransicionUnica()) return;
            dispose();
            alVolver.run();
        });
        contenido.add(botonVolver);
        contenido.add(Box.createVerticalGlue());

        contenedorCentral.add(contenido);

        JScrollPane scroll = new JScrollPane(contenedorCentral);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel construirBannerResultado(int puntaje, boolean aprobado) {
        JPanel banner = new JPanel(new BorderLayout()) {
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
        banner.setOpaque(false);
        banner.setBorder(new EmptyBorder(18, 20, 18, 20));
        banner.setAlignmentX(LEFT_ALIGNMENT);
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JPanel bloque = new JPanel();
        bloque.setOpaque(false);
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));

        JLabel tituloLbl = new JLabel(aprobado ? "✓ ¡Aprobaste el curso!" : "No alcanzaste el puntaje mínimo");
        tituloLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tituloLbl.setForeground(aprobado ? EstiloUI.EXITO : EstiloUI.ERROR);

        JLabel detalleLbl = new JLabel("Obtuviste " + puntaje + " / 100  ·  aprueba con "
            + ControladorTest.puntajeAprobacion() + " / 100. Revisá tus respuestas debajo.");
        detalleLbl.setFont(EstiloUI.FUENTE_CUERPO);
        detalleLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);

        bloque.add(tituloLbl);
        bloque.add(Box.createVerticalStrut(4));
        bloque.add(detalleLbl);

        banner.add(bloque, BorderLayout.CENTER);
        return banner;
    }

    /** Tarjeta de revisión: enunciado, la respuesta elegida y, si estuvo mal, cuál era la correcta. */
    private JPanel construirTarjetaRevision(int numero, PreguntaTest pregunta) {
        Integer opcionElegidaId = respuestasSeleccionadas.get(pregunta.getId());
        OpcionTest opcionElegida = null;
        OpcionTest opcionCorrecta = null;
        for (OpcionTest opcion : pregunta.getOpciones()) {
            if (opcionElegidaId != null && opcion.getId() == opcionElegidaId) opcionElegida = opcion;
            if (opcion.isCorrecta()) opcionCorrecta = opcion;
        }
        boolean esCorrecta = opcionElegida != null && opcionElegida.isCorrecta();

        JPanel tarjeta = new JPanel() {
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
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(16, 20, 16, 20));
        tarjeta.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel enunciadoLbl = new JLabel("<html><body style='width: 650px'>" + numero + ". " + pregunta.getEnunciado() + "</body></html>");
        enunciadoLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        enunciadoLbl.setForeground(EstiloUI.TEXTO_PRIMARIO);
        enunciadoLbl.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.add(enunciadoLbl);
        tarjeta.add(Box.createVerticalStrut(8));

        JLabel tuRespuestaLbl = new JLabel((esCorrecta ? "✓  " : "✕  ") + "Tu respuesta: "
            + (opcionElegida != null ? opcionElegida.getTexto() : "(sin responder)"));
        tuRespuestaLbl.setFont(EstiloUI.FUENTE_CUERPO);
        tuRespuestaLbl.setForeground(esCorrecta ? EstiloUI.EXITO : EstiloUI.ERROR);
        tuRespuestaLbl.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.add(tuRespuestaLbl);

        if (!esCorrecta) {
            tarjeta.add(Box.createVerticalStrut(4));
            JLabel correctaLbl = new JLabel("Respuesta correcta: " + (opcionCorrecta != null ? opcionCorrecta.getTexto() : "—"));
            correctaLbl.setFont(EstiloUI.FUENTE_CUERPO);
            correctaLbl.setForeground(EstiloUI.EXITO);
            correctaLbl.setAlignmentX(LEFT_ALIGNMENT);
            tarjeta.add(correctaLbl);
        }

        return tarjeta;
    }
}
