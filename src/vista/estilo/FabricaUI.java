package vista.estilo;

import dao.ImagenDAOJdbc;
import modelo.Imagen;
import vista.componentes.BotonAccionIcono;
import vista.componentes.BotonRedondeado;
import vista.componentes.CampoConPlaceholder;
import vista.componentes.IconoVectorial;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/** Fábrica de componentes Swing con el estilo visual de Educ G, basada en {@link EstiloUI}. */
public final class FabricaUI {

    private FabricaUI() {}

    /** Logo/ícono de ventana viven en la tabla {@code imagenes}; se cachean acá para no consultar
     * la base en cada ventana construida (ambos son estáticos durante toda la sesión). */
    private static final Map<String, byte[]> CACHE_IMAGENES = new HashMap<>();

    // ── Paneles ───────────────────────────────────────────────────────────────

    /** Panel con fondo estándar morado, listo para ser contentPane del JFrame. */
    public static JPanel crearFondoEstandar() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(EstiloUI.MORADO_ACENTO);
        return panel;
    }

    /** Tarjeta blanca con esquinas redondeadas y sombra sutil. */
    public static JPanel crearTarjeta() {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(EstiloUI.SOMBRA);
                g2.fill(new RoundRectangle2D.Float(4, 6, getWidth() - 5, getHeight() - 6, 18, 18));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 5, getHeight() - 7, 18, 18));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }

    // ── Campos de texto ───────────────────────────────────────────────────────

    public static JTextField crearCampo() {
        JTextField f = new JTextField();
        estilizarCampo(f);
        return f;
    }

    public static JPasswordField crearCampoPassword() {
        JPasswordField f = new JPasswordField();
        estilizarCampo(f);
        return f;
    }

    /** Campo de texto con un texto de ejemplo tenue mientras está vacío (ej. campos de búsqueda). */
    public static JTextField crearCampoConPlaceholder(String placeholder) {
        JTextField f = new CampoConPlaceholder(placeholder);
        estilizarCampo(f);
        return f;
    }

    private static void estilizarCampo(JTextField f) {
        f.setFont(EstiloUI.FUENTE_CUERPO);
        f.setBackground(EstiloUI.FONDO_CAMPO);
        f.setForeground(EstiloUI.TEXTO_PRIMARIO);
        f.setCaretColor(EstiloUI.AZUL_CLARO);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EstiloUI.BORDE, 1, true),
            new EmptyBorder(EstiloUI.RELLENO_CAMPO_V, EstiloUI.RELLENO_CAMPO_H,
                             EstiloUI.RELLENO_CAMPO_V, EstiloUI.RELLENO_CAMPO_H)
        ));
        f.setPreferredSize(new Dimension(0, EstiloUI.ALTO_CAMPO));
    }

    // ── Etiquetas ─────────────────────────────────────────────────────────────

    public static JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(EstiloUI.FUENTE_ETIQUETA);
        lbl.setForeground(EstiloUI.TEXTO_PRIMARIO);
        return lbl;
    }

    // ── Botones ───────────────────────────────────────────────────────────────

    public static JButton crearBotonPrimario(String texto) {
        return new BotonRedondeado(texto, EstiloUI.AZUL_CLARO);
    }

    public static JButton crearBotonPrimario(String texto, IconoVectorial.Tipo icono) {
        return new BotonRedondeado(texto, EstiloUI.AZUL_CLARO, BotonRedondeado.Estilo.RELLENO, BotonRedondeado.Tamano.NORMAL, icono);
    }

    /** Variante con el ícono después del texto (en vez de antes, como el resto de los botones con ícono). */
    public static JButton crearBotonPrimarioIconoAlFinal(String texto, IconoVectorial.Tipo icono) {
        return new BotonRedondeado(texto, EstiloUI.AZUL_CLARO, BotonRedondeado.Estilo.RELLENO, BotonRedondeado.Tamano.NORMAL, icono, true);
    }

    public static JButton crearBotonSecundario(String texto) {
        return new BotonRedondeado(texto, EstiloUI.AZUL_CLARO, BotonRedondeado.Estilo.CONTORNO);
    }

    public static JButton crearBotonSecundario(String texto, IconoVectorial.Tipo icono) {
        return new BotonRedondeado(texto, EstiloUI.AZUL_CLARO, BotonRedondeado.Estilo.CONTORNO, BotonRedondeado.Tamano.NORMAL, icono);
    }

    /** Botón relleno compacto (encabezados, filas de lista) — altura EstiloUI.ALTO_BOTON_PEQUENO. */
    public static JButton crearBotonPrimarioPequeno(String texto) {
        return new BotonRedondeado(texto, EstiloUI.AZUL_CLARO, BotonRedondeado.Estilo.RELLENO, BotonRedondeado.Tamano.PEQUENO);
    }

    public static JButton crearBotonPrimarioPequeno(String texto, IconoVectorial.Tipo icono) {
        return new BotonRedondeado(texto, EstiloUI.AZUL_CLARO, BotonRedondeado.Estilo.RELLENO, BotonRedondeado.Tamano.PEQUENO, icono);
    }

    /** Botón contorno compacto (encabezados, filas de lista) — altura EstiloUI.ALTO_BOTON_PEQUENO. */
    public static JButton crearBotonSecundarioPequeno(String texto) {
        return new BotonRedondeado(texto, EstiloUI.AZUL_CLARO, BotonRedondeado.Estilo.CONTORNO, BotonRedondeado.Tamano.PEQUENO);
    }

    public static JButton crearBotonSecundarioPequeno(String texto, IconoVectorial.Tipo icono) {
        return new BotonRedondeado(texto, EstiloUI.AZUL_CLARO, BotonRedondeado.Estilo.CONTORNO, BotonRedondeado.Tamano.PEQUENO, icono);
    }

    /** Botón compacto solo-ícono con tooltip, para acciones dentro de tablas u otras barras densas. */
    public static JButton crearBotonAccionIcono(IconoVectorial.Tipo tipo, Color color, String tooltip) {
        return new BotonAccionIcono(tipo, color, tooltip);
    }

    // ── Logo ───────────────────────────────────────────────────────────────────

    /** Logo de Educ G escalado al alto especificado. Retorna un JLabel con la imagen. */
    public static JLabel crearLogoEducG(int alto) {
        try {
            Image img = ImageIO.read(new ByteArrayInputStream(obtenerImagenCacheada("logo_app")));
            int ancho = (img.getWidth(null) * alto) / img.getHeight(null);
            Image imgEscalada = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            JLabel lbl = new JLabel(new ImageIcon(imgEscalada));
            lbl.setOpaque(false);
            return lbl;
        } catch (Exception e) {
            JLabel fallback = new JLabel("Educ G");
            fallback.setFont(EstiloUI.FUENTE_TITULO_COMPACTO);
            fallback.setForeground(Color.WHITE);
            return fallback;
        }
    }

    /** Establece el ícono de Educ G en la barra de título de una ventana. */
    public static void establecerIconoVentana(JFrame ventana) {
        try {
            ventana.setIconImage(ImageIO.read(new ByteArrayInputStream(obtenerImagenCacheada("icono_ventana"))));
        } catch (Exception e) {
            // Si falla, el ícono por defecto de la ventana se mantiene
        }
    }

    /** Bytes de una imagen de la tabla {@code imagenes} por su clave, cacheados tras la primera consulta. */
    private static byte[] obtenerImagenCacheada(String clave) throws SQLException {
        byte[] cacheado = CACHE_IMAGENES.get(clave);
        if (cacheado != null) return cacheado;
        Imagen imagen = new ImagenDAOJdbc().obtenerPorClave(clave);
        if (imagen == null) throw new SQLException("No existe la imagen '" + clave + "'.");
        CACHE_IMAGENES.put(clave, imagen.getDatos());
        return imagen.getDatos();
    }
}
