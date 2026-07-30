package vista.componentes;

import modelo.Curso;
import vista.estilo.EstiloUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** Tarjeta visual para un curso. */
public class TarjetaCurso extends JPanel {

    private static final Color COLOR_TARJETA_BG = Color.WHITE;
    private static final Color COLOR_DIVISOR     = new Color(230, 236, 240);
    private static final Color COLOR_SOMBRA      = new Color(0, 0, 0, 18);

    private final BotonRedondeado accionBtn;
    private boolean inscripto;
    private Runnable alInscribir    = () -> {};
    private Runnable alIniciarCurso = () -> {};

    public TarjetaCurso(Curso curso, boolean yaInscripto) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        setPreferredSize(new Dimension(280, 400));

        JPanel interior = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_SOMBRA);
                g2.fill(new RoundRectangle2D.Float(3, 5, getWidth() - 4, getHeight() - 4, 16, 16));
                g2.setColor(COLOR_TARJETA_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 5, 16, 16));
                g2.dispose();
            }
        };
        interior.setOpaque(false);
        interior.setLayout(new BoxLayout(interior, BoxLayout.Y_AXIS));
        interior.setBorder(new EmptyBorder(20, 22, 20, 22));

        // ── Emoji + Título (ícono a la izquierda, título a la derecha) ───────
        JPanel encabezado = new JPanel(new BorderLayout(10, 0));
        encabezado.setOpaque(false);
        encabezado.setAlignmentX(LEFT_ALIGNMENT);
        encabezado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel emojiLbl = IconoCurso.crearEtiqueta(curso, 40);

        JLabel tituloLbl = new JLabel("<html>" + curso.getTitulo() + "</html>");
        tituloLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tituloLbl.setForeground(EstiloUI.TEXTO_PRIMARIO);
        tituloLbl.setVerticalAlignment(SwingConstants.CENTER);

        encabezado.add(emojiLbl, BorderLayout.WEST);
        encabezado.add(tituloLbl, BorderLayout.CENTER);

        // ── Descripción ─────────────────────────────────────────────────────
        JLabel descLbl = new JLabel("<html>" + curso.getDescripcion() + "</html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        descLbl.setAlignmentX(LEFT_ALIGNMENT);
        descLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        // ── Divisor ─────────────────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(COLOR_DIVISOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);

        // ── Contenido del curso ─────────────────────────────────────────────
        JLabel contenidoTitulo = new JLabel("Contenido:");
        contenidoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        contenidoTitulo.setForeground(EstiloUI.TEXTO_PRIMARIO);
        contenidoTitulo.setAlignmentX(LEFT_ALIGNMENT);

        JPanel topicosPanel = new JPanel();
        topicosPanel.setOpaque(false);
        topicosPanel.setLayout(new BoxLayout(topicosPanel, BoxLayout.Y_AXIS));
        topicosPanel.setAlignmentX(LEFT_ALIGNMENT);
        for (String topico : curso.getTopicos()) {
            JLabel t = new JLabel("• " + topico);
            t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            t.setForeground(EstiloUI.TEXTO_SECUNDARIO);
            t.setAlignmentX(LEFT_ALIGNMENT);
            topicosPanel.add(t);
        }

        // ── Botón Inscribirse / Iniciar Curso ────────────────────────────────
        accionBtn = new BotonRedondeado("Inscribirse", EstiloUI.AZUL_CLARO);
        accionBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, EstiloUI.ALTO_BOTON));
        accionBtn.setAlignmentX(LEFT_ALIGNMENT);
        accionBtn.addActionListener(e -> {
            if (inscripto) alIniciarCurso.run();
            else            alInscribir.run();
        });
        this.inscripto = yaInscripto;
        if (yaInscripto) aplicarEstiloIniciarCurso();

        // ── Ensamblar ────────────────────────────────────────────────────────
        interior.add(encabezado);
        interior.add(Box.createVerticalStrut(10));
        interior.add(descLbl);
        interior.add(Box.createVerticalStrut(10));
        interior.add(sep);
        interior.add(Box.createVerticalStrut(8));
        interior.add(contenidoTitulo);
        interior.add(Box.createVerticalStrut(4));
        interior.add(topicosPanel);
        interior.add(Box.createVerticalGlue());
        interior.add(Box.createVerticalStrut(14));
        interior.add(accionBtn);

        add(interior, BorderLayout.CENTER);
        setBorder(new EmptyBorder(6, 6, 6, 6));
    }

    /** Registra la acción a ejecutar cuando se hace click en "Inscribirse". */
    public void alHacerClicInscribir(Runnable accion) {
        this.alInscribir = accion;
    }

    /** Registra la acción a ejecutar cuando se hace click en "Iniciar Curso". */
    public void alHacerClicIniciarCurso(Runnable accion) {
        this.alIniciarCurso = accion;
    }

    /** Refleja que el usuario ya está inscripto: el botón pasa a "Iniciar Curso" (verde, habilitado). */
    public void marcarInscripto() {
        this.inscripto = true;
        aplicarEstiloIniciarCurso();
    }

    private void aplicarEstiloIniciarCurso() {
        accionBtn.setText("Iniciar Curso");
        accionBtn.cambiarColorBase(EstiloUI.EXITO);
    }
}
