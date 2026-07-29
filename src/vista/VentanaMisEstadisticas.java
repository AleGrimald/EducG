package vista;

import controlador.ControladorPanelUsuario;
import modelo.EstadisticasUsuario;
import modelo.ResultadoTest;
import vista.componentes.IconoVectorial;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/** Módulo "Estadísticas": resumen general del progreso e historial de tests del alumno. */
public class VentanaMisEstadisticas extends VentanaBase {

    private final ControladorPanelUsuario controlador = new ControladorPanelUsuario();
    private final String emailUsuario;

    public VentanaMisEstadisticas(String emailUsuario) {
        super("Educ G – Estadísticas", EXIT_ON_CLOSE);
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
        wrapper.add(construirPanelEstadisticas(), BorderLayout.CENTER);
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

        JLabel subLbl = new JLabel("Mi Panel – Estadísticas");
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
}
