import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** Tarjeta visual para un curso. */
public class CourseCard extends JPanel {

    private static final Color COLOR_CARD_BG  = Color.WHITE;
    private static final Color COLOR_TAG_BG   = new Color(235, 245, 255);
    private static final Color COLOR_TAG_FG   = new Color(41, 128, 185);
    private static final Color COLOR_DIVIDER  = new Color(230, 236, 240);
    private static final Color COLOR_SHADOW   = new Color(0, 0, 0, 18);

    public CourseCard(Course course, Runnable onEnroll) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setMaximumSize(new Dimension(340, Integer.MAX_VALUE));
        setPreferredSize(new Dimension(300, 320));

        JPanel inner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Sombra
                g2.setColor(COLOR_SHADOW);
                g2.fill(new RoundRectangle2D.Float(3, 5, getWidth() - 4, getHeight() - 4, 16, 16));
                // Fondo blanco
                g2.setColor(COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 5, 16, 16));
                g2.dispose();
            }
        };
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(20, 22, 20, 22));

        // ── Emoji + Título ──────────────────────────────────────────────────
        JLabel emojiLbl = new JLabel(course.getEmoji());
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        emojiLbl.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel(course.getTitle());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(UIFactory.COLOR_TEXT);
        titleLbl.setAlignmentX(LEFT_ALIGNMENT);

        // ── Duración (tag) ──────────────────────────────────────────────────
        JLabel durationTag = new JLabel("⏱ " + course.getDuration());
        durationTag.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        durationTag.setForeground(COLOR_TAG_FG);
        durationTag.setOpaque(true);
        durationTag.setBackground(COLOR_TAG_BG);
        durationTag.setBorder(new EmptyBorder(3, 8, 3, 8));
        durationTag.setAlignmentX(LEFT_ALIGNMENT);

        // ── Descripción ─────────────────────────────────────────────────────
        JTextArea descArea = new JTextArea(course.getDescription());
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descArea.setForeground(UIFactory.COLOR_MUTED);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setAlignmentX(LEFT_ALIGNMENT);
        descArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        // ── Divider ─────────────────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(COLOR_DIVIDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);

        // ── Contenido del curso ─────────────────────────────────────────────
        JLabel contentTitle = new JLabel("Contenido:");
        contentTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        contentTitle.setForeground(UIFactory.COLOR_TEXT);
        contentTitle.setAlignmentX(LEFT_ALIGNMENT);

        JPanel topicsPanel = new JPanel();
        topicsPanel.setOpaque(false);
        topicsPanel.setLayout(new BoxLayout(topicsPanel, BoxLayout.Y_AXIS));
        topicsPanel.setAlignmentX(LEFT_ALIGNMENT);
        for (String topic : course.getTopics()) {
            JLabel t = new JLabel("• " + topic);
            t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            t.setForeground(UIFactory.COLOR_MUTED);
            t.setAlignmentX(LEFT_ALIGNMENT);
            topicsPanel.add(t);
        }

        // ── Botón Inscribirse ───────────────────────────────────────────────
        JButton enrollBtn = UIFactory.createPrimaryButton("Inscribirse");
        enrollBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        enrollBtn.setAlignmentX(LEFT_ALIGNMENT);
        enrollBtn.addActionListener(e -> onEnroll.run());

        // ── Ensamblar ────────────────────────────────────────────────────────
        inner.add(emojiLbl);
        inner.add(Box.createVerticalStrut(6));
        inner.add(titleLbl);
        inner.add(Box.createVerticalStrut(8));
        inner.add(durationTag);
        inner.add(Box.createVerticalStrut(10));
        inner.add(descArea);
        inner.add(Box.createVerticalStrut(10));
        inner.add(sep);
        inner.add(Box.createVerticalStrut(8));
        inner.add(contentTitle);
        inner.add(Box.createVerticalStrut(4));
        inner.add(topicsPanel);
        inner.add(Box.createVerticalGlue());
        inner.add(Box.createVerticalStrut(14));
        inner.add(enrollBtn);

        add(inner, BorderLayout.CENTER);
        setBorder(new EmptyBorder(6, 6, 6, 6));
    }
}
