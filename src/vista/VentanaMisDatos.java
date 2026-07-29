package vista;

import controlador.ControladorPanelUsuario;
import modelo.Usuario;
import vista.componentes.DialogoPersonalizado;
import vista.componentes.FiltroCaracteres;
import vista.componentes.IconoVectorial;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/** Módulo "Mis Datos": datos personales y cambio de contraseña del alumno. */
public class VentanaMisDatos extends VentanaBase {

    private final ControladorPanelUsuario controlador = new ControladorPanelUsuario();
    private final String emailUsuario;

    public VentanaMisDatos(String emailUsuario) {
        super("Educ G – Mis Datos", EXIT_ON_CLOSE);
        this.emailUsuario = emailUsuario;
        construirUI();
        activarBurbujaChatbot(emailUsuario);
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new BorderLayout());
        setContentPane(raiz);

        raiz.add(construirEncabezado(), BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 20, 20, 20));
        wrapper.add(construirPanelDatos(), BorderLayout.CENTER);
        raiz.add(wrapper, BorderLayout.CENTER);
    }

    private JPanel construirEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(new EmptyBorder(24, 32, 16, 32));

        JPanel bloqueTitulo = new JPanel();
        bloqueTitulo.setOpaque(false);
        bloqueTitulo.setLayout(new BoxLayout(bloqueTitulo, BoxLayout.Y_AXIS));

        JLabel appLbl = new JLabel("Educ G");
        appLbl.setFont(EstiloUI.FUENTE_TITULO_COMPACTO);
        appLbl.setForeground(Color.WHITE);

        JLabel subLbl = new JLabel("Mi Panel – Mis Datos");
        subLbl.setFont(EstiloUI.FUENTE_SUBTITULO_COMPACTO);
        subLbl.setForeground(new Color(180, 210, 255));

        bloqueTitulo.add(appLbl);
        bloqueTitulo.add(Box.createVerticalStrut(2));
        bloqueTitulo.add(subLbl);

        JButton botonVolver = FabricaUI.crearBotonSecundarioPequeno("Volver al Panel", IconoVectorial.Tipo.INICIO);
        botonVolver.addActionListener(e -> {
            if (!iniciarTransicionUnica()) return;
            dispose();
            new VentanaPanelUsuario(emailUsuario).setVisible(true);
        });

        JButton botonCerrarSesion = FabricaUI.crearBotonSecundarioPequeno("Cerrar Sesión", IconoVectorial.Tipo.SALIR);
        botonCerrarSesion.addActionListener(e -> {
            if (!iniciarTransicionUnica()) return;
            dispose();
            new VentanaLogin().setVisible(true);
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(botonVolver);
        botones.add(botonCerrarSesion);

        encabezado.add(bloqueTitulo, BorderLayout.WEST);
        encabezado.add(botones, BorderLayout.EAST);
        return encabezado;
    }

    private JScrollPane construirPanelDatos() {
        JPanel contenido = new JPanel();
        contenido.setBackground(new Color(245, 248, 252));
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(24, 40, 24, 40));

        Usuario usuarioActual = null;
        String nombre = "", apellido = "", dni = "", telefono = "";
        try {
            usuarioActual = controlador.obtenerDatosUsuario(emailUsuario);
            if (usuarioActual != null) {
                nombre   = usuarioActual.getNombre();
                apellido = usuarioActual.getApellido();
                dni      = String.valueOf(usuarioActual.getDni());
                telefono = usuarioActual.getTelefono();
            }
        } catch (Exception ignored) {}
        final Usuario usuario = usuarioActual;

        agregarTituloSeccion(contenido, "Datos Personales");
        contenido.add(Box.createVerticalStrut(14));

        JTextField campoNombre   = FabricaUI.crearCampo();
        JTextField campoApellido = FabricaUI.crearCampo();
        campoNombre.setText(nombre);
        campoApellido.setText(apellido);

        JPanel filaNombre = new JPanel(new GridLayout(1, 2, 16, 0));
        filaNombre.setOpaque(false);
        filaNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        filaNombre.setAlignmentX(LEFT_ALIGNMENT);
        filaNombre.add(bloqueCampo("Nombre", campoNombre));
        filaNombre.add(bloqueCampo("Apellido", campoApellido));
        contenido.add(filaNombre);
        contenido.add(Box.createVerticalStrut(12));

        JTextField campoDni      = FabricaUI.crearCampo();
        JTextField campoTelefono = FabricaUI.crearCampo();
        campoDni.setText(dni);
        campoTelefono.setText(telefono);
        FiltroCaracteres.aplicarA(campoDni, "[0-9]");
        FiltroCaracteres.aplicarA(campoTelefono, "[0-9+\\-() ]");

        JPanel filaDniTelefono = new JPanel(new GridLayout(1, 2, 16, 0));
        filaDniTelefono.setOpaque(false);
        filaDniTelefono.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        filaDniTelefono.setAlignmentX(LEFT_ALIGNMENT);
        filaDniTelefono.add(bloqueCampo("DNI", campoDni));
        filaDniTelefono.add(bloqueCampo("Teléfono", campoTelefono));
        contenido.add(filaDniTelefono);
        contenido.add(Box.createVerticalStrut(12));

        JTextField campoEmail = FabricaUI.crearCampo();
        campoEmail.setText(emailUsuario);
        contenido.add(bloqueCampo("Correo electrónico", campoEmail));
        contenido.add(Box.createVerticalStrut(18));

        JButton botonGuardar = FabricaUI.crearBotonPrimario("Guardar cambios", IconoVectorial.Tipo.GUARDAR);
        botonGuardar.setAlignmentX(LEFT_ALIGNMENT);
        botonGuardar.addActionListener(e -> {
            if (usuario == null) { mostrarError("No se pudieron cargar tus datos."); return; }
            String n = campoNombre.getText().trim();
            String a = campoApellido.getText().trim();
            String d = campoDni.getText().trim();
            String t = campoTelefono.getText().trim();
            String em = campoEmail.getText().trim();
            try {
                controlador.actualizarDatosPersonales(usuario.getId(), em, n, a, d, t);
                if (!em.equals(emailUsuario)) {
                    if (!iniciarTransicionUnica()) return;
                    dispose();
                    new VentanaMisDatos(em).setVisible(true);
                    return;
                }
                DialogoPersonalizado.mostrarExito(this, "¡Datos actualizados correctamente!");
            } catch (IllegalArgumentException ex) {
                mostrarError(ex.getMessage());
            } catch (SQLException ex) {
                mostrarError("Error al guardar: " + ex.getMessage());
            }
        });
        contenido.add(botonGuardar);

        contenido.add(Box.createVerticalStrut(30));
        contenido.add(crearSeparador());
        contenido.add(Box.createVerticalStrut(22));

        JPanel panelPassword = new JPanel();
        panelPassword.setOpaque(false);
        panelPassword.setLayout(new BoxLayout(panelPassword, BoxLayout.Y_AXIS));
        panelPassword.setAlignmentX(LEFT_ALIGNMENT);
        panelPassword.setVisible(false);

        agregarTituloSeccion(panelPassword, "Cambiar Contraseña");
        panelPassword.add(Box.createVerticalStrut(14));

        JPasswordField campoPasswordActual = FabricaUI.crearCampoPassword();
        JPasswordField campoPasswordNueva  = FabricaUI.crearCampoPassword();
        JPasswordField campoPasswordConfirmar = FabricaUI.crearCampoPassword();

        panelPassword.add(bloqueCampo("Contraseña actual", campoPasswordActual));
        panelPassword.add(Box.createVerticalStrut(10));
        panelPassword.add(bloqueCampo("Nueva contraseña  (6–20 caracteres alfanuméricos)", campoPasswordNueva));
        panelPassword.add(Box.createVerticalStrut(10));
        panelPassword.add(bloqueCampo("Confirmar nueva contraseña", campoPasswordConfirmar));
        panelPassword.add(Box.createVerticalStrut(18));

        JButton botonCambiarPassword = FabricaUI.crearBotonPrimario("Cambiar contraseña", IconoVectorial.Tipo.GUARDAR);
        botonCambiarPassword.setAlignmentX(LEFT_ALIGNMENT);
        botonCambiarPassword.addActionListener(e -> {
            String actual    = new String(campoPasswordActual.getPassword());
            String nueva     = new String(campoPasswordNueva.getPassword());
            String confirmar = new String(campoPasswordConfirmar.getPassword());
            try {
                if (controlador.cambiarPassword(emailUsuario, actual, nueva, confirmar)) {
                    campoPasswordActual.setText("");
                    campoPasswordNueva.setText("");
                    campoPasswordConfirmar.setText("");
                    DialogoPersonalizado.mostrarExito(this, "¡Contraseña actualizada correctamente!", () -> {
                        panelPassword.setVisible(false);
                        contenido.revalidate();
                        contenido.repaint();
                    });
                } else {
                    mostrarError("La contraseña actual es incorrecta.");
                }
            } catch (IllegalArgumentException ex) {
                mostrarError(ex.getMessage());
            } catch (SQLException ex) {
                mostrarError("Error: " + ex.getMessage());
            }
        });
        panelPassword.add(botonCambiarPassword);

        JButton botonMostrarPassword = FabricaUI.crearBotonSecundario("Cambiar Contraseña", IconoVectorial.Tipo.EDITAR);
        botonMostrarPassword.setAlignmentX(LEFT_ALIGNMENT);
        botonMostrarPassword.addActionListener(e -> {
            panelPassword.setVisible(!panelPassword.isVisible());
            contenido.revalidate();
            contenido.repaint();
        });
        contenido.add(botonMostrarPassword);
        contenido.add(Box.createVerticalStrut(14));
        contenido.add(panelPassword);
        contenido.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        return scroll;
    }

    private void agregarTituloSeccion(JPanel panel, String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(EstiloUI.TEXTO_PRIMARIO);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(lbl);
    }

    private JPanel bloqueCampo(String textoEtiqueta, JComponent campo) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));

        JLabel lbl = FabricaUI.crearEtiqueta(textoEtiqueta);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        campo.setAlignmentX(LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        p.add(campo);
        return p;
    }

    private JSeparator crearSeparador() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(210, 220, 230));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        return sep;
    }

    private void mostrarError(String msg) {
        DialogoPersonalizado.mostrarError(this, msg);
    }
}
