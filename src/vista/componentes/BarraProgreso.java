package vista.componentes;

import vista.estilo.EstiloUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class BarraProgreso extends JComponent {

    private int actual = 0;
    private int total = 1;
    private static final int ALTURA = 6;
    private static final int RADIO = 3;

    public BarraProgreso() {
        setPreferredSize(new Dimension(0, ALTURA));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTURA));
    }

    public void setProgreso(int actual, int total) {
        this.actual = Math.max(0, Math.min(actual, total));
        this.total = Math.max(1, total);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();

        g2.setColor(new Color(255, 255, 255, 20));
        g2.fill(new RoundRectangle2D.Float(0, 0, ancho, alto, RADIO, RADIO));

        int anchoRelleno = (int) (ancho * (double) actual / total);
        if (anchoRelleno > 0) {
            g2.setColor(EstiloUI.EXITO);
            g2.fill(new RoundRectangle2D.Float(0, 0, anchoRelleno, alto, RADIO, RADIO));
        }

        g2.dispose();
    }
}
