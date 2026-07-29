package vista.componentes;

import vista.estilo.EstiloUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** Botón compacto, solo ícono (con tooltip), para acciones densas: filas de tabla, barras de herramientas. */
public class BotonAccionIcono extends JButton {

    private static final int TAMANO = 30;
    private static final int TAMANO_ICONO = 16;

    private final IconoVectorial.Tipo tipo;
    private final Color color;

    public BotonAccionIcono(IconoVectorial.Tipo tipo, Color color, String tooltip) {
        this.tipo = tipo;
        this.color = color;
        setToolTipText(tooltip);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(TAMANO, TAMANO);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isEnabled() && (getModel().isRollover() || getModel().isPressed())) {
            g2.setColor(EstiloUI.conAlpha(color, getModel().isPressed() ? 55 : 30));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        }

        int off = (TAMANO - TAMANO_ICONO) / 2;
        IconoVectorial.dibujar(g2, tipo, off, off, TAMANO_ICONO, isEnabled() ? color : EstiloUI.TEXTO_SECUNDARIO);
        g2.dispose();
    }
}
