package email;

public class EmailException extends Exception {

    public EmailException(String mensaje) {
        super(mensaje);
    }

    public EmailException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
