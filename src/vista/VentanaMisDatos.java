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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

/** Módulo "Mis Datos": datos personales y cambio de contraseña del alumno. */
public class VentanaMisDatos extends VentanaBase {

    private final ControladorPanelUsuario controlador = new ControladorPanelUsuario();
    private final String emailUsuario;

    public VentanaMisDatos(String emailUsuario) {
        super("Educ G", EXIT_ON_CLOSE);
        this.emailUsuario = emailUsuario;
        construirUI();
        FabricaUI.establecerIconoVentana(this);
        activarBurbujaChatbot(emailUsuario);
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new BorderLayout());
        setContentPane(raiz);

        String nombreUsuario = resolverNombreUsuario();
        setTitle("Educ G – " + nombreUsuario);

        raiz.add(construirEncabezado(), BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 20, 20, 20));
        wrapper.add(construirPanelDatos(), BorderLayout.CENTER);
        raiz.add(wrapper, BorderLayout.CENTER);
    }

    private JPanel construirEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(true);
        encabezado.setBackground(EstiloUI.FONDO_SUAVE);
        encabezado.setBorder(new EmptyBorder(24, 32, 16, 32));

        JPanel bloqueTitulo = new JPanel();
        bloqueTitulo.setOpaque(false);
        bloqueTitulo.setLayout(new BoxLayout(bloqueTitulo, BoxLayout.Y_AXIS));

        JLabel appLbl = FabricaUI.crearLogoEducG(100);


        bloqueTitulo.add(appLbl);

        JButton botonCerrarSesion = FabricaUI.crearBotonSecundarioPequeno("Cerrar Sesión", IconoVectorial.Tipo.SALIR);
        botonCerrarSesion.addActionListener(e -> {
            if (!iniciarTransicionUnica()) return;
            dispose();
            new VentanaLogin().setVisible(true);
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(botonCerrarSesion);

        encabezado.add(bloqueTitulo, BorderLayout.WEST);
        encabezado.add(botones, BorderLayout.EAST);

        // ── Pestañas de navegación ─────────────────────────────────────────
        JPanel pestanas = crearPestanas();
        encabezado.add(pestanas, BorderLayout.SOUTH);
        return encabezado;
    }

    private JScrollPane construirPanelDatos() {
        // Título de la sección
        JLabel tituloSeccion = new JLabel("Mis Datos");
        tituloSeccion.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tituloSeccion.setForeground(Color.WHITE);
        tituloSeccion.setBorder(new EmptyBorder(24, 32, 16, 32));
        tituloSeccion.setOpaque(false);

        // Panel principal de contenido
        JPanel contenido = new JPanel();
        contenido.setBackground(EstiloUI.FONDO_SUAVE);
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

        contenido.add(panelPassword);
        contenido.add(Box.createVerticalGlue());

        // Panel con título + contenido
        JPanel panelConTitulo = new JPanel(new BorderLayout());
        panelConTitulo.setOpaque(false);
        panelConTitulo.add(tituloSeccion, BorderLayout.NORTH);
        panelConTitulo.add(contenido, BorderLayout.CENTER);

        // Envolver en contenedor centrado: 30% glue - 40% contenido - 30% glue
        JPanel contenedorCentrado = new JPanel(new GridBagLayout());
        contenedorCentrado.setOpaque(false);
        contenedorCentrado.setBorder(new EmptyBorder(24, 32, 26, 32));

        // Panel glue izquierda (30%)
        JPanel glueIzq = new JPanel();
        glueIzq.setOpaque(false);
        GridBagConstraints gbcIzq = new GridBagConstraints();
        gbcIzq.weightx = 0.225;
        gbcIzq.weighty = 1;
        gbcIzq.fill = GridBagConstraints.BOTH;
        contenedorCentrado.add(glueIzq, gbcIzq);

        // Contenido (40%)
        GridBagConstraints gbcContenido = new GridBagConstraints();
        gbcContenido.weightx = 0.55;
        gbcContenido.weighty = 1;
        gbcContenido.fill = GridBagConstraints.BOTH;
        contenedorCentrado.add(panelConTitulo, gbcContenido);

        // Panel glue derecha (30%)
        JPanel glueDer = new JPanel();
        glueDer.setOpaque(false);
        GridBagConstraints gbcDer = new GridBagConstraints();
        gbcDer.weightx = 0.225;
        gbcDer.weighty = 1;
        gbcDer.fill = GridBagConstraints.BOTH;
        contenedorCentrado.add(glueDer, gbcDer);

        JScrollPane scroll = new JScrollPane(contenedorCentrado);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
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

    private JPanel crearPestanas() {
        JPanel pestanas = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pestanas.setOpaque(false);
        pestanas.setBorder(new EmptyBorder(0, 0, 0, 0));

        String[] labels = {"Catálogo de Cursos", "Mis Datos", "Mis Cursos", "Estadísticas"};
        Runnable[] acciones = {
            () -> abrirVentana(new VentanaCursos(emailUsuario)),
            () -> {},
            () -> abrirVentana(new VentanaMisCursos(emailUsuario)),
            () -> abrirVentana(new VentanaMisEstadisticas(emailUsuario))
        };

        for (int i = 0; i < labels.length; i++) {
            JLabel pestaña = crearPestaña(labels[i], i == 1);
            final int index = i;
            pestaña.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (index == 1) return;
                    if (!iniciarTransicionUnica()) return;
                    dispose();
                    acciones[index].run();
                }
            });
            pestanas.add(pestaña);
        }

        return pestanas;
    }

    private JLabel crearPestaña(String texto, boolean activa) {
        JLabel pestaña = new JLabel(texto);
        pestaña.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pestaña.setForeground(activa ? new Color(37, 99, 235) : new Color(80, 100, 130));
        pestaña.setBorder(new EmptyBorder(6, 14, 6, 14));
        pestaña.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pestaña.setOpaque(false);

        if (!activa) {
            pestaña.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    pestaña.setForeground(new Color(37, 99, 235));
                }
                @Override public void mouseExited(MouseEvent e) {
                    pestaña.setForeground(new Color(80, 100, 130));
                }
            });
        }

        return pestaña;
    }

    private String resolverNombreUsuario() {
        try {
            Usuario usuario = controlador.obtenerDatosUsuario(emailUsuario);
            if (usuario != null) return usuario.getNombre();
        } catch (Exception ignored) {}
        return emailUsuario;
    }

    private void abrirVentana(VentanaBase ventana) {
        ventana.setVisible(true);
    }
}
