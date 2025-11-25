package com.musicfly.backend.services;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.UserCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class GmailOAuth2Service {

    @Value("${gmail.client-id}")
    private String clientId;

    @Value("${gmail.client-secret}")
    private String clientSecret;

    @Value("${gmail.refresh-token}")
    private String refreshToken;

    private AccessToken cachedToken; // Almacenamos el token en memoria

    /**
     * Obtiene el access token, renovándolo si es necesario.
     * @return access_token
     * @throws IOException
     */
    public synchronized String getAccessToken() throws IOException {
        // Si no hay token o está por expirar en los próximos 60 segundos
        if (cachedToken == null || isExpiringSoon(cachedToken)) {
            System.out.println("Token expirado o por expirar. Renovando para poder continuar con el reenvio...");

            // Usamos el refresh_token para obtener un nuevo access_token
            UserCredentials credentials = UserCredentials.newBuilder()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRefreshToken(refreshToken)
                    .build();

            cachedToken = credentials.refreshAccessToken();
        }

        return cachedToken.getTokenValue();
    }

    /**
     * Verifica si el token está a punto de expirar (menos de 60 segundos de validez)
     * @param token
     * @return true si el token está por caducar
     */
    private boolean isExpiringSoon(AccessToken token) {
        Instant expiresAt = token.getExpirationTime().toInstant();
        Instant now = Instant.now();
        return now.plusSeconds(60).isAfter(expiresAt); // Si faltan menos de 60 segundos
    }
}
