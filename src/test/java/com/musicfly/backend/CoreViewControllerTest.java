package com.musicfly.backend;

import com.musicfly.backend.controllers.CoreViewsController;
import com.musicfly.backend.models.DAO.User;
import com.musicfly.backend.models.UserOptionsUUID;
import com.musicfly.backend.repositories.UserRepository;
import com.musicfly.backend.services.RedisTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoreViewControllerTest {

    @InjectMocks
    private CoreViewsController controller;

    @Mock
    private RedisTokenService redisTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncorer;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inyectamos manualmente el valor de domainFrontend
        controller = new CoreViewsController(redisTokenService, userRepository, passwordEncorer);
        controller.domainFrontend = "https://frontend.com/";
    }

    @Test
    void testShowSuccessView() {
        ModelAndView mav = controller.showSuccessView();
        assertEquals("email/register_ok", mav.getViewName());
        assertEquals("https://frontend.com/login", mav.getModel().get("frontendURL"));
    }

    @Test
    void testShowSuccessVerifiedView() {
        ModelAndView mav = controller.showSuccessVerifiedView();
        assertEquals("email/verified_ok", mav.getViewName());
        assertEquals("https://frontend.com/login", mav.getModel().get("frontendURL"));
    }

    @Test
    void testMostrarFormularioVerificacion() {
        String view = controller.mostrarFormularioVerificacion(model);
        assertEquals("email/code-verified", view);
    }

    @Test
    void testVerificarCodigo_validToken() {
        String token = "validToken";
        String username = "user123";

        when(redisTokenService.hasTokenType(UserOptionsUUID.RESET_PASSWORD, token)).thenReturn(true);
        when(redisTokenService.getValueForKey(UserOptionsUUID.RESET_PASSWORD, token)).thenReturn(username);

        String result = controller.verificarCodigo(token, session, model);
        assertEquals("redirect:/core/views/password-reset-form?username=user123&passwordResetToken=validToken", result);
    }

    @Test
    void testVerificarCodigo_invalidToken() {
        String token = "invalidToken";

        when(redisTokenService.hasTokenType(UserOptionsUUID.RESET_PASSWORD, token)).thenReturn(false);

        String result = controller.verificarCodigo(token, session, model);
        assertEquals("email/code-verified", result);
        verify(model).addAttribute("error", "El código ingresado no es válido.");
    }

    @Test
    void testMostrarFormulario_passwordResetForm() {
        String view = controller.mostrarFormulario("token123", "user123", model);
        assertEquals("email/password-reset-form", view);
        verify(model).addAttribute("passwordResetToken", "token123");
        verify(model).addAttribute("username", "user123");
    }

    @Test
    void testProcesarFormulario_success() {
        String username = "user123";
        String passwordResetToken = "token123";
        String actual = "oldPass";
        String nueva = "newPass";
        String confirmar = "newPass";

        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(redisTokenService.hasTokenType(UserOptionsUUID.RESET_PASSWORD, passwordResetToken)).thenReturn(true);
        when(passwordEncorer.encode(nueva)).thenReturn("encodedNewPass");

        String view = controller.procesarFormulario(username, actual, nueva, confirmar, passwordResetToken, request, model);
        assertEquals("email/password-reset-form", view);
        verify(model).addAttribute("success", "Contraseña actualizada correctamente.");
        verify(redisTokenService).deleteToken(UserOptionsUUID.RESET_PASSWORD, passwordResetToken);
        assertEquals("encodedNewPass", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void testProcesarFormulario_passwordMismatch() {
        String username = "user123";
        String passwordResetToken = "token123";
        String actual = "oldPass";
        String nueva = "newPass";
        String confirmar = "differentPass";

        String view = controller.procesarFormulario(username, actual, nueva, confirmar, passwordResetToken, request, model);
        assertEquals("email/password-reset-form", view);
        verify(model).addAttribute("error", "Las contraseñas no coinciden.");
    }

    @Test
    void testProcesarFormulario_invalidToken() {
        String username = "user123";
        String passwordResetToken = "token123";
        String actual = "oldPass";
        String nueva = "newPass";
        String confirmar = "newPass";

        when(redisTokenService.hasTokenType(UserOptionsUUID.RESET_PASSWORD, passwordResetToken)).thenReturn(false);

        String view = controller.procesarFormulario(username, actual, nueva, confirmar, passwordResetToken, request, model);
        assertEquals("email/password-reset-form", view);
        verify(model).addAttribute("error", "Solicitud inválida o expirada.");
    }

    @Test
    void testProcesarFormulario_userNotFound() {
        String username = "user123";
        String passwordResetToken = "token123";
        String actual = "oldPass";
        String nueva = "newPass";
        String confirmar = "newPass";

        when(redisTokenService.hasTokenType(UserOptionsUUID.RESET_PASSWORD, passwordResetToken)).thenReturn(true);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        String view = controller.procesarFormulario(username, actual, nueva, confirmar, passwordResetToken, request, model);
        assertEquals("email/password-reset-form", view);
        verify(model).addAttribute("error", "Solicitud inválida o expirada.");
    }
}
