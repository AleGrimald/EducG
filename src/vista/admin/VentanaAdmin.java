package vista.admin;

import vista.VentanaBase;
import vista.VentanaLogin;
import vista.componentes.IconoVectorial;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/** Pantalla de aterrizaje del administrador: acceso a los 3 módulos (Alumnos, Cursos, Estadísticas). */
public class VentanaAdmin extends VentanaBase {

    private final String emailAdmin;

    public VentanaAdmin(String emailAdmin) {
        super("Educ G", EXIT_ON_CLOSE);
        this.emailAdmin = emailAdmin;
        construirUI();
        FabricaUI.establecerIconoVentana(this);
        setTitle("Educ G – " + emailAdmin);
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new BorderLayout());
        setContentPane(raiz);

        raiz.add(construirEncabezado(), BorderLayout.NORTH);

        JPanel grilla = new JPanel(new GridLayout(1, 3, 24, 24));
        grilla.setOpaque(false);
        grilla.setBorder(new EmptyBorder(40, 60, 60, 60));

        grilla.add(construirTarjetaModulo("👨‍🎓", "Alumnos", "Alta, búsqueda por DNI, modificación y bajas.",
            () -> abrir(new VentanaAdminAlumnos(emailAdmin))));
        grilla.add(construirTarjetaModulo("📚", "Cursos", "Crear cursos con su plan de estudio completo.",
            () -> abrir(new VentanaAdminCursos(emailAdmin))));
        grilla.add(construirTarjetaModulo("📊", "Estadísticas", "Métricas generales y por curso.",
            () -> abrir(new VentanaAdminEstadisticas(emailAdmin))));

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(grilla);
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
}
