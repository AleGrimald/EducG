package vista;

import controlador.ControladorTest;
import modelo.Curso;
import modelo.Leccion;
import vista.componentes.PanelDesplegable;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** Muestra el contenido de un curso (lecciones desplegables) al que el usuario ya está inscripto. */
public class VentanaContenidoCurso extends VentanaBase {

    private final ControladorTest controlador = new ControladorTest();
    private final Curso curso;
    private final String emailUsuario;
    private final String nombreUsuario;
    private final Runnable alVolver;

    public VentanaContenidoCurso(Curso curso, String emailUsuario, String nombreUsuario, Runnable alVolver) {
        super("Educ G – " + curso.getTitulo(), EXIT_ON_CLOSE);
        this.curso = curso;
        this.emailUsuario = emailUsuario;
        this.nombreUsuario = nombreUsuario;
        this.alVolver = alVolver;
        construirUI();
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new BorderLayout());
        setContentPane(raiz);

        raiz.add(construirEncabezado(), BorderLayout.NORTH);
        raiz.add(construirContenido(), BorderLayout.CENTER);
    }

    private JPanel construirEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(new EmptyBorder(24, 32, 16, 32));

        JPanel bloqueTitulo = new JPanel();
        bloqueTitulo.setOpaque(false);
        bloqueTitulo.setLayout(new BoxLayout(bloqueTitulo, BoxLayout.Y_AXIS));

        JLabel tituloLbl = new JLabel(curso.getEmoji() + "  " + curso.getTitulo());
        tituloLbl.setFont(EstiloUI.FUENTE_TITULO_COMPACTO);
        tituloLbl.setForeground(Color.WHITE);

        JLabel duracionLbl = new JLabel("⏱ " + curso.getDuracion() + "  ·  " + curso.getDescripcion());
        duracionLbl.setFont(EstiloUI.FUENTE_SUBTITULO_COMPACTO);
        duracionLbl.setForeground(new Color(200, 220, 255));

        bloqueTitulo.add(tituloLbl);
        bloqueTitulo.add(Box.createVerticalStrut(4));
        bloqueTitulo.add(duracionLbl);

        JButton botonVolver = FabricaUI.crearBotonSecundarioPequeno("← Volver");
        botonVolver.addActionListener(e -> {
            dispose();
            alVolver.run();
        });

        encabezado.add(bloqueTitulo, BorderLayout.WEST);
        encabezado.add(botonVolver, BorderLayout.EAST);
        return encabezado;
    }

    private JScrollPane construirContenido() {
        JPanel contenido = new JPanel();
        contenido.setBackground(new Color(245, 248, 252));
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(20, 32, 20, 32));

        contenido.add(construirBarraEvaluacion());
        contenido.add(Box.createVerticalStrut(24));

        JLabel tituloSeccion = new JLabel("Contenido del curso");
        tituloSeccion.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tituloSeccion.setForeground(EstiloUI.TEXTO_PRIMARIO);
        tituloSeccion.setAlignmentX(LEFT_ALIGNMENT);
        contenido.add(tituloSeccion);
        contenido.add(Box.createVerticalStrut(14));

        for (Leccion leccion : curso.getLecciones()) {
            PanelDesplegable panel = new PanelDesplegable(leccion.getTitulo(), leccion.getContenido());
            panel.setAlignmentX(LEFT_ALIGNMENT);
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            contenido.add(panel);
            contenido.add(Box.createVerticalStrut(10));
        }
        contenido.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    /** Tarjeta con el estado del test final (aprobado/no rendido) y los botones Hacer Test / Ver Certificado. */
    private JPanel construirBarraEvaluacion() {
        int puntajeObtenido = -1;
        try {
            puntajeObtenido = controlador.obtenerMejorPuntaje(emailUsuario, curso.getTitulo());
        } catch (Exception ignored) {}
        final int mejorPuntaje = puntajeObtenido;
        boolean aprobado = mejorPuntaje >= ControladorTest.puntajeAprobacion();

        JPanel tarjeta = new JPanel(new BorderLayout(16, 0)) {
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
        dispose();
        new VentanaTest(curso, emailUsuario, () ->
            new VentanaContenidoCurso(curso, emailUsuario, nombreUsuario, alVolver).setVisible(true)
        ).setVisible(true);
    }

    private void abrirCertificado(int puntaje) {
        new VentanaCertificado(curso, nombreUsuario, puntaje).setVisible(true);
    }
}
