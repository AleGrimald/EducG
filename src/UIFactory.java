import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** Fábrica de componentes y constantes de estilo compartidos para toda la app. */
public final class UIFactory {

    // ── Paleta ───────────────────────────────────────────────────────────────
    public static final Color COLOR_ACCENT    = new Color(41, 128, 185);
    public static final Color COLOR_TEXT      = new Color(44,  62,  80);
    public static final Color COLOR_MUTED     = new Color(127, 140, 141);
    public static final Color COLOR_FIELD_BG  = new Color(245, 248, 250);
    public static final Color COLOR_BORDER    = new Color(189, 195, 199);
    static final Color        COLOR_BG_TOP    = new Color(20,  40,  70);
    static final Color        COLOR_BG_BOTTOM = new Color(55,  95, 150);

    // ── Tipografía ────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD,  36);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_HEADING  = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font FONT_LABEL    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_FIELD    = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON   = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 12);

    private UIFactory() {}

    // ── Paneles ───────────────────────────────────────────────────────────────

    /** Panel con fondo estándar morado, listo para ser contentPane del JFrame. */
    public static JPanel createDefaultBackground() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIStyle.PRIMARY_ACCENT);
        return panel;
    }

    /** Panel con gradiente azul marino, listo para ser contentPane del JFrame. @deprecated usar createDefaultBackground() */
    @Deprecated
    public static JPanel createGradientBackground() {
        return new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, COLOR_BG_TOP, 0, getHeight(), COLOR_BG_BOTTOM));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
    }

    /** Tarjeta blanca con esquinas redondeadas y sombra sutil. */
    public static JPanel createCard() {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Sombra
                g2.setColor(new Color(0, 0, 0, 22));
                g2.fill(new RoundRectangle2D.Float(4, 6, getWidth() - 5, getHeight() - 6, 18, 18));
                // Fondo blanco
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 5, getHeight() - 7, 18, 18));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }

    // ── Campos de texto ───────────────────────────────────────────────────────

    public static JTextField createField() {
        JTextField f = new JTextField();
        styleField(f);
        return f;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField f = new JPasswordField();
        styleField(f);
        return f;
    }

    private static void styleField(JTextField f) {
        f.setFont(FONT_FIELD);
        f.setBackground(COLOR_FIELD_BG);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        f.setPreferredSize(new Dimension(0, 40));
    }

    // ── Etiquetas ─────────────────────────────────────────────────────────────

    public static JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(COLOR_TEXT);
        return lbl;
    }

    // ── Botones ───────────────────────────────────────────────────────────────

    public static JButton createPrimaryButton(String label) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if      (getModel().isPressed())  g2.setColor(new Color(25,  90, 150));
                else if (getModel().isRollover()) g2.setColor(new Color(52, 152, 219));
                else                              g2.setColor(COLOR_ACCENT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(Color.WHITE);
                g2.setFont(FONT_BUTTON);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label,
                    (getWidth()  - fm.stringWidth(label)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        applyButtonStyle(btn);
        return btn;
    }

    public static JButton createSecondaryButton(String label) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(236, 245, 253) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(COLOR_ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 8, 8));
                g2.setFont(FONT_BUTTON);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label,
                    (getWidth()  - fm.stringWidth(label)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        applyButtonStyle(btn);
        return btn;
    }

    private static void applyButtonStyle(JButton btn) {
        btn.setPreferredSize(new Dimension(0, 42));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
