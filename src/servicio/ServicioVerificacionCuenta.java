package servicio;

import dao.UsuarioDAO;
import dao.VerificacionDAO;
import email.EnviadorEmail;
import email.EmailException;
import modelo.Usuario;

import java.security.SecureRandom;
import java.sql.SQLException;

public class ServicioVerificacionCuenta {

    private final VerificacionDAO verificacionDAO;
    private final UsuarioDAO usuarioDAO;
    private final EnviadorEmail enviadorEmail;

    public ServicioVerificacionCuenta(VerificacionDAO verificacionDAO, UsuarioDAO usuarioDAO, EnviadorEmail enviadorEmail) {
        this.verificacionDAO = verificacionDAO;
        this.usuarioDAO = usuarioDAO;
        this.enviadorEmail = enviadorEmail;
    }

    /** Genera un código de 6 caracteres alfanuméricos (mayúsculas) y lo envía por email. */
    public void generarYEnviarCodigo(long dni, String email, String nombre) throws SQLException, EmailException {
        String codigo = generarCodigo();
        verificacionDAO.generarCodigo(dni, codigo, 10); // 10 minutos de expiración

        String asunto = "Verifica tu cuenta en Educ G";
        String cuerpo = "Hola " + nombre + ",\n\n" +
                "Tu código de verificación es: " + codigo + "\n\n" +
                "Este código expira en 10 minutos.\n\n" +
                "Si no solicitaste esto, ignorá este correo.\n\n" +
                "Saludos,\nEduc G";

        enviadorEmail.enviar(email, asunto, cuerpo);
    }

    /** @return 1 = válido y cuenta activada, 0 = código inválido/no existe, -1 = código expirado */
    public int verificarCodigo(long dni, String codigo) throws SQLException {
        return verificacionDAO.verificarCodigo(dni, codigo);
    }

    /** Reenvía el código a un usuario (por DNI). Busca el email desde la base. */
    public void reenviarCodigoPorDni(long dni) throws SQLException, EmailException {
        Usuario usuario = usuarioDAO.obtenerUsuarioPorDni(dni);
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario con ese DNI no existe.");
        }
        generarYEnviarCodigo(dni, usuario.getEmail(), usuario.getNombre());
    }

    private static String generarCodigo() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            codigo.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return codigo.toString();
    }
}
