package vista.componentes;

import vista.estilo.EstiloUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class DialogoPersonalizado extends JDialog {

    public enum TipoDialogo { EXITO, ERROR, INFO, CONFIRMACION }

    private final TipoDialogo tipo;
    private final String titulo;
    private final String mensaje;
    private final String textoBotonConfirmar;
    private Runnable alCerrar;
    private Runnable alConfirmar;

    /** Diálogo simple (éxito/error/info) con un solo botón "Aceptar". */
    public DialogoPersonalizado(JFrame padre, TipoDialogo tipo, String titulo, String mensaje) {
        this(padre, tipo, titulo, mensaje, null);
    }

    /**
     * Diálogo de confirmación con dos botones: "Cancelar" y uno con el texto indicado
     * en {@code textoBotonConfirmar} (ej. "Sí, dar de baja"). Usar junto con
     * {@link #establecerListenerConfirmar(Runnable)}.
     */
    public DialogoPersonalizado(JFrame padre, TipoDialogo tipo, String titulo, String mensaje, String textoBotonConfirmar) {
        super(padre, false);
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.textoBotonConfirmar = textoBotonConfirmar;

        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(EstiloUI.ANCHO_DIALOGO, EstiloUI.ALTO_DIALOGO);
        setLocationRelativeTo(padre);
        setResizable(false);
        setAlwaysOnTop(true);

        construirUI();
        animarEntrada();

        // Cerrar automáticamente si es éxito o info (nunca en confirmaciones ni errores).
        if (tipo == TipoDialogo.EXITO || tipo == TipoDialogo.INFO) {
            Timer timerAutoCierre = new Timer(2000, e -> animarSalida());
            timerAutoCierre.setRepeats(false);
            timerAutoCierre.start();
        }
    }

    public void establecerListenerCierre(Runnable alCerrar) {
        this.alCerrar = alCerrar;
    }

    /** Se ejecuta solo si el usuario confirma (botón de confirmar), no si cancela. */
    public void establecerListenerConfirmar(Runnable alConfirmar) {
        this.alConfirmar = alConfirmar;
    }

    private void construirUI() {
        JPanel panelPrincipal = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 30));
                g2.fill(new RoundRectangle2D.Float(3, 5, getWidth() - 4, getHeight() - 5, 20, 20));

                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 5, 20, 20));

                g2.dispose();
            }
        };
        panelPrincipal.setOpaque(false);
        panelPrincipal.setBorder(null);
        setContentPane(panelPrincipal);

        // Panel superior con icono y título
        JPanel panelEncabezado = new JPanel(new BorderLayout());
        panelEncabezado.setOpaque(false);
        panelEncabezado.setBorder(new javax.swing.border.EmptyBorder(30, 40, 20, 40));

        IconoCirculo icono = new IconoCirculo(obtenerColorAcento(), obtenerGlifoParaTipo());
        icono.setPreferredSize(new Dimension(64, 64));

        JPanel panelIcono = new JPanel(new BorderLayout());
        panelIcono.setOpaque(false);
        panelIcono.setBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 8));
        panelIcono.add(icono, BorderLayout.NORTH);

        JPanel panelTexto = new JPanel(new BorderLayout());
        panelTexto.setOpaque(false);
        panelTexto.setBorder(new javax.swing.border.EmptyBorder(6, 20, 0, 0));

        JLabel tituloLbl = new JLabel(titulo);
        tituloLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tituloLbl.setForeground(obtenerColorTitulo());

        JLabel mensajeLbl = new JLabel("<html><p style='width: 280px'>" + mensaje + "</p></html>");
        mensajeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mensajeLbl.setForeground(new Color(100, 120, 140));

        panelTexto.add(tituloLbl, BorderLayout.NORTH);
        panelTexto.add(mensajeLbl, BorderLayout.CENTER);

        panelEncabezado.add(panelIcono, BorderLayout.WEST);
        panelEncabezado.add(panelTexto, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 16, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(new javax.swing.border.EmptyBorder(0, 40, 30, 40));

        if (esConfirmacion()) {
            JButton botonCancelar = new BotonRedondeado("Cancelar", EstiloUI.TEXTO_SECUNDARIO, BotonRedondeado.Estilo.CONTORNO);
            botonCancelar.addActionListener(e -> animarSalida());

            JButton botonConfirmar = new BotonRedondeado(textoBotonConfirmar, obtenerColorAcento());
            botonConfirmar.addActionListener(e -> {
                if (alConfirmar != null) alConfirmar.run();
                animarSalida();
            });

            panelBotones.add(botonCancelar);
            panelBotones.add(botonConfirmar);
        } else {
            JButton botonOk = new BotonRedondeado("Aceptar", obtenerColorAcento());
            botonOk.addActionListener(e -> animarSalida());
            panelBotones.add(botonOk);
        }

        panelPrincipal.add(panelEncabezado, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
    }

    private boolean esConfirmacion() {
        return tipo == TipoDialogo.CONFIRMACION && textoBotonConfirmar != null;
    }

    private String obtenerGlifoParaTipo() {
        switch (tipo) {
            case EXITO:        return "check";
            case ERROR:        return "x";
            case CONFIRMACION: return "!";
            case INFO:
            default:           return "i";
        }
    }

    private Color obtenerColorTitulo() {
        switch (tipo) {
            case EXITO:        return EstiloUI.EXITO;
            case ERROR:        return EstiloUI.ERROR;
            case INFO:         return EstiloUI.INFO;
            case CONFIRMACION: return EstiloUI.ADVERTENCIA;
            default:           return EstiloUI.TEXTO_PRIMARIO;
        }
    }

    private Color obtenerColorAcento() {
        switch (tipo) {
            case EXITO:        return EstiloUI.EXITO;
            case ERROR:        return EstiloUI.ERROR;
            case CONFIRMACION: return EstiloUI.ADVERTENCIA;
            case INFO:
            default:           return EstiloUI.INFO;
        }
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
                if (alCerrar != null) {
                    alCerrar.run();
                }
                dispose();
            }
            setOpacity(opacidad[0]);
        });
        timer.start();
    }

    public static void mostrarExito(JFrame padre, String mensaje) {
        new DialogoPersonalizado(padre, TipoDialogo.EXITO, "¡Éxito!", mensaje);
    }

    /** Igual que {@link #mostrarExito(JFrame, String)}, pero ejecuta {@code alCerrar} cuando el diálogo termina de cerrarse. */
    public static void mostrarExito(JFrame padre, String mensaje, Runnable alCerrar) {
        DialogoPersonalizado dialogo = new DialogoPersonalizado(padre, TipoDialogo.EXITO, "¡Éxito!", mensaje);
        dialogo.establecerListenerCierre(alCerrar);
    }

    public static void mostrarError(JFrame padre, String mensaje) {
        new DialogoPersonalizado(padre, TipoDialogo.ERROR, "Error", mensaje);
    }

    public static void mostrarInfo(JFrame padre, String mensaje) {
        new DialogoPersonalizado(padre, TipoDialogo.INFO, "Información", mensaje);
    }

    /**
     * Muestra una confirmación con dos botones. {@code alConfirmar} se ejecuta solo si el
     * usuario elige la opción de confirmar, nunca si cancela o cierra el diálogo.
     */
    public static DialogoPersonalizado mostrarConfirmacion(JFrame padre, String titulo, String mensaje,
                                                            String textoBotonConfirmar, Runnable alConfirmar) {
        DialogoPersonalizado dialogo = new DialogoPersonalizado(padre, TipoDialogo.CONFIRMACION, titulo, mensaje, textoBotonConfirmar);
        dialogo.establecerListenerConfirmar(alConfirmar);
        return dialogo;
    }

    /** Círculo de color con un glifo dibujado a mano (no depende de que la fuente tenga el símbolo Unicode). */
    private static class IconoCirculo extends JComponent {
        private final Color color;
        private final String glifo;

        IconoCirculo(Color color, String glifo) {
            this.color = color;
            this.glifo = glifo;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int diametro = Math.min(getWidth(), getHeight());
            int x = (getWidth() - diametro) / 2;
            int y = (getHeight() - diametro) / 2;

            g2.setColor(color);
            g2.fill(new Ellipse2D.Float(x, y, diametro, diametro));

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(diametro * 0.09f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (glifo) {
                case "check":
                    g2.drawLine(x + (int) (diametro * 0.28), y + (int) (diametro * 0.52),
                                x + (int) (diametro * 0.44), y + (int) (diametro * 0.68));
                    g2.drawLine(x + (int) (diametro * 0.44), y + (int) (diametro * 0.68),
                                x + (int) (diametro * 0.74), y + (int) (diametro * 0.32));
                    break;
                case "x":
                    g2.drawLine(x + (int) (diametro * 0.30), y + (int) (diametro * 0.30),
                                x + (int) (diametro * 0.70), y + (int) (diametro * 0.70));
                    g2.drawLine(x + (int) (diametro * 0.70), y + (int) (diametro * 0.30),
                                x + (int) (diametro * 0.30), y + (int) (diametro * 0.70));
                    break;
                default:
                    // "i" / "!" — letra o signo ASCII, siempre disponible en cualquier fuente del sistema.
                    g2.setFont(new Font("Segoe UI", Font.BOLD, (int) (diametro * 0.5)));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(glifo,
                        x + (diametro - fm.stringWidth(glifo)) / 2,
                        y + (diametro - fm.getHeight()) / 2 + fm.getAscent());
                    break;
            }

            g2.dispose();
        }
    }
}
