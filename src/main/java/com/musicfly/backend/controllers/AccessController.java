package com.musicfly.backend.controllers;

import com.musicfly.backend.services.AccessService;
import com.musicfly.backend.services.GoogleAuthGrantCodeService;
import com.musicfly.backend.services.MailService;
import com.musicfly.backend.views.DTO.LoginRequest;
import com.musicfly.backend.views.DTO.PasswordResetDTO;
import com.musicfly.backend.views.DTO.PasswordResetRequestDTO;
import com.musicfly.backend.views.DTO.RegisterRequest;
import com.musicfly.backend.views.DTO.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/access")
@RequiredArgsConstructor
public class AccessController {
    private final AccessService accessService;
    private final GoogleAuthGrantCodeService googleAuthGrantCodeService;
    private final MailService mailService;

    @PostMapping(value = "register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request, HttpServletRequest allRequest){
        return accessService.register(request,allRequest);
    }

    @PostMapping(value = "/public/passwordResetRequest")
    public ResponseEntity<?> passwordResetRequest(@RequestBody PasswordResetRequestDTO passwordResetRequestDTO) {
        return accessService.passwordResetRequest(passwordResetRequestDTO);
    }

    @PostMapping(value = "passwordReset")
    public ResponseEntity<?> passwordReset(@RequestBody PasswordResetDTO passwordResetDTO) {
        return accessService.passwordReset(passwordResetDTO);
    }

    @PostMapping(value = "confirmResetPassword")
    public ResponseEntity<String> confirmResetPassword(@RequestBody String id, @RequestBody String password) {
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest allRequest, HttpServletResponse allResponse){
        return accessService.refresh(allRequest,allResponse);
    }

    @PostMapping(value = "logout")
    public ResponseEntity<?> logout(HttpServletRequest allRequest, HttpServletResponse allResponse){
        return ResponseEntity.ok(accessService.logout(allRequest,allResponse));
    }


    // GET
    @GetMapping( "/loginWithGoogleConfirm")
    public ResponseEntity<?> grantCode(@RequestParam("code") String code, @RequestParam("scope") String scope, @RequestParam("authuser") String authUser, @RequestParam("prompt") String prompt, HttpServletRequest allRequest, HttpServletResponse allResponse) {
        return googleAuthGrantCodeService.getOauthAccessTokenGoogle(code,allRequest, allResponse);
    }

    @GetMapping("/confirmAccount")
    public ResponseEntity<?> confirmAccount(@RequestParam String id, HttpServletRequest allRequest) {
        return accessService.confirmAccount(id,allRequest);
    }

    @GetMapping("/newVerifiedId")
    public ResponseEntity<?> newVerifiedId(@RequestParam String id) {
        return accessService.newVerifiedId(id);
    }

    @GetMapping("/getAgainVerifiedID")
    public ResponseEntity<?> getAgainVerifiedId(@RequestParam String id) {
        return accessService.resendVerificationEmail(id);
    }
}
