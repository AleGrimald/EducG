package chatbot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/** Motor real: llama a la API de Mensajes de Claude (Anthropic) por HTTP directo. */
public class MotorClaude implements MotorChatbot {

    private static final String ENDPOINT = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";
    private static final int MAX_TOKENS = 1024;

    private final String apiKey;
    private final String modelo;
    private final HttpClient httpClient;

    public MotorClaude(String apiKey, String modelo) {
        this.apiKey = apiKey;
        this.modelo = modelo;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();
    }

    @Override
    public String enviarMensaje(List<MensajeChat> historial, String contextoSistema) throws MotorChatbotException {
        if (apiKey == null || apiKey.isBlank()) {
            throw new MotorChatbotException(
                "Falta configurar CHATBOT_API_KEY en el archivo .env. Contactá al administrador.");
        }

        JSONObject cuerpo = new JSONObject();
        cuerpo.put("model", modelo);
        cuerpo.put("max_tokens", MAX_TOKENS);
        cuerpo.put("system", contextoSistema);
        JSONArray mensajes = new JSONArray();
        for (MensajeChat m : historial) {
            mensajes.put(new JSONObject().put("role", m.getRol()).put("content", m.getContenido()));
        }
        cuerpo.put("messages", mensajes);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(ENDPOINT))
            .timeout(Duration.ofSeconds(30))
            .header("x-api-key", apiKey)
            .header("anthropic-version", ANTHROPIC_VERSION)
            .header("content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(cuerpo.toString()))
            .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new MotorChatbotException(
                "No se pudo conectar con el servicio de chatbot (Claude). Verificá tu conexión a internet.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MotorChatbotException("La consulta al chatbot fue interrumpida.", e);
        }

        if (response.statusCode() == 401) {
            throw new MotorChatbotException("La clave CHATBOT_API_KEY no es válida.");
        }
        if (response.statusCode() == 429) {
            throw new MotorChatbotException("El servicio de chatbot está temporalmente saturado. Probá de nuevo en un momento.");
        }
        if (response.statusCode() != 200) {
            throw new MotorChatbotException(
                "Error del servicio de chatbot (código " + response.statusCode() + "): " + extraerMensajeError(response.body()));
        }

        return extraerTexto(response.body());
    }

    private String extraerTexto(String cuerpoJson) throws MotorChatbotException {
        try {
            JSONObject json = new JSONObject(cuerpoJson);
            JSONArray content = json.getJSONArray("content");
            for (int i = 0; i < content.length(); i++) {
                JSONObject bloque = content.getJSONObject(i);
                if ("text".equals(bloque.optString("type"))) {
                    return bloque.getString("text");
                }
            }
            return "";
        } catch (Exception e) {
            throw new MotorChatbotException("No se pudo interpretar la respuesta del chatbot.", e);
        }
    }

    private String extraerMensajeError(String cuerpoJson) {
        try {
            return new JSONObject(cuerpoJson).getJSONObject("error").optString("message", "sin detalle");
        } catch (Exception e) {
            return "sin detalle";
        }
    }
}
