package com.musicfly.backend.controllers;

import com.musicfly.backend.services.AccessService;
import com.musicfly.backend.services.GoogleAuthGrantCodeService;
import com.musicfly.backend.services.MailService;
import com.musicfly.backend.views.DTO.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AccessControllerTest {

    @Mock
    private AccessService accessService;
    @Mock
    private GoogleAuthGrantCodeService googleAuthGrantCodeService;
    @Mock
    private MailService mailService;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AccessController accessController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----------- LOGIN -----------
    @Test
    void login_shouldDelegateToAccessService() {
        LoginRequest loginRequest = new LoginRequest();
        ResponseEntity<Object> expected = new ResponseEntity<>(HttpStatus.OK);
        when(accessService.login(loginRequest, request, response)).thenReturn((ResponseEntity) expected);

        ResponseEntity<?> result = accessController.login(loginRequest, request, response);

        verify(accessService).login(loginRequest, request, response);
        assertThat(result).isEqualTo(expected);
    }

    // ----------- REGISTER -----------
    @Test
    void register_shouldDelegateToAccessService() {
        RegisterRequest registerRequest = new RegisterRequest();
        ResponseEntity<Object> expected = new ResponseEntity<>(HttpStatus.CREATED);
        when(accessService.register(registerRequest, request)).thenReturn((ResponseEntity) expected);

        ResponseEntity<?> result = accessController.register(registerRequest, request);

        verify(accessService).register(registerRequest, request);
        assertThat(result).isEqualTo(expected);
    }

    // ----------- PASSWORD RESET REQUEST -----------
    @Test
    void passwordResetRequest_shouldCallAccessService() {
        PasswordResetRequestDTO dto = new PasswordResetRequestDTO();
        ResponseEntity<Object> expected = new ResponseEntity<>(HttpStatus.OK);
        when(accessService.passwordResetRequest(dto)).thenReturn((ResponseEntity) expected);

        ResponseEntity<?> result = accessController.passwordResetRequest(dto);

        verify(accessService).passwordResetRequest(dto);
        assertThat(result).isEqualTo(expected);
    }

    // ----------- PASSWORD RESET -----------
    @Test
    void passwordReset_shouldCallAccessService() {
        PasswordResetDTO dto = new PasswordResetDTO();
        ResponseEntity<Object> expected = new ResponseEntity<>(HttpStatus.OK);
        when(accessService.passwordReset(dto)).thenReturn((ResponseEntity) expected);

        ResponseEntity<?> result = accessController.passwordReset(dto);

        verify(accessService).passwordReset(dto);
        assertThat(result).isEqualTo(expected);
    }

    // ----------- REFRESH TOKEN -----------
    @Test
    void refreshToken_shouldDelegateToAccessService() {
        ResponseEntity<Object> expected = new ResponseEntity<>(HttpStatus.ACCEPTED);
        when(accessService.refresh(request, response)).thenReturn((ResponseEntity) expected);

        ResponseEntity<?> result = accessController.refreshToken(request, response);

        verify(accessService).refresh(request, response);
        assertThat(result).isEqualTo(expected);
    }

    // ----------- LOGOUT -----------
    @Test
    void logout_shouldReturnOkResponse() {
        ResponseEntity<Object> expected = new ResponseEntity<>(HttpStatus.ACCEPTED);
        when(accessService.logout(request, response)).thenReturn((ResponseEntity) expected);

        ResponseEntity<?> result = accessController.logout(request, response);

        verify(accessService).logout(request, response);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(expected);
    }

    // ----------- LOGIN WITH GOOGLE CONFIRM -----------
    @Test
    void grantCode_shouldDelegateToGoogleAuthService() {
        String code = "testCode";
        String scope = "scope";
        String authUser = "1";
        String prompt = "consent";
        ResponseEntity<Object> expected = new ResponseEntity<>(HttpStatus.OK);
        when(googleAuthGrantCodeService.getOauthAccessTokenGoogle(code, request, response))
                .thenReturn((ResponseEntity) expected);

        ResponseEntity<?> result = accessController.grantCode(code, scope, authUser, prompt, request, response);

        verify(googleAuthGrantCodeService).getOauthAccessTokenGoogle(code, request, response);
        assertThat(result).isEqualTo(expected);
    }

    // ----------- CONFIRM ACCOUNT -----------
    @Test
    void confirmAccount_shouldDelegateToAccessService() {
        String id = "someId";
        ResponseEntity<Object> expected = new ResponseEntity<>(HttpStatus.OK);
        when(accessService.confirmAccount(id, request)).thenReturn((ResponseEntity) expected);

        ResponseEntity<?> result = accessController.confirmAccount(id, request);

        verify(accessService).confirmAccount(id, request);
        assertThat(result).isEqualTo(expected);
    }

    // ----------- NEW VERIFIED ID -----------
    @Test
    void newVerifiedId_shouldDelegateToAccessService() {
        String id = "user123";
        ResponseEntity<Object> expected = new ResponseEntity<>(HttpStatus.ACCEPTED);
        when(accessService.newVerifiedId(id)).thenReturn((ResponseEntity) expected);

        ResponseEntity<?> result = accessController.newVerifiedId(id);

        verify(accessService).newVerifiedId(id);
        assertThat(result).isEqualTo(expected);
    }

    // ----------- GET AGAIN VERIFIED ID -----------
    @Test
    void getAgainVerifiedId_shouldDelegateToAccessService() {
        String id = "user456";
        ResponseEntity<Object> expected = new ResponseEntity<>(HttpStatus.OK);
        when(accessService.resendVerificationEmail(id)).thenReturn((ResponseEntity) expected);

        ResponseEntity<?> result = accessController.getAgainVerifiedId(id);

        verify(accessService).resendVerificationEmail(id);
        assertThat(result).isEqualTo(expected);
    }
}
