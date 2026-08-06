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
 * número de píxeles distinto. Opcionalmente puede llevar un {@link IconoVectorial.Tipo}
 * antes del texto (null = solo texto, como hasta ahora), o después del texto si se
 * pide explícitamente con el constructor de 6 argumentos.
 */
public class BotonRedondeado extends JButton {

    public enum Estilo { RELLENO, CONTORNO }

    /** NORMAL = acciones principales (formularios, tarjetas). PEQUENO = encabezados y filas compactas. */
    public enum Tamano { NORMAL, PEQUENO }

    private static final int   AJUSTE_HOVER = 30;
    private static final int   AJUSTE_PRESIONADO = 40;
    private static final int   RELLENO_HORIZONTAL = 24;
    private static final int   TAMANO_ICONO = 15;
    private static final int   ESPACIO_ICONO_TEXTO = 8;

    private Color colorBase;
    private final Estilo estilo;
    private final int alto;
    private final IconoVectorial.Tipo icono;
    private final boolean iconoDespuesDelTexto;

    public BotonRedondeado(String texto, Color colorBase) {
        this(texto, colorBase, Estilo.RELLENO, Tamano.NORMAL, null);
    }

    public BotonRedondeado(String texto, Color colorBase, Estilo estilo) {
        this(texto, colorBase, estilo, Tamano.NORMAL, null);
    }

    public BotonRedondeado(String texto, Color colorBase, Estilo estilo, Tamano tamano) {
        this(texto, colorBase, estilo, tamano, null);
    }

    public BotonRedondeado(String texto, Color colorBase, Estilo estilo, Tamano tamano, IconoVectorial.Tipo icono) {
        this(texto, colorBase, estilo, tamano, icono, false);
    }

    public BotonRedondeado(String texto, Color colorBase, Estilo estilo, Tamano tamano, IconoVectorial.Tipo icono,
                            boolean iconoDespuesDelTexto) {
        super(texto);
        this.colorBase = colorBase;
        this.estilo = estilo;
        this.alto = (tamano == Tamano.PEQUENO) ? EstiloUI.ALTO_BOTON_PEQUENO : EstiloUI.ALTO_BOTON;
        this.icono = icono;
        this.iconoDespuesDelTexto = iconoDespuesDelTexto;
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
        if (icono != null) ancho += TAMANO_ICONO + ESPACIO_ICONO_TEXTO;
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
            g2.setColor(getModel().isRollover()
                ? EstiloUI.aclarar(EstiloUI.FONDO_SUAVE, 15)
                : EstiloUI.FONDO_SUAVE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radio, radio));
            g2.setColor(colorBase);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, radio, radio));
            colorTexto = colorBase;
        }

        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        String texto = getText();
        int anchoBloque = fm.stringWidth(texto) + (icono != null ? TAMANO_ICONO + ESPACIO_ICONO_TEXTO : 0);
        int xInicio = (getWidth() - anchoBloque) / 2;

        int yIcono = (getHeight() - TAMANO_ICONO) / 2;
        int yTexto = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

        if (icono != null && !iconoDespuesDelTexto) {
            IconoVectorial.dibujar(g2, icono, xInicio, yIcono, TAMANO_ICONO, colorTexto);
        }

        g2.setColor(colorTexto);
        int xTexto = xInicio + (icono != null && !iconoDespuesDelTexto ? TAMANO_ICONO + ESPACIO_ICONO_TEXTO : 0);
        g2.drawString(texto, xTexto, yTexto);

        if (icono != null && iconoDespuesDelTexto) {
            int xIcono = xInicio + fm.stringWidth(texto) + ESPACIO_ICONO_TEXTO;
            IconoVectorial.dibujar(g2, icono, xIcono, yIcono, TAMANO_ICONO, colorTexto);
        }
        g2.dispose();
    }
}
