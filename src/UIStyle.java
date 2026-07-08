import java.awt.*;

/**
 * Centraliza toda la paleta de colores, fuentes y estilos de la aplicación.
 * Úsalo siempre para mantener consistencia visual en toda la UI.
 */
public final class UIStyle {

    // ── PALETA DE COLORES ─────────────────────────────────────────────────────

    // Colores principales
    public static final Color PRIMARY_DARK = new Color(20, 40, 70);      // Azul oscuro
    public static final Color PRIMARY_LIGHT = new Color(41, 128, 185);   // Azul claro
    public static final Color PRIMARY_ACCENT = new Color(30, 5, 80);     // Morado oscuro

    // Colores de estado
    public static final Color SUCCESS = new Color(39, 174, 96);          // Verde
    public static final Color ERROR = new Color(231, 76, 60);            // Rojo
    public static final Color INFO = new Color(41, 128, 185);            // Azul

    // Colores de texto
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);      // Gris oscuro
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141); // Gris medio
    public static final Color TEXT_MUTED = new Color(100, 120, 140);     // Gris claro
    public static final Color TEXT_WHITE = Color.WHITE;

    // Colores de fondo
    public static final Color BG_WHITE = Color.WHITE;
    public static final Color BG_LIGHT_GRAY = new Color(240, 242, 245);  // Gris muy claro
    public static final Color BG_FIELD = new Color(248, 250, 252);       // Gris campo
    public static final Color BG_BORDER = new Color(220, 225, 230);      // Gris borde

    // Colores para componentes
    public static final Color BUTTON_PRESSED = new Color(25, 90, 150);   // Botón presionado
    public static final Color BUTTON_HOVER = new Color(52, 152, 219);    // Botón hover
    public static final Color SHADOW = new Color(0, 0, 0, 20);            // Sombra

    // ── FUENTES ───────────────────────────────────────────────────────────────

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 48);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_ICON = new Font("Arial", Font.PLAIN, 60);

    // ── TAMAÑOS ESTÁNDAR ───────────────────────────────────────────────────────

    // Botones
    public static final int BUTTON_HEIGHT = 46;
    public static final int BUTTON_HEIGHT_SMALL = 38;
    public static final int BUTTON_WIDTH_STANDARD = 120;

    // Campos de texto
    public static final int FIELD_HEIGHT = 42;
    public static final int FIELD_PADDING_H = 12;
    public static final int FIELD_PADDING_V = 10;

    // Espacios y márgenes
    public static final int PADDING_LARGE = 40;
    public static final int PADDING_MEDIUM = 20;
    public static final int PADDING_SMALL = 8;
    public static final int PADDING_TINY = 4;

    public static final int MARGIN_LARGE = 30;
    public static final int MARGIN_MEDIUM = 16;
    public static final int MARGIN_SMALL = 12;

    // Bordes redondeados
    public static final int BORDER_RADIUS_LARGE = 20;
    public static final int BORDER_RADIUS_MEDIUM = 10;
    public static final int BORDER_RADIUS_SMALL = 8;

    // Tamaños de ventana
    public static final int PANEL_LEFT_WEIGHT = 50;      // 50% del ancho
    public static final int PANEL_RIGHT_WEIGHT = 70;     // 70% del ancho
    public static final int DIALOG_WIDTH = 450;
    public static final int DIALOG_HEIGHT = 280;
    public static final int FORM_CARD_WIDTH = 420;
    public static final int FORM_CARD_HEIGHT = 600;

    // ── MÉTODOS AUXILIARES ─────────────────────────────────────────────────────

    /**
     * Obtiene un color más oscuro (para estados presionados)
     */
    public static Color darker(Color color, int reduction) {
        return new Color(
            Math.max(0, color.getRed() - reduction),
            Math.max(0, color.getGreen() - reduction),
            Math.max(0, color.getBlue() - reduction)
        );
    }

    /**
     * Obtiene un color más claro (para estados hover)
     */
    public static Color lighter(Color color, int increase) {
        return new Color(
            Math.min(255, color.getRed() + increase),
            Math.min(255, color.getGreen() + increase),
            Math.min(255, color.getBlue() + increase)
        );
    }

    /**
     * Crea un color con transparencia
     */
    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    private UIStyle() {}
}