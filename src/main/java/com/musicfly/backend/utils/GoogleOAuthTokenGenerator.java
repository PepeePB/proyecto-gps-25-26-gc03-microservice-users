package com.musicfly.backend.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

/**
 * Herramienta usada para obtener por primera vez el refresh token, ha sido un lio, pero al final esta ha funcionado
 * ya que habia que añadir cuentas test para que esto fuera posible
 *
 * 80% powered by chatGPT -> Traduciendo un codigo de python
 */


@Data
@Builder
@RequiredArgsConstructor
public class GoogleOAuthTokenGenerator {

    private final static String CLIENT_ID = "499577998343-3fkc1iaedib757ue5o8cjp2npkkrvqp9.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String SCOPE = "https://mail.google.com/";

    private void start() throws Exception{
        String REDIRECT_URI = "http://localhost:8080/oauth2callback";
        System.out.println(REDIRECT_URI);
        String authUrl = String.format(
                "https://accounts.google.com/o/oauth2/v2/auth?scope=%s&access_type=offline&include_granted_scopes=true&response_type=code&client_id=%s&redirect_uri=%s",
                URLEncoder.encode(SCOPE, "UTF-8"),
                CLIENT_ID,
                URLEncoder.encode(REDIRECT_URI, "UTF-8")
        );

        // Abrir en el navegador
        if (Desktop.isDesktopSupported()) {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(authUrl));
                } else {
                    // Hack para Linux: usar xdg-open
                    Runtime.getRuntime().exec(new String[]{"xdg-open", authUrl});
                }
            } catch (Exception e) {
                System.out.println("No se pudo abrir el navegador automáticamente. Abre esta URL manualmente: " + authUrl);
                e.printStackTrace();
            }
        } else {
            System.out.println("Abre este link manualmente:\n" + authUrl);
        }

        // Iniciar servidor local para capturar el código
        var server = HttpServer.create(new InetSocketAddress(8080), 0);
        final String[] codeHolder = new String[1];

        server.createContext("/oauth2callback", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            String code = Arrays.stream(query.split("&"))
                    .filter(s -> s.startsWith("code="))
                    .map(s -> s.substring(5))
                    .findFirst()
                    .orElse(null);

            codeHolder[0] = code;

            String response = "<h2>¡Código recibido! Puedes volver a la consola.</h2>";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });

        new Thread(server::start).start();
        System.out.println("▶ Esperando autorización...");

        // Esperar hasta que llegue el código
        while (codeHolder[0] == null) {
            Thread.sleep(1000);
        }
        server.stop(0);

        // Intercambiar el código por refresh_token
        System.out.println("✅ Código recibido. Solicitando tokens...");

        String requestBody = String.format(
                "code=%s&client_id=%s&client_secret=%s&redirect_uri=%s&grant_type=authorization_code",
                codeHolder[0], CLIENT_ID, CLIENT_SECRET, REDIRECT_URI
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth2.googleapis.com/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.body());

        System.out.println("🔐 Access Token: " + json.get("access_token").asText());
        System.out.println("🔄 Refresh Token: " + json.get("refresh_token").asText());
    }
    public static void main(String[] args) throws Exception {
        GoogleOAuthTokenGenerator.builder().build().start();
    }
}
