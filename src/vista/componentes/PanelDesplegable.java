package vista.componentes;

import vista.estilo.EstiloUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Panel de acordeón: título con toggle + contenido colapsable. Usado en el wizard "Crear Curso" (pasos 2 y 3). */
public class PanelDesplegable extends JPanel {

    private final String tituloBase;
    private final JButton toggle;
    private final JPanel contenedorContenido;
    private boolean expandido = false;

    public PanelDesplegable(String titulo, JComponent contenido) {
        this.tituloBase = titulo;
        setLayout(new BorderLayout());
        setOpaque(false);
        setAlignmentX(LEFT_ALIGNMENT);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, EstiloUI.BORDE),
            new EmptyBorder(0, 0, 8, 0)));

        toggle = new JButton(textoToggle());
        toggle.setFont(EstiloUI.FUENTE_ETIQUETA);
        toggle.setForeground(EstiloUI.TEXTO_PRIMARIO);
        toggle.setHorizontalAlignment(SwingConstants.LEFT);
        toggle.setContentAreaFilled(false);
        toggle.setBorderPainted(false);
        toggle.setFocusPainted(false);
        toggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggle.setBorder(new EmptyBorder(10, 4, 10, 4));
        toggle.addActionListener(e -> alternar());

        contenedorContenido = new JPanel(new BorderLayout());
        contenedorContenido.setOpaque(false);
        contenedorContenido.setBorder(new EmptyBorder(0, 16, 12, 4));
        contenedorContenido.add(contenido, BorderLayout.CENTER);
        contenedorContenido.setVisible(false);

        add(toggle, BorderLayout.NORTH);
        add(contenedorContenido, BorderLayout.CENTER);
    }

    public void expandir() {
        if (expandido) return;
        alternar();
    }

    private void alternar() {
        expandido = !expandido;
        contenedorContenido.setVisible(expandido);
        toggle.setText(textoToggle());
        revalidate();
        repaint();
    }

    private String textoToggle() {
        return (expandido ? "▾  " : "▸  ") + tituloBase;
    }
}
