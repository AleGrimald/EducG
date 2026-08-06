package email;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Base64;

public class EnviadorEmail {

    /** Sin esto, un bloqueo de red silencioso (firewall/antivirus/router comiéndose los
     * paquetes en vez de rechazarlos) cuelga el Socket indefinidamente — sin excepción, sin
     * feedback — y el SwingWorker que llama a este método nunca llega a done(). */
    private static final int TIMEOUT_CONEXION_MS = 15_000;
    private static final int TIMEOUT_LECTURA_MS = 20_000;
    /** Ancho de línea recomendado por RFC 2045 para contenido codificado en base64. */
    private static final int ANCHO_LINEA_BASE64 = 76;

    /** Conexión SMTP ya autenticada (EHLO + AUTH LOGIN), lista para MAIL FROM/RCPT TO/DATA. */
    private static final class SesionSmtp implements Closeable {
        final Socket socket;
        final BufferedReader br;
        final BufferedWriter bw;

        SesionSmtp(Socket socket, BufferedReader br, BufferedWriter bw) {
            this.socket = socket;
            this.br = br;
            this.bw = bw;
        }

        @Override
        public void close() {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    public void enviar(String destinatario, String asunto, String cuerpo) throws EmailException {
        enviar(destinatario, asunto, cuerpo, null, null, null);
    }

    /** Como {@link #enviar}, pero con un archivo adjunto (ej. el certificado en PNG). */
    public void enviarConAdjunto(String destinatario, String asunto, String cuerpo,
                                  String nombreArchivo, byte[] datosAdjunto, String mimeType) throws EmailException {
        if (nombreArchivo == null || nombreArchivo.isBlank() || datosAdjunto == null || datosAdjunto.length == 0 || mimeType == null) {
            throw new EmailException("Adjunto incompleto: falta nombre, datos o tipo MIME.");
        }
        if (contieneCrLf(nombreArchivo) || contieneCrLf(mimeType)) {
            throw new EmailException("El nombre de archivo o tipo MIME contienen caracteres no permitidos.");
        }
        enviar(destinatario, asunto, cuerpo, nombreArchivo, datosAdjunto, mimeType);
    }

    private void enviar(String destinatario, String asunto, String cuerpo,
                         String nombreArchivo, byte[] datosAdjunto, String mimeType) throws EmailException {
        String usuario = ConfiguracionEmail.obtenerUsuario();
        String contrasena = ConfiguracionEmail.obtenerContrasena();
        String desdeNombre = ConfiguracionEmail.obtenerDesdeNombre();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            throw new EmailException("Credenciales de SMTP no configuradas en .env (SMTP_USER y SMTP_PASSWORD).");
        }
        // EnviadorEmail no debería depender de que el caller sea cuidadoso: sin esto, un \r\n
        // en cualquiera de estos tres valores podría inyectar headers SMTP adicionales o cortar
        // el mensaje a mitad de camino. Hoy no es explotable (los callers reales usan un asunto
        // hardcodeado y un email ya validado por Validador.esEmailValido()), pero la función en
        // sí misma debe ser segura por construcción, no por confiar en quien la llama.
        if (contieneCrLf(destinatario) || contieneCrLf(asunto) || contieneCrLf(desdeNombre)) {
            throw new EmailException("El destinatario, asunto o remitente contienen caracteres no permitidos.");
        }

        try (SesionSmtp sesion = conectarYAutenticar(usuario, contrasena)) {
            enviarComando(sesion.bw, sesion.br, "MAIL FROM:<" + usuario + ">");
            enviarComando(sesion.bw, sesion.br, "RCPT TO:<" + destinatario + ">");
            enviarComando(sesion.bw, sesion.br, "DATA");

            sesion.bw.write("From: " + desdeNombre + " <" + usuario + ">\r\n");
            sesion.bw.write("To: " + destinatario + "\r\n");
            sesion.bw.write("Subject: " + asunto + "\r\n");
            sesion.bw.write("MIME-Version: 1.0\r\n");

            if (nombreArchivo == null) {
                sesion.bw.write("Content-Type: text/plain; charset=utf-8\r\n");
                sesion.bw.write("\r\n");
                sesion.bw.write(normalizarCrLf(cuerpo) + "\r\n");
            } else {
                // multipart/mixed: una parte de texto plano + una parte binaria en base64 con
                // Content-Disposition: attachment. Formato mínimo estándar (RFC 2045/2046),
                // sin librería — mismo criterio "raw sockets" que el resto de este archivo.
                String boundary = "----=_EducG_" + System.currentTimeMillis();
                sesion.bw.write("Content-Type: multipart/mixed; boundary=\"" + boundary + "\"\r\n");
                sesion.bw.write("\r\n");

                sesion.bw.write("--" + boundary + "\r\n");
                sesion.bw.write("Content-Type: text/plain; charset=utf-8\r\n");
                sesion.bw.write("\r\n");
                sesion.bw.write(normalizarCrLf(cuerpo) + "\r\n");

                sesion.bw.write("\r\n--" + boundary + "\r\n");
                sesion.bw.write("Content-Type: " + mimeType + "; name=\"" + nombreArchivo + "\"\r\n");
                sesion.bw.write("Content-Transfer-Encoding: base64\r\n");
                sesion.bw.write("Content-Disposition: attachment; filename=\"" + nombreArchivo + "\"\r\n");
                sesion.bw.write("\r\n");
                escribirBase64EnLineas(sesion.bw, datosAdjunto);

                sesion.bw.write("\r\n--" + boundary + "--\r\n");
            }
            sesion.bw.write(".\r\n");
            sesion.bw.flush();

            leerRespuesta(sesion.br);

            sesion.bw.write("QUIT\r\n");
            sesion.bw.flush();
            leerRespuesta(sesion.br);

        } catch (IOException e) {
            throw new EmailException("Error SMTP: " + e.getMessage(), e);
        }
    }

    /**
     * TLS directo desde el primer byte (SMTPS, puerto 465) — no STARTTLS sobre 587. Se probó
     * STARTTLS primero (RFC más común) y funcionaba hasta el propio handshake TLS: la conexión
     * en texto plano, EHLO y el comando STARTTLS se completaban bien, pero el handshake TLS que
     * sigue —a mitad de la misma conexión TCP— se colgaba sin respuesta (típico de un SMTP ALG
     * del router/ISP que inspecciona el protocolo y no sabe manejar el cambio a binario TLS a
     * mitad de conexión). TLS directo desde el connect no tiene ese tramo en texto plano que un
     * middlebox pueda malinterpretar — confirmado con un handshake de control contra
     * smtp.gmail.com:465 que respondió al instante en la misma red donde STARTTLS/587 se colgaba.
     */
    private SesionSmtp conectarYAutenticar(String usuario, String contrasena) throws IOException, EmailException {
        String host = ConfiguracionEmail.obtenerHost();
        int puerto = ConfiguracionEmail.obtenerPuerto();

        SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket socket = ssf.createSocket();
        try {
            socket.connect(new InetSocketAddress(host, puerto), TIMEOUT_CONEXION_MS);
            socket.setSoTimeout(TIMEOUT_LECTURA_MS);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            leerRespuesta(br); // 220 Bienvenida (ya sobre TLS)

            enviarComando(bw, br, "EHLO localhost");

            enviarComando(bw, br, "AUTH LOGIN");
            enviarComando(bw, br, Base64.getEncoder().encodeToString(usuario.getBytes()));
            enviarComando(bw, br, Base64.getEncoder().encodeToString(contrasena.getBytes()));

            return new SesionSmtp(socket, br, bw);
        } catch (IOException | EmailException e) {
            try {
                socket.close();
            } catch (IOException ignored) {}
            throw e;
        }
    }

    private void enviarComando(BufferedWriter bw, BufferedReader br, String comando) throws IOException, EmailException {
        bw.write(comando + "\r\n");
        bw.flush();
        String respuesta = leerRespuesta(br);
        if (respuesta.isEmpty()) {
            // No se incluye `comando` en el mensaje: en los pasos de AUTH LOGIN es directamente
            // el usuario/contraseña en base64, y este mensaje puede terminar mostrado en un
            // diálogo de la UI (VentanaVerificacionCodigo) — nunca hay que filtrar eso.
            throw new EmailException("El servidor SMTP cerró la conexión sin responder "
                + "(posible bloqueo de red, firewall, o credenciales rechazadas).");
        }
        String codigo = respuesta.substring(0, 3);
        int codigoNum = Integer.parseInt(codigo);
        if (codigoNum >= 400) {
            throw new EmailException("SMTP error " + codigo + ": " + respuesta);
        }
    }

    private void escribirBase64EnLineas(BufferedWriter bw, byte[] datos) throws IOException {
        String base64 = Base64.getEncoder().encodeToString(datos);
        for (int i = 0; i < base64.length(); i += ANCHO_LINEA_BASE64) {
            int fin = Math.min(i + ANCHO_LINEA_BASE64, base64.length());
            bw.write(base64, i, fin - i);
            bw.write("\r\n");
        }
    }

    private static boolean contieneCrLf(String valor) {
        return valor != null && (valor.indexOf('\r') >= 0 || valor.indexOf('\n') >= 0);
    }

    /** Normaliza cualquier mezcla de \n / \r\n a \r\n, sin duplicar \r si ya viniera mixto. */
    private static String normalizarCrLf(String texto) {
        return texto.replace("\r\n", "\n").replace("\n", "\r\n");
    }

    private String leerRespuesta(BufferedReader br) throws IOException {
        StringBuilder respuesta = new StringBuilder();
        String linea;
        while ((linea = br.readLine()) != null) {
            respuesta.append(linea).append("\n");
            // Línea sin '-' después del código = última línea de respuesta
            if (linea.length() > 3 && linea.charAt(3) != '-') {
                break;
            }
        }
        return respuesta.toString();
    }
}
