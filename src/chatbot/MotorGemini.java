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

/** Motor real: llama a la Generative Language API de Google (Gemini) por HTTP directo. */
public class MotorGemini implements MotorChatbot {

    private static final String ENDPOINT_BASE = "https://generativelanguage.googleapis.com/v1beta/models/";
    private static final int MAX_OUTPUT_TOKENS = 1024;

    private final String apiKey;
    private final String modelo;
    private final HttpClient httpClient;

    public MotorGemini(String apiKey, String modelo) {
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
        cuerpo.put("system_instruction", new JSONObject()
            .put("parts", new JSONArray().put(new JSONObject().put("text", contextoSistema))));

        JSONArray contents = new JSONArray();
        for (MensajeChat m : historial) {
            // Gemini usa "user"/"model" en vez de "user"/"assistant".
            String rolGemini = "assistant".equals(m.getRol()) ? "model" : "user";
            contents.put(new JSONObject()
                .put("role", rolGemini)
                .put("parts", new JSONArray().put(new JSONObject().put("text", m.getContenido()))));
        }
        cuerpo.put("contents", contents);
        cuerpo.put("generationConfig", new JSONObject().put("maxOutputTokens", MAX_OUTPUT_TOKENS));

        String endpoint = ENDPOINT_BASE + modelo + ":generateContent?key=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(endpoint))
            .timeout(Duration.ofSeconds(30))
            .header("content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(cuerpo.toString()))
            .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new MotorChatbotException(
                "No se pudo conectar con el servicio de chatbot (Gemini). Verificá tu conexión a internet.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MotorChatbotException("La consulta al chatbot fue interrumpida.", e);
        }

        if (response.statusCode() == 400 || response.statusCode() == 403) {
            throw new MotorChatbotException("La clave CHATBOT_API_KEY no es válida para Gemini.");
        }
        if (response.statusCode() == 429) {
            throw new MotorChatbotException("Se alcanzó el límite gratuito de Gemini por ahora. Probá de nuevo en un momento.");
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
            JSONArray candidatos = json.getJSONArray("candidates");
            if (candidatos.isEmpty()) return "";
            JSONObject primerCandidato = candidatos.getJSONObject(0);
            JSONArray partes = primerCandidato.getJSONObject("content").getJSONArray("parts");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < partes.length(); i++) {
                sb.append(partes.getJSONObject(i).optString("text", ""));
            }
            return sb.toString();
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
