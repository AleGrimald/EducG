package vista;

import controlador.ControladorRegistro;
import vista.componentes.DialogoPersonalizado;
import vista.componentes.FiltroCaracteres;
import vista.componentes.IconoVectorial;
import vista.componentes.VentanaVerificacionCodigo;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;

public class VentanaRegistro extends VentanaBase {

    /** Letras (con acentos/ñ) y espacios — usado en Nombre/Apellido. */
    private static final String PATRON_LETRAS = "[a-zA-ZÁÉÍÓÚÑÜáéíóúñü ]";

    private final ControladorRegistro controlador = new ControladorRegistro();
    private final VentanaLogin ventanaLogin;

    private CardLayout cardLayoutPasos;
    private JPanel      panelPasos;
    private JButton      botonSiguiente;
    private JButton      botonRegistrar;

    private JTextField     campoNombre;
    private JTextField     campoApellido;
    private JTextField     campoDni;
    private JTextField     campoTelefono;
    private JTextField     campoEmail;
    private JPasswordField campoPassword;
    private JPasswordField campoConfirmarPassword;
    private JCheckBox      checkMostrarPassword;

    public VentanaRegistro(VentanaLogin ventanaLogin) {
        super("Educ G – Crear cuenta", DISPOSE_ON_CLOSE);
        this.ventanaLogin = ventanaLogin;
        construirUI();
        FabricaUI.establecerIconoVentana(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ventanaLogin.setVisible(true);
            }
        });
    }

    private void construirUI() {
        JPanel raiz = new JPanel(new GridBagLayout());
        raiz.setBackground(Color.WHITE);
        setContentPane(raiz);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;

        JPanel panelIzquierdo = crearPanelIzquierdo();
        gbc.gridx = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        raiz.add(panelIzquierdo, gbc);

        // ── Panel Derecho: Formulario de Registro ─────────────────────────────
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

        java.io.File archivoImagen = new java.io.File("assets/registro-panel-izquierdo.png");

        if (archivoImagen.exists()) {
            try {
                ImageIcon icono = new ImageIcon("assets/registro-panel-izquierdo.png");
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

        JLabel tituloPrincipal = FabricaUI.crearLogoEducG(80);
        tituloPrincipal.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(tituloPrincipal);
        panel.add(Box.createVerticalStrut(10));

        JLabel subtitulo = new JLabel("Programación y Desarrollo");
        subtitulo.setFont(EstiloUI.FUENTE_SUBTITULO_COMPACTO);
        subtitulo.setForeground(new Color(180, 210, 255));
        subtitulo.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(subtitulo);
        panel.add(Box.createVerticalGlue());

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
        contenedorCentral.setMaximumSize(new Dimension(490, 620));
        contenedorCentral.setPreferredSize(new Dimension(490, 620));

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

        JLabel tituloTarjeta = new JLabel("Crear cuenta");
        tituloTarjeta.setFont(EstiloUI.FUENTE_ENCABEZADO);
        tituloTarjeta.setForeground(EstiloUI.AZUL_OSCURO);
        tituloTarjeta.setHorizontalAlignment(SwingConstants.CENTER);
        agregarFilaTarjeta(tarjeta, tituloTarjeta, gbcTarjeta, 0, new Insets(0, 0, 25, 0));

        cardLayoutPasos = new CardLayout();
        panelPasos = new JPanel(cardLayoutPasos);
        panelPasos.setOpaque(false);
        panelPasos.add(crearPasoUno(), "paso1");
        panelPasos.add(crearPasoDos(), "paso2");
        agregarFilaTarjeta(tarjeta, panelPasos, gbcTarjeta, 1, new Insets(0, 0, 14, 0));

        JSeparator sep = new JSeparator();
        sep.setForeground(EstiloUI.BORDE);
        agregarFilaTarjeta(tarjeta, sep, gbcTarjeta, 2, new Insets(0, 0, 14, 0));

        JButton botonVolver = FabricaUI.crearBotonSecundario("Volver al login", IconoVectorial.Tipo.VOLVER);
        botonVolver.addActionListener(e -> volverAlLogin());
        agregarFilaTarjeta(tarjeta, botonVolver, gbcTarjeta, 3, new Insets(5, 0, 10, 0));

        contenedorCentral.add(tarjeta, BorderLayout.CENTER);
        panel.add(contenedorCentral);

        getRootPane().setDefaultButton(botonSiguiente);
        return panel;
    }

    private JPanel crearPasoUno() {
        JPanel paso = new JPanel(new GridBagLayout());
        paso.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        agregarFilaTarjeta(paso, FabricaUI.crearEtiqueta("Apellido"), gbc, 0, new Insets(0, 0, 8, 0));
        campoApellido = FabricaUI.crearCampo();
        FiltroCaracteres.aplicarA(campoApellido, PATRON_LETRAS);
        agregarFilaTarjeta(paso, campoApellido, gbc, 1, new Insets(0, 0, 15, 0));

        agregarFilaTarjeta(paso, FabricaUI.crearEtiqueta("Nombre"), gbc, 2, new Insets(0, 0, 8, 0));
        campoNombre = FabricaUI.crearCampo();
        FiltroCaracteres.aplicarA(campoNombre, PATRON_LETRAS);
        agregarFilaTarjeta(paso, campoNombre, gbc, 3, new Insets(0, 0, 15, 0));

        agregarFilaTarjeta(paso, FabricaUI.crearEtiqueta("DNI"), gbc, 4, new Insets(0, 0, 8, 0));
        campoDni = FabricaUI.crearCampo();
        FiltroCaracteres.aplicarA(campoDni, "[0-9]");
        agregarFilaTarjeta(paso, campoDni, gbc, 5, new Insets(0, 0, 20, 0));

        botonSiguiente = FabricaUI.crearBotonPrimario("Siguiente", IconoVectorial.Tipo.SIGUIENTE);
        botonSiguiente.addActionListener(e -> manejarSiguiente());
        agregarFilaTarjeta(paso, botonSiguiente, gbc, 6, new Insets(10, 0, 0, 0));

        return paso;
    }

    private JPanel crearPasoDos() {
        JPanel paso = new JPanel(new GridBagLayout());
        paso.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        agregarFilaTarjeta(paso, FabricaUI.crearEtiqueta("Teléfono"), gbc, 0, new Insets(0, 0, 8, 0));
        campoTelefono = FabricaUI.crearCampo();
        FiltroCaracteres.aplicarA(campoTelefono, "[0-9+\\-() ]");
        agregarFilaTarjeta(paso, campoTelefono, gbc, 1, new Insets(0, 0, 15, 0));

        agregarFilaTarjeta(paso, FabricaUI.crearEtiqueta("Correo Electrónico"), gbc, 2, new Insets(0, 0, 8, 0));
        campoEmail = FabricaUI.crearCampo();
        campoEmail.setToolTipText("ejemplo@dominio.com");
        agregarFilaTarjeta(paso, campoEmail, gbc, 3, new Insets(0, 0, 15, 0));

        agregarFilaTarjeta(paso, FabricaUI.crearEtiqueta("Contraseña (6–20 caracteres)"), gbc, 4, new Insets(0, 0, 8, 0));
        campoPassword = FabricaUI.crearCampoPassword();
        FiltroCaracteres.aplicarA(campoPassword, "[a-zA-Z0-9]");
        agregarFilaTarjeta(paso, campoPassword, gbc, 5, new Insets(0, 0, 15, 0));

        agregarFilaTarjeta(paso, FabricaUI.crearEtiqueta("Confirmar contraseña"), gbc, 6, new Insets(0, 0, 8, 0));
        campoConfirmarPassword = FabricaUI.crearCampoPassword();
        FiltroCaracteres.aplicarA(campoConfirmarPassword, "[a-zA-Z0-9]");
        agregarFilaTarjeta(paso, campoConfirmarPassword, gbc, 7, new Insets(0, 0, 12, 0));

        checkMostrarPassword = new JCheckBox("Mostrar contraseñas");
        checkMostrarPassword.setFont(EstiloUI.FUENTE_PEQUENA);
        checkMostrarPassword.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        checkMostrarPassword.setOpaque(false);
        checkMostrarPassword.addActionListener(e -> alternarVisibilidadPassword());
        agregarFilaTarjeta(paso, checkMostrarPassword, gbc, 8, new Insets(0, 0, 20, 0));

        botonRegistrar = FabricaUI.crearBotonPrimario("Crear cuenta", IconoVectorial.Tipo.GUARDAR);
        botonRegistrar.addActionListener(e -> manejarRegistro());
        agregarFilaTarjeta(paso, botonRegistrar, gbc, 9, new Insets(0, 0, 0, 0));

        return paso;
    }

    private void agregarFilaTarjeta(JPanel panel, JComponent comp, GridBagConstraints gbc, int fila, Insets insets) {
        gbc.gridy = fila;
        gbc.insets = insets;
        panel.add(comp, gbc);
    }

    private void alternarVisibilidadPassword() {
        char echo = checkMostrarPassword.isSelected() ? (char) 0 : '•';
        campoPassword.setEchoChar(echo);
        campoConfirmarPassword.setEchoChar(echo);
    }

    private void manejarSiguiente() {
        String apellido = campoApellido.getText().trim();
        String nombre   = campoNombre.getText().trim();
        String dni      = campoDni.getText().trim();

        try {
            controlador.validarPaso1(nombre, apellido, dni);
            cardLayoutPasos.show(panelPasos, "paso2");
            getRootPane().setDefaultButton(botonRegistrar);
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void manejarRegistro() {
        String nombre    = campoNombre.getText().trim();
        String apellido  = campoApellido.getText().trim();
        String dni       = campoDni.getText().trim();
        String telefono  = campoTelefono.getText().trim();
        String email     = campoEmail.getText().trim();
        String password  = new String(campoPassword.getPassword());
        String confirmar = new String(campoConfirmarPassword.getPassword());

        try {
            if (controlador.registrar(nombre, apellido, dni, telefono, email, password, confirmar)) {
                // Registro exitoso: abrir modal de verificación
                VentanaVerificacionCodigo ventanaVerif = new VentanaVerificacionCodigo(
                    this, Long.parseLong(dni), email, nombre);
                ventanaVerif.establecerListenerVerificacionExitosa(() -> {
                    DialogoPersonalizado.mostrarExito(VentanaRegistro.this,
                        "¡Cuenta verificada exitosamente!\nYa podés iniciar sesión.",
                        VentanaRegistro.this::volverAlLogin);
                });
                ventanaVerif.setVisible(true);
                // Si cierra sin verificar: también volver al login (la cuenta sigue inactiva)
                // Se manejará en el WindowListener de ventanaVerif
            } else {
                mostrarError("El correo electrónico ya está registrado.\nUsá otro o iniciá sesión.");
            }
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        } catch (SQLException ex) {
            mostrarError("No se pudo conectar con la base de datos:\n" + ex.getMessage());
        }
    }

    private void volverAlLogin() {
        ventanaLogin.setVisible(true);
        dispose();
    }

    private void mostrarError(String msg) {
        DialogoPersonalizado.mostrarError(this, msg);
    }
}
