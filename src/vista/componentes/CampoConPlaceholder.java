package vista.componentes;

import vista.estilo.EstiloUI;

import javax.swing.*;
import java.awt.*;

/** JTextField que muestra un texto tenue de ejemplo (tipo placeholder HTML) mientras está vacío. */
public class CampoConPlaceholder extends JTextField {

    private final String placeholder;

    public CampoConPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (placeholder == null || !getText().isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(EstiloUI.TEXTO_SECUNDARIO);
        g2.setFont(getFont());
        Insets insets = getInsets();
        FontMetrics fm = g2.getFontMetrics();
        int y = insets.top + (getHeight() - insets.top - insets.bottom - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(placeholder, insets.left, y);
        g2.dispose();
    }
}
