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

    @PostMapping(value = "refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest allRequest, HttpServletResponse allResponse){
        return null;
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
