package vista.componentes;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Íconos cargados desde los PNG de {@code assets/} (monocromáticos: negro sobre transparente,
 * mismo patrón que las imágenes de fondo de Login/Registro — ver {@code CargadorEnv}/rutas
 * relativas al working directory). Se recolorean al color pedido (preservando el alpha del
 * borde antialiseado del PNG original) y se escalan al tamaño pedido; original, recoloreado y
 * escalado quedan cacheados para no repetir el trabajo en cada repintado.
 */
public final class IconoPng {

    private static final String CARPETA = "assets/";
    private static final Map<String, BufferedImage> ORIGINALES = new ConcurrentHashMap<>();
    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

    private IconoPng() {}

    /** @return la imagen lista para dibujar (recoloreada + escalada), o null si el archivo no existe. */
    public static Image obtener(String archivo, Color color, int size) {
        String clave = archivo + "|" + color.getRGB() + "|" + size;
        return CACHE.computeIfAbsent(clave, k -> construir(archivo, color, size));
    }

    private static Image construir(String archivo, Color color, int size) {
        BufferedImage original = cargarOriginal(archivo);
        if (original == null) return null;
        BufferedImage teñido = teñir(original, color);
        return teñido.getScaledInstance(size, size, Image.SCALE_SMOOTH);
    }

    private static BufferedImage cargarOriginal(String archivo) {
        return ORIGINALES.computeIfAbsent(archivo, a -> {
            try {
                File f = new File(CARPETA + a);
                return f.exists() ? ImageIO.read(f) : null;
            } catch (IOException e) {
                return null;
            }
        });
    }

    /** Reemplaza el RGB de cada píxel visible por {@code color}, preservando su alpha original (bordes suaves). */
    private static BufferedImage teñir(BufferedImage origen, Color color) {
        int ancho = origen.getWidth(), alto = origen.getHeight();
        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        int rgbSinAlpha = color.getRGB() & 0x00FFFFFF;
        for (int py = 0; py < alto; py++) {
            for (int px = 0; px < ancho; px++) {
                int argb = origen.getRGB(px, py);
                int alpha = argb >>> 24;
                if (alpha == 0) continue;
                resultado.setRGB(px, py, (alpha << 24) | rgbSinAlpha);
            }
        }
        return resultado;
    }
}
