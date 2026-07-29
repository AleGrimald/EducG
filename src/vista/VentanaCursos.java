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
import java.util.List;

/** Ventana principal que muestra el catálogo de cursos disponibles. */
public class VentanaCursos extends VentanaBase {

    private final ControladorCursos controlador = new ControladorCursos();
    private final String emailUsuario;

    public VentanaCursos(String emailUsuario) {
        super("Educ G – Cursos disponibles", EXIT_ON_CLOSE);
        this.emailUsuario = emailUsuario;
        construirUI();
        activarBurbujaChatbot(emailUsuario);
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new BorderLayout());
        setContentPane(raiz);

        String nombreUsuario = resolverNombreUsuario();

        // ── Encabezado ────────────────────────────────────────────────────────
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(new EmptyBorder(24, 32, 16, 32));

        JPanel bloqueTitulo = new JPanel();
        bloqueTitulo.setOpaque(false);
        bloqueTitulo.setLayout(new BoxLayout(bloqueTitulo, BoxLayout.Y_AXIS));

        JLabel appLbl = new JLabel("Educ G");
        appLbl.setFont(EstiloUI.FUENTE_TITULO_COMPACTO);
        appLbl.setForeground(Color.WHITE);

        JLabel bienvenidaLbl = new JLabel("Bienvenido/a, " + nombreUsuario);
        bienvenidaLbl.setFont(EstiloUI.FUENTE_SUBTITULO_COMPACTO);
        bienvenidaLbl.setForeground(new Color(180, 210, 255));

        bloqueTitulo.add(appLbl);
        bloqueTitulo.add(Box.createVerticalStrut(2));
        bloqueTitulo.add(bienvenidaLbl);

        JButton botonPanel = FabricaUI.crearBotonSecundarioPequeno("Mi Perfil", IconoVectorial.Tipo.USUARIO);
        botonPanel.addActionListener(e -> {
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

        JPanel botonesEncabezado = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botonesEncabezado.setOpaque(false);
        botonesEncabezado.add(botonPanel);
        botonesEncabezado.add(botonCerrarSesion);

        encabezado.add(bloqueTitulo, BorderLayout.WEST);
        encabezado.add(botonesEncabezado, BorderLayout.EAST);

        // ── Subtítulo ──────────────────────────────────────────────────────────
        JLabel subtitulo = new JLabel("Explorá nuestros cursos de programación");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitulo.setForeground(new Color(200, 220, 255));
        subtitulo.setBorder(new EmptyBorder(0, 32, 16, 32));
        subtitulo.setOpaque(false);

        JPanel seccionSuperior = new JPanel(new BorderLayout());
        seccionSuperior.setOpaque(false);
        seccionSuperior.add(encabezado, BorderLayout.NORTH);
        seccionSuperior.add(subtitulo, BorderLayout.SOUTH);

        // ── Grid de tarjetas ───────────────────────────────────────────────────
        JPanel grilla = new JPanel(new GridLayout(0, 3, 8, 8));
        grilla.setOpaque(false);
        grilla.setBorder(new EmptyBorder(4, 26, 26, 26));

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

        JScrollPane scroll = new JScrollPane(grilla);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        raiz.add(seccionSuperior, BorderLayout.NORTH);
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
}
