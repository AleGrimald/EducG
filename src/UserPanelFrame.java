import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Panel principal del usuario: datos personales, cursos inscriptos y estadísticas. */
public class UserPanelFrame extends JFrame {

    private final String userEmail;
    private JTabbedPane tabPane;

    public UserPanelFrame(String userEmail) {
        this.userEmail = userEmail;
        setTitle("Educ G – Mi Panel");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = UIFactory.createDefaultBackground();
        root.setLayout(new BorderLayout());
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);

        tabPane = new JTabbedPane(JTabbedPane.TOP);
        tabPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabPane.addTab("  👤  Mis Datos  ",    buildDatosPanel());
        tabPane.addTab("  📚  Mis Cursos  ",   buildCursosPanel());
        tabPane.addTab("  📊  Estadísticas  ", buildEstadisticasPanel());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(4, 20, 20, 20));
        wrapper.add(tabPane, BorderLayout.CENTER);
        root.add(wrapper, BorderLayout.CENTER);
    }

    private void refreshCursosTab()      { tabPane.setComponentAt(1, buildCursosPanel()); }
    private void refreshEstadisticasTab() { tabPane.setComponentAt(2, buildEstadisticasPanel()); }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 32, 10, 32));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        JLabel appLbl = new JLabel("Educ G");
        appLbl.setFont(UIFactory.FONT_TITLE);
        appLbl.setForeground(Color.WHITE);

        JLabel pageLbl = new JLabel("Mi Panel – " + userEmail);
        pageLbl.setFont(UIFactory.FONT_SUBTITLE);
        pageLbl.setForeground(new Color(180, 210, 255));

        titleBlock.add(appLbl);
        titleBlock.add(Box.createVerticalStrut(2));
        titleBlock.add(pageLbl);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton catalogBtn = UIFactory.createSecondaryButton("Ver Catálogo");
        catalogBtn.setPreferredSize(new Dimension(135, 38));
        catalogBtn.addActionListener(e -> { dispose(); new CoursesFrame(userEmail).setVisible(true); });

        JButton logoutBtn = UIFactory.createSecondaryButton("Cerrar Sesión");
        logoutBtn.setPreferredSize(new Dimension(140, 38));
        logoutBtn.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });

        btnPanel.add(catalogBtn);
        btnPanel.add(logoutBtn);

        header.add(titleBlock, BorderLayout.WEST);
        header.add(btnPanel,   BorderLayout.EAST);
        return header;
    }

    // ── Tab 1: Mis Datos ─────────────────────────────────────────────────────

    private JScrollPane buildDatosPanel() {
        JPanel content = new JPanel();
        content.setBackground(new Color(245, 248, 252));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24, 40, 24, 40));

        String nombre = "", apellido = "";
        try {
            String[] data = AuthService.getUserData(userEmail);
            if (data != null) { nombre = data[0]; apellido = data[1]; }
        } catch (Exception ignored) {}

        // Datos personales
        addSectionTitle(content, "Datos Personales");
        content.add(Box.createVerticalStrut(14));

        JTextField nombreField   = UIFactory.createField();
        JTextField apellidoField = UIFactory.createField();
        nombreField.setText(nombre);
        apellidoField.setText(apellido);

        JPanel nameRow = new JPanel(new GridLayout(1, 2, 16, 0));
        nameRow.setOpaque(false);
        nameRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        nameRow.setAlignmentX(LEFT_ALIGNMENT);
        nameRow.add(fieldBlock("Nombre", nombreField));
        nameRow.add(fieldBlock("Apellido", apellidoField));
        content.add(nameRow);
        content.add(Box.createVerticalStrut(12));

        JTextField emailField = UIFactory.createField();
        emailField.setText(userEmail);
        emailField.setEditable(false);
        emailField.setBackground(new Color(235, 240, 245));
        content.add(fieldBlock("Correo electrónico (no modificable)", emailField));
        content.add(Box.createVerticalStrut(18));

        JButton saveBtn = UIFactory.createPrimaryButton("Guardar cambios");
        saveBtn.setMaximumSize(new Dimension(200, 42));
        saveBtn.setAlignmentX(LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            String n = nombreField.getText().trim();
            String a = apellidoField.getText().trim();
            if (!Validator.isValidName(n)) { showError("El nombre debe tener entre 2 y 100 caracteres."); return; }
            if (!Validator.isValidName(a)) { showError("El apellido debe tener entre 2 y 100 caracteres."); return; }
            try {
                AuthService.updatePersonalData(userEmail, n, a);
                JOptionPane.showMessageDialog(this, "¡Datos actualizados correctamente!",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) { showError("Error al guardar: " + ex.getMessage()); }
        });
        content.add(saveBtn);

        // Separador
        content.add(Box.createVerticalStrut(30));
        content.add(makeSeparator());
        content.add(Box.createVerticalStrut(22));

        // Cambiar contraseña
        addSectionTitle(content, "Cambiar Contraseña");
        content.add(Box.createVerticalStrut(14));

        JPasswordField currentPwField = UIFactory.createPasswordField();
        JPasswordField newPwField     = UIFactory.createPasswordField();
        JPasswordField confirmPwField = UIFactory.createPasswordField();

        content.add(fieldBlock("Contraseña actual", currentPwField));
        content.add(Box.createVerticalStrut(10));
        content.add(fieldBlock("Nueva contraseña  (6–20 caracteres alfanuméricos)", newPwField));
        content.add(Box.createVerticalStrut(10));
        content.add(fieldBlock("Confirmar nueva contraseña", confirmPwField));
        content.add(Box.createVerticalStrut(18));

        JButton changePwBtn = UIFactory.createPrimaryButton("Cambiar contraseña");
        changePwBtn.setMaximumSize(new Dimension(200, 42));
        changePwBtn.setAlignmentX(LEFT_ALIGNMENT);
        changePwBtn.addActionListener(e -> {
            String current = new String(currentPwField.getPassword());
            String newPw   = new String(newPwField.getPassword());
            String confirm = new String(confirmPwField.getPassword());
            if (current.isEmpty())              { showError("Ingresá tu contraseña actual."); return; }
            if (!Validator.isValidPassword(newPw)) { showError("La nueva contraseña debe tener 6–20 caracteres alfanuméricos."); return; }
            if (!newPw.equals(confirm))         { showError("Las contraseñas no coinciden."); return; }
            try {
                if (AuthService.updatePassword(userEmail, current, newPw)) {
                    JOptionPane.showMessageDialog(this, "¡Contraseña actualizada correctamente!",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    currentPwField.setText("");
                    newPwField.setText("");
                    confirmPwField.setText("");
                } else {
                    showError("La contraseña actual es incorrecta.");
                }
            } catch (SQLException ex) { showError("Error: " + ex.getMessage()); }
        });
        content.add(changePwBtn);
        content.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        return scroll;
    }

    // ── Tab 2: Mis Cursos ────────────────────────────────────────────────────

    private JScrollPane buildCursosPanel() {
        JPanel content = new JPanel();
        content.setBackground(new Color(245, 248, 252));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        try {
            List<String[]> courses = AuthService.getEnrolledCourses(userEmail);
            if (courses.isEmpty()) {
                addSectionTitle(content, "Mis Cursos");
                content.add(Box.createVerticalStrut(16));
                JLabel emptyLbl = new JLabel("No estás inscripto en ningún curso aún.");
                emptyLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                emptyLbl.setForeground(UIFactory.COLOR_MUTED);
                emptyLbl.setAlignmentX(LEFT_ALIGNMENT);
                content.add(emptyLbl);
                content.add(Box.createVerticalStrut(14));
                JButton goBtn = UIFactory.createPrimaryButton("Explorar Catálogo");
                goBtn.setMaximumSize(new Dimension(190, 42));
                goBtn.setAlignmentX(LEFT_ALIGNMENT);
                goBtn.addActionListener(e -> { dispose(); new CoursesFrame(userEmail).setVisible(true); });
                content.add(goBtn);
            } else {
                addSectionTitle(content, "Tus cursos inscriptos  (" + courses.size() + ")");
                content.add(Box.createVerticalStrut(14));
                for (String[] row : courses) {
                    content.add(buildCourseRow(row[0], row[1]));
                    content.add(Box.createVerticalStrut(10));
                }
            }
        } catch (Exception ex) {
            JLabel errLbl = new JLabel("Error al cargar los cursos: " + ex.getMessage());
            errLbl.setFont(UIFactory.FONT_SMALL);
            errLbl.setForeground(Color.RED);
            errLbl.setAlignmentX(LEFT_ALIGNMENT);
            content.add(errLbl);
        }

        content.add(Box.createVerticalGlue());
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        return scroll;
    }

    private JPanel buildCourseRow(String courseTitle, String enrollDate) {
        Course course = CoursesFrame.COURSES.stream()
            .filter(c -> c.getTitle().equals(courseTitle))
            .findFirst().orElse(null);
        String emoji = (course != null) ? course.getEmoji() : "📘";

        JPanel row = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Float(3, 4, getWidth() - 4, getHeight() - 3, 12, 12));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 4, 12, 12));
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(12, 16, 12, 16));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        row.setAlignmentX(LEFT_ALIGNMENT);

        // Info izquierda
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel titleLbl = new JLabel(emoji + "  " + courseTitle);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(UIFactory.COLOR_TEXT);

        String dateStr = (enrollDate != null && enrollDate.length() >= 10)
            ? enrollDate.substring(0, 10) : (enrollDate != null ? enrollDate : "—");
        JLabel dateLbl = new JLabel("Inscripto el: " + dateStr);
        dateLbl.setFont(UIFactory.FONT_SMALL);
        dateLbl.setForeground(UIFactory.COLOR_MUTED);

        info.add(titleLbl);
        info.add(Box.createVerticalStrut(4));
        info.add(dateLbl);

        // Botones derecha
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);

        JButton enterBtn = UIFactory.createPrimaryButton("Ingresar");
        enterBtn.setPreferredSize(new Dimension(100, 36));
        enterBtn.addActionListener(e -> openCourse(course, courseTitle));

        JButton leaveBtn = UIFactory.createSecondaryButton("Darse de baja");
        leaveBtn.setPreferredSize(new Dimension(130, 36));
        leaveBtn.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(this,
                "¿Confirmar baja del curso \"" + courseTitle + "\"?",
                "Darse de baja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                try {
                    AuthService.unenrollCourse(userEmail, courseTitle);
                    refreshCursosTab();
                    refreshEstadisticasTab();
                } catch (SQLException ex) {
                    showError("Error al procesar la baja: " + ex.getMessage());
                }
            }
        });

        btns.add(enterBtn);
        btns.add(leaveBtn);

        row.add(info, BorderLayout.CENTER);
        row.add(btns, BorderLayout.EAST);
        return row;
    }

    private void openCourse(Course course, String courseTitle) {
        if (course == null) {
            JOptionPane.showMessageDialog(this, "Contenido no disponible.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.setPreferredSize(new Dimension(430, 300));

        JLabel titleLbl = new JLabel(course.getEmoji() + "  " + course.getTitle());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        panel.add(titleLbl);
        panel.add(Box.createVerticalStrut(8));

        JLabel descLbl = new JLabel(
            "<html><body style='width:390px'>" + course.getDescription() + "</body></html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLbl.setForeground(UIFactory.COLOR_MUTED);
        panel.add(descLbl);
        panel.add(Box.createVerticalStrut(14));

        JLabel contenidoLbl = new JLabel("Contenido del curso:");
        contenidoLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(contenidoLbl);
        panel.add(Box.createVerticalStrut(6));

        for (String topic : course.getTopics()) {
            JLabel t = new JLabel("  ✓  " + topic);
            t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            panel.add(t);
            panel.add(Box.createVerticalStrut(3));
        }

        panel.add(Box.createVerticalStrut(12));
        JLabel durLbl = new JLabel("⏱  Duración: " + course.getDuration());
        durLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        durLbl.setForeground(UIFactory.COLOR_MUTED);
        panel.add(durLbl);

        JOptionPane.showMessageDialog(this, panel, "Contenido del Curso", JOptionPane.PLAIN_MESSAGE);
    }

    // ── Tab 3: Estadísticas ───────────────────────────────────────────────────

    private JScrollPane buildEstadisticasPanel() {
        JPanel content = new JPanel();
        content.setBackground(new Color(245, 248, 252));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        int[] stats = {0, 0, 0};
        List<String[]> testResults = new ArrayList<>();
        try {
            stats       = AuthService.getStats(userEmail);
            testResults = AuthService.getTestResults(userEmail);
        } catch (Exception ignored) {}

        // Tarjetas de resumen
        addSectionTitle(content, "Resumen General");
        content.add(Box.createVerticalStrut(14));

        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 14, 0));
        cardsRow.setOpaque(false);
        cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));
        cardsRow.setAlignmentX(LEFT_ALIGNMENT);
        cardsRow.add(statCard("📚", "Cursos Inscriptos", String.valueOf(stats[0])));
        cardsRow.add(statCard("📝", "Tests Realizados",  String.valueOf(stats[1])));
        cardsRow.add(statCard("⭐", "Promedio",          stats[1] > 0 ? stats[2] + " pts" : "—"));
        content.add(cardsRow);

        // Historial de tests
        content.add(Box.createVerticalStrut(28));
        content.add(makeSeparator());
        content.add(Box.createVerticalStrut(20));
        addSectionTitle(content, "Historial de Tests");
        content.add(Box.createVerticalStrut(12));

        if (testResults.isEmpty()) {
            JLabel noLbl = new JLabel("No hay resultados de tests registrados aún.");
            noLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            noLbl.setForeground(UIFactory.COLOR_MUTED);
            noLbl.setAlignmentX(LEFT_ALIGNMENT);
            content.add(noLbl);
        } else {
            String[] cols = {"Curso", "Test", "Puntaje", "Fecha"};
            Object[][] data = testResults.stream().map(r -> new Object[]{
                r[0], r[1], r[2] + " / 100",
                r[3] != null && r[3].length() >= 10 ? r[3].substring(0, 10) : r[3]
            }).toArray(Object[][]::new);

            JTable table = new JTable(data, cols) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            table.setFont(UIFactory.FONT_LABEL);
            table.setRowHeight(28);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            table.setSelectionBackground(new Color(220, 235, 255));

            JScrollPane tableScroll = new JScrollPane(table);
            tableScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
            tableScroll.setAlignmentX(LEFT_ALIGNMENT);
            tableScroll.setBorder(BorderFactory.createLineBorder(UIFactory.COLOR_BORDER, 1));
            content.add(tableScroll);
        }

        content.add(Box.createVerticalGlue());
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        return scroll;
    }

    // ── Componentes auxiliares ────────────────────────────────────────────────

    private void addSectionTitle(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(UIFactory.COLOR_TEXT);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(lbl);
    }

    private JPanel fieldBlock(String labelText, JComponent field) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));

        JLabel lbl = UIFactory.createLabel(labelText);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        field.setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        p.add(field);
        return p;
    }

    private JPanel statCard(String icon, String label, String value) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fill(new RoundRectangle2D.Float(3, 5, getWidth() - 4, getHeight() - 4, 14, 14));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 5, 14, 14));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        iconLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLbl.setForeground(UIFactory.COLOR_ACCENT);
        valueLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(UIFactory.FONT_SMALL);
        labelLbl.setForeground(UIFactory.COLOR_MUTED);
        labelLbl.setAlignmentX(CENTER_ALIGNMENT);

        card.add(iconLbl);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLbl);
        card.add(Box.createVerticalStrut(3));
        card.add(labelLbl);
        return card;
    }

    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(210, 220, 230));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        return sep;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
