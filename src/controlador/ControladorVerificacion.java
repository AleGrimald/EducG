package controlador;

import dao.VerificacionDAOJdbc;
import dao.UsuarioDAOJdbc;
import email.EnviadorEmail;
import email.EmailException;
import servicio.ServicioVerificacionCuenta;
import util.Validador;

import java.sql.SQLException;

public class ControladorVerificacion {

    private final ServicioVerificacionCuenta servicio;

    public ControladorVerificacion() {
        this.servicio = new ServicioVerificacionCuenta(
            new VerificacionDAOJdbc(),
            new UsuarioDAOJdbc(),
            new EnviadorEmail()
        );
    }

    /** Genera y envía un código de verificación a un email.
     * @throws IllegalArgumentException si el DNI o email son inválidos
     * @throws SQLException si hay error en la base de datos
     * @throws EmailException si hay error al enviar el email
     */
    public void generarYEnviarCodigo(long dni, String email, String nombre)
            throws IllegalArgumentException, SQLException, EmailException {
        if (!Validador.esDniValido(String.valueOf(dni))) {
            throw new IllegalArgumentException("El DNI no es válido.");
        }
        if (!Validador.esEmailValido(email)) {
            throw new IllegalArgumentException("El email no es válido.");
        }
        servicio.generarYEnviarCodigo(dni, email, nombre);
    }

    /** Verifica un código ingresado.
     * @return true si el código es válido y la cuenta se activó, false si es inválido
     * @throws IllegalArgumentException si el código expiró o el DNI es inválido
     * @throws SQLException si hay error en la base de datos
     */
    public boolean verificarCodigo(long dni, String codigo)
            throws IllegalArgumentException, SQLException {
        if (!Validador.esDniValido(String.valueOf(dni))) {
            throw new IllegalArgumentException("El DNI no es válido.");
        }
        String codigoMayuscula = codigo.toUpperCase();
        if (!Validador.esCodigoVerificacionValido(codigoMayuscula)) {
            throw new IllegalArgumentException("El código debe tener 6 caracteres alfanuméricos.");
        }

        int resultado = servicio.verificarCodigo(dni, codigoMayuscula);
        if (resultado == 1) {
            return true;
        } else if (resultado == -1) {
            throw new IllegalArgumentException("El código expiró. Pedí uno nuevo con 'Reenviar código'.");
        } else {
            return false; // Código inválido/no existe
        }
    }

    /** Reenvia el código a un DNI (sin email/nombre, los busca de la base).
     * @throws IllegalArgumentException si el usuario no existe o hay validación
     * @throws SQLException si hay error en la base de datos
     * @throws EmailException si hay error al enviar el email
     */
    public void reenviarCodigoPorDni(long dni)
            throws IllegalArgumentException, SQLException, EmailException {
        if (!Validador.esDniValido(String.valueOf(dni))) {
            throw new IllegalArgumentException("El DNI no es válido.");
        }
        servicio.reenviarCodigoPorDni(dni);
    }
}
