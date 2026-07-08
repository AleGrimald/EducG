import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class CustomDialog extends JDialog {

    public enum DialogType {
        SUCCESS, ERROR, INFO
    }

    private DialogType type;
    private String title;
    private String message;
    private Runnable onClose;

    public CustomDialog(JFrame parent, DialogType type, String title, String message) {
        super(parent, false);
        this.type = type;
        this.title = title;
        this.message = message;

        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(450, 280);
        setLocationRelativeTo(parent);
        setResizable(false);
        setAlwaysOnTop(true);

        buildUI();
        animateIn();

        // Cerrar automáticamente si es success o info
        if (type == DialogType.SUCCESS || type == DialogType.INFO) {
            Timer autoCloseTimer = new Timer(2000, e -> animateOut());
            autoCloseTimer.setRepeats(false);
            autoCloseTimer.start();
        }
    }

    public void setOnCloseListener(Runnable onClose) {
        this.onClose = onClose;
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Sombra
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fill(new RoundRectangle2D.Float(3, 5, getWidth() - 4, getHeight() - 5, 20, 20));

                // Fondo blanco
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 5, 20, 20));

                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(null);
        setContentPane(mainPanel);

        // Panel superior con icono y título
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new javax.swing.border.EmptyBorder(30, 40, 20, 40));

        // Icono
        JLabel iconLabel = new JLabel(getIconForType());
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 60));
        iconLabel.setPreferredSize(new Dimension(80, 80));

        // Contenedor de texto
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.setBorder(new javax.swing.border.EmptyBorder(10, 20, 0, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(getTitleColor());

        JLabel messageLabel = new JLabel("<html><p style='width: 280px'>" + message + "</p></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setForeground(new Color(100, 120, 140));

        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(messageLabel, BorderLayout.CENTER);

        headerPanel.add(iconLabel, BorderLayout.WEST);
        headerPanel.add(textPanel, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new javax.swing.border.EmptyBorder(0, 40, 30, 40));

        JButton okButton = createStyledButton("Aceptar");
        okButton.addActionListener(e -> animateOut());

        buttonPanel.add(okButton);

        mainPanel.add(headerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private String getIconForType() {
        switch (type) {
            case SUCCESS: return "✓";
            case ERROR: return "✕";
            case INFO: return "ℹ";
            default: return "●";
        }
    }

    private Color getTitleColor() {
        switch (type) {
            case SUCCESS: return new Color(39, 174, 96);
            case ERROR: return new Color(231, 76, 60);
            case INFO: return new Color(41, 128, 185);
            default: return new Color(44, 62, 80);
        }
    }

    private Color getAccentColor() {
        switch (type) {
            case SUCCESS: return new Color(39, 174, 96);
            case ERROR: return new Color(231, 76, 60);
            case INFO: return new Color(41, 128, 185);
            default: return new Color(41, 128, 185);
        }
    }

    private JButton createStyledButton(String label) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = getAccentColor();
                if (getModel().isPressed()) {
                    g2.setColor(new Color(
                        Math.max(0, bgColor.getRed() - 40),
                        Math.max(0, bgColor.getGreen() - 40),
                        Math.max(0, bgColor.getBlue() - 40)
                    ));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(
                        Math.min(255, bgColor.getRed() + 30),
                        Math.min(255, bgColor.getGreen() + 30),
                        Math.min(255, bgColor.getBlue() + 30)
                    ));
                } else {
                    g2.setColor(bgColor);
                }

                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label,
                    (getWidth() - fm.stringWidth(label)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(120, 38));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void animateIn() {
        Timer timer = new Timer(10, null);
        final float[] opacity = {0f};
        timer.addActionListener(e -> {
            opacity[0] += 0.05f;
            if (opacity[0] >= 1.0f) {
                opacity[0] = 1.0f;
                ((Timer) e.getSource()).stop();
            }
            setOpacity(opacity[0]);
        });
        setOpacity(0f);
        setVisible(true);
        timer.start();
    }

    private void animateOut() {
        Timer timer = new Timer(10, null);
        final float[] opacity = {1f};
        timer.addActionListener(e -> {
            opacity[0] -= 0.05f;
            if (opacity[0] <= 0f) {
                opacity[0] = 0f;
                ((Timer) e.getSource()).stop();
                if (onClose != null) {
                    onClose.run();
                }
                dispose();
            }
            setOpacity(opacity[0]);
        });
        timer.start();
    }

    public static void showSuccess(JFrame parent, String message) {
        new CustomDialog(parent, DialogType.SUCCESS, "¡Éxito!", message);
    }

    public static void showError(JFrame parent, String message) {
        new CustomDialog(parent, DialogType.ERROR, "Error", message);
    }

    public static void showInfo(JFrame parent, String message) {
        new CustomDialog(parent, DialogType.INFO, "Información", message);
    }
}