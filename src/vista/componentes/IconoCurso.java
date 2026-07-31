package vista.componentes;

import modelo.Curso;
import vista.estilo.EstiloUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Ícono de un curso: el PNG guardado en {@code cursos.emoji} ({@code LONGBLOB} — ver
 * {@link SelectorIconoCurso}, que es quien copia los bytes de uno de los PNG de
 * {@code assets/} al elegirlo), o un placeholder con la inicial del título si el curso
 * todavía no tiene ninguno asignado.
 */
public final class IconoCurso {

    private static final Color[] PALETA_PLACEHOLDER = {
        EstiloUI.AZUL_CLARO, EstiloUI.EXITO, EstiloUI.ADVERTENCIA, EstiloUI.ERROR, new Color(142, 68, 173)
    };

    private IconoCurso() {}

    /** @return el PNG escalado a {@code size}x{@code size}, o null si no hay datos o no se puede decodificar. */
    public static Image cargar(byte[] datosPng, int size) {
        if (datosPng == null || datosPng.length == 0) return null;
        try {
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(datosPng));
            if (original == null) return null;
            return original.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            return null;
        }
    }

    /** Etiqueta cuadrada lista para usar como ícono de un curso. */
    public static JLabel crearEtiqueta(Curso curso, int size) {
        return crearEtiqueta(curso.getEmoji(), curso.getTitulo(), size);
    }

    public static JLabel crearEtiqueta(byte[] datosPng, String tituloCurso, int size) {
        JLabel lbl;
        Image icono = cargar(datosPng, size);
        if (icono != null) {
            lbl = new JLabel(new ImageIcon(icono));
            lbl.setOpaque(false);
        } else {
            lbl = new JLabel(inicial(tituloCurso));
            lbl.setFont(new Font("Segoe UI", Font.BOLD, Math.round(size * 0.42f)));
            lbl.setForeground(Color.WHITE);
            lbl.setOpaque(true);
            lbl.setBackground(colorParaPlaceholder(tituloCurso));
        }
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        Dimension tamano = new Dimension(size, size);
        lbl.setPreferredSize(tamano);
        lbl.setMinimumSize(tamano);
        lbl.setMaximumSize(tamano);
        return lbl;
    }

    private static String inicial(String titulo) {
        return (titulo != null && !titulo.isBlank()) ? titulo.substring(0, 1).toUpperCase(Locale.ROOT) : "?";
    }

    /** Color estable por curso (derivado del hash del título) para que el placeholder no cambie entre recargas. */
    private static Color colorParaPlaceholder(String titulo) {
        int hash = (titulo == null) ? 0 : titulo.hashCode();
        return PALETA_PLACEHOLDER[Math.floorMod(hash, PALETA_PLACEHOLDER.length)];
    }
}
