package vista.estilo;

import vista.componentes.BotonAccionIcono;
import vista.componentes.BotonRedondeado;
import vista.componentes.IconoVectorial;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** Fábrica de componentes Swing con el estilo visual de Educ G, basada en {@link EstiloUI}. */
public final class FabricaUI {

    private FabricaUI() {}

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
}
