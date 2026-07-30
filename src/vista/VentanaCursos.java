package vista;

import controlador.ControladorCursos;
import modelo.Curso;
import modelo.Usuario;
import vista.componentes.DialogoPersonalizado;
import vista.componentes.IconoVectorial;
import vista.componentes.TarjetaCurso;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/** Ventana principal que muestra el catálogo de cursos disponibles. */
public class VentanaCursos extends VentanaBase {

    private final ControladorCursos controlador = new ControladorCursos();
    private final String emailUsuario;

    public VentanaCursos(String emailUsuario) {
        super("Educ G", EXIT_ON_CLOSE);
        this.emailUsuario = emailUsuario;
        construirUI();
        activarBurbujaChatbot(emailUsuario);
        FabricaUI.establecerIconoVentana(this);
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new BorderLayout());
        setContentPane(raiz);

        String nombreUsuario = resolverNombreUsuario();
        setTitle("Educ G – " + nombreUsuario);

        // ── Encabezado ────────────────────────────────────────────────────────
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

        JPanel botonesEncabezado = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botonesEncabezado.setOpaque(false);
        botonesEncabezado.add(botonCerrarSesion);

        encabezado.add(bloqueTitulo, BorderLayout.WEST);
        encabezado.add(botonesEncabezado, BorderLayout.EAST);

        // ── Pestañas de navegación ─────────────────────────────────────────
        JPanel pestanas = crearPestanas();
        encabezado.add(pestanas, BorderLayout.SOUTH);

        JPanel seccionSuperior = new JPanel(new BorderLayout());
        seccionSuperior.setOpaque(false);
        seccionSuperior.add(encabezado, BorderLayout.NORTH);

        // ── Grid de tarjetas (4 columnas) ──────────────────────────────────────
        JPanel grilla = new JPanel(new GridLayout(0, 4, 16, 16));
        grilla.setOpaque(false);
        grilla.setBorder(new EmptyBorder(4, 0, 26, 0));

        try {
            List<Curso> cursos = controlador.obtenerCatalogo();
            for (Curso curso : cursos) {
                boolean yaInscripto = false;
                try {
                    yaInscripto = controlador.estaInscripto(emailUsuario, curso.getId());
                } catch (Exception ignored) {}

                TarjetaCurso tarjeta = new TarjetaCurso(curso, yaInscripto);
                tarjeta.alHacerClicInscribir(() -> manejarInscripcion(curso, tarjeta));
                tarjeta.alHacerClicIniciarCurso(() -> abrirContenidoCurso(curso, nombreUsuario));
                grilla.add(tarjeta);
            }
        } catch (Exception ex) {
            JLabel errLbl = new JLabel("No se pudo cargar el catálogo de cursos: " + ex.getMessage());
            errLbl.setForeground(Color.WHITE);
            grilla.add(errLbl);
        }

        // Título de la sección de cursos
        JLabel tituloCursos = new JLabel("Nuestros Cursos Disponibles");
        tituloCursos.setFont(new Font("Segoe UI", Font.BOLD, 24));
        tituloCursos.setForeground(Color.WHITE);
        tituloCursos.setBorder(new EmptyBorder(24, 32, 16, 32));
        tituloCursos.setOpaque(false);

        // Panel que contiene el título y la grilla
        JPanel panelConTitulo = new JPanel(new BorderLayout());
        panelConTitulo.setOpaque(false);
        panelConTitulo.add(tituloCursos, BorderLayout.NORTH);
        panelConTitulo.add(grilla, BorderLayout.CENTER);

        // Envolver en contenedor centrado: 4 tarjetas (280px) + 3 gaps (16px) + padding = ~1220px
        JPanel contenedorCentrado = new JPanel(new GridBagLayout());
        contenedorCentrado.setOpaque(false);
        contenedorCentrado.setBorder(new EmptyBorder(24, 32, 26, 32));
        panelConTitulo.setMaximumSize(new Dimension(1220, Integer.MAX_VALUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        contenedorCentrado.add(panelConTitulo, gbc);

        JScrollPane scroll = new JScrollPane(contenedorCentrado);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        // Panel que contiene la sección superior y el separador
        JPanel panelEncabezadoConSeparador = new JPanel(new BorderLayout());
        panelEncabezadoConSeparador.setOpaque(false);
        panelEncabezadoConSeparador.add(seccionSuperior, BorderLayout.NORTH);

        JSeparator separador = new JSeparator();
        separador.setForeground(new Color(100, 130, 180));
        panelEncabezadoConSeparador.add(separador, BorderLayout.SOUTH);

        raiz.add(panelEncabezadoConSeparador, BorderLayout.NORTH);
        raiz.add(scroll, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> scroll.getViewport().setViewPosition(new Point(0, 0)));
    }

    private void manejarInscripcion(Curso curso, TarjetaCurso tarjeta) {
        try {
            boolean inscripto = controlador.inscribirCurso(emailUsuario, curso.getId());
            tarjeta.marcarInscripto();
            if (inscripto) {
                DialogoPersonalizado.mostrarExito(this,
                    "¡Te inscribiste en \"" + curso.getTitulo() + "\"!\n"
                    + "Podés acceder al curso desde \"Mi Panel\".");
            } else {
                DialogoPersonalizado.mostrarInfo(this,
                    "Ya estás inscripto en \"" + curso.getTitulo() + "\".\n"
                    + "Accedé al curso desde \"Mi Panel\".");
            }
        } catch (Exception ex) {
            DialogoPersonalizado.mostrarError(this,
                "No se pudo procesar la inscripción:\n" + ex.getMessage());
        }
    }

    private void abrirContenidoCurso(Curso curso, String nombreUsuario) {
        if (!iniciarTransicionUnica()) return;
        dispose();
        new VentanaContenidoCurso(curso, emailUsuario, nombreUsuario,
            () -> new VentanaCursos(emailUsuario).setVisible(true)).setVisible(true);
    }

    private String resolverNombreUsuario() {
        try {
            Usuario usuario = controlador.obtenerDatosUsuario(emailUsuario);
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
            () -> {},
            () -> abrirVentana(new VentanaMisDatos(emailUsuario)),
            () -> abrirVentana(new VentanaMisCursos(emailUsuario)),
            () -> abrirVentana(new VentanaMisEstadisticas(emailUsuario))
        };

        for (int i = 0; i < labels.length; i++) {
            JLabel pestaña = crearPestaña(labels[i], i == 0);
            final int index = i;
            pestaña.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (index == 0) return;
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
