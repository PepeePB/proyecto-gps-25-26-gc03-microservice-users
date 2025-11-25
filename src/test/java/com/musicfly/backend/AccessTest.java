// Test suite for AccessService using JUnit 5 and Mockito
// Refactored for current method signatures

package com.musicfly.backend;

import com.musicfly.backend.jwt.JwtAuthenticationFilter;
import com.musicfly.backend.models.DAO.User;
import com.musicfly.backend.models.RoleList;
import com.musicfly.backend.models.TokenStates;
import com.musicfly.backend.models.UserOptionsUUID;
import com.musicfly.backend.repositories.UserRepository;
import com.musicfly.backend.services.AccessService;
import com.musicfly.backend.services.JwtService;
import com.musicfly.backend.services.MailService;
import com.musicfly.backend.services.RedisTokenService;
import com.musicfly.backend.views.DTO.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccessTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest requestMock;

    @Mock
    private HttpServletResponse responseMock;

    @Mock
    private RedisTokenService redisTokenService;

    @Mock
    private JwtService jwtService;

    @Mock
    private MailService mailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AccessService accessService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest dto = new RegisterRequest();
        dto.setUsername("user1");
        dto.setPassword("pass123");
        dto.setName("Name");
        dto.setSurname("Surname");
        dto.setEmail("user1@example.com");
        dto.setPhone("1234567890");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = accessService.register(dto, requestMock);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("registration_ok"));
    }

    @Test
    void testLoginUserNotFound() {
        LoginRequest dto = new LoginRequest();
        dto.setUsername("nouser");
        dto.setPassword("pass");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        ResponseEntity<?> response = accessService.login(dto, requestMock, responseMock);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testLoginSuccess() throws Exception {
        // Mock del request y response
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Mock del usuario
        User user = User.builder()
                .username("testuser")
                .password("password")
                .verified(true)
                .isArtist(false)
                .build();

        // Request de login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        // Mockito para UserRepository
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Mockito para AuthenticationManager (devuelve un Authentication mock)
        Authentication authMock = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authMock);

        // Mockito para JwtService
        when(jwtService.getToken(anyMap(), any(User.class))).thenReturn("mocked-token");

        // Mockito para RedisTokenService
        when(redisTokenService.hasTokenType(any(), eq("testuser"))).thenReturn(false);
        doNothing().when(redisTokenService).validTokenList(anyString(), eq("testuser"));

        // Ejecutar el método
        ResponseEntity<?> responseEntity = accessService.login(loginRequest, request, response);

        // Comprobar resultado
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        AuthResponse authResponse = (AuthResponse) responseEntity.getBody();
        assertNotNull(authResponse);
        assertEquals("mocked-token", authResponse.getToken());
        assertEquals(TokenStates.CREATED.toString(), authResponse.getState());

        // Verificar que se añadieron cookies
        verify(response, times(3)).addCookie(any(Cookie.class));
    }



    @Test
    void testLogoutWithToken() {
        when(jwtAuthenticationFilter.getTokenFromRequest(any())).thenReturn("token123");
        when(redisTokenService.hasTokenType(UserOptionsUUID.VALID_TOKEN, "token123")).thenReturn(true);

        ResponseEntity<?> response = accessService.logout(requestMock, responseMock);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains(TokenStates.DELETED.toString()));
    }

    @Test
    void testConfirmAccountSuccess() {
        String id = "id123";
        User user = User.builder().username("user1").build();
        when(redisTokenService.getValueForKey(UserOptionsUUID.CONFIRM_ACCOUNT, id)).thenReturn("user1");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = accessService.confirmAccount(id, requestMock);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(user.isVerified());
    }
}
