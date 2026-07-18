package util;

import java.util.regex.Pattern;

public final class Validador {

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

    /** Solo dígitos, 7 a 9 caracteres (DNI argentino) */
    private static final Pattern DNI_PATTERN = Pattern.compile(
        "^[0-9]{7,9}$"
    );

    /** Dígitos, espacios y los símbolos habituales de un teléfono (+, -, paréntesis), hasta 20 caracteres */
    private static final Pattern TELEFONO_PATTERN = Pattern.compile(
        "^[0-9+\\-()\\s]{6,20}$"
    );

    private Validador() {}

    public static boolean esEmailValido(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /** La contraseña es alfanumérica pura → no puede tener símbolos de inyección por definición */
    public static boolean esPasswordValida(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean tieneRiesgoInyeccion(String input) {
        return input != null && INJECTION_PATTERN.matcher(input).find();
    }

    /** Nombre/apellido: 2-100 caracteres, sin símbolos de inyección */
    public static boolean esNombreValido(String nombre) {
        return nombre != null
            && nombre.length() >= 2
            && nombre.length() <= 100
            && !tieneRiesgoInyeccion(nombre);
    }

    /** DNI: solo dígitos, 7 a 9 caracteres */
    public static boolean esDniValido(String dni) {
        return dni != null && DNI_PATTERN.matcher(dni).matches();
    }

    /** Teléfono: dígitos y símbolos habituales (+, -, paréntesis), 6 a 20 caracteres */
    public static boolean esTelefonoValido(String telefono) {
        return telefono != null && TELEFONO_PATTERN.matcher(telefono).matches();
    }
}
