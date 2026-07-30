package vista;

import controlador.ControladorLogin;
import vista.componentes.DialogoPersonalizado;
import vista.componentes.IconoVectorial;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;

public class VentanaLogin extends VentanaBase {

    private final ControladorLogin controlador = new ControladorLogin();

    private JTextField     campoEmail;
    private JPasswordField campoPassword;
    private JCheckBox      checkMostrarPassword;

    public VentanaLogin() {
        super("Educ G – Iniciar Sesión", EXIT_ON_CLOSE);
        construirUI();
        FabricaUI.establecerIconoVentana(this);
    }

    private void construirUI() {
        JPanel raiz = new JPanel(new GridBagLayout());
        raiz.setBackground(Color.WHITE);
        setContentPane(raiz);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;

        // ── Panel Izquierdo: Información del Proyecto ────────────────────────
        JPanel panelIzquierdo = crearPanelIzquierdo();
        gbc.gridx = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        raiz.add(panelIzquierdo, gbc);

        // ── Panel Derecho: Formulario de Login ────────────────────────────────
        JPanel panelDerecho = crearPanelDerecho();
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        raiz.add(panelDerecho, gbc);
    }

    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(EstiloUI.AZUL_OSCURO);

        java.io.File archivoImagen = new java.io.File("assets/login-panel-izquierdo.png");

        if (archivoImagen.exists()) {
            try {
                ImageIcon icono = new ImageIcon("assets/login-panel-izquierdo.png");
                JLabel etiquetaImagen = new JLabel() {
                    private final ImageIcon icon = icono;

                    @Override
                    public void paint(Graphics g) {
                        if (icon != null && icon.getImage() != null) {
                            g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
                        }
                    }
                };
                panel.add(etiquetaImagen, BorderLayout.CENTER);
            } catch (Exception e) {
                panel.add(crearPanelTexto(), BorderLayout.CENTER);
            }
        } else {
            panel.add(crearPanelTexto(), BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel crearPanelTexto() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(80, 60, 80, 60));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(EstiloUI.MORADO_ACENTO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);

        JPanel contenedorCentral = new JPanel(new BorderLayout());
        contenedorCentral.setOpaque(false);
        contenedorCentral.setMaximumSize(new Dimension(420, 600));
        contenedorCentral.setPreferredSize(new Dimension(420, 600));

        JPanel tarjeta = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fill(new RoundRectangle2D.Float(6, 10, getWidth() - 8, getHeight() - 10, 20, 20));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 6, getHeight() - 8, 20, 20));
                g2.setColor(EstiloUI.BORDE);
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 7, getHeight() - 9, 20, 20));
                g2.dispose();
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbcTarjeta = new GridBagConstraints();
        gbcTarjeta.fill = GridBagConstraints.HORIZONTAL;
        gbcTarjeta.weightx = 1.0;
        gbcTarjeta.gridx = 0;

        JLabel tituloTarjeta = new JLabel("Iniciar Sesión");
        tituloTarjeta.setFont(EstiloUI.FUENTE_ENCABEZADO);
        tituloTarjeta.setForeground(EstiloUI.AZUL_OSCURO);
        tituloTarjeta.setHorizontalAlignment(SwingConstants.CENTER);
        agregarFilaTarjeta(tarjeta, tituloTarjeta, gbcTarjeta, 0, new Insets(0, 0, 30, 0));

        agregarFilaTarjeta(tarjeta, FabricaUI.crearEtiqueta("Correo Electrónico"), gbcTarjeta, 1, new Insets(0, 0, 8, 0));
        campoEmail = FabricaUI.crearCampo();
        campoEmail.setToolTipText("ejemplo@dominio.com");
        agregarFilaTarjeta(tarjeta, campoEmail, gbcTarjeta, 2, new Insets(0, 0, 20, 0));

        agregarFilaTarjeta(tarjeta, FabricaUI.crearEtiqueta("Contraseña"), gbcTarjeta, 3, new Insets(0, 0, 8, 0));
        campoPassword = FabricaUI.crearCampoPassword();
        agregarFilaTarjeta(tarjeta, campoPassword, gbcTarjeta, 4, new Insets(0, 0, 12, 0));

        checkMostrarPassword = new JCheckBox("Mostrar contraseña");
        checkMostrarPassword.setFont(EstiloUI.FUENTE_PEQUENA);
        checkMostrarPassword.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        checkMostrarPassword.setOpaque(false);
        checkMostrarPassword.addActionListener(e -> alternarVisibilidadPassword());
        agregarFilaTarjeta(tarjeta, checkMostrarPassword, gbcTarjeta, 5, new Insets(0, 0, 28, 0));

        JButton botonLogin = FabricaUI.crearBotonPrimario("Iniciar Sesión", IconoVectorial.Tipo.USUARIO);
        botonLogin.addActionListener(e -> manejarLogin());
        agregarFilaTarjeta(tarjeta, botonLogin, gbcTarjeta, 6, new Insets(0, 0, 16, 0));

        JSeparator sep = new JSeparator();
        sep.setForeground(EstiloUI.BORDE);
        agregarFilaTarjeta(tarjeta, sep, gbcTarjeta, 7, new Insets(16, 0, 16, 0));

        JLabel sinCuentaLbl = new JLabel("¿No tenés cuenta?");
        sinCuentaLbl.setFont(EstiloUI.FUENTE_PEQUENA);
        sinCuentaLbl.setForeground(EstiloUI.TEXTO_ATENUADO);
        sinCuentaLbl.setHorizontalAlignment(SwingConstants.CENTER);
        agregarFilaTarjeta(tarjeta, sinCuentaLbl, gbcTarjeta, 8, new Insets(0, 0, 12, 0));

        JButton botonRegistro = FabricaUI.crearBotonSecundario("Crear Nueva Cuenta", IconoVectorial.Tipo.AGREGAR);
        botonRegistro.addActionListener(e -> abrirRegistro());
        agregarFilaTarjeta(tarjeta, botonRegistro, gbcTarjeta, 9, new Insets(0, 0, 0, 0));

        contenedorCentral.add(tarjeta, BorderLayout.CENTER);
        panel.add(contenedorCentral);

        getRootPane().setDefaultButton(botonLogin);
        return panel;
    }

    private void agregarFilaTarjeta(JPanel panel, JComponent comp, GridBagConstraints gbc, int fila, Insets insets) {
        gbc.gridy = fila;
        gbc.insets = insets;
        panel.add(comp, gbc);
    }

    private void alternarVisibilidadPassword() {
        campoPassword.setEchoChar(checkMostrarPassword.isSelected() ? (char) 0 : '•');
    }

    private void manejarLogin() {
        String email    = campoEmail.getText().trim();
        String password = new String(campoPassword.getPassword());

        try {
            if (controlador.iniciarSesion(email, password)) {
                if (!iniciarTransicionUnica()) return;
                dispose();
                if (controlador.esAdmin(email)) {
                    new vista.admin.VentanaAdmin(email).setVisible(true);
                } else {
                    new VentanaCursos(email).setVisible(true);
                }
            } else {
                mostrarError("Correo electrónico o contraseña incorrectos.");
            }
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        } catch (SQLException ex) {
            mostrarError("No se pudo conectar con la base de datos:\n" + ex.getMessage());
        }
    }

    private void abrirRegistro() {
        if (!iniciarTransicionUnica()) return;
        setVisible(false);
        new VentanaRegistro(this).setVisible(true);
    }

    private void mostrarError(String msg) {
        DialogoPersonalizado.mostrarError(this, msg);
    }
}
