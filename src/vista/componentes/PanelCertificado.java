package vista.componentes;

import modelo.Curso;
import vista.estilo.EstiloUI;
import vista.estilo.FabricaUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * El certificado de finalización en sí — extraído de {@link vista.VentanaCertificado} para
 * poder reusarlo tal cual también al renderizarlo como imagen (ver {@link #renderizarComoPng()}),
 * que es lo que se manda adjunto por email al aprobar un curso por primera vez.
 */
public class PanelCertificado extends JPanel {

    private static final int ANCHO = 780;
    private static final int ALTO = 620;

    public PanelCertificado(Curso curso, String nombreUsuario, int puntaje) {
        setOpaque(false);
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setBorder(new EmptyBorder(40, 50, 40, 50));
        setLayout(new BorderLayout());
        construirContenido(curso, nombreUsuario, puntaje);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fill(new RoundRectangle2D.Float(6, 10, getWidth() - 8, getHeight() - 10, 14, 14));
        g2.setColor(EstiloUI.FONDO_SUAVE);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 6, getHeight() - 8, 14, 14));
        g2.setColor(EstiloUI.AZUL_CLARO);
        g2.setStroke(new BasicStroke(4f));
        g2.draw(new RoundRectangle2D.Float(14, 14, getWidth() - 34, getHeight() - 32, 8, 8));
        g2.dispose();
    }

    private void construirContenido(Curso curso, String nombreUsuario, int puntaje) {
        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        JLabel marcaLbl = FabricaUI.crearLogoEducG(160);
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

        JPanel filaCurso = new JPanel();
        filaCurso.setOpaque(false);
        filaCurso.setLayout(new BoxLayout(filaCurso, BoxLayout.X_AXIS));
        filaCurso.setAlignmentX(CENTER_ALIGNMENT);

        JLabel iconoCursoLbl = IconoCurso.crearEtiqueta(curso, 28);

        JLabel cursoLbl = new JLabel(curso.getTitulo());
        cursoLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        cursoLbl.setForeground(EstiloUI.TEXTO_PRIMARIO);

        filaCurso.add(iconoCursoLbl);
        filaCurso.add(Box.createHorizontalStrut(8));
        filaCurso.add(cursoLbl);

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
        centro.add(filaCurso);
        centro.add(Box.createVerticalStrut(30));
        centro.add(fechaLbl);

        add(centro, BorderLayout.CENTER);
    }

    /**
     * Rasteriza este panel a PNG sin necesidad de mostrarlo en pantalla — para el adjunto del
     * email. {@code paint()} sobre un panel nunca agregado a una ventana funciona igual siempre
     * que tenga tamaño asignado (acá, el {@code setPreferredSize} del constructor + doLayout).
     */
    public byte[] renderizarComoPng() throws IOException {
        setSize(getPreferredSize());
        // NO alcanza con doLayout() (solo posiciona los hijos directos) ni con validate()
        // (Container.validate() es un no-op si el componente no tiene un peer nativo — y este
        // panel nunca se agrega a una ventana real, así que nunca lo tiene). Hay que bajar la
        // jerarquía a mano y hacer doLayout() en cada nivel — doLayout() en sí no depende de
        // ningún peer, solo de que el nodo ya tenga su propio tamaño asignado por el nivel de
        // arriba. Sin esto, el PNG sale con el borde pero todo el contenido en blanco (0x0).
        layoutRecursivo(this);

        BufferedImage imagen = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagen.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Fondo blanco sólido: sin esto el PNG queda con transparencia donde este panel es
        // opaque=false, y un visor de imágenes sin soporte de alpha lo mostraría en negro.
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());
        print(g2);
        g2.dispose();

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        ImageIO.write(imagen, "png", salida);
        return salida.toByteArray();
    }

    /** doLayout() recursivo: cada Container necesita el suyo, ya que doLayout() solo posiciona
     * a sus hijos directos (no baja más), y a diferencia de validate() no depende de un peer. */
    private static void layoutRecursivo(Component componente) {
        if (!(componente instanceof Container)) return;
        Container contenedor = (Container) componente;
        contenedor.doLayout();
        for (Component hijo : contenedor.getComponents()) {
            layoutRecursivo(hijo);
        }
    }
}
