import java.util.regex.Pattern;

public final class Validator {

    /** Solo letras, dígitos y los símbolos permitidos en emails estándar */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$"
    );

    /** Solo alfanumérico, entre 6 y 20 caracteres — excluye todo símbolo de inyección */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9]{6,20}$"
    );

    /** Caracteres típicos de inyección SQL/XSS */
    private static final Pattern INJECTION_PATTERN = Pattern.compile(
        "[';\"\\\\\\-\\-/\\*=<>|&%^#!~`]"
    );

    private Validator() {}

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /** La contraseña es alfanumérica pura → no puede tener símbolos de inyección por definición */
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean hasInjectionRisk(String input) {
        return input != null && INJECTION_PATTERN.matcher(input).find();
    }

    /** Nombre/apellido: 2-100 caracteres, sin símbolos de inyección */
    public static boolean isValidName(String name) {
        return name != null
            && name.length() >= 2
            && name.length() <= 100
            && !hasInjectionRisk(name);
    }
}
