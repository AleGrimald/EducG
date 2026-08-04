package vista.componentes;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import java.util.regex.Pattern;

/** DocumentFilter que permite solo caracteres del regex y los convierte a mayúsculas. */
public class FiltroMayuscula extends DocumentFilter {

    private final Pattern patronPermitido;

    public FiltroMayuscula(String regexCaracterPermitido) {
        this.patronPermitido = Pattern.compile(regexCaracterPermitido);
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (esValido(string)) {
            super.insertString(fb, offset, string.toUpperCase(), attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (esValido(text)) {
            super.replace(fb, offset, length, text.toUpperCase(), attrs);
        }
    }

    private boolean esValido(String texto) {
        if (texto == null || texto.isEmpty()) return true;
        for (int i = 0; i < texto.length(); i++) {
            if (!patronPermitido.matcher(String.valueOf(texto.charAt(i))).matches()) return false;
        }
        return true;
    }

    public static void aplicarA(JTextComponent campo, String regexCaracterPermitido) {
        if (campo.getDocument() instanceof AbstractDocument) {
            ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroMayuscula(regexCaracterPermitido));
        }
    }
}
