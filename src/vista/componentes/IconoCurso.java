package vista.componentes;

import modelo.Curso;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Logos a color de tecnologías (java.png, python.png, sql.png, react.png, github.png,
 * algoritmo.png en {@code assets/}), usados como ícono de un curso cuando su título
 * menciona esa tecnología. A diferencia de {@link IconoPng} (íconos de UI monocromáticos,
 * recoloreados al vuelo) estos se dibujan con sus colores originales — nunca se tiñen.
 */
public final class IconoCurso {

    private static final String CARPETA = "assets/";

    /** Palabra clave (buscada en minúsculas dentro del título) → archivo. Se evalúa en orden. */
    private static final String[][] MAPEO = {
        {"python", "python.png"},
        {"sql", "sql.png"},
        {"java", "java.png"},
        {"github", "github.png"},
        {"git", "github.png"},
        {"algoritmo", "algoritmo.png"},
        {"full stack", "react.png"},
        {"react", "react.png"},
        {"web", "react.png"},
    };

    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

    private IconoCurso() {}

    /** @return el logo escalado a {@code size}x{@code size} si el título matchea una tecnología conocida, o null. */
    public static Image obtener(String tituloCurso, int size) {
        String archivo = resolverArchivo(tituloCurso);
        if (archivo == null) return null;
        String clave = archivo + "|" + size;
        return CACHE.computeIfAbsent(clave, k -> cargarEscalado(archivo, size));
    }

    /**
     * Etiqueta cuadrada lista para usar como ícono de un curso: el logo a color si el título
     * matchea una tecnología conocida, o el emoji del curso como respaldo si no.
     */
    public static JLabel crearEtiqueta(Curso curso, int size) {
        JLabel lbl;
        Image logo = obtener(curso.getTitulo(), size);
        if (logo != null) {
            lbl = new JLabel(new ImageIcon(logo));
        } else {
            lbl = new JLabel(curso.getEmoji());
            // Los glifos de emoji a color de Windows (Segoe UI Emoji) se renderizan bastante
            // más grandes que el tamaño de fuente nominal, así que una fuente chica + una caja
            // algo más grande evita que Swing los recorte contra los bordes.
            lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, Math.round(size * 0.45f)));
        }
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        Dimension tamano = new Dimension(size, size);
        lbl.setPreferredSize(tamano);
        lbl.setMinimumSize(tamano);
        lbl.setMaximumSize(tamano);
        return lbl;
    }

    private static String resolverArchivo(String titulo) {
        if (titulo == null) return null;
        String t = titulo.toLowerCase(Locale.ROOT);
        for (String[] par : MAPEO) {
            if (t.contains(par[0])) return par[1];
        }
        return null;
    }

    private static Image cargarEscalado(String archivo, int size) {
        try {
            File f = new File(CARPETA + archivo);
            if (!f.exists()) return null;
            BufferedImage original = ImageIO.read(f);
            return original.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            return null;
        }
    }
}
