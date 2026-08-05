package vista.componentes;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;
import vista.estilo.EstiloUI;

import javax.swing.*;
import java.awt.*;

/** Editor de código multilínea (sin ejecución todavía) para el paso de ejercicio de un curso. */
public class PanelEditorCodigo extends JPanel {

    private final RSyntaxTextArea areaCodigo;

    public PanelEditorCodigo(int ancho, int alto) {
        super(new BorderLayout());
        areaCodigo = new RSyntaxTextArea();
        areaCodigo.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        areaCodigo.setCodeFoldingEnabled(false);
        areaCodigo.setFont(EstiloUI.FUENTE_CODIGO);
        areaCodigo.setBackground(EstiloUI.FONDO_CAMPO);
        areaCodigo.setForeground(EstiloUI.TEXTO_PRIMARIO);
        areaCodigo.setCaretColor(EstiloUI.TEXTO_PRIMARIO);
        areaCodigo.setSelectionColor(EstiloUI.AZUL_CLARO);
        areaCodigo.setSelectedTextColor(EstiloUI.TEXTO_BLANCO);
        // RSyntaxTextArea resalta la línea del cursor con un amarillo pálido por defecto,
        // pensado para temas claros — sobre el tema oscuro de EstiloUI eso se ve como una
        // franja amarilla brillante que tapa el texto que se está tipeando. La themeamos
        // con un tono sutil (apenas más claro que el fondo del campo) en vez de dejarla así.
        areaCodigo.setCurrentLineHighlightColor(EstiloUI.FONDO_SUAVE);
        areaCodigo.setTabSize(4);

        RTextScrollPane scroll = new RTextScrollPane(areaCodigo);
        scroll.setLineNumbersEnabled(true);
        scroll.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE, 1, true));
        scroll.setFoldIndicatorEnabled(false);
        scroll.setBackground(EstiloUI.FONDO_CAMPO);

        // El gutter (numeración de línea) también viene themeado para fondo claro por
        // defecto — mismo problema de contraste que el resaltado de línea.
        Gutter gutter = scroll.getGutter();
        gutter.setBackground(EstiloUI.FONDO_CAMPO);
        gutter.setBorderColor(EstiloUI.BORDE);
        gutter.setLineNumberColor(EstiloUI.TEXTO_SECUNDARIO);

        Dimension tamano = new Dimension(ancho, alto);
        setPreferredSize(tamano);
        setMinimumSize(tamano);
        setMaximumSize(tamano);
        setAlignmentX(LEFT_ALIGNMENT);
        setOpaque(false);

        add(scroll, BorderLayout.CENTER);
    }

    public String getCodigo() {
        return areaCodigo.getText();
    }

    /** Bloquea/desbloquea la edición (ej. mientras se espera la evaluación de la IA). */
    public void setHabilitado(boolean habilitado) {
        areaCodigo.setEditable(habilitado);
        areaCodigo.setEnabled(habilitado);
    }
}
