package servicio;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/** Hashing de contraseñas con SHA-256 + salt aleatorio. Formato almacenado: {@code <saltHex>:<hashHex>}. */
public final class HasheadorPassword {

    private HasheadorPassword() {}

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static String hashear(String password) throws NoSuchAlgorithmException {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

        return bytesToHex(salt) + ":" + bytesToHex(hash);
    }

    public static boolean verificar(String password, String almacenado) {
        if (almacenado == null || !almacenado.contains(":")) return false;
        try {
            String[] partes = almacenado.split(":", 2);
            String saltHex = partes[0];

            byte[] salt = new byte[saltHex.length() / 2];
            for (int i = 0; i < salt.length; i++) {
                salt[i] = (byte) Integer.parseInt(saltHex.substring(i * 2, i * 2 + 2), 16);
            }

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(hash).equals(partes[1]);
        } catch (Exception e) {
            return false;
        }
    }
}
