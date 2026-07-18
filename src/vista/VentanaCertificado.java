package vista;

import modelo.Curso;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Certificado de finalización de un curso aprobado. */
public class VentanaCertificado extends VentanaBase {

    private final Curso curso;
    private final String nombreUsuario;
    private final int puntaje;

    public VentanaCertificado(Curso curso, String nombreUsuario, int puntaje) {
        super("Educ G – Certificado", DISPOSE_ON_CLOSE);
        this.curso = curso;
        this.nombreUsuario = nombreUsuario;
        this.puntaje = puntaje;
        construirUI();
    }

    private void construirUI() {
        JPanel raiz = FabricaUI.crearFondoEstandar();
        raiz.setLayout(new GridBagLayout());
        setContentPane(raiz);
        raiz.add(construirCertificado());
    }

    private JPanel construirCertificado() {
        JPanel certificado = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fill(new RoundRectangle2D.Float(6, 10, getWidth() - 8, getHeight() - 10, 14, 14));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 6, getHeight() - 8, 14, 14));
                g2.setColor(EstiloUI.AZUL_CLARO);
                g2.setStroke(new BasicStroke(4f));
                g2.draw(new RoundRectangle2D.Float(14, 14, getWidth() - 34, getHeight() - 32, 8, 8));
                g2.dispose();
            }
        };
        certificado.setOpaque(false);
        certificado.setPreferredSize(new Dimension(760, 520));
        certificado.setBorder(new EmptyBorder(50, 60, 40, 60));

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        JLabel marcaLbl = new JLabel("🎓 EDUC G");
        marcaLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        marcaLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        marcaLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel tituloLbl = new JLabel("Certificado de Finalización");
        tituloLbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        tituloLbl.setForeground(EstiloUI.TEXTO_PRIMARIO);
        tituloLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel otorgadoLbl = new JLabel("Se certifica que");
        otorgadoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        otorgadoLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        otorgadoLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel nombreLbl = new JLabel(nombreUsuario);
        nombreLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        nombreLbl.setForeground(EstiloUI.AZUL_CLARO);
        nombreLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel completoLbl = new JLabel("completó satisfactoriamente el curso");
        completoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        completoLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        completoLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel cursoLbl = new JLabel(curso.getEmoji() + " " + curso.getTitulo());
        cursoLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        cursoLbl.setForeground(EstiloUI.TEXTO_PRIMARIO);
        cursoLbl.setAlignmentX(CENTER_ALIGNMENT);

        String fecha = LocalDate.now().format(
            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "ES")));
        JLabel fechaLbl = new JLabel("Emitido el " + fecha + "  ·  Puntaje obtenido: " + puntaje + " / 100");
        fechaLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fechaLbl.setForeground(EstiloUI.TEXTO_SECUNDARIO);
        fechaLbl.setAlignmentX(CENTER_ALIGNMENT);

        centro.add(marcaLbl);
        centro.add(Box.createVerticalStrut(18));
        centro.add(tituloLbl);
        centro.add(Box.createVerticalStrut(26));
        centro.add(otorgadoLbl);
        centro.add(Box.createVerticalStrut(8));
        centro.add(nombreLbl);
        centro.add(Box.createVerticalStrut(8));
        centro.add(completoLbl);
        centro.add(Box.createVerticalStrut(6));
        centro.add(cursoLbl);
        centro.add(Box.createVerticalStrut(30));
        centro.add(fechaLbl);

        JButton botonCerrar = FabricaUI.crearBotonSecundario("Cerrar");
        botonCerrar.addActionListener(e -> dispose());

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setOpaque(false);
        panelBoton.add(botonCerrar);

        certificado.add(centro, BorderLayout.CENTER);
        certificado.add(panelBoton, BorderLayout.SOUTH);
        return certificado;
    }
}
