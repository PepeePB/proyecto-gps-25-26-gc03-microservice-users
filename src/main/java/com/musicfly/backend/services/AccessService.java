package com.musicfly.backend.services;

import com.musicfly.backend.jwt.JwtAuthenticationFilter;
import com.musicfly.backend.models.DAO.User;
import com.musicfly.backend.models.RoleList;
import com.musicfly.backend.models.TokenStates;
import com.musicfly.backend.models.UserOptionsUUID;
import com.musicfly.backend.properties.MessageProperties;
import com.musicfly.backend.repositories.UserRepository;
import com.musicfly.backend.views.DTO.*;
import com.musicfly.backend.views.DTO.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccessService {

    @Value("${app.domain}")
    private String domain;

    @Value("${app.domain.frontend}")
    private String domainFrontend;

    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RedisTokenService redisTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;

    private final static Map<String, Map<String,String>> DICTIONARY = MessageProperties.MESSAGE_PROPERTIES.getDictionary();

    @Value("${app.locate}")
    private String LOCATE;

    public ResponseEntity<?> login(LoginRequest request, HttpServletRequest allRequest, HttpServletResponse allResponse) {

        Map<String, Object> extraClaims = getExtraClientClaims(allRequest);
        Cookie cookieJWT, cookieUsername, cookieIsArtist;

        boolean isBrowser = isRequestFromBrowser(allRequest);

        try {
            User user;
            if(request.getUsername().contains("@")){
                user = userRepository.findByEmail(request.getUsername()).orElseThrow();
            }else{
                user = userRepository.findByUsername(request.getUsername()).orElseThrow();
            }
            user = userRepository.findByUsername(user.getUsername()).orElseThrow();
            if(user.verified){
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),request.getPassword()));
                cookieUsername = new Cookie("username", user.getUsername());
                cookieIsArtist = new Cookie("isArtist",String.valueOf(user.isArtist()));
                cookieUsername.setHttpOnly(false); cookieIsArtist.setHttpOnly(false);
                cookieUsername.setSecure(false); cookieIsArtist.setSecure(false);
                cookieUsername.setPath("/"); cookieIsArtist.setPath("/");
                cookieUsername.setMaxAge(24 * 60 * 60); cookieIsArtist.setMaxAge(24 * 60 * 60); // 1 dia
                allResponse.addCookie(cookieUsername);
                allResponse.addCookie(cookieIsArtist);

                if(redisTokenService.hasTokenType(UserOptionsUUID.VALID_TOKEN,user.getUsername())) {
                    return refresh(user,allRequest,allResponse);
                }

                String token = jwtService.getToken(extraClaims, user);

                cookieJWT = new Cookie("token", token);
                cookieJWT.setHttpOnly(true);
                cookieJWT.setSecure(false);
                cookieJWT.setPath("/");
                cookieJWT.setMaxAge(24 * 60 * 60); // 1 día
                allResponse.addCookie(cookieJWT);

                redisTokenService.validTokenList(token,user.getUsername());

                if(isBrowser){ // Devuelve vista HTML renderizada si es navegador
                    return ResponseEntity.status(HttpStatus.FOUND)
                            .location(URI.create(domainFrontend+"home"))  // La URL de redirección
                            .build();  // Realiza el redirect a /home
                }

                return new ResponseEntity<>(AuthResponse.builder()
                        .token(token)
                        .state(TokenStates.CREATED.toString())
                        .build(), HttpStatus.CREATED);
            }
            return new ResponseEntity<>(ErrorResponseDTO.builder()
                    .error("bad_credentials")
                    .message("Incorrect username and/or password.")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .timestamp(LocalDateTime.now().toString())
                    .build(), HttpStatus.BAD_REQUEST);

        }catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(ErrorResponseDTO.builder()
                    .error("unauthorized")
                    .message("Invalid username or password.")
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .timestamp(LocalDateTime.now().toString())
                    .build(), HttpStatus.UNAUTHORIZED);
        }


    }

    public ResponseEntity<?> logout(HttpServletRequest allRequest, HttpServletResponse allResponse) {
        String token = jwtAuthenticationFilter.getTokenFromRequest(allRequest);
        Cookie cookieBorrada = new Cookie("token", null);
        cookieBorrada.setPath("/");
        cookieBorrada.setMaxAge(0);
        if(allResponse != null) allResponse.addCookie(cookieBorrada);
        if(redisTokenService.hasTokenType(UserOptionsUUID.VALID_TOKEN,token)){
            redisTokenService.blacklistToken(token);
            redisTokenService.deleteValidTokenJWT(token);
            return new ResponseEntity<>(AuthResponse.builder()
                    .token(token)
                    .state(TokenStates.DELETED.toString())
                    .build(),HttpStatus.ACCEPTED);
        }else{
            return new ResponseEntity<>(ErrorResponseDTO.builder()
                    .error("not_property_token")
                    .message("This token has expired or is not owned by the client")
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .timestamp(LocalDateTime.now().toString())
                    .build(),HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<?> refresh(User userRequest, HttpServletRequest allRequest, HttpServletResponse allResponse) {
        Map<String, Object> extraClaims = getExtraClientClaims(allRequest);

        String token = jwtAuthenticationFilter.getTokenFromRequest(allRequest);

        if(token == null) return new ResponseEntity<>(AuthResponse.builder()
                .token(redisTokenService.getValueForKey(UserOptionsUUID.VALID_TOKEN, userRequest.getUsername()))
                .state(TokenStates.RENEWED.toString())
                .build(),HttpStatus.ACCEPTED);

        String ip = jwtService.getClaim(token,"ip");
        String webAgent = jwtService.getClaim(token,"webAgent");
        String usernameFromToken = jwtService.getUsernameFromToken(token);

        if(ip != null && webAgent != null
                &&
                ip.equals(extraClaims.get("ip")) && webAgent.equals(extraClaims.get("webAgent"))
                &&
                redisTokenService.hasTokenType(UserOptionsUUID.VALID_TOKEN,usernameFromToken)){
            UserDetails user = userDetailsService.loadUserByUsername(usernameFromToken);

            String newToken = jwtService.getToken(extraClaims, user);

            redisTokenService.blacklistToken(token);
            redisTokenService.deleteValidTokenJWT(token);
            redisTokenService.validTokenList(newToken,usernameFromToken);

            Cookie cookieJWT;

            cookieJWT = new Cookie("token", newToken);
            cookieJWT.setHttpOnly(true);
            cookieJWT.setSecure(false);
            cookieJWT.setPath("/");
            cookieJWT.setMaxAge(24 * 60 * 60);

            allResponse.addCookie(cookieJWT);

            return new ResponseEntity<>(AuthResponse.builder()
                    .token(newToken)
                    .state(TokenStates.RENEWED.toString())
                    .build(),HttpStatus.ACCEPTED);
        }else {
            // Si no coincide pero existe un token aun, hacemos un logout
            return logout(allRequest, allResponse);
        }
    }

    public ResponseEntity<?> register(RegisterRequest request, HttpServletRequest httpRequest) {

        String idVerified = UUID.randomUUID().toString();
        Map<String, Object> model = new HashMap<>();
        User newUser;

        boolean isBrowser = isRequestFromBrowser(httpRequest);

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // Contraseña cifrada en la BD
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(RoleList.ROLE_USER)
                .verified(false)
                .build();

        try {
            newUser = userRepository.save(user);
        }catch (Exception e){
            return new ResponseEntity<>(ErrorResponseDTO.builder()
                    .error("already_exists")
                    .message("User already exists.")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .timestamp(LocalDateTime.now().toString())
                    .build(),HttpStatus.BAD_REQUEST);
        }

        redisTokenService.insertUserOptionsId(newUser.getUsername(), UserOptionsUUID.CONFIRM_ACCOUNT,idVerified);

        model.put("name", newUser.getName());
        model.put("confirmationLink", domain+"access/confirmAccount?id="+idVerified);

        mailService.sendTemplateEmail(newUser.getEmail(), DICTIONARY.get(LOCATE).get("verified.subject"), "account-confirm.ftl", model);

        if(isBrowser){ // Devuelve vista HTML renderizada si es navegador
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(domain+"core/views/register/success"))  // La URL de redirección
                    .build();  // Realiza el redirect a /home
        }

        return new ResponseEntity<>(SuccessfulResponseDTO.builder()
                .successful("registration_ok")
                .message("User created and validation email sent successfully.")
                .statusCode(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now().toString())
                .build(),HttpStatus.OK);
    }

    public ResponseEntity<?> resendVerificationEmail(String username) {
        String idVerified = redisTokenService.getValueForKey(UserOptionsUUID.CONFIRM_ACCOUNT, username);
        Optional<User> userGet = userRepository.findByUsername(username);
        Map<String,Object> model = new HashMap<>();
        if(userGet.isPresent()) {
            User user = userGet.get();
            if (idVerified != null) {
                model.put("name", user.getName());
                model.put("confirmationLink", domain + "access/confirmAccount?id=" + idVerified);

                mailService.sendTemplateEmail(user.getEmail(), DICTIONARY.get(LOCATE).get("verified.subject"), "account-confirm.ftl", model);
                return new ResponseEntity<>(SuccessfulResponseDTO.builder()
                        .successful("again_verified_id")
                        .message("An email with the new verification ID has been sent again.")
                        .statusCode(HttpStatus.ACCEPTED.value())
                        .timestamp(LocalDateTime.now().toString())
                        .build(),HttpStatus.ACCEPTED);
            }

            return new ResponseEntity<>(ErrorResponseDTO.builder()
                    .error("verified_expired")
                    .message("Please, renew your verified id!")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .timestamp(LocalDateTime.now().toString())
                    .build(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ErrorResponseDTO.builder()
                .error("verified_expired")
                .message("Please, renew your verified id!")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now().toString())
                .build(),HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> newVerifiedId(String username) {
        String idVerified = UUID.randomUUID().toString();

        if(redisTokenService.getValueForKey(UserOptionsUUID.CONFIRM_ACCOUNT, username) != null){
            return new ResponseEntity<>(ErrorResponseDTO.builder()
                    .error("verified_exits")
                    .message("You already have an active verification code.")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .timestamp(LocalDateTime.now().toString())
                    .build(),HttpStatus.BAD_REQUEST);
        }

        Map<String,Object> model = new HashMap<>();
        model.put("name", username);
        model.put("confirmationLink", domain + "access/confirmAccount?id=" + idVerified);

        redisTokenService.insertUserOptionsId(username,UserOptionsUUID.CONFIRM_ACCOUNT,idVerified);

        mailService.sendTemplateEmail(username, DICTIONARY.get(LOCATE).get("verified.subject"), "account-confirm.ftl", model);

        return new ResponseEntity<>(SuccessfulResponseDTO.builder()
                .successful("again_verified_id")
                .message("An email with the new verification ID has been sent again.")
                .statusCode(HttpStatus.ACCEPTED.value())
                .timestamp(LocalDateTime.now().toString())
                .build(),HttpStatus.ACCEPTED);
    }

    public ResponseEntity<?> confirmAccount(String id, HttpServletRequest allRequest) {
        String username = redisTokenService.getValueForKey(UserOptionsUUID.CONFIRM_ACCOUNT,id);
        Optional<User> user = userRepository.findByUsername(username);
        boolean isBrowser = allRequest==null || isRequestFromBrowser(allRequest);
        if(user.isPresent()) {
            user.get().setVerified(true);
            userRepository.save(user.get());
            redisTokenService.deleteToken(UserOptionsUUID.CONFIRM_ACCOUNT,id);
            redisTokenService.deleteToken(UserOptionsUUID.CONFIRM_ACCOUNT,username);
            if(isBrowser){ // Devuelve vista HTML renderizada si es navegador
                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(domain+"core/views/verified/success"))  // La URL de redirección
                        .build();  // Realiza el redirect a /home
            }
            return new ResponseEntity<>(SuccessfulResponseDTO.builder()
                    .successful("confirmed_email")
                    .message("Confirmed email successfully.")
                    .statusCode(HttpStatus.OK.value())
                    .timestamp(LocalDateTime.now().toString())
                    .build(),HttpStatus.OK);
        }
        return new ResponseEntity<>(ErrorResponseDTO.builder()
                .error("invalid_id")
                .message("Invalid ID to confirm an account.")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now().toString())
                .build(),HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity<?> passwordResetRequest(PasswordResetRequestDTO passwordResetRequestDTO) {

        int number = (int)(Math.random() * 1_000_000); // Entre 0 y 999999
        String code = String.format("%06d", number);
        String identificador = passwordResetRequestDTO.getIdentificador();
        Map<String, Object> model = new HashMap<>();

        Optional<User> userGet = (identificador.contains("@")) ?
                userRepository.findByEmail(identificador)
                :
                userRepository.findByUsername(identificador);

        if(userGet.isPresent()) {
            User user = userGet.get();
            redisTokenService.insertUserOptionsId(user.getUsername(), UserOptionsUUID.RESET_PASSWORD,code);
            model.put("code", code.chars()
                    .mapToObj(c -> String.valueOf((char) c))
                    .collect(Collectors.toList()));
            model.put("resetPasswordLink",domain+"/core/views/code-verified");
            mailService.sendTemplateEmail(user.getEmail(), DICTIONARY.get(LOCATE).get("reset.password.subject"), "password-reset.ftl", model);
            return new ResponseEntity<>(SuccessfulResponseDTO.builder()
                    .successful("sent_password_reset")
                    .message("Sent password reset email successfully.")
                    .statusCode(HttpStatus.OK.value())
                    .timestamp(LocalDateTime.now().toString())
                    .build(),HttpStatus.OK);
        }
        return new ResponseEntity<>(ErrorResponseDTO.builder()
                .error("invalid_user")
                .message("The user provided does not exists.")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now().toString())
                .build(),HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity<?> passwordReset(PasswordResetDTO passwordResetDTO) {
        String username = passwordResetDTO.getUsername();
        String newPassword = passwordResetDTO.getPassword();
        String code = passwordResetDTO.getCode();
        if(username != null && newPassword != null && code != null){
            if( redisTokenService.hasTokenType(UserOptionsUUID.RESET_PASSWORD,passwordResetDTO.getUsername())
                    && redisTokenService.hasTokenType(UserOptionsUUID.RESET_PASSWORD,code)){
                if (redisTokenService.getValueForKey(UserOptionsUUID.RESET_PASSWORD,code).equals(username)){
                    Optional<User> userGet = userRepository.findByUsername(username);
                    if(userGet.isPresent()){
                        userGet.get().setPassword(passwordEncoder.encode(newPassword));
                        userRepository.save(userGet.get());
                        redisTokenService.deleteToken(UserOptionsUUID.RESET_PASSWORD,username);
                        redisTokenService.deleteToken(UserOptionsUUID.RESET_PASSWORD,code);
                        return new ResponseEntity<>(SuccessfulResponseDTO.builder()
                                .successful("password_reset")
                                .message("Password reset successfully.")
                                .statusCode(HttpStatus.OK.value())
                                .timestamp(LocalDateTime.now().toString())
                                .build(),HttpStatus.OK);
                    }
                }
            }
            return new ResponseEntity<>(ErrorResponseDTO.builder()
                    .error("invalid_code")
                    .message("The code provided does not match any user.")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .timestamp(LocalDateTime.now().toString())
                    .build(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ErrorResponseDTO.builder()
                .error("invalid_request")
                .message("The request does not contain all the required fields.")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now().toString())
                .build(),HttpStatus.BAD_REQUEST);
    }

    private String getClientIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    private Map<String, Object> getExtraClientClaims(HttpServletRequest allRequest) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("ip",getClientIp(allRequest));
        extraClaims.put("webAgent", allRequest.getHeader("User-Agent"));
        return extraClaims;
    }

    private boolean isRequestFromBrowser(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        String userAgent = request.getHeader("User-Agent");

        return accept != null && accept.contains("text/html") &&
                userAgent != null && !userAgent.toLowerCase().contains("httpclient");
    }


}
