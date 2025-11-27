package com.musicfly.backend.services;

import com.nimbusds.jose.jwk.JWKException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public String getToken(UserDetails user) {
        return getToken(new HashMap<>(), user);
    }

    public String getToken(Map<String, Object> extraClaims, UserDetails user) {
        return jwtEncoder.encode(JwtEncoderParameters.from(
                JwtClaimsSet.builder()
                        .subject(user.getUsername()) // En undersounds, los token se generan en base al username
                        .issuedAt(Instant.now()) // Fecha de creacion del token
                        .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))// 24 Horas
                        .claims(claims -> claims.putAll(extraClaims)) // Claims adicionales
                        .build())).getTokenValue();
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, JwtClaimNames.SUB);
    }

    /***
     * Comprueba si el token es valido, basandose en si el username coincide y el token no
     * ha expirado
     * @param token
     * @param userDetails
     * @return True si el token es valido
     */
    public boolean isTokenValid(String token, UserDetails userDetails, HttpServletRequest request) {
        final String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isExpiredThisToken(token);
    }

    /***
     * Obtiene un claim del token desencriptandolo
     * @param token
     * @return
     * @param <S> Generic tipe
     */
    public <S> S getClaim(String token, String claim){
        return jwtDecoder.decode(token).getClaim(claim);
    }

    /***
     * Haciendo uso del metodo getClaim obtenemos la fecha del token
     * @param token
     * @return Fecha de expiración del token como instancia Date
     */
    private Instant getExpirationDate(String token){
        return getClaim(token, JwtClaimNames.EXP);
    }

    /***
     * Hacinedo uso del metodo before de la clase Date comprobamos si el token
     * pasado por parametros ha vencido, es decir, si ha caducado, esto es posible
     * saberlo si la fecha de hoy es superior a la del token o, como dice la linea, si la fecha
     * que obtenemos del token es anterior a la fecha de hoy.
     * @param token
     * @return True si el token ha expirado ya
     */
    private boolean isExpiredThisToken(String token){
        return getExpirationDate(token).isBefore(Instant.now());
    }
}
