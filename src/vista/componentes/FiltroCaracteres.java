package vista.componentes;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import java.util.regex.Pattern;

/**
 * DocumentFilter que solo permite tipear caracteres que matcheen el patrón dado.
 * Evita a nivel de UI que se ingresen símbolos o tipos de caracteres inválidos
 * (ej. letras en un campo de DNI, o números en un campo de Nombre), en vez de
 * solo detectarlo al validar el formulario completo.
 */
public class FiltroCaracteres extends DocumentFilter {

    private final Pattern patronPermitido;

    public FiltroCaracteres(String regexCaracterPermitido) {
        this.patronPermitido = Pattern.compile(regexCaracterPermitido);
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (esValido(string)) super.insertString(fb, offset, string, attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (esValido(text)) super.replace(fb, offset, length, text, attrs);
    }

    private boolean esValido(String texto) {
        if (texto == null || texto.isEmpty()) return true;
        for (int i = 0; i < texto.length(); i++) {
            if (!patronPermitido.matcher(String.valueOf(texto.charAt(i))).matches()) return false;
        }
        return true;
    }

    /** Aplica el filtro a un campo de texto existente, permitiendo solo caracteres que matcheen el regex dado. */
    public static void aplicarA(JTextComponent campo, String regexCaracterPermitido) {
        if (campo.getDocument() instanceof AbstractDocument) {
            ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroCaracteres(regexCaracterPermitido));
        }
    }
}
