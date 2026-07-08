import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;

public class RegisterFrame extends JFrame {

    private final LoginFrame loginFrame;

    private JTextField     nombreField;
    private JTextField     apellidoField;
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JCheckBox      showPasswordCheck;

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        setTitle("Educ G – Crear cuenta");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        buildUI();

        // Si el usuario cierra con la X, volver al login
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loginFrame.setVisible(true);
            }
        });
    }

    private void buildUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Color.WHITE);
        setContentPane(root);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;

        // ── Panel Izquierdo: Información del Proyecto (50% del ancho) ────────
        JPanel leftPanel = createLeftPanel();
        gbc.gridx = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        root.add(leftPanel, gbc);

        // ── Panel Derecho: Formulario de Registro (70% del ancho) ────────────
        JPanel rightPanel = createRightPanel();
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        root.add(rightPanel, gbc);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(new Color(20, 40, 70));

        // Intentar cargar la imagen
        java.io.File imageFile = new java.io.File("assets/registro-panel-izquierdo.png");

        if (imageFile.exists()) {
            try {
                ImageIcon imageIcon = new ImageIcon("assets/registro-panel-izquierdo.png");

                JLabel imageLabel = new JLabel() {
                    private ImageIcon icon = imageIcon;

                    @Override
                    public void paint(Graphics g) {
                        if (icon != null && icon.getImage() != null) {
                            Image img = icon.getImage();
                            g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                        }
                    }
                };

                panel.add(imageLabel, BorderLayout.CENTER);
            } catch (Exception e) {
                panel.add(createTextPanel(), BorderLayout.CENTER);
            }
        } else {
            panel.add(createTextPanel(), BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createTextPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(80, 60, 80, 60));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel mainTitle = new JLabel("Educ G");
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 48));
        mainTitle.setForeground(Color.WHITE);
        mainTitle.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(mainTitle);
        panel.add(Box.createVerticalStrut(10));

        JLabel subtitle = new JLabel("Programación y Desarrollo");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(180, 210, 255));
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(subtitle);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(30, 5, 80));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);

        // Contenedor centrado
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setOpaque(false);
        centerContainer.setMaximumSize(new Dimension(420, 750));
        centerContainer.setPreferredSize(new Dimension(420, 750));

        // Tarjeta blanca con sombra mejorada
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Sombra externa
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fill(new RoundRectangle2D.Float(6, 10, getWidth() - 8, getHeight() - 10, 20, 20));
                // Fondo blanco
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 6, getHeight() - 8, 20, 20));
                // Borde sutil
                g2.setColor(new Color(220, 225, 230));
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 7, getHeight() - 9, 20, 20));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardGbc.weightx = 1.0;
        cardGbc.gridx = 0;

        // Título de la tarjeta
        JLabel cardTitle = new JLabel("Crear cuenta");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        cardTitle.setForeground(new Color(20, 40, 70));
        cardTitle.setHorizontalAlignment(SwingConstants.CENTER);
        addCardRow(card, cardTitle, cardGbc, 0, new Insets(0, 0, 25, 0));

        // Nombre
        addCardRow(card, createSectionLabel("Nombre"), cardGbc, 1, new Insets(0, 0, 8, 0));
        nombreField = createStyledField();
        addCardRow(card, nombreField, cardGbc, 2, new Insets(0, 0, 15, 0));

        // Apellido
        addCardRow(card, createSectionLabel("Apellido"), cardGbc, 3, new Insets(0, 0, 8, 0));
        apellidoField = createStyledField();
        addCardRow(card, apellidoField, cardGbc, 4, new Insets(0, 0, 15, 0));

        // Email
        addCardRow(card, createSectionLabel("Correo Electrónico"), cardGbc, 5, new Insets(0, 0, 8, 0));
        emailField = createStyledField();
        emailField.setToolTipText("ejemplo@dominio.com");
        addCardRow(card, emailField, cardGbc, 6, new Insets(0, 0, 15, 0));

        // Contraseña
        addCardRow(card, createSectionLabel("Contraseña (6–20 caracteres)"), cardGbc, 7, new Insets(0, 0, 8, 0));
        passwordField = createStyledPasswordField();
        addCardRow(card, passwordField, cardGbc, 8, new Insets(0, 0, 15, 0));

        // Confirmar contraseña
        addCardRow(card, createSectionLabel("Confirmar contraseña"), cardGbc, 9, new Insets(0, 0, 8, 0));
        confirmPasswordField = createStyledPasswordField();
        addCardRow(card, confirmPasswordField, cardGbc, 10, new Insets(0, 0, 12, 0));

        // Checkbox mostrar contraseñas
        showPasswordCheck = new JCheckBox("Mostrar contraseñas");
        showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheck.setForeground(new Color(127, 140, 141));
        showPasswordCheck.setOpaque(false);
        showPasswordCheck.addActionListener(e -> togglePasswordVisibility());
        addCardRow(card, showPasswordCheck, cardGbc, 11, new Insets(0, 0, 25, 0));

        // Botón registrarse
        JButton registerBtn = createRegisterButton("Crear cuenta");
        registerBtn.addActionListener(e -> handleRegister());
        addCardRow(card, registerBtn, cardGbc, 12, new Insets(0, 0, 14, 0));

        // Divisor
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(220, 225, 230));
        addCardRow(card, sep, cardGbc, 13, new Insets(14, 0, 14, 0));

        // Botón volver
        JButton backBtn = createBackButton("← Volver al inicio de sesión");
        backBtn.addActionListener(e -> backToLogin());
        addCardRow(card, backBtn, cardGbc, 14, new Insets(0, 0, 0, 0));

        centerContainer.add(card, BorderLayout.CENTER);
        panel.add(centerContainer);

        getRootPane().setDefaultButton(registerBtn);
        return panel;
    }

    private JLabel createSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(44, 62, 80));
        return lbl;
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(new Color(248, 250, 252));
        f.setForeground(new Color(44, 62, 80));
        f.setCaretColor(new Color(41, 128, 185));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 230), 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));
        f.setPreferredSize(new Dimension(0, 42));
        return f;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField f = new JPasswordField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(new Color(248, 250, 252));
        f.setForeground(new Color(44, 62, 80));
        f.setCaretColor(new Color(41, 128, 185));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 230), 1, true),
            new EmptyBorder(10, 12, 10, 12)
        ));
        f.setPreferredSize(new Dimension(0, 42));
        return f;
    }

    private JButton createRegisterButton(String label) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(25, 90, 150));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(52, 152, 219));
                } else {
                    g2.setColor(new Color(41, 128, 185));
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label,
                    (getWidth() - fm.stringWidth(label)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(0, 46));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createBackButton(String label) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(236, 245, 253) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(41, 128, 185));
                g2.setStroke(new BasicStroke(2.0f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 10, 10));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.setColor(new Color(41, 128, 185));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label,
                    (getWidth() - fm.stringWidth(label)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(0, 46));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void addCardRow(JPanel panel, JComponent comp, GridBagConstraints gbc, int row, Insets insets) {
        gbc.gridy = row;
        gbc.insets = insets;
        panel.add(comp, gbc);
    }

    private void togglePasswordVisibility() {
        char echo = showPasswordCheck.isSelected() ? (char) 0 : '•';
        passwordField.setEchoChar(echo);
        confirmPasswordField.setEchoChar(echo);
    }

    private void handleRegister() {
        String nombre   = nombreField.getText().trim();
        String apellido = apellidoField.getText().trim();
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm  = new String(confirmPasswordField.getPassword());

        if (!Validator.isValidName(nombre)) {
            showError("El nombre debe tener entre 2 y 100 caracteres y no contener símbolos especiales.");
            nombreField.requestFocus(); return;
        }
        if (!Validator.isValidName(apellido)) {
            showError("El apellido debe tener entre 2 y 100 caracteres y no contener símbolos especiales.");
            apellidoField.requestFocus(); return;
        }
        if (!Validator.isValidEmail(email)) {
            showError("Ingresá un correo electrónico válido.\nEjemplo: usuario@dominio.com");
            emailField.requestFocus(); return;
        }
        if (!Validator.isValidPassword(password)) {
            showError("La contraseña debe tener entre 6 y 20 caracteres alfanuméricos\n"
                    + "(solo letras y números, sin símbolos).");
            passwordField.requestFocus(); return;
        }
        if (!password.equals(confirm)) {
            showError("Las contraseñas no coinciden.");
            confirmPasswordField.requestFocus(); return;
        }

        try {
            if (AuthService.register(email, password, nombre, apellido)) {
                CustomDialog successDialog = new CustomDialog(this, CustomDialog.DialogType.SUCCESS,
                    "¡Registro exitoso!", "¡Cuenta creada exitosamente! Ya podés iniciar sesión.");
                successDialog.setOnCloseListener(this::backToLogin);
            } else {
                showError("El correo electrónico ya está registrado.\nUsá otro o iniciá sesión.");
            }
        } catch (Exception ex) {
            showError("No se pudo conectar con la base de datos:\n" + ex.getMessage());
        }
    }

    private void backToLogin() {
        loginFrame.setVisible(true);
        dispose();
    }

    private void showError(String msg) {
        CustomDialog.showError(this, msg);
    }

    private void showSuccess(String msg) {
        CustomDialog.showSuccess(this, msg);
    }
}
