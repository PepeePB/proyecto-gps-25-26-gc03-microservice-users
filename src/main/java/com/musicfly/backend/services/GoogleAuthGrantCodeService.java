package com.musicfly.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicfly.backend.models.DAO.User;
import com.musicfly.backend.repositories.UserRepository;
import com.musicfly.backend.views.DTO.*;
import com.musicfly.backend.views.DTO.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleAuthGrantCodeService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${app.domain}")
    private String domain;

    private final UserRepository userRepository;

    private final AccessService accessService;
    public ResponseEntity<?> getOauthAccessTokenGoogle(String code, HttpServletRequest allRequest, HttpServletResponse allResponse) {
        DataGoogleDTO dataGoogleDTO;

        try {
            dataGoogleDTO = new ObjectMapper().readValue(requestToGoogle(code), DataGoogleDTO.class);
            UserGoogleDTO userGoogleDTO = getUserToGoogleData(dataGoogleDTO.getAccess_token());
            return googleUserAddToSystem(userGoogleDTO,allRequest, allResponse);
        } catch (JsonProcessingException e){
            e.printStackTrace();
            return new ResponseEntity<>(ErrorResponseDTO.builder()
                    .error("conflict")
                    .message("Conflict occurred while processing the request.")
                    .statusCode(HttpStatus.CONFLICT.value())
                    .timestamp(LocalDateTime.now().toString())
                    .build(), HttpStatus.CONFLICT);
        }
    }

    /**
     * Primera fase, solicitar el access_token
     * @param code
     * @return
     */
    private String requestToGoogle(String code){
        String redirectUri = domain+"access/loginWithGoogleConfirm";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("redirect_uri", redirectUri);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("scope", "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile");
        params.add("scope", "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email");
        params.add("scope", "openid");
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, httpHeaders);

        params.forEach((k,v) -> System.out.println(k+" : "+v));

        String url = "https://oauth2.googleapis.com/token";
        return restTemplate.postForObject(url, requestEntity, String.class);
    }

    private UserGoogleDTO getUserToGoogleData(String accessToken){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<UserGoogleDTO> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET,
                entity,
                UserGoogleDTO.class
        );

        return response.getBody();
    }

    public ResponseEntity<?> googleUserAddToSystem(UserGoogleDTO userGoogleDTO, HttpServletRequest allRequest, HttpServletResponse allResponse) {

        Optional<User> user = userRepository.findByUsername(userGoogleDTO.getSub());
        if (user.isPresent()) {
            LoginRequest loginRequest = LoginRequest.builder()
                    .username(userGoogleDTO.getSub())
                    .password(userGoogleDTO.getSub())
                    .build();
            return accessService.login(loginRequest, allRequest, allResponse);
        } else {
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .username(userGoogleDTO.getSub())
                    .email(userGoogleDTO.getEmail())
                    .password(userGoogleDTO.getSub())
                    .name(userGoogleDTO.getName())
                    .surname(userGoogleDTO.getGiven_name())
                    .build();

            return accessService.register(registerRequest, allRequest);
        }
    }

}
