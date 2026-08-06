package vista;

import controlador.ControladorPanelUsuario;
import modelo.Usuario;
import vista.componentes.IconoVectorial;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Pantalla de aterrizaje de "Mi Panel": acceso a los 3 módulos del alumno (Datos, Cursos, Estadísticas). */
public class VentanaPanelUsuario extends VentanaBase {

    private final ControladorPanelUsuario controlador = new ControladorPanelUsuario();
    private final String emailUsuario;

    public VentanaPanelUsuario(String emailUsuario) {
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
        raiz.add(construirEncabezado(), BorderLayout.NORTH);

        JPanel contenidoVacio = new JPanel();
        contenidoVacio.setOpaque(false);
        raiz.add(contenidoVacio, BorderLayout.CENTER);
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

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setOpaque(false);
        panelBotones.add(botonCerrarSesion);

        encabezado.add(bloqueTitulo, BorderLayout.WEST);
        encabezado.add(panelBotones, BorderLayout.EAST);

        // ── Pestañas de navegación ─────────────────────────────────────────
        JPanel pestanas = crearPestanas();
        encabezado.add(pestanas, BorderLayout.SOUTH);

        return encabezado;
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
            () -> abrirVentana(new VentanaMisEstadisticas(emailUsuario))
        };

        for (int i = 0; i < labels.length; i++) {
            JLabel pestaña = crearPestaña(labels[i]);
            final int index = i;
            pestaña.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (!iniciarTransicionUnica()) return;
                    dispose();
                    acciones[index].run();
                }
            });
            pestanas.add(pestaña);
        }

        return pestanas;
    }

    private JLabel crearPestaña(String texto) {
        JLabel pestaña = new JLabel(texto);
        pestaña.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pestaña.setForeground(new Color(80, 100, 130));
        pestaña.setBorder(new EmptyBorder(6, 14, 6, 14));
        pestaña.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pestaña.setOpaque(false);

        pestaña.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                pestaña.setForeground(new Color(37, 99, 235));
            }
            @Override public void mouseExited(MouseEvent e) {
                pestaña.setForeground(new Color(80, 100, 130));
            }
        });

        return pestaña;
    }

    private void abrirVentana(VentanaBase ventana) {
        ventana.setVisible(true);
    }


    private String resolverNombreUsuario() {
        try {
            Usuario usuario = controlador.obtenerDatosUsuario(emailUsuario);
            if (usuario != null) return usuario.getNombre();
        } catch (Exception ignored) {}
        return emailUsuario;
    }
}
