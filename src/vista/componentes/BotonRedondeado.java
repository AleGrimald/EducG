package vista.componentes;

import vista.estilo.EstiloUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Botón con esquinas redondeadas y estados hover/presionado pintados a mano.
 * Reemplaza las subclases anónimas de JButton repetidas en distintas ventanas.
 *
 * El ancho se calcula automáticamente a partir del texto (como un JButton normal);
 * el alto queda fijo según el {@link Tamano} para que todos los botones de la app
 * midan lo mismo dentro de su categoría, en vez de que cada pantalla elija un
 * número de píxeles distinto.
 */
public class BotonRedondeado extends JButton {

    public enum Estilo { RELLENO, CONTORNO }

    /** NORMAL = acciones principales (formularios, tarjetas). PEQUENO = encabezados y filas compactas. */
    public enum Tamano { NORMAL, PEQUENO }

    private static final Color CONTORNO_HOVER_BG = new Color(236, 245, 253);
    private static final int   AJUSTE_HOVER = 30;
    private static final int   AJUSTE_PRESIONADO = 40;
    private static final int   RELLENO_HORIZONTAL = 24;

    private Color colorBase;
    private final Estilo estilo;
    private final int alto;

    public BotonRedondeado(String texto, Color colorBase) {
        this(texto, colorBase, Estilo.RELLENO, Tamano.NORMAL);
    }

    public BotonRedondeado(String texto, Color colorBase, Estilo estilo) {
        this(texto, colorBase, estilo, Tamano.NORMAL);
    }

    public BotonRedondeado(String texto, Color colorBase, Estilo estilo, Tamano tamano) {
        super(texto);
        this.colorBase = colorBase;
        this.estilo = estilo;
        this.alto = (tamano == Tamano.PEQUENO) ? EstiloUI.ALTO_BOTON_PEQUENO : EstiloUI.ALTO_BOTON;
        setFont(EstiloUI.FUENTE_BOTON);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Cambia el color base (relleno o contorno, según el estilo) y repinta. */
    public void cambiarColorBase(Color nuevoColor) {
        this.colorBase = nuevoColor;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        int ancho = fm.stringWidth(getText()) + RELLENO_HORIZONTAL * 2;
        return new Dimension(ancho, alto);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int radio = EstiloUI.RADIO_BORDE_MEDIANO;

        Color colorTexto;
        if (!isEnabled()) {
            g2.setColor(EstiloUI.BORDE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radio, radio));
            colorTexto = EstiloUI.TEXTO_SECUNDARIO;
        } else if (estilo == Estilo.RELLENO) {
            if (getModel().isPressed())      g2.setColor(EstiloUI.oscurecer(colorBase, AJUSTE_PRESIONADO));
            else if (getModel().isRollover()) g2.setColor(EstiloUI.aclarar(colorBase, AJUSTE_HOVER));
            else                              g2.setColor(colorBase);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radio, radio));
            colorTexto = Color.WHITE;
        } else {
            g2.setColor(getModel().isRollover() ? CONTORNO_HOVER_BG : Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radio, radio));
            g2.setColor(colorBase);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, radio, radio));
            colorTexto = colorBase;
        }

        g2.setColor(colorTexto);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        String texto = getText();
        g2.drawString(texto,
            (getWidth()  - fm.stringWidth(texto)) / 2,
            (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }
}
