package vista.componentes;

import chatbot.MotorChatbotException;
import controlador.ControladorChatbot;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

/** Ventanita de chat flotante ("Robotito") anclada cerca de la burbuja que la abrió. */
public class VentanaChatFlotante extends JDialog {

    private final String emailUsuario;
    private final String cursoTituloActual;
    private ControladorChatbot controlador;      // null si falló la inicialización (proveedor mal configurado)
    private String errorInicializacion;

    private JPanel panelMensajes;
    private JScrollPane scrollMensajes;
    private JTextField campoEntrada;
    private JButton botonEnviar;
    private JLabel indicadorEscribiendo;

    public VentanaChatFlotante(JFrame padre, String emailUsuario, String cursoTituloActual) {
        super(padre, false);
        this.emailUsuario = emailUsuario;
        this.cursoTituloActual = cursoTituloActual;

        try {
            this.controlador = new ControladorChatbot();
        } catch (IllegalStateException ex) {
            this.errorInicializacion = ex.getMessage();
        }

        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(EstiloUI.ANCHO_VENTANA_CHAT, EstiloUI.ALTO_VENTANA_CHAT);
        setResizable(false);
        setAlwaysOnTop(true);
        posicionarCercaDeLaBurbuja(padre);

        construirUI();
        animarEntrada();
    }

    /** Ancla la ventana abajo a la derecha del frame padre, cerca de la burbuja — nunca centrada.
     *  Es solo el punto de apertura: la ventana es arrastrable después (ver {@link #habilitarArrastre}). */
    private void posicionarCercaDeLaBurbuja(JFrame padre) {
        int x = padre.getX() + padre.getWidth() - EstiloUI.ANCHO_VENTANA_CHAT - EstiloUI.MARGEN_BURBUJA_CHATBOT;
        int y = padre.getY() + padre.getHeight()
              - EstiloUI.ALTO_VENTANA_CHAT - EstiloUI.TAMANO_BURBUJA_CHATBOT
              - EstiloUI.MARGEN_BURBUJA_CHATBOT_INFERIOR - EstiloUI.MARGEN_BURBUJA_CHATBOT;
        setLocation(Math.max(padre.getX(), x), Math.max(padre.getY(), y));
    }

    /** Permite arrastrar la ventana (undecorated, sin barra de título nativa) tomándola desde {@code zona}. */
    private void habilitarArrastre(JComponent zona) {
        MouseAdapter arrastre = new MouseAdapter() {
            private Point origenClic;
            @Override public void mousePressed(MouseEvent e) { origenClic = e.getPoint(); }
            @Override public void mouseDragged(MouseEvent e) {
                Point actual = getLocation();
                setLocation(actual.x + e.getX() - origenClic.x, actual.y + e.getY() - origenClic.y);
            }
        };
        zona.addMouseListener(arrastre);
        zona.addMouseMotionListener(arrastre);
    }

    private void construirUI() {
        JPanel raiz = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fill(new RoundRectangle2D.Float(3, 5, getWidth() - 4, getHeight() - 5, 18, 18));
                g2.setColor(EstiloUI.FONDO_SUAVE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 5, 18, 18));
                g2.dispose();
            }
        };
        raiz.setOpaque(false);
        setContentPane(raiz);

        raiz.add(construirEncabezado(), BorderLayout.NORTH);

        if (errorInicializacion != null) {
            raiz.add(construirPanelError(), BorderLayout.CENTER);
            return;
        }

        panelMensajes = new JPanel();
        panelMensajes.setLayout(new BoxLayout(panelMensajes, BoxLayout.Y_AXIS));
        panelMensajes.setOpaque(false);
        panelMensajes.setBorder(new EmptyBorder(12, 16, 12, 16));
        scrollMensajes = new JScrollPane(panelMensajes);
        scrollMensajes.setOpaque(false);
        scrollMensajes.getViewport().setOpaque(false);
        scrollMensajes.setBorder(null);
        scrollMensajes.getVerticalScrollBar().setUnitIncrement(16);
        raiz.add(scrollMensajes, BorderLayout.CENTER);

        agregarMensajeVisual("¡Hola! Preguntame lo que quieras sobre tus cursos.", false);

        raiz.add(construirPanelEntrada(), BorderLayout.SOUTH);
    }

    private JPanel construirEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(new EmptyBorder(16, 20, 12, 12));

        JPanel bloqueTitulo = new JPanel();
        bloqueTitulo.setOpaque(false);
        bloqueTitulo.setLayout(new BoxLayout(bloqueTitulo, BoxLayout.Y_AXIS));

        JLabel tituloLbl = new JLabel("Robotito");
        tituloLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tituloLbl.setForeground(EstiloUI.TEXTO_PRIMARIO);

        JLabel subtituloLbl = new JLabel("Asistente de tus cursos");
        subtituloLbl.setFont(EstiloUI.FUENTE_PEQUENA);
        subtituloLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);

        bloqueTitulo.add(tituloLbl);
        bloqueTitulo.add(subtituloLbl);

        JButton botonCerrar = new JButton("x");
        botonCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botonCerrar.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        botonCerrar.setBorderPainted(false);
        botonCerrar.setContentAreaFilled(false);
        botonCerrar.setFocusPainted(false);
        botonCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonCerrar.addActionListener(e -> animarSalida());

        encabezado.add(bloqueTitulo, BorderLayout.WEST);
        encabezado.add(botonCerrar, BorderLayout.EAST);

        // Arrastrable desde cualquier punto del encabezado (menos el botón de cerrar) —
        // es una ventana sin barra de título nativa (undecorated), así el usuario la puede
        // correr si le tapa algo del curso.
        habilitarArrastre(encabezado);
        habilitarArrastre(bloqueTitulo);
        habilitarArrastre(tituloLbl);
        habilitarArrastre(subtituloLbl);

        return encabezado;
    }

    private JPanel construirPanelError() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JLabel errorLbl = new JLabel("<html><body style='width: 280px'>" + escaparHtml(errorInicializacion) + "</body></html>");
        errorLbl.setFont(EstiloUI.FUENTE_CUERPO);
        errorLbl.setForeground(EstiloUI.ERROR);
        panel.add(errorLbl, BorderLayout.NORTH);
        return panel;
    }

    private JPanel construirPanelEntrada() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 16, 16, 16));

        indicadorEscribiendo = new JLabel("Escribiendo...");
        indicadorEscribiendo.setFont(EstiloUI.FUENTE_PEQUENA);
        indicadorEscribiendo.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        indicadorEscribiendo.setBorder(new EmptyBorder(0, 4, 4, 0));
        indicadorEscribiendo.setVisible(false);

        campoEntrada = FabricaUI.crearCampo();
        campoEntrada.addActionListener(e -> manejarEnviar());

        botonEnviar = new BotonRedondeado("Enviar", EstiloUI.AZUL_CLARO);
        botonEnviar.addActionListener(e -> manejarEnviar());

        JPanel filaEntrada = new JPanel(new BorderLayout(8, 0));
        filaEntrada.setOpaque(false);
        filaEntrada.add(campoEntrada, BorderLayout.CENTER);
        filaEntrada.add(botonEnviar, BorderLayout.EAST);

        panel.add(indicadorEscribiendo, BorderLayout.NORTH);
        panel.add(filaEntrada, BorderLayout.CENTER);
        return panel;
    }

    private void manejarEnviar() {
        String texto = campoEntrada.getText();
        if (texto == null || texto.trim().isEmpty()) return;

        agregarMensajeVisual(texto, true);
        campoEntrada.setText("");
        establecerEstadoEscribiendo(true);

        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws Exception {
                return controlador.enviarPregunta(emailUsuario, cursoTituloActual, texto);
            }
            @Override protected void done() {
                establecerEstadoEscribiendo(false);
                try {
                    agregarMensajeVisual(get(), false);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ee) {
                    agregarMensajeVisual(traducirError(ee.getCause()), false);
                }
            }
        }.execute();
    }

    /** Mapea la excepción real a un texto ya en español para mostrar como respuesta del bot. */
    private String traducirError(Throwable causa) {
        if (causa instanceof IllegalArgumentException) return causa.getMessage();
        if (causa instanceof MotorChatbotException)     return causa.getMessage();
        if (causa instanceof SQLException)
            return "No se pudieron cargar tus datos para responder. Intentá de nuevo en un momento.";
        return "Ocurrió un error inesperado. Intentá de nuevo.";
    }

    private void establecerEstadoEscribiendo(boolean escribiendo) {
        campoEntrada.setEnabled(!escribiendo);
        botonEnviar.setEnabled(!escribiendo);
        indicadorEscribiendo.setVisible(escribiendo);
    }

    private void agregarMensajeVisual(String texto, boolean esUsuario) {
        JPanel fila = new JPanel(new FlowLayout(esUsuario ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        Color fondoBurbuja = esUsuario ? EstiloUI.AZUL_CLARO : EstiloUI.FONDO_BLANCO;
        Color colorTexto = esUsuario ? Color.WHITE : EstiloUI.TEXTO_PRIMARIO;

        JLabel mensajeLbl = new JLabel(
            "<html><body style='width: 200px; color: " + colorAHex(colorTexto) + "'>" + escaparHtml(texto) + "</body></html>");
        mensajeLbl.setFont(EstiloUI.FUENTE_CUERPO);
        mensajeLbl.setOpaque(false);
        mensajeLbl.setBorder(new EmptyBorder(8, 12, 8, 12));

        JPanel burbuja = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fondoBurbuja);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        burbuja.setOpaque(false);
        burbuja.add(mensajeLbl, BorderLayout.CENTER);

        fila.add(burbuja);
        panelMensajes.add(fila);
        panelMensajes.add(Box.createVerticalStrut(8));
        panelMensajes.revalidate();
        panelMensajes.repaint();

        SwingUtilities.invokeLater(() ->
            scrollMensajes.getVerticalScrollBar().setValue(scrollMensajes.getVerticalScrollBar().getMaximum()));
    }

    private String colorAHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private String escaparHtml(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
    }

    private void animarEntrada() {
        Timer timer = new Timer(10, null);
        final float[] opacidad = {0f};
        timer.addActionListener(e -> {
            opacidad[0] += 0.05f;
            if (opacidad[0] >= 1.0f) {
                opacidad[0] = 1.0f;
                ((Timer) e.getSource()).stop();
            }
            setOpacity(opacidad[0]);
        });
        setOpacity(0f);
        setVisible(true);
        timer.start();
    }

    private void animarSalida() {
        Timer timer = new Timer(10, null);
        final float[] opacidad = {1f};
        timer.addActionListener(e -> {
            opacidad[0] -= 0.05f;
            if (opacidad[0] <= 0f) {
                opacidad[0] = 0f;
                ((Timer) e.getSource()).stop();
                dispose();
            }
            setOpacity(opacidad[0]);
        });
        timer.start();
    }
}
