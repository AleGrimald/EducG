package vista;

import controlador.ControladorPanelUsuario;
import modelo.EstadisticasUsuario;
import modelo.ResultadoTest;
import modelo.Usuario;
import vista.componentes.IconoVectorial;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/** Módulo "Estadísticas": resumen general del progreso e historial de tests del alumno. */
public class VentanaMisEstadisticas extends VentanaBase {

    private final ControladorPanelUsuario controlador = new ControladorPanelUsuario();
    private final String emailUsuario;

    public VentanaMisEstadisticas(String emailUsuario) {
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
        wrapper.add(construirPanelEstadisticas(), BorderLayout.CENTER);
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

    private JScrollPane construirPanelEstadisticas() {
        // Título de la sección
        JLabel tituloSeccion = new JLabel("Estadísticas");
        tituloSeccion.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tituloSeccion.setForeground(Color.WHITE);
        tituloSeccion.setBorder(new EmptyBorder(24, 32, 16, 32));
        tituloSeccion.setOpaque(false);

        // Panel principal de contenido
        JPanel contenido = new JPanel();
        contenido.setBackground(EstiloUI.FONDO_SUAVE);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(24, 40, 24, 40));

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
            String[] columnas = {"Curso", "Puntaje", "Fecha"};
            Object[][] datos = resultadosTests.stream().map(r -> new Object[]{
                r.getCursoTitulo(), r.getPuntaje() + " / 100",
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
            int anchoPorDefecto = scrollTabla.getPreferredSize().width;
            int altoHeader = tabla.getTableHeader().getPreferredSize().height;
            int altoMaximo = 240;
            int altoTabla = Math.min(altoHeader + tabla.getRowHeight() * resultadosTests.size(), altoMaximo);
            scrollTabla.setPreferredSize(new Dimension(anchoPorDefecto, altoTabla));
            scrollTabla.setMinimumSize(new Dimension(0, altoTabla));
            scrollTabla.setMaximumSize(new Dimension(Integer.MAX_VALUE, altoTabla));
            scrollTabla.setAlignmentX(LEFT_ALIGNMENT);
            scrollTabla.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE, 1));
            contenido.add(scrollTabla);
        }

        contenido.add(Box.createVerticalGlue());

        // Panel con título + contenido
        JPanel panelConTitulo = new JPanel(new BorderLayout());
        panelConTitulo.setOpaque(false);
        panelConTitulo.add(tituloSeccion, BorderLayout.NORTH);
        panelConTitulo.add(contenido, BorderLayout.CENTER);

        // Envolver en contenedor centrado: 22.5% glue - 55% contenido - 22.5% glue
        JPanel contenedorCentrado = new JPanel(new GridBagLayout());
        contenedorCentrado.setOpaque(false);
        contenedorCentrado.setBorder(new EmptyBorder(24, 32, 26, 32));

        // Panel glue izquierda
        JPanel glueIzq = new JPanel();
        glueIzq.setOpaque(false);
        GridBagConstraints gbcIzq = new GridBagConstraints();
        gbcIzq.weightx = 0.225;
        gbcIzq.weighty = 1;
        gbcIzq.fill = GridBagConstraints.BOTH;
        contenedorCentrado.add(glueIzq, gbcIzq);

        // Contenido
        GridBagConstraints gbcContenido = new GridBagConstraints();
        gbcContenido.weightx = 0.55;
        gbcContenido.weighty = 1;
        gbcContenido.fill = GridBagConstraints.BOTH;
        contenedorCentrado.add(panelConTitulo, gbcContenido);

        // Panel glue derecha
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

    private JPanel tarjetaEstadistica(String icono, String etiqueta, String valor) {
        JPanel tarjeta = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fill(new RoundRectangle2D.Float(3, 5, getWidth() - 4, getHeight() - 4, 14, 14));
                g2.setColor(EstiloUI.FONDO_SUAVE);
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

    private JPanel crearPestanas() {
        JPanel pestanas = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pestanas.setOpaque(false);
        pestanas.setBorder(new EmptyBorder(0, 0, 0, 0));

        String[] labels = {"Catálogo de Cursos", "Mis Datos", "Mis Cursos", "Estadísticas"};
        Runnable[] acciones = {
            () -> abrirVentana(new VentanaCursos(emailUsuario)),
            () -> abrirVentana(new VentanaMisDatos(emailUsuario)),
            () -> abrirVentana(new VentanaMisCursos(emailUsuario)),
            () -> {}
        };

        for (int i = 0; i < labels.length; i++) {
            JLabel pestaña = crearPestaña(labels[i], i == 3);
            final int index = i;
            pestaña.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (index == 3) return;
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
