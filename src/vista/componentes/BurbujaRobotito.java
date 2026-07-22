package vista.componentes;

import vista.estilo.EstiloUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/** Botón circular flotante ("robotito") que abre la ventana de chat al hacer clic. */
public class BurbujaRobotito extends JComponent {

    private boolean hover = false;
    private boolean presionado = false;
    private Runnable alHacerClic;

    public BurbujaRobotito() {
        setPreferredSize(new Dimension(EstiloUI.TAMANO_BURBUJA_CHATBOT, EstiloUI.TAMANO_BURBUJA_CHATBOT));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setOpaque(false);
        setToolTipText("Preguntale a Robotito");
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hover = false; presionado = false; repaint(); }
            @Override public void mousePressed(MouseEvent e) { presionado = true;  repaint(); }
            @Override public void mouseReleased(MouseEvent e){ presionado = false; repaint(); }
            @Override public void mouseClicked(MouseEvent e) { if (alHacerClic != null) alHacerClic.run(); }
        });
    }

    /** Registrado por VentanaBase.activarBurbujaChatbot(...) para abrir/enfocar la ventana de chat. */
    public void alHacerClic(Runnable accion) { this.alHacerClic = accion; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int diametro = Math.min(getWidth(), getHeight());
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fill(new Ellipse2D.Float(2, 4, diametro - 4, diametro - 4));

        Color base = EstiloUI.AZUL_CLARO;
        Color relleno = presionado ? EstiloUI.oscurecer(base, 40)
                       : hover     ? EstiloUI.aclarar(base, 30)
                       : base;
        g2.setColor(relleno);
        g2.fill(new Ellipse2D.Float(0, 0, diametro - 4, diametro - 4));

        dibujarGlifoRobot(g2, diametro - 4);
        g2.dispose();
    }

    /** Glifo de robot dibujado a mano (cabeza redondeada + ojos + antena) — sin depender de emoji Unicode. */
    private void dibujarGlifoRobot(Graphics2D g2, int d) {
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(d * 0.06f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int cabezaX = (int) (d * 0.26), cabezaY = (int) (d * 0.34);
        int cabezaW = (int) (d * 0.48), cabezaH = (int) (d * 0.38);
        g2.draw(new RoundRectangle2D.Float(cabezaX, cabezaY, cabezaW, cabezaH, d * 0.12f, d * 0.12f));

        int ojoR = (int) (d * 0.045);
        g2.fillOval(cabezaX + (int) (cabezaW * 0.28) - ojoR, cabezaY + (int) (cabezaH * 0.42) - ojoR, ojoR * 2, ojoR * 2);
        g2.fillOval(cabezaX + (int) (cabezaW * 0.72) - ojoR, cabezaY + (int) (cabezaH * 0.42) - ojoR, ojoR * 2, ojoR * 2);

        int antenaX = cabezaX + cabezaW / 2;
        g2.drawLine(antenaX, cabezaY, antenaX, cabezaY - (int) (d * 0.10));
        g2.fillOval(antenaX - (int) (d * 0.035), cabezaY - (int) (d * 0.14), (int) (d * 0.07), (int) (d * 0.07));
    }
}
