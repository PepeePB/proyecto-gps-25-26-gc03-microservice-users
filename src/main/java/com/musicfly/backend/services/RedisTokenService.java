package com.musicfly.backend.services;

import com.musicfly.backend.models.UserOptionsUUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/***
 * Servicio para cachear ID generados para distintas operaciones, hay que tener en cuenta que las peticiones solo
 * se almacenan por un delay time establecido, a excepcion de la blacklist que hace que un token este durante su
 * vida util mas los minutos seteados en delay
 */
@Service
public class RedisTokenService {
    private final Duration delayTime = Duration.ofMinutes(10);

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtService jwtService;

    @Value("${redis.token.expiration-time}")  // Tiempo de expiración del token
    private long expirationTime;


    public RedisTokenService(RedisTemplate<String, String> redisTemplate, JwtService jwtService) {
        this.redisTemplate = redisTemplate;
        this.jwtService = jwtService;
    }

    public String getValueForKey(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public String getValueForKey(UserOptionsUUID type, String key){
        return redisTemplate.opsForValue().get(type+key);
    }

    public List<String> getKeysForKeyPatter(String key){
        Cursor<String> match = null;
        List<String> listOfMatchedKeys = new ArrayList<>();
        try {
            match = redisTemplate.scan(ScanOptions.scanOptions().match(key+"*").build());
            while (match.hasNext()){
                listOfMatchedKeys.add(match.next());
            }
        }catch (Exception ignored){

        }
        return listOfMatchedKeys;
    }

    public void insertUserOptionsId(String username, UserOptionsUUID type, String id){
        // Crea dos registros para que sea posible recuperar el enlace enviado
        redisTemplate.opsForValue().set(type+id,username,delayTime);
        redisTemplate.opsForValue().set(type+username,id,delayTime);
    }

    public void deleteToken(UserOptionsUUID type,String key) {
        redisTemplate.delete(type+key);
    }

    public void deleteValidTokenJWT(String token) {
        UserOptionsUUID type = UserOptionsUUID.VALID_TOKEN;
        redisTemplate.delete(type+token);
        redisTemplate.delete(type+jwtService.getUsernameFromToken(token));
    }

    public boolean hasTokenType(UserOptionsUUID type, String token) {
        // Verifica si el token está en la lista, consultando Redis
        return redisTemplate.hasKey(type + token);
    }

    public void blacklistToken(String token) {
        // Guardamos el token en Redis con un tiempo de expiración igual al del token JWT
        // Deberías obtener el tiempo de expiración del JWT
        Instant expirationTime = jwtService.getClaim(token, "exp");

        // Guardar el token en Redis con la expiración, usando la clave "BT#<token>"
        redisTemplate.opsForValue().set(UserOptionsUUID.BLACK_LIST + token, token, getExpirationTime(expirationTime));
    }

    public void validTokenList(String token,String username) {
        // Guardamos el token en Redis con un tiempo de expiración igual al del token JWT
        // Deberías obtener el tiempo de expiración del JWT
        Instant expirationTime = jwtService.getClaim(token, "exp");

        // Guardar el token en Redis con la expiración, usando la clave "VT#<token>"
        redisTemplate.opsForValue().set(UserOptionsUUID.VALID_TOKEN + token, username, getExpirationTime(expirationTime));
        redisTemplate.opsForValue().set(UserOptionsUUID.VALID_TOKEN + username, token, getExpirationTime(expirationTime));
    }

    // Método para limpiar toda la blacklist si fuera necesario
    public void clearTypeList(UserOptionsUUID type) {
        // Puedes usar un patrón para eliminar todas las claves de la blacklist
        redisTemplate.delete(redisTemplate.keys(type+"*"));
    }

    private Duration getExpirationTime(Instant expiracion){
        return Duration.between(Instant.now(), expiracion.plus(delayTime));
    }

}
