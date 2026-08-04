package email;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class EnviadorEmail {

    public void enviar(String destinatario, String asunto, String cuerpo) throws EmailException {
        String host = ConfiguracionEmail.obtenerHost();
        int puerto = ConfiguracionEmail.obtenerPuerto();
        String usuario = ConfiguracionEmail.obtenerUsuario();
        String contrasena = ConfiguracionEmail.obtenerContrasena();
        String desdeNombre = ConfiguracionEmail.obtenerDesdeNombre();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            throw new EmailException("Credenciales de SMTP no configuradas en .env (SMTP_USER y SMTP_PASSWORD).");
        }

        Socket socket = null;
        try {
            // Conectar al servidor SMTP en plain text
            socket = new Socket(host, puerto);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            leerRespuesta(br); // 220 Bienvenida

            // EHLO
            enviarComando(bw, br, "EHLO localhost");

            // STARTTLS
            enviarComando(bw, br, "STARTTLS");
            socket.close();

            // Reconectar con SSL
            SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) ssf.createSocket(host, puerto);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            leerRespuesta(br); // 220 de SSL

            // EHLO de nuevo
            enviarComando(bw, br, "EHLO localhost");

            // AUTH LOGIN
            enviarComando(bw, br, "AUTH LOGIN");
            enviarComando(bw, br, Base64.getEncoder().encodeToString(usuario.getBytes()));
            enviarComando(bw, br, Base64.getEncoder().encodeToString(contrasena.getBytes()));

            // MAIL FROM
            enviarComando(bw, br, "MAIL FROM:<" + usuario + ">");

            // RCPT TO
            enviarComando(bw, br, "RCPT TO:<" + destinatario + ">");

            // DATA
            enviarComando(bw, br, "DATA");

            // Cuerpo del email
            bw.write("From: " + desdeNombre + " <" + usuario + ">\r\n");
            bw.write("To: " + destinatario + "\r\n");
            bw.write("Subject: " + asunto + "\r\n");
            bw.write("Content-Type: text/plain; charset=utf-8\r\n");
            bw.write("\r\n");
            bw.write(cuerpo + "\r\n");
            bw.write(".\r\n");
            bw.flush();

            leerRespuesta(br);

            // QUIT
            bw.write("QUIT\r\n");
            bw.flush();
            leerRespuesta(br);

        } catch (IOException e) {
            throw new EmailException("Error SMTP: " + e.getMessage(), e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }
    }

    private void enviarComando(BufferedWriter bw, BufferedReader br, String comando) throws IOException, EmailException {
        bw.write(comando + "\r\n");
        bw.flush();
        String respuesta = leerRespuesta(br);
        String codigo = respuesta.substring(0, 3);
        int codigoNum = Integer.parseInt(codigo);
        if (codigoNum >= 400) {
            throw new EmailException("SMTP error " + codigo + ": " + respuesta);
        }
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
