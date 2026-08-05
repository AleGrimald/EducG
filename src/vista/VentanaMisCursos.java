package vista;

import controlador.ControladorPanelUsuario;
import controlador.ControladorTest;
import modelo.Curso;
import modelo.Inscripcion;
import vista.componentes.DialogoPersonalizado;
import vista.componentes.IconoCurso;
import vista.componentes.IconoVectorial;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.List;

/** Módulo "Mis Cursos": listado de cursos en los que el alumno está inscripto, con acceso e ingreso al contenido. */
public class VentanaMisCursos extends VentanaBase {

    private final ControladorPanelUsuario controlador = new ControladorPanelUsuario();
    private final ControladorTest controladorTest = new ControladorTest();
    private final String emailUsuario;

    public VentanaMisCursos(String emailUsuario) {
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
        wrapper.add(construirPanelCursos(), BorderLayout.CENTER);
        raiz.add(wrapper, BorderLayout.CENTER);
    }

    private JPanel construirEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(true);
        encabezado.setBackground(new Color(240, 245, 250));
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

    private JScrollPane construirPanelCursos() {
        JPanel contenido = new JPanel();
        contenido.setBackground(new Color(245, 248, 252));
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(20, 24, 20, 24));

        try {
            List<Inscripcion> inscripciones = controlador.obtenerCursosInscriptos(emailUsuario);
            if (inscripciones.isEmpty()) {
                contenido.add(Box.createVerticalStrut(16));
                JLabel vacioLbl = new JLabel("No estás inscripto en ningún curso aún.");
                vacioLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
                vacioLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
                vacioLbl.setAlignmentX(LEFT_ALIGNMENT);
                contenido.add(vacioLbl);
                contenido.add(Box.createVerticalStrut(14));
                JButton botonIr = FabricaUI.crearBotonPrimario("Explorar Catálogo", IconoVectorial.Tipo.LISTA);
                botonIr.setAlignmentX(LEFT_ALIGNMENT);
                botonIr.addActionListener(e -> {
                    if (!iniciarTransicionUnica()) return;
                    dispose();
                    new VentanaCursos(emailUsuario).setVisible(true);
                });
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

        // Título de la sección
        JLabel tituloSeccion = new JLabel("Mis Cursos");
        tituloSeccion.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tituloSeccion.setForeground(Color.WHITE);
        tituloSeccion.setBorder(new EmptyBorder(24, 32, 16, 32));
        tituloSeccion.setOpaque(false);

        // Panel con título + contenido
        JPanel panelConTitulo = new JPanel(new BorderLayout());
        panelConTitulo.setOpaque(false);
        panelConTitulo.add(tituloSeccion, BorderLayout.NORTH);
        panelConTitulo.add(contenido, BorderLayout.CENTER);

        // Envolver en contenedor centrado: 30% glue - 40% contenido - 30% glue
        JPanel contenedorCentrado = new JPanel(new GridBagLayout());
        contenedorCentrado.setOpaque(false);
        contenedorCentrado.setBorder(new EmptyBorder(24, 32, 26, 32));

        // Panel glue izquierda ()
        JPanel glueIzq = new JPanel();
        glueIzq.setOpaque(false);
        GridBagConstraints gbcIzq = new GridBagConstraints();
        gbcIzq.weightx = 0.225;
        gbcIzq.weighty = 1;
        gbcIzq.fill = GridBagConstraints.BOTH;
        contenedorCentrado.add(glueIzq, gbcIzq);

        // Contenido ()
        GridBagConstraints gbcContenido = new GridBagConstraints();
        gbcContenido.weightx = 0.55;
        gbcContenido.weighty = 1;
        gbcContenido.fill = GridBagConstraints.BOTH;
        contenedorCentrado.add(panelConTitulo, gbcContenido);

        // Panel glue derecha ()
        JPanel glueDer = new JPanel();
        glueDer.setOpaque(false);
        GridBagConstraints gbcDer = new GridBagConstraints();
        gbcDer.weightx = 0.255;
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

    private JPanel construirFilaCurso(Inscripcion inscripcion) throws SQLException {
        String cursoTitulo = inscripcion.getCursoTitulo();
        Curso curso = controlador.buscarCurso(cursoTitulo);

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

        JLabel tituloLbl = new JLabel(cursoTitulo);
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

        int mejorPuntaje = -1;
        try {
            if (curso != null) mejorPuntaje = controladorTest.obtenerMejorPuntaje(emailUsuario, curso.getId());
        } catch (Exception ignored) {}
        boolean aprobado = mejorPuntaje >= ControladorTest.puntajeAprobacion();

        if (aprobado && curso != null) {
            int puntajeAprobado = mejorPuntaje;
            JButton botonCertificado = FabricaUI.crearBotonSecundarioPequeno("Ver Certificado");
            botonCertificado.addActionListener(e ->
                new VentanaCertificado(curso, resolverNombreUsuario(), puntajeAprobado).setVisible(true));
            botones.add(botonCertificado);
        }

        JButton botonIngresar = FabricaUI.crearBotonPrimarioPequeno("Ingresar", IconoVectorial.Tipo.SIGUIENTE);
        botonIngresar.addActionListener(e -> abrirCurso(curso));

        JButton botonBaja = FabricaUI.crearBotonAccionIcono(IconoVectorial.Tipo.ELIMINAR, EstiloUI.ERROR, "Darse de baja");
        botonBaja.addActionListener(e -> DialogoPersonalizado.mostrarConfirmacion(this,
            "Darse de baja", "¿Confirmar baja del curso \"" + cursoTitulo + "\"?", "Sí, dar de baja",
            () -> {
                try {
                    controlador.darDeBajaCurso(emailUsuario, inscripcion.getCursoId());
                    if (!iniciarTransicionUnica()) return;
                    dispose();
                    new VentanaMisCursos(emailUsuario).setVisible(true);
                } catch (SQLException ex) {
                    mostrarError("Error al procesar la baja: " + ex.getMessage());
                }
            }));

        botones.add(botonIngresar);
        botones.add(botonBaja);

        JLabel iconoLbl = IconoCurso.crearEtiqueta(curso != null ? curso.getEmoji() : null, curso != null ? curso.getEmojiClave() : null, cursoTitulo, 44);
        fila.add(iconoLbl, BorderLayout.WEST);
        fila.add(info, BorderLayout.CENTER);
        fila.add(botones, BorderLayout.EAST);
        return fila;
    }

    private void abrirCurso(Curso curso) {
        if (curso == null) {
            mostrarError("Contenido no disponible.");
            return;
        }
        if (!iniciarTransicionUnica()) return;
        String nombreUsuario = resolverNombreUsuario();
        dispose();
        new VentanaContenidoCurso(curso, emailUsuario, nombreUsuario,
            () -> new VentanaMisCursos(emailUsuario).setVisible(true)).setVisible(true);
    }

    private void agregarTituloSeccion(JPanel panel, String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(EstiloUI.TEXTO_PRIMARIO);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(lbl);
    }

    private void mostrarError(String msg) {
        DialogoPersonalizado.mostrarError(this, msg);
    }

    private String resolverNombreUsuario() {
        try {
            modelo.Usuario usuario = controlador.obtenerDatosUsuario(emailUsuario);
            if (usuario != null) return usuario.getNombre();
        } catch (Exception ignored) {}
        return emailUsuario;
    }

    private JPanel crearPestanas() {
        JPanel pestanas = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pestanas.setOpaque(false);
        pestanas.setBorder(new EmptyBorder(0, 0, 0, 0));

        String[] labels = {"Catálogo de Cursos", "Mis Datos", "Mis Cursos", "Estadísticas"};
        Runnable[] acciones = {
            () -> abrirVentana(new VentanaCursos(emailUsuario)),
            () -> abrirVentana(new VentanaMisDatos(emailUsuario)),
            () -> {},
            () -> abrirVentana(new VentanaMisEstadisticas(emailUsuario))
        };

        for (int i = 0; i < labels.length; i++) {
            JLabel pestaña = crearPestaña(labels[i], i == 2);
            final int index = i;
            pestaña.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (index == 2) return;
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

    private void abrirVentana(VentanaBase ventana) {
        ventana.setVisible(true);
    }
}
