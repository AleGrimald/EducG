import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthService {

    private AuthService() {}

    // ── Hashing ──────────────────────────────────────────────────────────────

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    /**
     * Genera un hash SHA-256 con salt aleatorio.
     * Formato almacenado: {@code <saltHex>:<hashHex>}
     */
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

        return bytesToHex(salt) + ":" + bytesToHex(hash);
    }

    public static boolean verifyPassword(String password, String stored) {
        if (stored == null || !stored.contains(":")) return false;
        try {
            String[] parts = stored.split(":", 2);
            String saltHex = parts[0];

            byte[] salt = new byte[saltHex.length() / 2];
            for (int i = 0; i < salt.length; i++) {
                salt[i] = (byte) Integer.parseInt(saltHex.substring(i * 2, i * 2 + 2), 16);
            }

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(hash).equals(parts[1]);
        } catch (Exception e) {
            return false;
        }
    }

    // ── Operaciones de DB ────────────────────────────────────────────────────

    /**
     * @return true si el email/password son correctos y la cuenta está activa
     */
    public static boolean login(String email, String password) throws SQLException {
        final String sql = "SELECT password_hash FROM usuarios WHERE email = ? AND activo = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return verifyPassword(password, rs.getString("password_hash"));
                }
            }
        }
        return false;
    }

    /**
     * @return true si el registro fue exitoso, false si el email ya existe
     */
    public static boolean register(String email, String password, String nombre, String apellido)
            throws SQLException {

        // Verificar si el email ya existe
        final String checkSql = "SELECT id FROM usuarios WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return false;
            }
        }

        final String insertSql =
            "INSERT INTO usuarios (email, password_hash, nombre, apellido) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {
            String hash;
            try {
                hash = hashPassword(password);
            } catch (NoSuchAlgorithmException e) {
                throw new SQLException("Error al procesar la contraseña.", e);
            }
            ps.setString(1, email);
            ps.setString(2, hash);
            ps.setString(3, nombre);
            ps.setString(4, apellido);
            ps.executeUpdate();
            return true;
        }
    }

    // ── Datos de usuario ─────────────────────────────────────────────────────

    /** @return {nombre, apellido, email} o null si no existe */
    public static String[] getUserData(String email) throws SQLException {
        final String sql = "SELECT nombre, apellido FROM usuarios WHERE email = ? AND activo = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new String[]{rs.getString("nombre"), rs.getString("apellido"), email};
            }
        }
        return null;
    }

    public static boolean updatePersonalData(String email, String nombre, String apellido) throws SQLException {
        final String sql = "UPDATE usuarios SET nombre = ?, apellido = ? WHERE email = ? AND activo = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, email);
            return ps.executeUpdate() > 0;
        }
    }

    /** Verifica la contraseña actual antes de actualizar. @return false si la contraseña actual es incorrecta */
    public static boolean updatePassword(String email, String currentPw, String newPw) throws SQLException {
        final String verifySql = "SELECT password_hash FROM usuarios WHERE email = ? AND activo = 1";
        String storedHash = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(verifySql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) storedHash = rs.getString("password_hash");
            }
        }
        if (storedHash == null || !verifyPassword(currentPw, storedHash)) return false;

        final String updateSql = "UPDATE usuarios SET password_hash = ? WHERE email = ? AND activo = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {
            String hash;
            try { hash = hashPassword(newPw); }
            catch (NoSuchAlgorithmException e) { throw new SQLException("Error al procesar la contraseña.", e); }
            ps.setString(1, hash);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Inscripciones ────────────────────────────────────────────────────────

    private static int getUserId(String email) throws SQLException {
        final String sql = "SELECT id FROM usuarios WHERE email = ? AND activo = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        return -1;
    }

    /** @return true si la inscripción fue nueva o reactivada; false si ya estaba activa */
    public static boolean enrollCourse(String email, String courseTitle) throws SQLException {
        int userId = getUserId(email);
        if (userId == -1) return false;

        // Verificar si ya existe el registro
        final String checkSql = "SELECT id, activo FROM inscripciones WHERE usuario_id = ? AND curso_titulo = ?";
        int existingId = -1;
        boolean wasActive = false;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, userId);
            ps.setString(2, courseTitle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { existingId = rs.getInt("id"); wasActive = rs.getInt("activo") == 1; }
            }
        }

        if (existingId != -1) {
            if (wasActive) return false; // ya inscripto
            // Reactivar inscripción previa
            final String reactivateSql =
                "UPDATE inscripciones SET activo = 1, fecha_inscripcion = NOW() WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(reactivateSql)) {
                ps.setInt(1, existingId);
                ps.executeUpdate();
                return true;
            }
        }

        final String insertSql = "INSERT INTO inscripciones (usuario_id, curso_titulo) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, userId);
            ps.setString(2, courseTitle);
            ps.executeUpdate();
            return true;
        }
    }

    public static boolean isEnrolled(String email, String courseTitle) throws SQLException {
        int userId = getUserId(email);
        if (userId == -1) return false;
        final String sql = "SELECT id FROM inscripciones WHERE usuario_id = ? AND curso_titulo = ? AND activo = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, courseTitle);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public static void unenrollCourse(String email, String courseTitle) throws SQLException {
        int userId = getUserId(email);
        if (userId == -1) return;
        final String sql = "UPDATE inscripciones SET activo = 0 WHERE usuario_id = ? AND curso_titulo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, courseTitle);
            ps.executeUpdate();
        }
    }

    /** @return lista de {curso_titulo, fecha_inscripcion} */
    public static List<String[]> getEnrolledCourses(String email) throws SQLException {
        int userId = getUserId(email);
        List<String[]> courses = new ArrayList<>();
        if (userId == -1) return courses;
        final String sql =
            "SELECT curso_titulo, fecha_inscripcion FROM inscripciones " +
            "WHERE usuario_id = ? AND activo = 1 ORDER BY fecha_inscripcion DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    courses.add(new String[]{rs.getString("curso_titulo"), rs.getString("fecha_inscripcion")});
            }
        }
        return courses;
    }

    // ── Tests y Estadísticas ─────────────────────────────────────────────────

    /** @return lista de {curso_titulo, test_nombre, puntaje, fecha} */
    public static List<String[]> getTestResults(String email) throws SQLException {
        int userId = getUserId(email);
        List<String[]> results = new ArrayList<>();
        if (userId == -1) return results;
        final String sql =
            "SELECT curso_titulo, test_nombre, puntaje, fecha FROM test_resultados " +
            "WHERE usuario_id = ? ORDER BY fecha DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    results.add(new String[]{
                        rs.getString("curso_titulo"), rs.getString("test_nombre"),
                        String.valueOf(rs.getInt("puntaje")), rs.getString("fecha")
                    });
            }
        }
        return results;
    }

    /** @return int[]{cursosInscritos, totalTests, promedioTests} */
    public static int[] getStats(String email) throws SQLException {
        int userId = getUserId(email);
        if (userId == -1) return new int[]{0, 0, 0};

        int cursosInscritos = 0;
        final String countSql = "SELECT COUNT(*) FROM inscripciones WHERE usuario_id = ? AND activo = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(countSql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) cursosInscritos = rs.getInt(1); }
        }

        int totalTests = 0, promedio = 0;
        final String statsSql =
            "SELECT COUNT(*) AS total, COALESCE(AVG(puntaje), 0) AS avg_puntaje " +
            "FROM test_resultados WHERE usuario_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(statsSql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalTests = rs.getInt("total");
                    promedio   = (int) Math.round(rs.getDouble("avg_puntaje"));
                }
            }
        }
        return new int[]{cursosInscritos, totalTests, promedio};
    }
}
