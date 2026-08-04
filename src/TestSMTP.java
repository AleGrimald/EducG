import email.*;

public class TestSMTP {
    public static void main(String[] args) {
        try {
            System.out.println("=== TEST SMTP ===");
            System.out.println("Host: " + ConfiguracionEmail.obtenerHost());
            System.out.println("Puerto: " + ConfiguracionEmail.obtenerPuerto());
            System.out.println("Usuario: " + ConfiguracionEmail.obtenerUsuario());
            System.out.println("Password: " + (ConfiguracionEmail.obtenerContrasena().isEmpty() ? "(vacío)" : "***"));

            EnviadorEmail enviador = new EnviadorEmail();
            System.out.println("\nIntentando enviar email de prueba...");

            enviador.enviar(
                "grimaldialejandro5@gmail.com",
                "Test SMTP",
                "Si ves este mensaje, el SMTP funciona correctamente."
            );

            System.out.println("✓ Email enviado exitosamente!");
        } catch (Exception ex) {
            System.out.println("✗ Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
