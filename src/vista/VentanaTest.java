package vista;

import controlador.ControladorTest;
import modelo.Curso;
import modelo.OpcionTest;
import modelo.PreguntaTest;
import vista.componentes.DialogoPersonalizado;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private final Map<Integer, ButtonGroup> gruposPorPregunta = new LinkedHashMap<>();

    public VentanaTest(Curso curso, String emailUsuario, Runnable alVolver) {
        super("Educ G – Test: " + curso.getTitulo(), EXIT_ON_CLOSE);
        this.curso = curso;
        this.emailUsuario = emailUsuario;
        this.alVolver = alVolver;
        cargarPreguntas();
        construirUI();
    }

    private void cargarPreguntas() {
        try {
            preguntas = controlador.obtenerPreguntas(curso.getTitulo());
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
        raiz.add(construirPreguntas(), BorderLayout.CENTER);
    }

    private JPanel construirEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(new EmptyBorder(24, 32, 16, 32));

        JPanel bloqueTitulo = new JPanel();
        bloqueTitulo.setOpaque(false);
        bloqueTitulo.setLayout(new BoxLayout(bloqueTitulo, BoxLayout.Y_AXIS));

        JLabel tituloLbl = new JLabel("📝  Test: " + curso.getTitulo());
        tituloLbl.setFont(EstiloUI.FUENTE_TITULO_COMPACTO);
        tituloLbl.setForeground(Color.WHITE);

        JLabel ayudaLbl = new JLabel("Respondé las " + preguntas.size() + " preguntas y presioná Finalizar.");
        ayudaLbl.setFont(EstiloUI.FUENTE_SUBTITULO_COMPACTO);
        ayudaLbl.setForeground(new Color(200, 220, 255));

        bloqueTitulo.add(tituloLbl);
        bloqueTitulo.add(Box.createVerticalStrut(4));
        bloqueTitulo.add(ayudaLbl);

        JButton botonVolver = FabricaUI.crearBotonSecundarioPequeno("← Cancelar");
        botonVolver.addActionListener(e -> {
            dispose();
            alVolver.run();
        });

        encabezado.add(bloqueTitulo, BorderLayout.WEST);
        encabezado.add(botonVolver, BorderLayout.EAST);
        return encabezado;
    }

    private JScrollPane construirPreguntas() {
        JPanel contenido = new JPanel();
        contenido.setBackground(new Color(245, 248, 252));
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(20, 32, 20, 32));

        if (errorCarga != null) {
            JLabel errLbl = new JLabel("No se pudieron cargar las preguntas: " + errorCarga);
            errLbl.setForeground(EstiloUI.ERROR);
            errLbl.setAlignmentX(LEFT_ALIGNMENT);
            contenido.add(errLbl);
        }

        int numero = 1;
        for (PreguntaTest pregunta : preguntas) {
            contenido.add(construirTarjetaPregunta(numero, pregunta));
            contenido.add(Box.createVerticalStrut(14));
            numero++;
        }

        if (!preguntas.isEmpty()) {
            JButton botonFinalizar = FabricaUI.crearBotonPrimario("Finalizar Test");
            botonFinalizar.setAlignmentX(LEFT_ALIGNMENT);
            botonFinalizar.addActionListener(e -> manejarFinalizar());
            contenido.add(botonFinalizar);
            contenido.add(Box.createVerticalStrut(20));
        }

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel construirTarjetaPregunta(int numero, PreguntaTest pregunta) {
        JPanel tarjeta = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Float(2, 3, getWidth() - 3, getHeight() - 3, 12, 12));
                g2.setColor(Color.WHITE);
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

        ButtonGroup grupo = new ButtonGroup();
        gruposPorPregunta.put(pregunta.getId(), grupo);

        for (OpcionTest opcion : pregunta.getOpciones()) {
            JRadioButton radio = new JRadioButton(opcion.getTexto());
            radio.setActionCommand(String.valueOf(opcion.getId()));
            radio.setFont(EstiloUI.FUENTE_CUERPO);
            radio.setForeground(EstiloUI.TEXTO_PRIMARIO);
            radio.setOpaque(false);
            radio.setAlignmentX(LEFT_ALIGNMENT);
            radio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            grupo.add(radio);
            tarjeta.add(radio);
        }

        return tarjeta;
    }

    private void manejarFinalizar() {
        Map<Integer, Integer> respuestas = new HashMap<>();
        for (PreguntaTest pregunta : preguntas) {
            ButtonGroup grupo = gruposPorPregunta.get(pregunta.getId());
            Integer opcionElegida = obtenerSeleccionada(grupo);
            if (opcionElegida != null) respuestas.put(pregunta.getId(), opcionElegida);
        }

        if (respuestas.size() < preguntas.size()) {
            DialogoPersonalizado.mostrarError(this, "Respondé todas las preguntas antes de finalizar.");
            return;
        }

        try {
            int puntaje = controlador.corregirYGuardar(emailUsuario, curso.getTitulo(), preguntas, respuestas);
            boolean aprobado = puntaje >= ControladorTest.puntajeAprobacion();
            String mensaje = "Obtuviste " + puntaje + " / 100.\n"
                + (aprobado ? "¡Aprobaste el curso!" : "No alcanzaste el puntaje mínimo (" + ControladorTest.puntajeAprobacion() + ").");

            DialogoPersonalizado dialogo = new DialogoPersonalizado(this,
                aprobado ? DialogoPersonalizado.TipoDialogo.EXITO : DialogoPersonalizado.TipoDialogo.INFO,
                aprobado ? "¡Aprobaste!" : "Resultado del test", mensaje);
            dialogo.establecerListenerCierre(() -> {
                dispose();
                alVolver.run();
            });
        } catch (SQLException ex) {
            DialogoPersonalizado.mostrarError(this, "No se pudo guardar el resultado:\n" + ex.getMessage());
        }
    }

    private Integer obtenerSeleccionada(ButtonGroup grupo) {
        Enumeration<AbstractButton> elementos = grupo.getElements();
        while (elementos.hasMoreElements()) {
            AbstractButton boton = elementos.nextElement();
            if (boton.isSelected()) return Integer.parseInt(boton.getActionCommand());
        }
        return null;
    }
}
