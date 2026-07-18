package vista;

import controlador.ControladorPanelUsuario;
import modelo.Curso;
import modelo.EstadisticasUsuario;
import modelo.Inscripcion;
import modelo.ResultadoTest;
import modelo.Usuario;
import vista.componentes.DialogoPersonalizado;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Panel principal del usuario: datos personales, cursos inscriptos y estadísticas. */
public class VentanaPanelUsuario extends VentanaBase {

    private final ControladorPanelUsuario controlador = new ControladorPanelUsuario();
    private final String emailUsuario;
    private JTabbedPane tabPane;

    public VentanaPanelUsuario(String emailUsuario) {
        super("Educ G – Mi Panel", EXIT_ON_CLOSE);
        this.emailUsuario = emailUsuario;
        construirUI();
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new BorderLayout());
        setContentPane(raiz);

        raiz.add(construirEncabezado(), BorderLayout.NORTH);

        tabPane = new JTabbedPane(JTabbedPane.TOP);
        tabPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabPane.addTab("  👤  Mis Datos  ",    construirPanelDatos());
        tabPane.addTab("  📚  Mis Cursos  ",   construirPanelCursos());
        tabPane.addTab("  📊  Estadísticas  ", construirPanelEstadisticas());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(4, 20, 20, 20));
        wrapper.add(tabPane, BorderLayout.CENTER);
        raiz.add(wrapper, BorderLayout.CENTER);
    }

    private void actualizarPestanaCursos()        { tabPane.setComponentAt(1, construirPanelCursos()); }
    private void actualizarPestanaEstadisticas()  { tabPane.setComponentAt(2, construirPanelEstadisticas()); }

    // ── Encabezado ────────────────────────────────────────────────────────────

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

        JLabel paginaLbl = new JLabel("Mi Panel – " + resolverNombreUsuario());
        paginaLbl.setFont(EstiloUI.FUENTE_SUBTITULO_COMPACTO);
        paginaLbl.setForeground(new Color(180, 210, 255));

        bloqueTitulo.add(appLbl);
        bloqueTitulo.add(Box.createVerticalStrut(2));
        bloqueTitulo.add(paginaLbl);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setOpaque(false);

        JButton botonCatalogo = FabricaUI.crearBotonSecundarioPequeno("Ver Catálogo");
        botonCatalogo.addActionListener(e -> { dispose(); new VentanaCursos(emailUsuario).setVisible(true); });

        JButton botonCerrarSesion = FabricaUI.crearBotonSecundarioPequeno("Cerrar Sesión");
        botonCerrarSesion.addActionListener(e -> { dispose(); new VentanaLogin().setVisible(true); });

        panelBotones.add(botonCatalogo);
        panelBotones.add(botonCerrarSesion);

        encabezado.add(bloqueTitulo, BorderLayout.WEST);
        encabezado.add(panelBotones, BorderLayout.EAST);
        return encabezado;
    }

    // ── Pestaña 1: Mis Datos ─────────────────────────────────────────────────

    private JScrollPane construirPanelDatos() {
        JPanel contenido = new JPanel();
        contenido.setBackground(new Color(245, 248, 252));
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(24, 40, 24, 40));

        String nombre = "", apellido = "";
        try {
            Usuario usuario = controlador.obtenerDatosUsuario(emailUsuario);
            if (usuario != null) { nombre = usuario.getNombre(); apellido = usuario.getApellido(); }
        } catch (Exception ignored) {}

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

        JTextField campoEmail = FabricaUI.crearCampo();
        campoEmail.setText(emailUsuario);
        campoEmail.setEditable(false);
        campoEmail.setBackground(new Color(235, 240, 245));
        contenido.add(bloqueCampo("Correo electrónico (no modificable)", campoEmail));
        contenido.add(Box.createVerticalStrut(18));

        JButton botonGuardar = FabricaUI.crearBotonPrimario("Guardar cambios");
        botonGuardar.setAlignmentX(LEFT_ALIGNMENT);
        botonGuardar.addActionListener(e -> {
            String n = campoNombre.getText().trim();
            String a = campoApellido.getText().trim();
            try {
                controlador.actualizarDatosPersonales(emailUsuario, n, a);
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

        agregarTituloSeccion(contenido, "Cambiar Contraseña");
        contenido.add(Box.createVerticalStrut(14));

        JPasswordField campoPasswordActual = FabricaUI.crearCampoPassword();
        JPasswordField campoPasswordNueva  = FabricaUI.crearCampoPassword();
        JPasswordField campoPasswordConfirmar = FabricaUI.crearCampoPassword();

        contenido.add(bloqueCampo("Contraseña actual", campoPasswordActual));
        contenido.add(Box.createVerticalStrut(10));
        contenido.add(bloqueCampo("Nueva contraseña  (6–20 caracteres alfanuméricos)", campoPasswordNueva));
        contenido.add(Box.createVerticalStrut(10));
        contenido.add(bloqueCampo("Confirmar nueva contraseña", campoPasswordConfirmar));
        contenido.add(Box.createVerticalStrut(18));

        JButton botonCambiarPassword = FabricaUI.crearBotonPrimario("Cambiar contraseña");
        botonCambiarPassword.setAlignmentX(LEFT_ALIGNMENT);
        botonCambiarPassword.addActionListener(e -> {
            String actual    = new String(campoPasswordActual.getPassword());
            String nueva     = new String(campoPasswordNueva.getPassword());
            String confirmar = new String(campoPasswordConfirmar.getPassword());
            try {
                if (controlador.cambiarPassword(emailUsuario, actual, nueva, confirmar)) {
                    DialogoPersonalizado.mostrarExito(this, "¡Contraseña actualizada correctamente!");
                    campoPasswordActual.setText("");
                    campoPasswordNueva.setText("");
                    campoPasswordConfirmar.setText("");
                } else {
                    mostrarError("La contraseña actual es incorrecta.");
                }
            } catch (IllegalArgumentException ex) {
                mostrarError(ex.getMessage());
            } catch (SQLException ex) {
                mostrarError("Error: " + ex.getMessage());
            }
        });
        contenido.add(botonCambiarPassword);
        contenido.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        return scroll;
    }

    // ── Pestaña 2: Mis Cursos ────────────────────────────────────────────────

    private JScrollPane construirPanelCursos() {
        JPanel contenido = new JPanel();
        contenido.setBackground(new Color(245, 248, 252));
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(20, 24, 20, 24));

        try {
            List<Inscripcion> inscripciones = controlador.obtenerCursosInscriptos(emailUsuario);
            if (inscripciones.isEmpty()) {
                agregarTituloSeccion(contenido, "Mis Cursos");
                contenido.add(Box.createVerticalStrut(16));
                JLabel vacioLbl = new JLabel("No estás inscripto en ningún curso aún.");
                vacioLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                vacioLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
                vacioLbl.setAlignmentX(LEFT_ALIGNMENT);
                contenido.add(vacioLbl);
                contenido.add(Box.createVerticalStrut(14));
                JButton botonIr = FabricaUI.crearBotonPrimario("Explorar Catálogo");
                botonIr.setAlignmentX(LEFT_ALIGNMENT);
                botonIr.addActionListener(e -> { dispose(); new VentanaCursos(emailUsuario).setVisible(true); });
                contenido.add(botonIr);
            } else {
                agregarTituloSeccion(contenido, "Tus cursos inscriptos  (" + inscripciones.size() + ")");
                contenido.add(Box.createVerticalStrut(14));
                for (Inscripcion inscripcion : inscripciones) {
                    contenido.add(construirFilaCurso(inscripcion));
                    contenido.add(Box.createVerticalStrut(10));
                }
            }
        } catch (Exception ex) {
            JLabel errLbl = new JLabel("Error al cargar los cursos: " + ex.getMessage());
            errLbl.setFont(EstiloUI.FUENTE_PEQUENA);
            errLbl.setForeground(Color.RED);
            errLbl.setAlignmentX(LEFT_ALIGNMENT);
            contenido.add(errLbl);
        }

        contenido.add(Box.createVerticalGlue());
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        return scroll;
    }

    private JPanel construirFilaCurso(Inscripcion inscripcion) throws SQLException {
        String cursoTitulo = inscripcion.getCursoTitulo();
        Curso curso = controlador.buscarCurso(cursoTitulo);
        String emoji = (curso != null) ? curso.getEmoji() : "📘";

        JPanel fila = new JPanel(new BorderLayout(12, 0)) {
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
        fila.setOpaque(false);
        fila.setBorder(new EmptyBorder(12, 16, 12, 16));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        fila.setAlignmentX(LEFT_ALIGNMENT);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel tituloLbl = new JLabel(emoji + "  " + cursoTitulo);
        tituloLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tituloLbl.setForeground(EstiloUI.TEXTO_PRIMARIO);

        String fechaInscripcion = inscripcion.getFechaInscripcion();
        String fechaStr = (fechaInscripcion != null && fechaInscripcion.length() >= 10)
            ? fechaInscripcion.substring(0, 10) : (fechaInscripcion != null ? fechaInscripcion : "—");
        JLabel fechaLbl = new JLabel("Inscripto el: " + fechaStr);
        fechaLbl.setFont(EstiloUI.FUENTE_PEQUENA);
        fechaLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);

        info.add(tituloLbl);
        info.add(Box.createVerticalStrut(4));
        info.add(fechaLbl);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);

        JButton botonIngresar = FabricaUI.crearBotonPrimarioPequeno("Ingresar");
        botonIngresar.addActionListener(e -> abrirCurso(curso));

        JButton botonBaja = FabricaUI.crearBotonSecundarioPequeno("Darse de baja");
        botonBaja.addActionListener(e -> DialogoPersonalizado.mostrarConfirmacion(this,
            "Darse de baja", "¿Confirmar baja del curso \"" + cursoTitulo + "\"?", "Sí, dar de baja",
            () -> {
                try {
                    controlador.darDeBajaCurso(emailUsuario, cursoTitulo);
                    actualizarPestanaCursos();
                    actualizarPestanaEstadisticas();
                } catch (SQLException ex) {
                    mostrarError("Error al procesar la baja: " + ex.getMessage());
                }
            }));

        botones.add(botonIngresar);
        botones.add(botonBaja);

        fila.add(info, BorderLayout.CENTER);
        fila.add(botones, BorderLayout.EAST);
        return fila;
    }

    private void abrirCurso(Curso curso) {
        if (curso == null) {
            mostrarError("Contenido no disponible.");
            return;
        }
        String nombreUsuario = resolverNombreUsuario();
        dispose();
        new VentanaContenidoCurso(curso, emailUsuario, nombreUsuario,
            () -> new VentanaPanelUsuario(emailUsuario).setVisible(true)).setVisible(true);
    }

    // ── Pestaña 3: Estadísticas ───────────────────────────────────────────────

    private JScrollPane construirPanelEstadisticas() {
        JPanel contenido = new JPanel();
        contenido.setBackground(new Color(245, 248, 252));
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(20, 24, 20, 24));

        EstadisticasUsuario estadisticas = new EstadisticasUsuario(0, 0, 0);
        List<ResultadoTest> resultadosTests = new ArrayList<>();
        try {
            estadisticas    = controlador.obtenerEstadisticas(emailUsuario);
            resultadosTests = controlador.obtenerResultadosTests(emailUsuario);
        } catch (Exception ignored) {}

        agregarTituloSeccion(contenido, "Resumen General");
        contenido.add(Box.createVerticalStrut(14));

        JPanel filaTarjetas = new JPanel(new GridLayout(1, 3, 14, 0));
        filaTarjetas.setOpaque(false);
        filaTarjetas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));
        filaTarjetas.setAlignmentX(LEFT_ALIGNMENT);
        filaTarjetas.add(tarjetaEstadistica("📚", "Cursos Inscriptos", String.valueOf(estadisticas.getCursosInscriptos())));
        filaTarjetas.add(tarjetaEstadistica("📝", "Tests Realizados",  String.valueOf(estadisticas.getTestsRealizados())));
        filaTarjetas.add(tarjetaEstadistica("⭐", "Promedio",
            estadisticas.getTestsRealizados() > 0 ? estadisticas.getPromedio() + " pts" : "—"));
        contenido.add(filaTarjetas);

        contenido.add(Box.createVerticalStrut(28));
        contenido.add(crearSeparador());
        contenido.add(Box.createVerticalStrut(20));
        agregarTituloSeccion(contenido, "Historial de Tests");
        contenido.add(Box.createVerticalStrut(12));

        if (resultadosTests.isEmpty()) {
            JLabel noLbl = new JLabel("No hay resultados de tests registrados aún.");
            noLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            noLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
            noLbl.setAlignmentX(LEFT_ALIGNMENT);
            contenido.add(noLbl);
        } else {
            String[] columnas = {"Curso", "Test", "Puntaje", "Fecha"};
            Object[][] datos = resultadosTests.stream().map(r -> new Object[]{
                r.getCursoTitulo(), r.getNombreTest(), r.getPuntaje() + " / 100",
                r.getFecha() != null && r.getFecha().length() >= 10 ? r.getFecha().substring(0, 10) : r.getFecha()
            }).toArray(Object[][]::new);

            JTable tabla = new JTable(datos, columnas) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            tabla.setFont(EstiloUI.FUENTE_ETIQUETA);
            tabla.setRowHeight(28);
            tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            tabla.setSelectionBackground(new Color(220, 235, 255));

            JScrollPane scrollTabla = new JScrollPane(tabla);
            scrollTabla.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
            scrollTabla.setAlignmentX(LEFT_ALIGNMENT);
            scrollTabla.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE, 1));
            contenido.add(scrollTabla);
        }

        contenido.add(Box.createVerticalGlue());
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        return scroll;
    }

    // ── Componentes auxiliares ────────────────────────────────────────────────

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

    private JPanel tarjetaEstadistica(String icono, String etiqueta, String valor) {
        JPanel tarjeta = new JPanel() {
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
        tarjeta.setOpaque(false);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel iconoLbl = new JLabel(icono);
        iconoLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        iconoLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel valorLbl = new JLabel(valor);
        valorLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valorLbl.setForeground(EstiloUI.AZUL_CLARO);
        valorLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel etiquetaLbl = new JLabel(etiqueta);
        etiquetaLbl.setFont(EstiloUI.FUENTE_PEQUENA);
        etiquetaLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        etiquetaLbl.setAlignmentX(CENTER_ALIGNMENT);

        tarjeta.add(iconoLbl);
        tarjeta.add(Box.createVerticalStrut(5));
        tarjeta.add(valorLbl);
        tarjeta.add(Box.createVerticalStrut(3));
        tarjeta.add(etiquetaLbl);
        return tarjeta;
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

    private String resolverNombreUsuario() {
        try {
            Usuario usuario = controlador.obtenerDatosUsuario(emailUsuario);
            if (usuario != null) return usuario.getNombre();
        } catch (Exception ignored) {}
        return emailUsuario;
    }
}
