// File: AccessController.java
package com.musicfly.backend.controllers;

import com.musicfly.backend.services.AccessService;
import com.musicfly.backend.services.GoogleAuthGrantCodeService;
import com.musicfly.backend.services.MailService;
import com.musicfly.backend.views.DTO.LoginRequest;
import com.musicfly.backend.views.DTO.PasswordResetDTO;
import com.musicfly.backend.views.DTO.PasswordResetRequestDTO;
import com.musicfly.backend.views.DTO.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/access")
@RequiredArgsConstructor
public class AccessController {
    private final AccessService accessService;
    private final GoogleAuthGrantCodeService googleAuthGrantCodeService;
    private final MailService mailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest allRequest, HttpServletResponse allResponse) {
        return accessService.login(request, allRequest, allResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request, HttpServletRequest allRequest){
        return accessService.register(request, allRequest);
    }

    @PostMapping("/public/passwordResetRequest")
    public ResponseEntity<?> passwordResetRequest(@RequestBody PasswordResetRequestDTO passwordResetRequestDTO) {
        return accessService.passwordResetRequest(passwordResetRequestDTO);
    }

    @PostMapping("/passwordReset")
    public ResponseEntity<?> passwordReset(@RequestBody PasswordResetDTO passwordResetDTO) {
        return accessService.passwordReset(passwordResetDTO);
    }

    /**
     * Endpoint flexible que recibe { "id": "...", "password": "..." }
     * y confirma el reseteo de contraseña asociado a ese código/id.
     */
    @PostMapping("/confirmResetPassword")
    public ResponseEntity<?> confirmResetPassword(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        String password = body.get("password");
        return accessService.confirmResetPassword(id, password);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest allRequest, HttpServletResponse allResponse){
        return accessService.refresh(allRequest,allResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest allRequest, HttpServletResponse allResponse){
        return ResponseEntity.ok(accessService.logout(allRequest,allResponse));
    }


    // GET
    @GetMapping("/loginWithGoogleConfirm")
    public ResponseEntity<?> grantCode(@RequestParam("code") String code, @RequestParam(value = "scope", required = false) String scope, @RequestParam(value = "authuser", required = false) String authUser, @RequestParam(value = "prompt", required = false) String prompt, HttpServletRequest allRequest, HttpServletResponse allResponse) {
        return googleAuthGrantCodeService.getOauthAccessTokenGoogle(code,allRequest, allResponse);
    }

    @GetMapping("/confirmAccount")
    public ResponseEntity<?> confirmAccount(@RequestParam String id, HttpServletRequest allRequest) {
        return accessService.confirmAccount(id,allRequest);
    }

    /**
     * Solicita un nuevo código de verificación para el usuario (username)
     */
    @GetMapping("/newVerifiedId")
    public ResponseEntity<?> newVerifiedId(@RequestParam String username) {
        return accessService.newVerifiedId(username);
    }

    @GetMapping("/getAgainVerifiedID")
    public ResponseEntity<?> getAgainVerifiedId(@RequestParam String username) {
        return accessService.resendVerificationEmail(username);
    }
}