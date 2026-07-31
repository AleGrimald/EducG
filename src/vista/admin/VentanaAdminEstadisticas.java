package vista.admin;

import controlador.ControladorAdminEstadisticas;
import modelo.EstadisticasCurso;
import modelo.EstadisticasGenerales;
import vista.VentanaBase;
import vista.VentanaLogin;
import vista.componentes.BarraProgreso;
import vista.componentes.DialogoPersonalizado;
import vista.componentes.IconoVectorial;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Módulo Estadísticas del panel de administrador: KPIs generales, detalle por curso e inscriptos por curso. */
public class VentanaAdminEstadisticas extends VentanaBase {

    private final ControladorAdminEstadisticas controlador = new ControladorAdminEstadisticas();
    private final String emailAdmin;

    public VentanaAdminEstadisticas(String emailAdmin) {
        super("Educ G", EXIT_ON_CLOSE);
        this.emailAdmin = emailAdmin;
        construirUI();
        FabricaUI.establecerIconoVentana(this);
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new BorderLayout());
        setContentPane(raiz);

        setTitle("Educ G – " + emailAdmin);

        raiz.add(construirEncabezado(), BorderLayout.NORTH);
        raiz.add(construirContenido(), BorderLayout.CENTER);
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

    private JPanel crearPestanas() {
        JPanel pestanas = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pestanas.setOpaque(false);
        pestanas.setBorder(new EmptyBorder(0, 0, 0, 0));

        String[] labels = {"Alumnos", "Cursos", "Estadísticas"};
        Runnable[] acciones = {
            () -> abrirVentana(new VentanaAdminAlumnos(emailAdmin)),
            () -> abrirVentana(new VentanaAdminCursos(emailAdmin)),
            () -> {}
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

    private JScrollPane construirContenido() {
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(4, 40, 30, 40));

        JLabel tituloSeccion = new JLabel("Estadísticas");
        tituloSeccion.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tituloSeccion.setForeground(Color.WHITE);
        tituloSeccion.setAlignmentX(LEFT_ALIGNMENT);
        tituloSeccion.setBorder(new EmptyBorder(16, 0, 16, 0));
        contenido.add(tituloSeccion);

        try {
            EstadisticasGenerales generales = controlador.obtenerGenerales();
            List<EstadisticasCurso> porCurso = controlador.obtenerPorCurso();
            Map<String, Integer> registrosMensuales = controlador.obtenerRegistrosMensuales();

            contenido.add(construirTarjetasKpi(generales));
            contenido.add(Box.createVerticalStrut(24));
            contenido.add(construirTarjetaSeccion("Inscriptos por curso", construirBarrasInscriptos(porCurso)));
            contenido.add(Box.createVerticalStrut(20));
            contenido.add(construirTarjetaSeccion("Detalle por curso", construirTablaCursos(porCurso)));
            contenido.add(Box.createVerticalStrut(20));
            contenido.add(construirTarjetaSeccion("Altas de alumnos por mes (últimos 6 meses)", construirBarrasRegistros(registrosMensuales)));
        } catch (SQLException ex) {
            JLabel errLbl = new JLabel("No se pudieron cargar las estadísticas: " + ex.getMessage());
            errLbl.setForeground(Color.WHITE);
            errLbl.setAlignmentX(LEFT_ALIGNMENT);
            contenido.add(errLbl);
        }

        contenido.add(Box.createVerticalGlue());
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel construirTarjetasKpi(EstadisticasGenerales g) {
        JPanel fila = new JPanel(new GridLayout(1, 5, 14, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        fila.add(tarjetaKpi("🎓", "Alumnos Activos", String.valueOf(g.getAlumnosActivos())));
        fila.add(tarjetaKpi("💤", "Alumnos Inactivos", String.valueOf(g.getAlumnosInactivos())));
        fila.add(tarjetaKpi("📚", "Cursos Activos", String.valueOf(g.getCursosActivos())));
        fila.add(tarjetaKpi("📝", "Inscripciones Activas", String.valueOf(g.getInscripcionesActivas())));
        fila.add(tarjetaKpi("🏆", "Aprobaciones Totales", String.valueOf(g.getAprobadosTotales())));
        return fila;
    }

    private JPanel tarjetaKpi(String icono, String etiqueta, String valor) {
        JPanel tarjeta = FabricaUI.crearTarjeta();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(16, 12, 16, 12));

        JLabel iconoLbl = new JLabel(icono);
        iconoLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        iconoLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel valorLbl = new JLabel(valor);
        valorLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valorLbl.setForeground(EstiloUI.AZUL_CLARO);
        valorLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel etiquetaLbl = new JLabel("<html><div style='text-align:center'>" + etiqueta + "</div></html>");
        etiquetaLbl.setFont(EstiloUI.FUENTE_PEQUENA);
        etiquetaLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        etiquetaLbl.setAlignmentX(CENTER_ALIGNMENT);
        etiquetaLbl.setHorizontalAlignment(SwingConstants.CENTER);

        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(iconoLbl);
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(valorLbl);
        tarjeta.add(Box.createVerticalStrut(4));
        tarjeta.add(etiquetaLbl);
        tarjeta.add(Box.createVerticalGlue());
        return tarjeta;
    }

    private JPanel construirTarjetaSeccion(String titulo, JComponent contenido) {
        JPanel tarjeta = FabricaUI.crearTarjeta();
        tarjeta.setLayout(new BorderLayout());
        tarjeta.setBorder(new EmptyBorder(20, 22, 20, 22));
        tarjeta.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel tituloLbl = new JLabel(titulo);
        tituloLbl.setFont(EstiloUI.FUENTE_SECCION);
        tituloLbl.setForeground(EstiloUI.TEXTO_PRIMARIO);
        tituloLbl.setBorder(new EmptyBorder(0, 0, 12, 0));

        tarjeta.add(tituloLbl, BorderLayout.NORTH);
        tarjeta.add(contenido, BorderLayout.CENTER);
        return tarjeta;
    }

    private JComponent construirBarrasInscriptos(List<EstadisticasCurso> porCurso) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        if (porCurso.isEmpty()) {
            panel.add(etiquetaVacio("Todavía no hay cursos activos con datos para mostrar."));
            return panel;
        }

        int maxInscriptos = porCurso.stream().mapToInt(EstadisticasCurso::getInscriptos).max().orElse(1);
        for (EstadisticasCurso c : porCurso) {
            JPanel fila = new JPanel(new BorderLayout(12, 0));
            fila.setOpaque(false);
            fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            fila.setBorder(new EmptyBorder(4, 0, 4, 0));

            JLabel etiqueta = new JLabel(c.getTitulo());
            etiqueta.setFont(EstiloUI.FUENTE_PEQUENA);
            etiqueta.setForeground(EstiloUI.TEXTO_PRIMARIO);
            etiqueta.setPreferredSize(new Dimension(220, 20));

            BarraProgreso barra = new BarraProgreso();
            barra.setProgreso(c.getInscriptos(), Math.max(maxInscriptos, 1));

            JLabel valorLbl = new JLabel(String.valueOf(c.getInscriptos()));
            valorLbl.setFont(EstiloUI.FUENTE_PEQUENA);
            valorLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
            valorLbl.setPreferredSize(new Dimension(28, 20));
            valorLbl.setHorizontalAlignment(SwingConstants.RIGHT);

            fila.add(etiqueta, BorderLayout.WEST);
            fila.add(envolverConFondoClaro(barra), BorderLayout.CENTER);
            fila.add(valorLbl, BorderLayout.EAST);
            panel.add(fila);
        }
        return panel;
    }

    private JPanel envolverConFondoClaro(BarraProgreso barra) {
        JPanel fondo = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(EstiloUI.FONDO_GRIS_CLARO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.dispose();
            }
        };
        fondo.setOpaque(false);
        fondo.setBorder(new EmptyBorder(4, 4, 4, 4));
        fondo.add(barra, BorderLayout.CENTER);
        return fondo;
    }

    private JComponent construirTablaCursos(List<EstadisticasCurso> porCurso) {
        String[] columnas = {"Curso", "Inscriptos", "Promedio", "% Aprobación"};
        Object[][] datos = new Object[porCurso.size()][columnas.length];
        for (int i = 0; i < porCurso.size(); i++) {
            EstadisticasCurso c = porCurso.get(i);
            datos[i] = new Object[]{
                c.getTitulo(), c.getInscriptos(),
                String.format("%.1f", c.getPromedio()),
                String.format("%.0f%%", c.getTasaAprobacion())
            };
        }

        JTable tabla = new JTable(datos, columnas) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
        tabla.setFont(EstiloUI.FUENTE_ETIQUETA);
        tabla.setRowHeight(30);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.setSelectionBackground(new Color(220, 235, 255));
        centrar(tabla, 1); // Inscriptos
        centrar(tabla, 2); // Promedio
        centrar(tabla, 3); // % Aprobación

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(0, Math.min(260, 60 + porCurso.size() * 30)));
        scroll.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE, 1));
        return scroll;
    }

    private JComponent construirBarrasRegistros(Map<String, Integer> registrosMensuales) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        if (registrosMensuales.isEmpty()) {
            panel.add(etiquetaVacio("No hubo altas de alumnos en los últimos 6 meses."));
            return panel;
        }

        int maxCantidad = registrosMensuales.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        for (Map.Entry<String, Integer> entrada : registrosMensuales.entrySet()) {
            JPanel fila = new JPanel(new BorderLayout(12, 0));
            fila.setOpaque(false);
            fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            fila.setBorder(new EmptyBorder(4, 0, 4, 0));

            JLabel etiqueta = new JLabel(entrada.getKey());
            etiqueta.setFont(EstiloUI.FUENTE_PEQUENA);
            etiqueta.setForeground(EstiloUI.TEXTO_PRIMARIO);
            etiqueta.setPreferredSize(new Dimension(80, 20));

            BarraProgreso barra = new BarraProgreso();
            barra.setProgreso(entrada.getValue(), Math.max(maxCantidad, 1));

            JLabel valorLbl = new JLabel(String.valueOf(entrada.getValue()));
            valorLbl.setFont(EstiloUI.FUENTE_PEQUENA);
            valorLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
            valorLbl.setPreferredSize(new Dimension(28, 20));
            valorLbl.setHorizontalAlignment(SwingConstants.RIGHT);

            fila.add(etiqueta, BorderLayout.WEST);
            fila.add(envolverConFondoClaro(barra), BorderLayout.CENTER);
            fila.add(valorLbl, BorderLayout.EAST);
            panel.add(fila);
        }
        return panel;
    }

    private void centrar(JTable tabla, int indice) {
        DefaultTableCellRenderer renderizador = new DefaultTableCellRenderer();
        renderizador.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(indice).setCellRenderer(renderizador);
    }

    private JLabel etiquetaVacio(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(EstiloUI.FUENTE_PEQUENA);
        lbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        return lbl;
    }
}
