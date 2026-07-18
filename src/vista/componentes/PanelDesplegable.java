package vista.componentes;

import vista.estilo.EstiloUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/** Sección desplegable (acordeón): un encabezado clickeable que muestra/oculta su contenido. */
public class PanelDesplegable extends JPanel {

    private static final Color COLOR_FONDO       = Color.WHITE;
    private static final Color COLOR_FONDO_HOVER = new Color(245, 248, 252);
    private static final Color COLOR_SOMBRA      = new Color(0, 0, 0, 15);

    private boolean expandido = false;
    private boolean hover = false;

    private JLabel iconoLbl;
    private JPanel encabezadoPanel;
    private JPanel cuerpoPanel;

    public PanelDesplegable(String titulo, String contenido) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(LEFT_ALIGNMENT);

        construirEncabezado(titulo);
        construirCuerpo(contenido);

        add(encabezadoPanel);
        add(cuerpoPanel);
    }

    private void construirEncabezado(String titulo) {
        encabezadoPanel = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_SOMBRA);
                g2.fill(new RoundRectangle2D.Float(2, 3, getWidth() - 3, getHeight() - 3, 10, 10));
                g2.setColor(hover ? COLOR_FONDO_HOVER : COLOR_FONDO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 4, 10, 10));
                g2.dispose();
            }
        };
        encabezadoPanel.setOpaque(false);
        encabezadoPanel.setBorder(new EmptyBorder(14, 18, 14, 18));
        encabezadoPanel.setAlignmentX(LEFT_ALIGNMENT);
        encabezadoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        encabezadoPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        iconoLbl = new JLabel("▶");
        iconoLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        iconoLbl.setForeground(EstiloUI.AZUL_CLARO);

        JLabel tituloLbl = new JLabel(titulo);
        tituloLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tituloLbl.setForeground(EstiloUI.TEXTO_PRIMARIO);

        encabezadoPanel.add(iconoLbl, BorderLayout.WEST);
        encabezadoPanel.add(tituloLbl, BorderLayout.CENTER);

        encabezadoPanel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { alternar(); }
            @Override public void mouseEntered(MouseEvent e) { hover = true;  encabezadoPanel.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hover = false; encabezadoPanel.repaint(); }
        });
    }

    private void construirCuerpo(String contenido) {
        cuerpoPanel = new JPanel(new BorderLayout());
        cuerpoPanel.setOpaque(false);
        cuerpoPanel.setAlignmentX(LEFT_ALIGNMENT);
        cuerpoPanel.setBorder(new EmptyBorder(2, 18, 18, 18));
        cuerpoPanel.setVisible(false);

        JLabel contenidoLbl = new JLabel("<html><body style='width: 700px'>" + contenido + "</body></html>");
        contenidoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contenidoLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        cuerpoPanel.add(contenidoLbl, BorderLayout.CENTER);
    }

    private void alternar() {
        expandido = !expandido;
        cuerpoPanel.setVisible(expandido);
        iconoLbl.setText(expandido ? "▼" : "▶");
        revalidate();
        repaint();
    }
}
