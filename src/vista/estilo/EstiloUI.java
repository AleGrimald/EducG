package vista.estilo;

import java.awt.*;

/**
 * Centraliza toda la paleta de colores, fuentes y estilos de la aplicación.
 * Única fuente de verdad visual: úsalo siempre para mantener consistencia en la UI.
 */
public final class EstiloUI {

    // ── PALETA DE COLORES ─────────────────────────────────────────────────────

    // Colores principales
    public static final Color AZUL_OSCURO   = new Color(20, 40, 70);      // Panel izquierdo (login, registro)
    public static final Color AZUL_CLARO    = new Color(41, 128, 185);    // Botones primarios, acciones
    public static final Color MORADO_ACENTO = new Color(30, 5, 80);       // Fondo estándar de todas las ventanas

    // Colores de estado
    public static final Color EXITO       = new Color(39, 174, 96);   // Verde
    public static final Color ERROR       = new Color(231, 76, 60);   // Rojo
    public static final Color INFO        = new Color(41, 128, 185);  // Azul
    public static final Color ADVERTENCIA = new Color(230, 162, 60);  // Ámbar — confirmaciones

    // Colores de texto
    public static final Color TEXTO_PRIMARIO   = new Color(44, 62, 80);      // Gris oscuro
    public static final Color TEXTO_SECUNDARIO = new Color(127, 140, 141);   // Gris medio
    public static final Color TEXTO_ATENUADO   = new Color(100, 120, 140);   // Gris claro
    public static final Color TEXTO_BLANCO     = Color.WHITE;

    // Colores de fondo
    public static final Color FONDO_BLANCO      = Color.WHITE;
    public static final Color FONDO_GRIS_CLARO  = new Color(240, 242, 245);  // Gris muy claro
    public static final Color FONDO_CAMPO       = new Color(248, 250, 252);  // Gris campo
    public static final Color BORDE             = new Color(220, 225, 230); // Gris borde
    /** Fondo suave para superficies grandes que no deben deslumbrar (p.ej. la ventana de chat) — más tenue que blanco puro. */
    public static final Color FONDO_SUAVE        = new Color(228, 233, 238);

    // Colores para componentes
    public static final Color BOTON_PRESIONADO = new Color(25, 90, 150);
    public static final Color BOTON_HOVER      = new Color(52, 152, 219);
    public static final Color SOMBRA           = new Color(0, 0, 0, 20);

    // ── FUENTES ───────────────────────────────────────────────────────────────

    public static final Font FUENTE_TITULO      = new Font("Segoe UI", Font.BOLD, 48);
    /** Logo "Educ G" compacto en encabezados de ventana (Cursos, Panel de Usuario). */
    public static final Font FUENTE_TITULO_COMPACTO    = new Font("Segoe UI", Font.BOLD, 36);
    public static final Font FUENTE_SUBTITULO_COMPACTO = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FUENTE_ENCABEZADO  = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FUENTE_SECCION     = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FUENTE_ETIQUETA    = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FUENTE_CUERPO      = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FUENTE_PEQUENA     = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FUENTE_BOTON       = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FUENTE_ICONO       = new Font("Arial", Font.PLAIN, 60);

    // ── TAMAÑOS ESTÁNDAR ───────────────────────────────────────────────────────

    // Botones
    public static final int ALTO_BOTON = 46;
    public static final int ALTO_BOTON_PEQUENO = 38;
    public static final int ANCHO_BOTON_ESTANDAR = 120;

    // Campos de texto
    public static final int ALTO_CAMPO = 42;
    public static final int RELLENO_CAMPO_H = 12;
    public static final int RELLENO_CAMPO_V = 10;

    // Espacios y márgenes
    public static final int RELLENO_GRANDE = 40;
    public static final int RELLENO_MEDIANO = 20;
    public static final int RELLENO_PEQUENO = 8;
    public static final int RELLENO_MINIMO = 4;

    public static final int MARGEN_GRANDE = 30;
    public static final int MARGEN_MEDIANO = 16;
    public static final int MARGEN_PEQUENO = 12;

    // Bordes redondeados
    public static final int RADIO_BORDE_GRANDE = 20;
    public static final int RADIO_BORDE_MEDIANO = 10;
    public static final int RADIO_BORDE_PEQUENO = 8;

    // Tamaños de ventana
    public static final int PESO_PANEL_IZQUIERDO = 50; // % del ancho
    public static final int PESO_PANEL_DERECHO = 70;    // % del ancho
    public static final int ANCHO_DIALOGO = 470;
    public static final int ALTO_DIALOGO = 300;
    public static final int ANCHO_TARJETA_FORM = 420;
    public static final int ALTO_TARJETA_FORM = 600;

    // Burbuja de chatbot flotante
    public static final int TAMANO_BURBUJA_CHATBOT = 64;
    public static final int MARGEN_BURBUJA_CHATBOT = 36;
    /** Margen inferior de la burbuja — más grande que el lateral para despegarla bien del borde/taskbar. */
    public static final int MARGEN_BURBUJA_CHATBOT_INFERIOR = 70;
    public static final int ANCHO_VENTANA_CHAT = 360;
    public static final int ALTO_VENTANA_CHAT = 480;

    // ── MÉTODOS AUXILIARES ─────────────────────────────────────────────────────

    /** Obtiene un color más oscuro (para estados presionados) */
    public static Color oscurecer(Color color, int reduccion) {
        return new Color(
            Math.max(0, color.getRed()   - reduccion),
            Math.max(0, color.getGreen() - reduccion),
            Math.max(0, color.getBlue()  - reduccion)
        );
    }

    /** Obtiene un color más claro (para estados hover) */
    public static Color aclarar(Color color, int incremento) {
        return new Color(
            Math.min(255, color.getRed()   + incremento),
            Math.min(255, color.getGreen() + incremento),
            Math.min(255, color.getBlue()  + incremento)
        );
    }

    /** Crea un color con transparencia */
    public static Color conAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    private EstiloUI() {}
}
