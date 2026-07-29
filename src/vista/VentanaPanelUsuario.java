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
        super("Educ G – Mi Panel", EXIT_ON_CLOSE);
        this.emailUsuario = emailUsuario;
        construirUI();
        activarBurbujaChatbot(emailUsuario);
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new BorderLayout());
        setContentPane(raiz);

        raiz.add(construirEncabezado(), BorderLayout.NORTH);

        JPanel grilla = new JPanel(new GridLayout(1, 3, 24, 24));
        grilla.setOpaque(false);
        grilla.setBorder(new EmptyBorder(40, 60, 60, 60));

        grilla.add(construirTarjetaModulo("👤", "Mis Datos", "Datos personales y cambio de contraseña.",
            () -> abrir(new VentanaMisDatos(emailUsuario))));
        grilla.add(construirTarjetaModulo("📚", "Mis Cursos", "Los cursos en los que estás inscripto.",
            () -> abrir(new VentanaMisCursos(emailUsuario))));
        grilla.add(construirTarjetaModulo("📊", "Estadísticas", "Tu progreso e historial de tests.",
            () -> abrir(new VentanaMisEstadisticas(emailUsuario))));

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(grilla);
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

        JLabel paginaLbl = new JLabel("Mi Panel – " + resolverNombreUsuario());
        paginaLbl.setFont(EstiloUI.FUENTE_SUBTITULO_COMPACTO);
        paginaLbl.setForeground(new Color(180, 210, 255));

        bloqueTitulo.add(appLbl);
        bloqueTitulo.add(Box.createVerticalStrut(2));
        bloqueTitulo.add(paginaLbl);

        JButton botonCatalogo = FabricaUI.crearBotonSecundarioPequeno("Ver Catálogo", IconoVectorial.Tipo.LISTA);
        botonCatalogo.addActionListener(e -> {
            if (!iniciarTransicionUnica()) return;
            dispose();
            new VentanaCursos(emailUsuario).setVisible(true);
        });

        JButton botonCerrarSesion = FabricaUI.crearBotonSecundarioPequeno("Cerrar Sesión", IconoVectorial.Tipo.SALIR);
        botonCerrarSesion.addActionListener(e -> {
            if (!iniciarTransicionUnica()) return;
            dispose();
            new VentanaLogin().setVisible(true);
        });

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setOpaque(false);
        panelBotones.add(botonCatalogo);
        panelBotones.add(botonCerrarSesion);

        encabezado.add(bloqueTitulo, BorderLayout.WEST);
        encabezado.add(panelBotones, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel construirTarjetaModulo(String icono, String titulo, String descripcion, Runnable alHacerClic) {
        JPanel tarjeta = FabricaUI.crearTarjeta();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(36, 28, 36, 28));
        tarjeta.setPreferredSize(new Dimension(260, 260));
        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconoLbl = new JLabel(icono);
        iconoLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconoLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel tituloLbl = new JLabel(titulo);
        tituloLbl.setFont(EstiloUI.FUENTE_SECCION);
        tituloLbl.setForeground(EstiloUI.TEXTO_PRIMARIO);
        tituloLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel descLbl = new JLabel("<html><div style='text-align:center;width:180px'>" + descripcion + "</div></html>");
        descLbl.setFont(EstiloUI.FUENTE_PEQUENA);
        descLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        descLbl.setAlignmentX(CENTER_ALIGNMENT);
        descLbl.setHorizontalAlignment(SwingConstants.CENTER);

        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(iconoLbl);
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(tituloLbl);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(descLbl);
        tarjeta.add(Box.createVerticalGlue());

        tarjeta.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { alHacerClic.run(); }
        });

        return tarjeta;
    }

    private void abrir(VentanaBase ventana) {
        if (!iniciarTransicionUnica()) return;
        dispose();
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
